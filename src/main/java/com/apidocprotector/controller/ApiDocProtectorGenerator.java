package com.apidocprotector.controller;

import com.apidocprotector.library.ApiDocProtectorLibrary;
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
import static com.apidocprotector.enumerator.ApiDocProtectorLibraryEnum.GENERATOR_ERROR;

@Hidden
@Controller
@CrossOrigin(origins = "*")
public class ApiDocProtectorGenerator extends ApiDocProtectorLibrary {

	@Operation(hidden = true)
	@GetMapping(path = "${apidocprotector.custom.uri-generator:/doc-protect/generator}")
	public String generator() {

		debugger("GENERATOR FORM IS START", null, true);
		auditor(GENERATOR_STARTED, null, null, 0);

		try {
			return apiDocProtectorRedirect.forwardToGeneratorGlass();
		} catch (RuntimeException re) {

			debugger("GENERATOR FORM [EXCEPTION]", re.getMessage(), true);
			logger("GENERATOR FORM [EXCEPTION]: " + re.getMessage(), "except");
			auditor(GENERATOR_EXCEPTION, re.getMessage(), null, 1);

			return apiDocProtectorErrorRedirect.redirectGeneratorError("error_to_load_form");
		}

	}

	@Operation(hidden = true)
	@GetMapping(path = "/doc-protect/protector/generator/glass")
	public String glass() {

		debugger("GENERATOR GLASS START", null, true);
		auditor(GENERATOR_GLASS_STARTED, null, null, 0);

		try {
			return apiDocProtectorRedirect.redirectToGeneratorForm();
		} catch (RuntimeException re) {

			debugger("GENERATOR IN GLASS [EXCEPTION]", re.getMessage(), true);
			logger("GENERATOR IN GLASS [EXCEPTION]: " + re.getMessage(), "except");
			auditor(GENERATOR_GLASS_EXCEPTION, re.getMessage(), null, 1);

			return apiDocProtectorErrorRedirect.redirectGeneratorError("glass_forward_error");
		}
	}

	@Operation(hidden = true)
	@GetMapping(path = "${apidocprotector.custom.uri-generator:/doc-protect/generator}/form")
	public ModelAndView form() {

		debugger("FORM IN GENERATOR IS START", null, true);
		auditor(GENERATOR_FORM_STARTED, null, null, 0);

		if (session.getAttribute("ADP-USER-GENERATOR") == null || !session.getAttribute("ADP-USER-GENERATOR").equals("1")) {

			debugger("GENERATOR FORM [EXCEPTION]", GENERATOR_FORM_INVALID_ACCCESS.getMessage(), true);
			logger("GENERATOR FORM [EXCEPTION]: " + GENERATOR_FORM_INVALID_ACCCESS.getMessage(), "info");
			auditor(GENERATOR_FORM_INVALID_ACCCESS, null, null, 2);

			return apiDocProtectorViewer.error(
					GENERATOR_ERROR.getMessage(),
					"invalid access",
					GENERATOR_ERROR.getStatusCode());
		}

		try {

			if (session.getAttribute("ADP-ACCOUNT-CREATED-SUCCESSFUL") != null) {
				if (session.getAttribute("ADP-ACCOUNT-CREATED-SUCCESSFUL").equals("1")) {

					debugger("GENERATOR ACCOUNT", GENERATOR_ACCOUNT_CREATED.getMessage(), true);
					logger("GENERATOR ACCOUNT: " + GENERATOR_ACCOUNT_CREATED.getMessage(), "info");
					auditor(GENERATOR_ACCOUNT_CREATED, null, null, 2);

					return apiDocProtectorViewer.generator(true);
				}
			}

			auditor(GENERATOR_VIEW_FORM, null, null, 2);
			return apiDocProtectorViewer.generator(false);

		} catch (RuntimeException re) {

			debugger("FORM IN GENERATOR [EXCEPTION]", re.getMessage(), true);
			logger("FORM IN GENERATOR [EXCEPTION]: " + re.getMessage(), "except");
			auditor(GENERATOR_EXCEPTION, re.getMessage(), null, 1);

			return apiDocProtectorViewer.error(
					GENERATOR_ERROR.getMessage(),
					"The request has caused a exception",
					GENERATOR_ERROR.getStatusCode());
		}
	}

