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

import static com.apidocprotector.enumerator.ApiDocProtectorLibraryEnum.PASSWORD_ERROR;
import static com.apidocprotector.enumerator.ApiDocProtectorRegisterEnum.*;

@Hidden
@Controller
@CrossOrigin(origins = "*")
public class ApiDocProtectorPassword extends ApiDocProtectorLibrary {

	@Operation(hidden = true)
	@GetMapping(path = "${apidocprotector.custom.uri-password:/doc-protect/password}")
	public String password() {

		register(PASSWORD_STARTED, null, "info", 0, "");

		try {
			return apiDocProtectorRedirect.forwardToPasswordGlass();
		} catch (RuntimeException re) {

			register(PASSWORD_EXCEPTION, null, "except", 1, re.getMessage());

			return apiDocProtectorErrorRedirect.redirectPasswordError("error_to_load_form_password");
		}

	}

	@Operation(hidden = true)
	@GetMapping(path = "/doc-protect/protector/password/glass")
	public String glass() {

		register(PASSWORD_GLASS_STARTED, null, "info", 0, "");

		try {
			return apiDocProtectorRedirect.redirectToPasswordForm();
		} catch (RuntimeException re) {

			register(PASSWORD_GLASS_EXCEPTION, null, "except", 1, re.getMessage());

			return apiDocProtectorErrorRedirect.redirectPasswordError("unknown");
		}
	}

	@Operation(hidden = true)
	@GetMapping(path = "${apidocprotector.custom.uri-password:/doc-protect/password}/form")
	public ModelAndView form() {

		register(PASSWORD_FORM_STARTED, null , "info", 0, "");

		if (session.getAttribute("ADP-USER-PASSWORD") == null || !session.getAttribute("ADP-USER-PASSWORD").equals("1")) {

			register(PASSWORD_FORM_INVALID_ACCCESS, null, "error", 2, "Invalid Access");

			return apiDocProtectorViewer.error(
					PASSWORD_ERROR.getMessage(),
					"invalid access in form password",
					PASSWORD_ERROR.getStatusCode());
		}

		try {

			if (session.getAttribute("ADP-ACCOUNT-PASSWORD-SUCCESSFUL") != null) {
				if (session.getAttribute("ADP-ACCOUNT-PASSWORD-SUCCESSFUL").equals("1")) {

					register(PASSWORD_CHANGED_SUCCESSFUL, null, "info", 2, "Password changed");

					return apiDocProtectorViewer.password(true);
				}
			}

			register(PASSWORD_VIEW_FORM, null, "info", 2, "");
			return apiDocProtectorViewer.password(false);

		} catch (RuntimeException re) {

			register(PASSWORD_EXCEPTION, null, "info", 1, re.getMessage());

			return apiDocProtectorViewer.error(
					PASSWORD_ERROR.getMessage(),
					"The request has caused a exception",
					PASSWORD_ERROR.getStatusCode());
		}
	}

	@Operation(hidden = true)
	@PostMapping(
			path = "${apidocprotector.custom.uri-user-password:/doc-protect/password/user}",
			consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE
	)
	public String update(@Valid @RequestParam Map<String, String> body) throws IOException {

		register(PASSWORD_DATA_POST, null, "info", 0, "");

		if (session.getAttribute("ADP-USER-PASSWORD") == null || !session.getAttribute("ADP-USER-PASSWORD").equals("1")) {
			register(PASSWORD_FORM_INVALID_SESSION, null, "error", 2, "Invalid Session");
			return apiDocProtectorErrorRedirect.redirectPasswordError("invalid_session_user_password");
		}

		ApiDocProtectorEntity user = apiDocProtectorRepository.findByEmail(body.get("email"));

		if (user == null) {

			register(PASSWORD_ACCOUNT_NOT_FOUND, null, "error", 2, "Account not found: " + body.get("email"));

			session.setAttribute("ADP-ACCOUNT-PASSWORD-SUCCESSFUL", null);
			return apiDocProtectorErrorRedirect.redirectPasswordError("user_not_found_password");
		}

		try {

			String subject = apiDocProtectorMailSender.subjectMail(user.getUsername());
			String content = apiDocProtectorMailSender.contentMailPassword(user);

			apiDocProtectorMailSender.sendMailAttached(user.getEmail(), subject, content);
			session.setAttribute("ADP-ACCOUNT-PASSWORD-SUCCESSFUL", "1");

			register(PASSWORD_MAIL_SENDER_OK, null, "info", 0, "");

			return apiDocProtectorRedirect.redirectToPasswordForm();

		} catch (RuntimeException re) {

			register(PASSWORD_EXCEPTION, null, "except", 1, re.getMessage());

			session.setAttribute("ADP-ACCOUNT-PASSWORD-SUCCESSFUL", null);
			return apiDocProtectorErrorRedirect.redirectPasswordError("password_recovery_error");
		}
	}

	@Operation(hidden = true)
	@GetMapping(path = "/doc-protect/password/error/{data}")
	public ModelAndView error(@PathVariable(required = false) String data) {

		register(PASSWORD_EXCEPTION, null, "error", 1, data);

		return apiDocProtectorViewer.error(
				PASSWORD_ERROR.getMessage(),
				data.replace("_", " "),
				PASSWORD_ERROR.getStatusCode());
	}

}
