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

import static com.apidocprotector.enumerator.ApiDocProtectorLibraryEnum.PASSWORD_RECOVERY_ERROR;
import static com.apidocprotector.enumerator.ApiDocProtectorRegisterEnum.*;

@Hidden
@Controller
@CrossOrigin(origins = "*")
public class ApiDocProtectorPasswordRecovery extends ApiDocProtectorLibrary {

	@Operation(hidden = true)
	@GetMapping(path = "${apidocprotector.custom.uri-password-recovery:/doc-protect/password/recovery}/{token}")
	public String password(@PathVariable(required = false, value = "token") String md5Token) {

		register(PASSWORD_RECOVERY_STARTED, null, "info", 0, "");

		try {
			return apiDocProtectorRedirect.forwardToPasswordRecoveryGlass(md5Token);
		} catch (RuntimeException re) {

			register(PASSWORD_EXCEPTION, null, "except", 1, re.getMessage());

			return apiDocProtectorErrorRedirect.redirectPasswordRecoveryError("error_to_load_form_password");
		}

	}

	@Operation(hidden = true)
	@GetMapping(path = "/doc-protect/protector/password/recovery/glass/{token}")
	public String glass(@PathVariable(required = true, value = "token") String md5Token) {

		register(PASSWORD_RECOVERY_GLASS_STARTED, null, "info", 0, "");

		try {
			return apiDocProtectorRedirect.redirectToPasswordRecoveryForm(md5Token);
		} catch (RuntimeException re) {

			register(PASSWORD_RECOVERY_GLASS_EXCEPTION, null, "except", 1, re.getMessage());

			return apiDocProtectorErrorRedirect.redirectPasswordRecoveryError("unknown");
		}
	}

	@Operation(hidden = true)
	@GetMapping(path = "${apidocprotector.custom.uri-password-recovery:/doc-protect/password/recovery}/form/{token}")
	public ModelAndView form(@PathVariable(required = true, value = "token") String md5Token) {

		register(PASSWORD_RECOVERY_FORM_STARTED, null, "info", 0, "");

		if (session.getAttribute("ADP-USER-PASSWORD-RECOVERY") == null || !session.getAttribute("ADP-USER-PASSWORD-RECOVERY").equals("1")) {

			register(PASSWORD_RECOVERY_FORM_INVALID_ACCCESS, null, "except", 2, "Invalid Access");

			return apiDocProtectorViewer.error(
					PASSWORD_RECOVERY_ERROR.getMessage(),
					"invalid access in form password",
					PASSWORD_RECOVERY_ERROR.getStatusCode());
		}

		try {

			if (session.getAttribute("ADP-ACCOUNT-PASSWORD-RECOVERY-SUCCESSFUL") != null) {
				if (session.getAttribute("ADP-ACCOUNT-PASSWORD-RECOVERY-SUCCESSFUL").equals("1")) {

					register(PASSWORD_RECOVERY_SUCCESSFUL, null, "info", 2, "Paasword Recovered");

					return apiDocProtectorViewer.passwordRecovery(true, md5Token);
				}
			}

			register(PASSWORD_RECOVERY_VIEW_FORM, null, "info", 0, "");
			return apiDocProtectorViewer.passwordRecovery(false, md5Token);

		} catch (RuntimeException re) {

			register(PASSWORD_RECOVERY_EXCEPTION, null, "except", 1, re.getMessage());

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

		register(PASSWORD_RECOVERY_DATA_POST, null, "info", 0, "");

		if (session.getAttribute("ADP-USER-PASSWORD-RECOVERY") == null || !session.getAttribute("ADP-USER-PASSWORD-RECOVERY").equals("1")) {

			register(PASSWORD_RECOVERY_FORM_INVALID_SESSION, null, "error", 2, "Invalid Session");

			return apiDocProtectorErrorRedirect.redirectPasswordRecoveryError("invalid_session_user_password_recovery");
		}

		ApiDocProtectorEntity user = apiDocProtectorRepository.findByToken(body.get("token"));

		if (user == null) {

			register(PASSWORD_RECOVERY_USER_NOT_FOUND, null, "error", 2, "User not found (using token) " + body.get("token"));

			session.setAttribute("ADP-ACCOUNT-PASSWORD-RECOVERY-SUCCESSFUL", null);
			return apiDocProtectorErrorRedirect.redirectPasswordRecoveryError("user_not_found");
		}

		String newToken = userPasswordUpdate(body, user);

		if (newToken == null) {

			register(PASSWORD_RECOVERY_EXCEPTION, null, "info", 1, "Password Recovery");

			return apiDocProtectorErrorRedirect.redirectPasswordRecoveryError("error_to_password_recovery");

		}

		try {

			String subject = apiDocProtectorMailSender.subjectMail(user.getUsername());
			String content = apiDocProtectorMailSender.contentMailPasswordRecovery(newToken, user);

			apiDocProtectorMailSender.sendMailAttached(user.getEmail(), subject, content);
			session.setAttribute("ADP-ACCOUNT-PASSWORD-RECOVERY-SUCCESSFUL", "1");

			register(PASSWORD_RECOVERY_MAIL_SENDER_OK, null, "info", 0, "Email sended to: " + user.getName());

			return apiDocProtectorRedirect.redirectToPasswordRecoveryForm(newToken);

		} catch (RuntimeException re) {

			register(PASSWORD_RECOVERY_EXCEPTION, null, "except", 1, re.getMessage());

			session.setAttribute("ADP-ACCOUNT-PASSWORD-RECOVERY-SUCCESSFUL", null);
			return apiDocProtectorErrorRedirect.redirectPasswordRecoveryError("password_recovery_error");
		}
	}

	@Operation(hidden = true)
	@GetMapping(path = "/doc-protect/password/recovery/error/{data}")
	public ModelAndView error(@PathVariable(required = false) String data) {

		register(PASSWORD_RECOVERY_EXCEPTION, null, "error", 2, data);

		return apiDocProtectorViewer.error(
				PASSWORD_RECOVERY_ERROR.getMessage(),
				data.replace("_", " "),
				PASSWORD_RECOVERY_ERROR.getStatusCode());
	}

}