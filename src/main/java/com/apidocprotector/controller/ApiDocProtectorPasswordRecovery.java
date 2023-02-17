package com.apidocprotector.controller;

import com.apidocprotector.library.ApiDocProtectorLibrary;
import com.apidocprotector.model.ApiDocProtectorEntity;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.io.IOException;
import java.util.Map;

import static com.apidocprotector.enumerator.ApiDocProtectorLibraryEnum.GENERATOR_ERROR;

@Hidden
@Controller
@CrossOrigin(origins = "*")
public class ApiDocProtectorPasswordRecovery extends ApiDocProtectorLibrary {

	@Operation(hidden = true)
	@GetMapping(path = "${apidocprotector.custom.uri-password-recovery:/doc-protect/password/recovery}/{token}")
	public String password(@PathVariable(required = false, value = "token") String md5Token) {

		logTerm("PASSWORD RECOVERY FORM IS START", null, true);

		try {
			return apiDocProtectorRedirect.forwardToPasswordRecoveryGlass(md5Token);
		} catch (RuntimeException re) {
			logTerm("PASSWORD RECOVERY FORM [EXCEPTION]", re.getMessage(), true);
		}

		return apiDocProtectorErrorRedirect.redirectPasswordRecoveryError("error_to_load_form_password");
	}

	@Operation(hidden = true)
	@GetMapping(path = "/doc-protect/protector/password/recovery/glass/{token}")
	public String glass(@PathVariable(required = true, value = "token") String md5Token) {

		logTerm("PASSWORD RECOVERY IN GLASS START", null, true);

		try {
			return apiDocProtectorRedirect.redirectToPasswordRecoveryForm(md5Token);
		} catch (RuntimeException re) {
			logTerm("PASSWORD RECOVERY IN GLASS [EXCEPTION]", re.getMessage(), true);
		}
		return apiDocProtectorErrorRedirect.redirectPasswordRecoveryError("unknown");
	}

	@Operation(hidden = true)
	@GetMapping(path = "${apidocprotector.custom.uri-password-recovery:/doc-protect/password/recovery}/form/{token}")
	public ModelAndView form(@PathVariable(required = true, value = "token") String md5Token) {

		logTerm("FORM IN PASSWORD RECOVERY IS START", null, true);

		if (session.getAttribute("ADP-USER-PASSWORD-RECOVERY") == null || !session.getAttribute("ADP-USER-PASSWORD-RECOVERY").equals("1")) {
			return apiDocProtectorViewer.error(
					GENERATOR_ERROR.getMessage(),
					"invalid access in form password",
					GENERATOR_ERROR.getStatusCode());
		}

		try {
			if (session.getAttribute("ADP-ACCOUNT-PASSWORD-RECOVERY-SUCCESSFUL") != null) {
				if (session.getAttribute("ADP-ACCOUNT-PASSWORD-RECOVERY-SUCCESSFUL").equals("1")) {
					return apiDocProtectorViewer.passwordRecovery(true, md5Token);
				}
			}
			return apiDocProtectorViewer.passwordRecovery(false, md5Token);
		} catch (RuntimeException re) {
			logTerm("FORM IN PASSWORD RECOVERY [EXCEPTION]", re.getMessage(), true);
			return apiDocProtectorViewer.error(
					GENERATOR_ERROR.getMessage(),
					"The request has caused a exception",
					GENERATOR_ERROR.getStatusCode());
		}
	}

	@Operation(hidden = true)
	@PostMapping(
			path = "${apidocprotector.custom.uri-user-password-recovery:/doc-protect/password/recovery/user}",
			consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE
	)
	public String update(@Valid @RequestParam Map<String, String> body) throws IOException {

		logTerm("PASSWORD RECOVERY USER IS START", null, true);

		if (session.getAttribute("ADP-USER-PASSWORD-RECOVERY") == null || !session.getAttribute("ADP-USER-PASSWORD-RECOVERY").equals("1")) {
			logTerm("INVALID SESSION FROM PASSWORD RECOVERY USER", null, true);
			return apiDocProtectorErrorRedirect.redirectPasswordRecoveryError("invalid_session_user_password_recovery");
		}

		ApiDocProtectorEntity user = apiDocProtectorRepository.findByToken(body.get("token"));

		if (user == null) {
			session.setAttribute("ADP-ACCOUNT-PASSWORD-RECOVERY-SUCCESSFUL", null);
			return apiDocProtectorErrorRedirect.redirectPasswordRecoveryError("user_not_found");
		}

		String newToken = userPasswordUpdate(body, user);

		if (newToken == null) {
			return apiDocProtectorErrorRedirect.redirectPasswordRecoveryError("error_to_password_recovery");
		}

		try {

			user.setToken(newToken);
			String subject = apiDocProtectorMailSender.subjectMail(user.getUsername());
			String content = apiDocProtectorMailSender.contentMailPasswordRecovery(user);

			apiDocProtectorMailSender.sendMailAttached(user.getEmail(), subject, content);

			session.setAttribute("ADP-ACCOUNT-PASSWORD-RECOVERY-SUCCESSFUL", "1");
			return apiDocProtectorRedirect.redirectToPasswordRecoveryForm(newToken);

		} catch (RuntimeException re) {
			session.setAttribute("ADP-ACCOUNT-PASSWORD-RECOVERY-SUCCESSFUL", null);
			logTerm("CREATE IN PASSWORD RECOVERY [EXCEPTION]", re.getMessage(), true);
			return apiDocProtectorErrorRedirect.redirectPasswordRecoveryError("password_recovery_error");
		}
	}

	@Operation(hidden = true)
	@GetMapping(path = "/doc-protect/password/recovery/error/{data}")
	public ModelAndView error(@PathVariable(required = false) String data) {
		return apiDocProtectorViewer.error(
				GENERATOR_ERROR.getMessage(),
				data.replace("_", " "),
				GENERATOR_ERROR.getStatusCode());
	}

}
