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
public class ApiDocProtectorRecovery extends ApiDocProtectorLibrary {

	@Operation(hidden = true)
	@GetMapping(path = "${apidocprotector.custom.uri-recovery:/doc-protect/recovery}")
	public String recovery() {

		logTerm("RECOVERY FORM IS START", null, true);

		try {
			return apiDocProtectorRedirect.forwardToRecoveryGlass();
		} catch (RuntimeException re) {
			logTerm("RECOVERY FORM [EXCEPTION]", re.getMessage(), true);
		}

		return apiDocProtectorErrorRedirect.redirectRecoveryError("error_to_load_form_recovery");
	}

	@Operation(hidden = true)
	@GetMapping(path = "/doc-protect/protector/recovery/glass")
	public String glass() {

		logTerm("RECOVERY IN GLASS START", null, true);

		try {
			return apiDocProtectorRedirect.redirectToRecoveryForm();
		} catch (RuntimeException re) {
			logTerm("RECOVERY IN GLASS [EXCEPTION]", re.getMessage(), true);
		}
		return apiDocProtectorErrorRedirect.redirectRecoveryError("unknown");
	}

	@Operation(hidden = true)
	@GetMapping(path = "${apidocprotector.custom.uri-recovery:/doc-protect/recovery}/form")
	public ModelAndView form() {

		logTerm("FORM IN RECOVERY IS START", null, true);

		if (session.getAttribute("ADP-USER-RECOVERY") == null || !session.getAttribute("ADP-USER-RECOVERY").equals("1")) {
			return apiDocProtectorViewer.error(
					GENERATOR_ERROR.getMessage(),
					"invalid access in form recovery ",
					GENERATOR_ERROR.getStatusCode());
		}

		try {
			if (session.getAttribute("ADP-ACCOUNT-RECOVERY-SUCCESSFUL") != null) {
				if (session.getAttribute("ADP-ACCOUNT-RECOVERY-SUCCESSFUL").equals("1")) {
					return apiDocProtectorViewer.recovery(true);
				}
			}
			return apiDocProtectorViewer.recovery(false);
		} catch (RuntimeException re) {
			logTerm("FORM IN RECOVERY [EXCEPTION]", re.getMessage(), true);
			return apiDocProtectorViewer.error(
					GENERATOR_ERROR.getMessage(),
					"The request has caused a exception",
					GENERATOR_ERROR.getStatusCode());
		}
	}

	@Operation(hidden = true)
	@PostMapping(
			path = "${apidocprotector.custom.uri-user-recovery:/doc-protect/recovery/user}",
			consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE
	)
	public String update(@Valid @RequestParam Map<String, String> body) throws IOException {

		logTerm("RECOVERY USER IS START", null, true);

		if (session.getAttribute("ADP-USER-RECOVERY") == null || !session.getAttribute("ADP-USER-RECOVERY").equals("1")) {
			logTerm("INVALID SESSION FROM RECOVERY USER", null, true);
			return apiDocProtectorErrorRedirect.redirectRecoveryError("invalid_session_user_recovery");
		}

		ApiDocProtectorEntity user = apiDocProtectorRepository.findByEmail(body.get("email"));

		if (user == null) {
			session.setAttribute("ADP-ACCOUNT-RECOVERY-SUCCESSFUL", null);
			return apiDocProtectorErrorRedirect.redirectRecoveryError("user_not_found");
		}

		try {

			String userToken = userRecovery(body, user);
			String emailTo = body.get("email");
			String subject = apiDocProtectorMailSender.subjectMail(user.getUsername());
			String content = apiDocProtectorMailSender.contentMailRecoveryUser(user.getName(), userToken);

			apiDocProtectorMailSender.sendMailAttached(emailTo, subject, content);

			session.setAttribute("ADP-ACCOUNT-RECOVERY-SUCCESSFUL", "1");
			return apiDocProtectorRedirect.redirectToRecoveryForm();

		} catch (RuntimeException re) {
			session.setAttribute("ADP-ACCOUNT-RECOVERY-SUCCESSFUL", null);
			logTerm("CREATE IN RECOVERY [EXCEPTION]", re.getMessage(), true);
			return apiDocProtectorErrorRedirect.redirectRecoveryError("account_recovery_error");
		}
	}

	@Operation(hidden = true)
	@GetMapping(path = "/doc-protect/recovery/error/{data}")
	public ModelAndView error(@PathVariable(required = false) String data) {
		return apiDocProtectorViewer.error(
				GENERATOR_ERROR.getMessage(),
				data.replace("_", " "),
				GENERATOR_ERROR.getStatusCode());
	}

}
