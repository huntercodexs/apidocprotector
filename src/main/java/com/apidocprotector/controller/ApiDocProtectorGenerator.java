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

import static com.apidocprotector.enumerator.ApiDocProtectorLibraryEnum.GENERATOR_ERROR;
import static com.apidocprotector.enumerator.ApiDocProtectorRegisterEnum.*;

@Hidden
@Controller
@CrossOrigin(origins = "*")
public class ApiDocProtectorGenerator extends ApiDocProtectorLibrary {

	@Operation(hidden = true)
	@GetMapping(path = "${apidocprotector.custom.uri-generator:/doc-protect/generator}")
	public String generator() {

		register(GENERATOR_STARTED, null, "info", 0, "");

		try {
			return apiDocProtectorRedirect.forwardToGeneratorGlass();
		} catch (RuntimeException re) {
			register(GENERATOR_EXCEPTION, null, "except", 1, "Generator Exception: "+re.getMessage());
			return apiDocProtectorErrorRedirect.redirectGeneratorError(base64Encode(re.getMessage()));
		}

	}

	@Operation(hidden = true)
	@GetMapping(path = "/doc-protect/protector/generator/glass")
	public String glass() {

		register(GENERATOR_GLASS_STARTED, null, "info", 0, "");

		try {
			return apiDocProtectorRedirect.redirectToGeneratorForm();
		} catch (RuntimeException re) {
			register(GENERATOR_GLASS_EXCEPTION, null, "except", 1, "Generator Glass Exception: " + re.getMessage());
			return apiDocProtectorErrorRedirect.redirectGeneratorError(base64Encode(re.getMessage()));
		}
	}

	@Operation(hidden = true)
	@GetMapping(path = "${apidocprotector.custom.uri-generator:/doc-protect/generator}/form")
	public ModelAndView form() {

		register(GENERATOR_FORM_STARTED, null, "info", 0, "");

		if (session.getAttribute("ADP-USER-GENERATOR") == null || !session.getAttribute("ADP-USER-GENERATOR").equals("1")) {

			register(GENERATOR_FORM_INVALID_ACCCESS, null, "warn", 2, "Generator Form Exception");

			return apiDocProtectorViewer.error(
					GENERATOR_ERROR.getMessage(),
					base64Encode("invalid access"),
					GENERATOR_ERROR.getStatusCode());
		}

		try {

			if (session.getAttribute("ADP-ACCOUNT-CREATED-SUCCESSFUL") != null) {
				if (session.getAttribute("ADP-ACCOUNT-CREATED-SUCCESSFUL").equals("1")) {
					register(GENERATOR_ACCOUNT_CREATED, null, "info", 2, "");
					return apiDocProtectorViewer.generator(true);
				}
			}

			register(GENERATOR_VIEW_FORM, null, "info", 2, "");
			return apiDocProtectorViewer.generator(false);

		} catch (RuntimeException re) {

			register(GENERATOR_EXCEPTION, null, "except", 1, "Form Exception: " + re.getMessage());

			return apiDocProtectorViewer.error(
					GENERATOR_ERROR.getMessage(),
					base64Encode("The request has caused a exception " + re.getMessage()),
					GENERATOR_ERROR.getStatusCode());
		}
	}

	@Operation(hidden = true)
	@PostMapping(
			path = "${apidocprotector.custom.uri-user-generator:/doc-protect/generator/user}",
			consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE
	)
	public String create(@Valid @RequestParam Map<String, String> body) throws IOException {

		register(GENERATOR_DATA_POST, null, "info", 0, "");

		try {
			register(GENERIC_MESSAGE, null, "info", 0, "Try create user: "+body.get("username"));
		} catch (RuntimeException re) {
			register(GENERATOR_EXCEPTION, null, "except", 1, "Create Exception: " + re.getMessage());
		}

		if (session.getAttribute("ADP-USER-GENERATOR") == null || !session.getAttribute("ADP-USER-GENERATOR").equals("1")) {
			register(GENERATOR_FORM_INVALID_SESSION, null, "warn", 2, "Invalid Session");
			return apiDocProtectorErrorRedirect.redirectGeneratorError(base64Encode("invalid_session_user_generator"));
		}

		if (apiDocProtectorRepository.findByUsernameOrEmail(body.get("username"), body.get("email")) != null) {
			register(GENERATOR_FORM_USER_ALREADY_EXISTS, null, "info", 2, "User Conflict: " + body.get("username"));
			session.setAttribute("ADP-ACCOUNT-CREATED-SUCCESSFUL", null);
			return apiDocProtectorErrorRedirect.redirectGeneratorError(base64Encode("user_already_exists"));
		}

		try {

			String userToken = userGenerator(body);
			String emailTo = body.get("email");
			String subject = apiDocProtectorMailSender.subjectMail(body.get("username"));
			String content = apiDocProtectorMailSender.contentMailGeneratorUser(body.get("name"), userToken);

			apiDocProtectorMailSender.sendMailAttached(emailTo, subject, content);
			register(GENERATOR_MAIL_SENDER_OK, null, "info", 0, "mail to" + emailTo);
			register(GENERIC_MESSAGE, null, "info", 0, "User created successful: "+body.get("username"));

			session.setAttribute("ADP-ACCOUNT-CREATED-SUCCESSFUL", "1");
			return apiDocProtectorRedirect.redirectToGeneratorForm();

		} catch (RuntimeException re) {

			register(GENERATOR_EXCEPTION, null, "except", 1, "Create in Generator: " + re.getMessage());

			session.setAttribute("ADP-ACCOUNT-CREATED-SUCCESSFUL", null);
			return apiDocProtectorErrorRedirect.redirectGeneratorError(base64Encode(re.getMessage()));
		}
	}

	@Operation(hidden = true)
	@GetMapping(path = "/doc-protect/generator/error/{data}")
	public ModelAndView error(@PathVariable(required = false) String data) {

		register(GENERATOR_EXCEPTION, null, "error", 1, data);

		return apiDocProtectorViewer.error(
				GENERATOR_ERROR.getMessage(),
				data,
				GENERATOR_ERROR.getStatusCode());
	}

}
