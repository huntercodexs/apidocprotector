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

import static com.apidocprotector.enumerator.ApiDocProtectorAuditEnum.*;
import static com.apidocprotector.enumerator.ApiDocProtectorLibraryEnum.PASSWORD_RECOVERY_ERROR;

@Hidden
@Controller
@CrossOrigin(origins = "*")
public class ApiDocProtectorPasswordRecovery extends ApiDocProtectorLibrary {

	@Operation(hidden = true)
	@GetMapping(path = "${apidocprotector.custom.uri-password-recovery:/doc-protect/password/recovery}/{token}")
	public String password(@PathVariable(required = false, value = "token") String md5Token) {

		logTerm("PASSWORD RECOVERY FORM IS START", null, true);
		auditor(PASSWORD_RECOVERY_STARTED, null, null);

		try {
			return apiDocProtectorRedirect.forwardToPasswordRecoveryGlass(md5Token);
		} catch (RuntimeException re) {
			logTerm("PASSWORD RECOVERY FORM [EXCEPTION]", re.getMessage(), true);
			auditor(PASSWORD_EXCEPTION, re.getMessage(), null);
			return apiDocProtectorErrorRedirect.redirectPasswordRecoveryError("error_to_load_form_password");
		}

	}

	@Operation(hidden = true)
	@GetMapping(path = "/doc-protect/protector/password/recovery/glass/{token}")
	public String glass(@PathVariable(required = true, value = "token") String md5Token) {

		logTerm("PASSWORD RECOVERY IN GLASS START", null, true);
		auditor(PASSWORD_RECOVERY_GLASS_STARTED, null, null);

		try {
			return apiDocProtectorRedirect.redirectToPasswordRecoveryForm(md5Token);
		} catch (RuntimeException re) {
			logTerm("PASSWORD RECOVERY IN GLASS [EXCEPTION]", re.getMessage(), true);
			auditor(PASSWORD_RECOVERY_GLASS_EXCEPTION, re.getMessage(), null);
			return apiDocProtectorErrorRedirect.redirectPasswordRecoveryError("unknown");
		}
	}

	@Operation(hidden = true)
	@GetMapping(path = "${apidocprotector.custom.uri-password-recovery:/doc-protect/password/recovery}/form/{token}")
	public ModelAndView form(@PathVariable(required = true, value = "token") String md5Token) {

		logTerm("FORM IN PASSWORD RECOVERY IS START", null, true);
		auditor(PASSWORD_RECOVERY_FORM_STARTED, null, null);

		if (session.getAttribute("ADP-USER-PASSWORD-RECOVERY") == null || !session.getAttribute("ADP-USER-PASSWORD-RECOVERY").equals("1")) {

			auditor(PASSWORD_RECOVERY_FORM_INVALID_ACCCESS, null, null);

			return apiDocProtectorViewer.error(
					PASSWORD_RECOVERY_ERROR.getMessage(),
					"invalid access in form password",
					PASSWORD_RECOVERY_ERROR.getStatusCode());
		}

		try {

			if (session.getAttribute("ADP-ACCOUNT-PASSWORD-RECOVERY-SUCCESSFUL") != null) {
				if (session.getAttribute("ADP-ACCOUNT-PASSWORD-RECOVERY-SUCCESSFUL").equals("1")) {
					auditor(PASSWORD_RECOVERY_SUCCESSFUL, null, null);
					return apiDocProtectorViewer.passwordRecovery(true, md5Token);
				}
			}

			auditor(PASSWORD_RECOVERY_VIEW_FORM, null, null);
			return apiDocProtectorViewer.passwordRecovery(false, md5Token);

		} catch (RuntimeException re) {
			logTerm("FORM IN PASSWORD RECOVERY [EXCEPTION]", re.getMessage(), true);
			auditor(PASSWORD_RECOVERY_EXCEPTION, re.getMessage(), null);
			return apiDocProtectorViewer.error(
					PASSWORD_RECOVERY_ERROR.getMessage(),
					"The request has caused a exception",
					PASSWORD_RECOVERY_ERROR.getStatusCode());
		}
	}

	@Operation(hidden = true)
	@PostMapping(
			path = "${apidocprotector.custom.uri-user-password-recovery:/doc-protect/password/recovery/user}",
			consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE
	)
	public String update(@Valid @RequestParam Map<String, String> body) throws IOException {

		logTerm("PASSWORD RECOVERY USER IS START", null, true);
		auditor(PASSWORD_RECOVERY_DATA_POST, null, null);

		if (session.getAttribute("ADP-USER-PASSWORD-RECOVERY") == null || !session.getAttribute("ADP-USER-PASSWORD-RECOVERY").equals("1")) {
			logTerm("INVALID SESSION FROM PASSWORD RECOVERY USER", null, true);
			auditor(PASSWORD_RECOVERY_FORM_INVALID_SESSION, null, null);
			return apiDocProtectorErrorRedirect.redirectPasswordRecoveryError("invalid_session_user_password_recovery");
		}

		ApiDocProtectorEntity user = apiDocProtectorRepository.findByToken(body.get("token"));

		if (user == null) {
			session.setAttribute("ADP-ACCOUNT-PASSWORD-RECOVERY-SUCCESSFUL", null);
			auditor(PASSWORD_RECOVERY_USER_NOT_FOUND, "User not found (using token) " + body.get("token"), null);
			return apiDocProtectorErrorRedirect.redirectPasswordRecoveryError("user_not_found");
		}

		String newToken = userPasswordUpdate(body, user);

		if (newToken == null) {
			auditor(PASSWORD_RECOVERY_EXCEPTION, "Error to password recovery", null);
			return apiDocProtectorErrorRedirect.redirectPasswordRecoveryError("error_to_password_recovery");
		}

		try {

			String subject = apiDocProtectorMailSender.subjectMail(user.getUsername());
			String content = apiDocProtectorMailSender.contentMailPasswordRecovery(newToken, user);

			apiDocProtectorMailSender.sendMailAttached(user.getEmail(), subject, content);
			session.setAttribute("ADP-ACCOUNT-PASSWORD-RECOVERY-SUCCESSFUL", "1");
			auditor(PASSWORD_RECOVERY_MAIL_SENDER_OK, null, null);

			return apiDocProtectorRedirect.redirectToPasswordRecoveryForm(newToken);

		} catch (RuntimeException re) {

			session.setAttribute("ADP-ACCOUNT-PASSWORD-RECOVERY-SUCCESSFUL", null);
			logTerm("CREATE IN PASSWORD RECOVERY [EXCEPTION]", re.getMessage(), true);
			auditor(PASSWORD_RECOVERY_EXCEPTION, re.getMessage(), null);

			return apiDocProtectorErrorRedirect.redirectPasswordRecoveryError("password_recovery_error");
		}
	}

	@Operation(hidden = true)
	@GetMapping(path = "/doc-protect/password/recovery/error/{data}")
	public ModelAndView error(@PathVariable(required = false) String data) {
		auditor(PASSWORD_RECOVERY_EXCEPTION, data, null);
		return apiDocProtectorViewer.error(
				PASSWORD_RECOVERY_ERROR.getMessage(),
				data.replace("_", " "),
				PASSWORD_RECOVERY_ERROR.getStatusCode());
	}

}
