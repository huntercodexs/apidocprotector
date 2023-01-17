package com.huntercodexs.sample.apidocprotector.controller;

import com.huntercodexs.sample.apidocprotector.library.ApiDocProtectorLibrary;
import com.huntercodexs.sample.apidocprotector.model.ApiDocProtectorEntity;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.io.IOException;
import java.util.Map;

import static com.huntercodexs.sample.apidocprotector.library.ApiDocProtectorErrorLibrary.GENERATOR_ERROR;

@Hidden
@Controller
@CrossOrigin(origins = "*")
public class ApiDocProtectorPassword extends ApiDocProtectorLibrary {

	@Operation(hidden = true)
	@GetMapping(path = "${apidocprotector.custom.uri-password:/doc-protect/password}")
	public String password() {

		logTerm("PASSWORD FORM IS START", null, true);

		try {
			return apiDocProtectorRedirect.forwardToPasswordGlass();
		} catch (RuntimeException re) {
			logTerm("PASSWORD FORM [EXCEPTION]", re.getMessage(), true);
		}

		return apiDocProtectorErrorRedirect.redirectPasswordError("error_to_load_form_password");
	}

	@Operation(hidden = true)
	@GetMapping(path = "/doc-protect/protector/password/glass")
	public String glass() {

		logTerm("PASSWORD IN GLASS START", null, true);

		try {
			return apiDocProtectorRedirect.redirectToPasswordForm();
		} catch (RuntimeException re) {
			logTerm("PASSWORD IN GLASS [EXCEPTION]", re.getMessage(), true);
		}
		return apiDocProtectorErrorRedirect.redirectPasswordError("unknown");
	}

	@Operation(hidden = true)
	@GetMapping(path = "${apidocprotector.custom.uri-password:/doc-protect/password}/form")
	public ModelAndView form() {

		logTerm("FORM IN PASSWORD IS START", null, true);

		if (session.getAttribute("ADP-USER-PASSWORD") == null || !session.getAttribute("ADP-USER-PASSWORD").equals("1")) {
			return apiDocProtectorViewer.error(
					GENERATOR_ERROR.getMessage(),
					"invalid access in form password",
					GENERATOR_ERROR.getStatusCode());
		}

		try {
			if (session.getAttribute("ADP-ACCOUNT-PASSWORD-SUCCESSFUL") != null) {
				if (session.getAttribute("ADP-ACCOUNT-PASSWORD-SUCCESSFUL").equals("1")) {
					return apiDocProtectorViewer.password(true);
				}
			}
			return apiDocProtectorViewer.password(false);
		} catch (RuntimeException re) {
			logTerm("FORM IN PASSWORD [EXCEPTION]", re.getMessage(), true);
			return apiDocProtectorViewer.error(
					GENERATOR_ERROR.getMessage(),
					"The request has caused a exception",
					GENERATOR_ERROR.getStatusCode());
		}
	}

	@Operation(hidden = true)
	@PostMapping(
			path = "${apidocprotector.custom.uri-user-password:/doc-protect/password/user}",
			consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE
	)
	public String update(@Valid @RequestParam Map<String, String> body) throws IOException {

		logTerm("PASSWORD USER IS START", null, true);

		if (session.getAttribute("ADP-USER-PASSWORD") == null || !session.getAttribute("ADP-USER-PASSWORD").equals("1")) {
			logTerm("INVALID SESSION FROM PASSWORD USER", null, true);
			return apiDocProtectorErrorRedirect.redirectPasswordError("invalid_session_user_password");
		}

		ApiDocProtectorEntity user = apiDocProtectorRepository.findByEmail(body.get("email"));

		if (user == null) {
			session.setAttribute("ADP-ACCOUNT-PASSWORD-SUCCESSFUL", null);
			return apiDocProtectorErrorRedirect.redirectPasswordError("user_not_found_password");
		}

		try {

			String subject = apiDocProtectorMailSender.subjectMail(user.getUsername());
			String content = apiDocProtectorMailSender.contentMailPassword(user);

			apiDocProtectorMailSender.sendMailAttached(user.getEmail(), subject, content);

			session.setAttribute("ADP-ACCOUNT-PASSWORD-SUCCESSFUL", "1");
			return apiDocProtectorRedirect.redirectToPasswordForm();

		} catch (RuntimeException re) {
			session.setAttribute("ADP-ACCOUNT-PASSWORD-SUCCESSFUL", null);
			logTerm("CREATE IN PASSWORD [EXCEPTION]", re.getMessage(), true);
			return apiDocProtectorErrorRedirect.redirectPasswordError("password_recovery_error");
		}
	}

	@Operation(hidden = true)
	@GetMapping(path = "/doc-protect/password/error/{data}")
	public ModelAndView error(@PathVariable(required = false) String data) {
		return apiDocProtectorViewer.error(
				GENERATOR_ERROR.getMessage(),
				data.replace("_", " "),
				GENERATOR_ERROR.getStatusCode());
	}

}
