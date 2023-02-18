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
import static com.apidocprotector.enumerator.ApiDocProtectorLibraryEnum.PASSWORD_ERROR;

@Hidden
@Controller
@CrossOrigin(origins = "*")
public class ApiDocProtectorPassword extends ApiDocProtectorLibrary {

	@Operation(hidden = true)
	@GetMapping(path = "${apidocprotector.custom.uri-password:/doc-protect/password}")
	public String password() {

		logTerm("PASSWORD FORM IS START", null, true);
		auditor(PASSWORD_STARTED, null, null);

		try {
			return apiDocProtectorRedirect.forwardToPasswordGlass();
		} catch (RuntimeException re) {
			logTerm("PASSWORD FORM [EXCEPTION]", re.getMessage(), true);
			auditor(PASSWORD_EXCEPTION, re.getMessage(), null);
			return apiDocProtectorErrorRedirect.redirectPasswordError("error_to_load_form_password");
		}

	}

	@Operation(hidden = true)
	@GetMapping(path = "/doc-protect/protector/password/glass")
	public String glass() {

		logTerm("PASSWORD IN GLASS START", null, true);
		auditor(PASSWORD_GLASS_STARTED, null, null);

		try {
			return apiDocProtectorRedirect.redirectToPasswordForm();
		} catch (RuntimeException re) {
			logTerm("PASSWORD IN GLASS [EXCEPTION]", re.getMessage(), true);
			auditor(PASSWORD_GLASS_EXCEPTION, re.getMessage(), null);
			return apiDocProtectorErrorRedirect.redirectPasswordError("unknown");
		}
	}

	@Operation(hidden = true)
	@GetMapping(path = "${apidocprotector.custom.uri-password:/doc-protect/password}/form")
	public ModelAndView form() {

		logTerm("FORM IN PASSWORD IS START", null, true);
		auditor(PASSWORD_FORM_STARTED, null, null);

		if (session.getAttribute("ADP-USER-PASSWORD") == null || !session.getAttribute("ADP-USER-PASSWORD").equals("1")) {

			auditor(PASSWORD_FORM_INVALID_ACCCESS, null, null);

			return apiDocProtectorViewer.error(
					PASSWORD_ERROR.getMessage(),
					"invalid access in form password",
					PASSWORD_ERROR.getStatusCode());
		}

		try {

			if (session.getAttribute("ADP-ACCOUNT-PASSWORD-SUCCESSFUL") != null) {
				if (session.getAttribute("ADP-ACCOUNT-PASSWORD-SUCCESSFUL").equals("1")) {
					auditor(PASSWORD_CHANGED_SUCCESSFUL, null, null);
					return apiDocProtectorViewer.password(true);
				}
			}

			auditor(PASSWORD_VIEW_FORM, null, null);
			return apiDocProtectorViewer.password(false);

		} catch (RuntimeException re) {
			logTerm("FORM IN PASSWORD [EXCEPTION]", re.getMessage(), true);
			auditor(PASSWORD_EXCEPTION, re.getMessage(), null);
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

		logTerm("PASSWORD USER IS START", null, true);
		auditor(PASSWORD_DATA_POST, null, null);

		if (session.getAttribute("ADP-USER-PASSWORD") == null || !session.getAttribute("ADP-USER-PASSWORD").equals("1")) {
			logTerm("INVALID SESSION FROM PASSWORD USER", null, true);
			auditor(PASSWORD_FORM_INVALID_SESSION, null, null);
			return apiDocProtectorErrorRedirect.redirectPasswordError("invalid_session_user_password");
		}

		ApiDocProtectorEntity user = apiDocProtectorRepository.findByEmail(body.get("email"));

		if (user == null) {
			auditor(PASSWORD_ACCOUNT_NOT_FOUND, "The account was not found " + body.get("email"), null);
			session.setAttribute("ADP-ACCOUNT-PASSWORD-SUCCESSFUL", null);
			return apiDocProtectorErrorRedirect.redirectPasswordError("user_not_found_password");
		}

		try {

			String subject = apiDocProtectorMailSender.subjectMail(user.getUsername());
			String content = apiDocProtectorMailSender.contentMailPassword(user);

			apiDocProtectorMailSender.sendMailAttached(user.getEmail(), subject, content);
			session.setAttribute("ADP-ACCOUNT-PASSWORD-SUCCESSFUL", "1");
			auditor(PASSWORD_MAIL_SENDER_OK, null, null);

			return apiDocProtectorRedirect.redirectToPasswordForm();

		} catch (RuntimeException re) {

			session.setAttribute("ADP-ACCOUNT-PASSWORD-SUCCESSFUL", null);
			logTerm("CREATE IN PASSWORD [EXCEPTION]", re.getMessage(), true);
			auditor(PASSWORD_EXCEPTION, re.getMessage(), null);

			return apiDocProtectorErrorRedirect.redirectPasswordError("password_recovery_error");
		}
	}

	@Operation(hidden = true)
	@GetMapping(path = "/doc-protect/password/error/{data}")
	public ModelAndView error(@PathVariable(required = false) String data) {
		auditor(PASSWORD_EXCEPTION, data, null);
		return apiDocProtectorViewer.error(
				PASSWORD_ERROR.getMessage(),
				data.replace("_", " "),
				PASSWORD_ERROR.getStatusCode());
	}

}
