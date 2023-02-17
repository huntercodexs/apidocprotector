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

		logTerm("GENERATOR FORM IS START", null, true);
		auditor(GENERATOR_STARTED, null, null);

		try {
			return apiDocProtectorRedirect.forwardToGeneratorGlass();
		} catch (RuntimeException re) {
			logTerm("GENERATOR FORM [EXCEPTION]", re.getMessage(), true);
			auditor(GENERATOR_EXCEPTION, re.getMessage(), null);
			return apiDocProtectorErrorRedirect.redirectGeneratorError("error_to_load_form");
		}

	}

	@Operation(hidden = true)
	@GetMapping(path = "/doc-protect/protector/generator/glass")
	public String glass() {

		logTerm("GENERATOR IN GLASS START", null, true);
		auditor(GENERATOR_GLASS_STARTED, null, null);

		try {
			return apiDocProtectorRedirect.redirectToGeneratorForm();
		} catch (RuntimeException re) {
			logTerm("GENERATOR IN GLASS [EXCEPTION]", re.getMessage(), true);
			auditor(GENERATOR_GLASS_EXCEPTION, re.getMessage(), null);
			return apiDocProtectorErrorRedirect.redirectGeneratorError("glass_forward_error");
		}
	}

	@Operation(hidden = true)
	@GetMapping(path = "${apidocprotector.custom.uri-generator:/doc-protect/generator}/form")
	public ModelAndView form() {

		logTerm("FORM IN GENERATOR IS START", null, true);
		auditor(GENERATOR_FORM_STARTED, null, null);

		if (session.getAttribute("ADP-USER-GENERATOR") == null || !session.getAttribute("ADP-USER-GENERATOR").equals("1")) {

			auditor(GENERATOR_FORM_INVALID_ACCCESS, null, null);

			return apiDocProtectorViewer.error(
					GENERATOR_ERROR.getMessage(),
					"invalid access",
					GENERATOR_ERROR.getStatusCode());
		}

		try {

			if (session.getAttribute("ADP-ACCOUNT-CREATED-SUCCESSFUL") != null) {
				if (session.getAttribute("ADP-ACCOUNT-CREATED-SUCCESSFUL").equals("1")) {
					auditor(GENERATOR_ACCOUNT_CREATED, null, null);
					return apiDocProtectorViewer.generator(true);
				}
			}

			auditor(GENERATOR_VIEW_FORM, null, null);
			return apiDocProtectorViewer.generator(false);

		} catch (RuntimeException re) {
			logTerm("FORM IN GENERATOR [EXCEPTION]", re.getMessage(), true);
			auditor(GENERATOR_EXCEPTION, re.getMessage(), null);
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

		logTerm("CREATE IN GENERATOR IS START", null, true);
		auditor(GENERATOR_DATA_POST, null, null);

		if (session.getAttribute("ADP-USER-GENERATOR") == null || !session.getAttribute("ADP-USER-GENERATOR").equals("1")) {
			logTerm("INVALID SESSION FROM CREATE IN GENERATOR", null, true);
			auditor(GENERATOR_FORM_INVALID_SESSION, null, null);
			return apiDocProtectorErrorRedirect.redirectGeneratorError("invalid_session_user_generator");
		}

		if (apiDocProtectorRepository.findByUsernameOrEmail(body.get("username"), body.get("email")) != null) {
			session.setAttribute("ADP-ACCOUNT-CREATED-SUCCESSFUL", null);
			auditor(GENERATOR_FORM_USER_ALREADY_EXISTS, null, null);
			return apiDocProtectorErrorRedirect.redirectGeneratorError("user_already_exists");
		}

		try {

			String userToken = userGenerator(body);
			String emailTo = body.get("email");
			String subject = apiDocProtectorMailSender.subjectMail(body.get("username"));
			String content = apiDocProtectorMailSender.contentMailGeneratorUser(body.get("name"), userToken);

			apiDocProtectorMailSender.sendMailAttached(emailTo, subject, content);
			auditor(GENERATOR_MAIL_SENDER_OK, null, null);

			session.setAttribute("ADP-ACCOUNT-CREATED-SUCCESSFUL", "1");
			return apiDocProtectorRedirect.redirectToGeneratorForm();

		} catch (RuntimeException re) {
			session.setAttribute("ADP-ACCOUNT-CREATED-SUCCESSFUL", null);
			logTerm("CREATE IN GENERATOR [EXCEPTION]", re.getMessage(), true);
			auditor(GENERATOR_EXCEPTION, re.getMessage(), null);
			return apiDocProtectorErrorRedirect.redirectGeneratorError("account_create_error");
		}
	}

	@Operation(hidden = true)
	@GetMapping(path = "/doc-protect/generator/error/{data}")
	public ModelAndView error(@PathVariable(required = false) String data) {
		auditor(GENERATOR_EXCEPTION, data, null);
		return apiDocProtectorViewer.error(
				GENERATOR_ERROR.getMessage(),
				data.replace("_", " "),
				GENERATOR_ERROR.getStatusCode());
	}

}