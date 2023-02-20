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
import static com.apidocprotector.enumerator.ApiDocProtectorLibraryEnum.RECOVERY_ERROR;

@Hidden
@Controller
@CrossOrigin(origins = "*")
public class ApiDocProtectorRecovery extends ApiDocProtectorLibrary {

	@Operation(hidden = true)
	@GetMapping(path = "${apidocprotector.custom.uri-recovery:/doc-protect/recovery}")
	public String recovery() {

		logTerm("RECOVERY FORM IS START", null, true);
		auditor(RECOVERY_STARTED, null, null, 0);

		try {
			return apiDocProtectorRedirect.forwardToRecoveryGlass();
		} catch (RuntimeException re) {
			logTerm("RECOVERY FORM [EXCEPTION]", re.getMessage(), true);
			auditor(RECOVERY_EXCEPTION, re.getMessage(), null, 1);
			return apiDocProtectorErrorRedirect.redirectRecoveryError("error_to_load_form_recovery");
		}

	}

	@Operation(hidden = true)
	@GetMapping(path = "/doc-protect/protector/recovery/glass")
	public String glass() {

		logTerm("RECOVERY IN GLASS START", null, true);
		auditor(RECOVERY_GLASS_STARTED, null, null, 0);

		try {
			return apiDocProtectorRedirect.redirectToRecoveryForm();
		} catch (RuntimeException re) {
			logTerm("RECOVERY IN GLASS [EXCEPTION]", re.getMessage(), true);
			auditor(RECOVERY_GLASS_EXCEPTION, re.getMessage(), null, 1);
			return apiDocProtectorErrorRedirect.redirectRecoveryError("unknown");
		}
	}

	@Operation(hidden = true)
	@GetMapping(path = "${apidocprotector.custom.uri-recovery:/doc-protect/recovery}/form")
	public ModelAndView form() {

		logTerm("FORM IN RECOVERY IS START", null, true);
		auditor(RECOVERY_FORM_STARTED, null, null, 0);

		if (session.getAttribute("ADP-USER-RECOVERY") == null || !session.getAttribute("ADP-USER-RECOVERY").equals("1")) {

			auditor(RECOVERY_FORM_INVALID_SESSION, null, null, 2);

			return apiDocProtectorViewer.error(
					RECOVERY_ERROR.getMessage(),
					"invalid access in form recovery ",
					RECOVERY_ERROR.getStatusCode());
		}

		try {

			if (session.getAttribute("ADP-ACCOUNT-RECOVERY-SUCCESSFUL") != null) {
				if (session.getAttribute("ADP-ACCOUNT-RECOVERY-SUCCESSFUL").equals("1")) {
					auditor(RECOVERY_ACCOUNT_SUCCESSFUL, null, null, 2);
					return apiDocProtectorViewer.recovery(true);
				}
			}

			auditor(RECOVERY_VIEW_FORM, null, null, 2);
			return apiDocProtectorViewer.recovery(false);

		} catch (RuntimeException re) {
			logTerm("FORM IN RECOVERY [EXCEPTION]", re.getMessage(), true);
			auditor(RECOVERY_EXCEPTION, null, null, 1);
			return apiDocProtectorViewer.error(
					RECOVERY_ERROR.getMessage(),
					"The request has caused a exception",
					RECOVERY_ERROR.getStatusCode());
		}
	}

	@Operation(hidden = true)
	@PostMapping(
			path = "${apidocprotector.custom.uri-user-recovery:/doc-protect/recovery/user}",
			consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE
	)
	public String update(@Valid @RequestParam Map<String, String> body) throws IOException {

		logTerm("RECOVERY USER IS START", null, true);
		auditor(RECOVERY_DATA_POST, null, null, 0);

		if (session.getAttribute("ADP-USER-RECOVERY") == null || !session.getAttribute("ADP-USER-RECOVERY").equals("1")) {
			logTerm("INVALID SESSION FROM RECOVERY USER", null, true);
			auditor(RECOVERY_FORM_INVALID_SESSION, null, null, 2);
			return apiDocProtectorErrorRedirect.redirectRecoveryError("invalid_session_user_recovery");
		}

		ApiDocProtectorEntity user = apiDocProtectorRepository.findByEmail(body.get("email"));

		if (user == null) {
			session.setAttribute("ADP-ACCOUNT-RECOVERY-SUCCESSFUL", null);
			auditor(PASSWORD_RECOVERY_USER_NOT_FOUND, "User not found " + body.get("email"), null, 2);
			return apiDocProtectorErrorRedirect.redirectRecoveryError("user_not_found");
		}

		try {

			String userToken = userRecovery(body, user);
			String emailTo = body.get("email");
			String subject = apiDocProtectorMailSender.subjectMail(user.getUsername());
			String content = apiDocProtectorMailSender.contentMailRecoveryUser(user.getName(), userToken);

			apiDocProtectorMailSender.sendMailAttached(emailTo, subject, content);
			session.setAttribute("ADP-ACCOUNT-RECOVERY-SUCCESSFUL", "1");
			auditor(PASSWORD_RECOVERY_MAIL_SENDER_OK, null, null, 0);

			return apiDocProtectorRedirect.redirectToRecoveryForm();

		} catch (RuntimeException re) {
			session.setAttribute("ADP-ACCOUNT-RECOVERY-SUCCESSFUL", null);
			logTerm("CREATE IN RECOVERY [EXCEPTION]", re.getMessage(), true);
			auditor(RECOVERY_EXCEPTION, re.getMessage(), null, 1);
			return apiDocProtectorErrorRedirect.redirectRecoveryError("account_recovery_error");
		}
	}

	@Operation(hidden = true)
	@GetMapping(path = "/doc-protect/recovery/error/{data}")
	public ModelAndView error(@PathVariable(required = false) String data) {
		auditor(RECOVERY_EXCEPTION, data, null, 1);
		return apiDocProtectorViewer.error(
				RECOVERY_ERROR.getMessage(),
				data.replace("_", " "),
				RECOVERY_ERROR.getStatusCode());
	}

}
