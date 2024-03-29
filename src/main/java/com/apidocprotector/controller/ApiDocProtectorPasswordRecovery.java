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
	@GetMapping(path = "${apidocprotector.custom.uri-password-recovery:/doc-protect/password/recovery}/{token64}")
	public String password(@PathVariable(required = false, value = "token64") String token64) {

		register(PASSWORD_RECOVERY_STARTED, null, "info", 0, "");

		try {
			return apiDocProtectorRedirect.forwardToPasswordRecoveryGlass(token64);
		} catch (RuntimeException re) {

			register(PASSWORD_EXCEPTION, null, "except", 1, re.getMessage());

			return apiDocProtectorErrorRedirect.redirectError(base64Encode(re.getMessage()));
		}

	}

	@Operation(hidden = true)
	@GetMapping(path = "/doc-protect/protector/password/recovery/glass/{token64}")
	public String glass(@PathVariable(required = true, value = "token64") String token64) {

		register(PASSWORD_RECOVERY_GLASS_STARTED, null, "info", 0, "");

		try {
			return apiDocProtectorRedirect.redirectToPasswordRecoveryForm(token64);
		} catch (RuntimeException re) {

			register(PASSWORD_RECOVERY_GLASS_EXCEPTION, null, "except", 1, re.getMessage());

			return apiDocProtectorErrorRedirect.redirectError(base64Encode(re.getMessage()));
		}
	}

	@Operation(hidden = true)
	@GetMapping(path = "${apidocprotector.custom.uri-password-recovery:/doc-protect/password/recovery}/form/{token64}")
	public ModelAndView form(@PathVariable(required = true, value = "token64") String token64) {

		register(PASSWORD_RECOVERY_FORM_STARTED, null, "info", 0, "");

		if (session.getAttribute("ADP-USER-PASSWORD-RECOVERY") == null || !session.getAttribute("ADP-USER-PASSWORD-RECOVERY").equals("1")) {

			register(PASSWORD_RECOVERY_FORM_INVALID_ACCCESS, null, "except", 2, "Invalid Access");

			return apiDocProtectorViewer.error(
					PASSWORD_RECOVERY_ERROR.getMessage(),
					base64Encode("invalid access in form password"),
					PASSWORD_RECOVERY_ERROR.getStatusCode());
		}

		try {

			if (session.getAttribute("ADP-ACCOUNT-PASSWORD-RECOVERY-SUCCESSFULL") != null) {
				if (session.getAttribute("ADP-ACCOUNT-PASSWORD-RECOVERY-SUCCESSFULL").equals("1")) {

					register(PASSWORD_RECOVERY_SUCCESSFUL, null, "info", 2, "Password Recovered");

					return apiDocProtectorViewer.passwordRecovery(true, token64);
				}
			}

			register(PASSWORD_RECOVERY_VIEW_FORM, null, "info", 0, "");
			return apiDocProtectorViewer.passwordRecovery(false, token64);

		} catch (RuntimeException re) {

			register(PASSWORD_RECOVERY_EXCEPTION, null, "except", 1, re.getMessage());

			return apiDocProtectorViewer.error(
					PASSWORD_RECOVERY_ERROR.getMessage(),
					base64Encode("The request has caused a exception " + re.getMessage()),
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
			register(PASSWORD_RECOVERY_FORM_INVALID_SESSION, null, "error", 2, "Invalid session");
			return apiDocProtectorErrorRedirect.redirectError(base64Encode("Invalid session"));
		}

		if (body.get("token") == null || body.get("token").equals("")) {
			register(GENERIC_MESSAGE, null, "error", 2, "Missing token on request (invalid form)");
			return apiDocProtectorErrorRedirect.redirectError(base64Encode("Missing token on request (invalid form)"));
		}

		if (body.get("password") == null || body.get("password").equals("")) {
			register(GENERIC_MESSAGE, null, "error", 2, "Missing password on request");
			return apiDocProtectorErrorRedirect.redirectError(base64Encode("Missing password on request"));
		}

		String md5TokenCrypt = base64Decode(body.get("token"));

		ApiDocProtectorEntity user = apiDocProtectorRepository.findByToken(md5TokenCrypt);

		if (user == null) {

			register(PASSWORD_RECOVERY_USER_NOT_FOUND, null, "error", 2, "User not found to token " + md5TokenCrypt);

			session.setAttribute("ADP-ACCOUNT-PASSWORD-RECOVERY-SUCCESSFULL", null);
			return apiDocProtectorErrorRedirect.redirectError(base64Encode("User not found to token " + md5TokenCrypt));
		}

		String updatedMd5TokenCrypt = userPasswordUpdate(body, user);

		if (updatedMd5TokenCrypt == null) {
			register(PASSWORD_RECOVERY_EXCEPTION, null, "info", 1, "Password recovery exception");
			return apiDocProtectorErrorRedirect.redirectError(base64Encode("Password recovery exception"));
		}

		try {

			String subject = apiDocProtectorMailSender.subjectMail("Password recovered", user.getUsername());
			String content = apiDocProtectorMailSender.contentMailPasswordRecovery(updatedMd5TokenCrypt, user);

			apiDocProtectorMailSender.sendMailAttached(user.getEmail(), subject, content);
			session.setAttribute("ADP-ACCOUNT-PASSWORD-RECOVERY-SUCCESSFULL", "1");

			register(PASSWORD_RECOVERY_MAIL_SENDER_OK, null, "info", 0, "Email sended to: " + user.getName());

			return apiDocProtectorRedirect.redirectToPasswordRecoveryForm(updatedMd5TokenCrypt);

		} catch (RuntimeException re) {

			register(PASSWORD_RECOVERY_EXCEPTION, null, "except", 1, re.getMessage());

			session.setAttribute("ADP-ACCOUNT-PASSWORD-RECOVERY-SUCCESSFULL", null);
			return apiDocProtectorErrorRedirect.redirectError(base64Encode(re.getMessage()));
		}
	}

}
