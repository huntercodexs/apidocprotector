package com.apidocprotector.controller;

import com.apidocprotector.library.ApiDocProtectorLibrary;
import com.apidocprotector.model.ApiDocProtectorEntity;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.io.IOException;
import java.util.Map;

import static com.apidocprotector.enumerator.ApiDocProtectorLibraryEnum.RECOVERY_ERROR;
import static com.apidocprotector.enumerator.ApiDocProtectorRegisterEnum.*;

@Hidden
@Controller
@CrossOrigin(origins = "*")
public class ApiDocProtectorRecovery extends ApiDocProtectorLibrary {

	@Operation(hidden = true)
	@GetMapping(path = "${apidocprotector.custom.uri-recovery:/doc-protect/recovery}")
	public String recovery() {

		register(RECOVERY_STARTED, null, "info", 0, "");

		try {
			return apiDocProtectorRedirect.forwardToRecoveryGlass();
		} catch (RuntimeException re) {

			register(RECOVERY_EXCEPTION, null, "except", 1, re.getMessage());

			return apiDocProtectorErrorRedirect.redirectError(base64Encode(re.getMessage()));
		}

	}

	@Operation(hidden = true)
	@GetMapping(path = "/doc-protect/protector/recovery/glass")
	public String glass() {

		register(RECOVERY_GLASS_STARTED, null, "info", 0, "");

		try {
			return apiDocProtectorRedirect.redirectToRecoveryForm();
		} catch (RuntimeException re) {

			register(RECOVERY_GLASS_EXCEPTION, null, "except", 1, re.getMessage());

			return apiDocProtectorErrorRedirect.redirectError(base64Encode(re.getMessage()));
		}
	}

	@Operation(hidden = true)
	@GetMapping(path = "${apidocprotector.custom.uri-recovery:/doc-protect/recovery}/form")
	public ModelAndView form() {

		register(RECOVERY_FORM_STARTED, null, "info", 0, "");

		if (session.getAttribute("ADP-USER-RECOVERY") == null || !session.getAttribute("ADP-USER-RECOVERY").equals("1")) {

			register(RECOVERY_FORM_INVALID_SESSION, null, "error", 2, "Invalid Session");

			return apiDocProtectorViewer.error(
					RECOVERY_ERROR.getMessage(),
					base64Encode("invalid access in form recovery"),
					RECOVERY_ERROR.getStatusCode());
		}

		try {

			if (session.getAttribute("ADP-ACCOUNT-RECOVERY-SUCCESSFUL") != null) {
				if (session.getAttribute("ADP-ACCOUNT-RECOVERY-SUCCESSFUL").equals("1")) {
					auditor(RECOVERY_ACCOUNT_SUCCESSFUL, null, null, 2);
					return apiDocProtectorViewer.recovery(true);
				}
			}

			register(RECOVERY_VIEW_FORM, null, "info", 2, "");
			return apiDocProtectorViewer.recovery(false);

		} catch (RuntimeException re) {

			register(RECOVERY_EXCEPTION, null, "except", 1, re.getMessage());

			return apiDocProtectorViewer.error(
					RECOVERY_ERROR.getMessage(),
					base64Encode("The request has caused a exception " + re.getMessage()),
					RECOVERY_ERROR.getStatusCode());
		}
	}

	@Operation(hidden = true)
	@PostMapping(
			path = "${apidocprotector.custom.uri-user-recovery:/doc-protect/recovery/user}",
			consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE
	)
	public String update(@Valid @RequestParam Map<String, String> body) throws IOException {

		register(RECOVERY_DATA_POST, null, "info", 0, "");

		if (session.getAttribute("ADP-USER-RECOVERY") == null || !session.getAttribute("ADP-USER-RECOVERY").equals("1")) {
			register(RECOVERY_FORM_INVALID_SESSION, null, "error", 2, "Ivalid session");
			return apiDocProtectorErrorRedirect.redirectError(base64Encode("Invalid session"));
		}

		if (body.get("email") == null || body.get("email").equals("")) {
			register(GENERIC_MESSAGE, null, "error", 2, "Missing email on request");
			return apiDocProtectorErrorRedirect.redirectError(base64Encode("Missing email on request"));
		}

		if (!mailValidator(body.get("email"))) {
			register(RECOVERY_FORM_INVALID_EMAIL, null, "error", 2, null);
			return apiDocProtectorErrorRedirect.redirectError(base64Encode(RECOVERY_FORM_INVALID_EMAIL.getMessage()));
		}

		ApiDocProtectorEntity user = apiDocProtectorRepository.findByEmail(body.get("email"));

		if (user == null) {

			register(PASSWORD_RECOVERY_USER_NOT_FOUND, null, "error", 2, "User not found " + body.get("email"));

			session.setAttribute("ADP-ACCOUNT-RECOVERY-SUCCESSFUL", null);
			return apiDocProtectorErrorRedirect.redirectError(base64Encode("User not found " + body.get("email")));
		}

		try {

			String md5TokenCrypt = userRecovery(body, user);
			String emailTo = body.get("email");
			String subject = apiDocProtectorMailSender.subjectMail("Account recovered", user.getUsername());
			String content = apiDocProtectorMailSender.contentMailRecoveryUser(user.getName(), md5TokenCrypt);

			apiDocProtectorMailSender.sendMailAttached(emailTo, subject, content);
			session.setAttribute("ADP-ACCOUNT-RECOVERY-SUCCESSFUL", "1");

			register(PASSWORD_RECOVERY_MAIL_SENDER_OK, null, "info", 0, "");
			return apiDocProtectorRedirect.redirectToRecoveryForm();

		} catch (RuntimeException re) {

			register(RECOVERY_EXCEPTION, null, "info", 1, re.getMessage());

			session.setAttribute("ADP-ACCOUNT-RECOVERY-SUCCESSFUL", null);
			return apiDocProtectorErrorRedirect.redirectError(base64Encode(re.getMessage()));
		}
	}

}