	@Operation(hidden = true)
	@PostMapping(
			path = "${apidocprotector.custom.uri-user-generator:/doc-protect/generator/user}",
			consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE
	)
	public String create(@Valid @RequestParam Map<String, String> body) throws IOException {

		debugger("CREATE IN GENERATOR IS START", null, true);
		auditor(GENERATOR_DATA_POST, null, null, 0);

		try {
			auditor(GENERIC_MESSAGE, "Try create user: "+body.get("username"), null, 0);
		} catch (RuntimeException re) {
			debugger("CREATE IN GENERATOR [EXCEPTION]", re.getMessage(), true);
			logger("CREATE IN GENERATOR[EXCEPTION]: " + re.getMessage(), "except");
			auditor(GENERATOR_EXCEPTION, re.getMessage(), null, 1);
		}

		if (session.getAttribute("ADP-USER-GENERATOR") == null || !session.getAttribute("ADP-USER-GENERATOR").equals("1")) {

			debugger("INVALID SESSION FROM CREATE IN GENERATOR", null, true);
			logger("INVALID SESSION FROM CREATE IN GENERATOR: " + GENERATOR_FORM_INVALID_SESSION.getMessage(), "info");
			auditor(GENERATOR_FORM_INVALID_SESSION, null, null, 2);

			return apiDocProtectorErrorRedirect.redirectGeneratorError("invalid_session_user_generator");
		}

		if (apiDocProtectorRepository.findByUsernameOrEmail(body.get("username"), body.get("email")) != null) {

			debugger("USER CONFLICT", GENERATOR_FORM_USER_ALREADY_EXISTS.getMessage(), true);
			logger("USER CONFLICT: " + GENERATOR_FORM_USER_ALREADY_EXISTS.getMessage(), "info");
			auditor(GENERATOR_FORM_USER_ALREADY_EXISTS, null, null, 2);

			session.setAttribute("ADP-ACCOUNT-CREATED-SUCCESSFUL", null);
			return apiDocProtectorErrorRedirect.redirectGeneratorError("user_already_exists");
		}

		try {

			String userToken = userGenerator(body);
			String emailTo = body.get("email");
			String subject = apiDocProtectorMailSender.subjectMail(body.get("username"));
			String content = apiDocProtectorMailSender.contentMailGeneratorUser(body.get("name"), userToken);

			apiDocProtectorMailSender.sendMailAttached(emailTo, subject, content);

			debugger("EMAIL SENDED", emailTo, true);
			logger(GENERATOR_MAIL_SENDER_OK.getMessage(), "info");
			auditor(GENERATOR_MAIL_SENDER_OK, null, null, 0);

			session.setAttribute("ADP-ACCOUNT-CREATED-SUCCESSFUL", "1");

			debugger("USER CREATED", body.get("username"), true);
			logger("User created successful: " + body.get("username"), "info");
			auditor(GENERIC_MESSAGE, "User created successful: "+body.get("username"), null, 0);

			return apiDocProtectorRedirect.redirectToGeneratorForm();

		} catch (RuntimeException re) {

			debugger("CREATE IN GENERATOR [EXCEPTION]", re.getMessage(), true);
			logger("CREATE IN GENERATOR [EXCEPTION]: " + re.getMessage(), "except");
			auditor(GENERATOR_EXCEPTION, re.getMessage(), null, 1);

			session.setAttribute("ADP-ACCOUNT-CREATED-SUCCESSFUL", null);
			return apiDocProtectorErrorRedirect.redirectGeneratorError("account_create_error");
		}
	}

	@Operation(hidden = true)
	@GetMapping(path = "/doc-protect/generator/error/{data}")
	public ModelAndView error(@PathVariable(required = false) String data) {

		debugger("GENERATOR EXCEPTION", data, true);
		logger("GENERATOR EXCEPTION: " + data, "error");
		auditor(GENERATOR_EXCEPTION, data, null, 1);

		return apiDocProtectorViewer.error(
				GENERATOR_ERROR.getMessage(),
				data.replace("_", " "),
				GENERATOR_ERROR.getStatusCode());
	}

}
