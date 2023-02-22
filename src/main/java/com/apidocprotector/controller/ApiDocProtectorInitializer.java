package com.apidocprotector.controller;

import com.apidocprotector.enumerator.ApiDocProtectorLibraryEnum;
import com.apidocprotector.library.ApiDocProtectorLibrary;
import com.apidocprotector.model.ApiDocProtectorEntity;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.ModelAndView;

import static com.apidocprotector.enumerator.ApiDocProtectorAuditEnum.*;
import static com.apidocprotector.enumerator.ApiDocProtectorAuditEnum.INITIALIZER_ERROR;
import static com.apidocprotector.enumerator.ApiDocProtectorLibraryEnum.*;

@Hidden
@Controller
@CrossOrigin(origins = "*")
public class ApiDocProtectorInitializer extends ApiDocProtectorLibrary {

	@Operation(hidden = true)
	@GetMapping(path = "${apidocprotector.custom.uri-login:/doc-protect/login}/{token}")
	public String initializer(@PathVariable("token") String token) {

		debugger("INITIALIZER IS START", null, true);
		auditor(INITIALIZER_STARTED, null, null, 2);

		String tokenCrypt = dataEncrypt(token);
		ApiDocProtectorEntity result = findAccountByTokenAndActive(tokenCrypt, "yes");

		debugger("RESULT TOKEN", result, true);
		logger("RESULT TOKEN: " + result, "info");
		auditor(INITIALIZER_TOKEN_OK, tokenCrypt, null, 2);

		if (result != null && result.getToken().equals(tokenCrypt)) {

			this.transfer = initEnv(token);

			debugger("INIT TRANSFER", this.transfer, true);
			logger("INIT TRANSFER: " + this.transfer, "info");
			auditor(INITIALIZER_ENVIRONMENT_OK, null, null, 2);

			sessionPrepare(session, this.transfer, result);

			debugger("SESSION CONFIGURE", session, true);
			logger("SERSSION CONFIGURE: " + session, "info");
			auditor(INITIALIZER_SESSION_PREPARE_OK, null, null, 2);

			return apiDocProtectorRedirect.forwardToGlass();
		}

		String error = token;
		if (findAccountByTokenAndActive(tokenCrypt, "no") != null) {
			error = "user_is_not_active";
		}

		debugger("INITIALIZER ERROR", INITIALIZER_ERROR.getMessage(), true);
		logger("INITIALIZER ERROR: " + INITIALIZER_ERROR.getMessage(), "info");
		auditor(INITIALIZER_ERROR, null, null, 2);

		return apiDocProtectorErrorRedirect.redirectInitializerError(error);
	}

	@Operation(hidden = true)
	@GetMapping(path = "/doc-protect/protector/glass")
	public String glass() {

		debugger("ACTIVATOR GLASS START", null, true);
		auditor(INITIALIZER_GLASS_STARTED, null, null, 2);

		try {
			return apiDocProtectorRedirect.redirectToForm();
		} catch (RuntimeException re) {

			debugger("GLASS IN INITIALIZER [EXCEPTION]", re.getMessage(), true);
			logger("GLASS IN INITIALIZER [EXCEPTION]: " + re.getMessage(), "except");
			auditor(INITIALIZER_EXCEPTION, re.getMessage(), null, 2);

			return apiDocProtectorErrorRedirect.redirectInitializerError("sessionId");
		}
	}

	@Operation(hidden = true)
	@GetMapping(path = {
			"${apidocprotector.custom.uri-form:/doc-protect/protector/form}",
			"${apidocprotector.custom.uri-form:/doc-protect/protector/form}/{expired}"
	})
	public ModelAndView form(@PathVariable(value = "expired", required = false) String expired) {

		ApiDocProtectorEntity sessionData = findDataSession(this.transfer.getKeypart(), this.transfer.getSecret());

		debugger("TRANSFER IN FORM", this.transfer, true);
		debugger("SESSION-DATA FROM TRANSFER IN FORM", sessionData, true);
		auditor(INITIALIZER_FORM_STARTED, null, null, 2);

		if (sessionData == null) {

			debugger("SESSION ERROR", INITIALIZER_SESSION_NOT_FOUND.getMessage(), true);
			logger("SESSION ERROR: " + INITIALIZER_SESSION_NOT_FOUND.getMessage(), "info");
			auditor(INITIALIZER_SESSION_NOT_FOUND, null, null, 2);

			return apiDocProtectorViewer.error(
					INVALID_SESSION.getMessage(),
					"The request is not valid",
					INVALID_SESSION.getStatusCode());
		}

		String sessionId = sessionData.getSessionVal();
		session.setAttribute("ADP-KEYPART", this.transfer.getKeypart());
		session.setAttribute("ADP-SECRET", this.transfer.getSecret());

		debugger("SESSION-ID FROM TRANSFER IN FORM", sessionId, true);
		debugger("SESSION-CURRENT FROM TRANSFER IN FORM", session.getAttribute(sessionId), true);
		debugger("SESSION-ADP-KEYPART FROM TRANSFER IN FORM", session.getAttribute("ADP-KEYPART"), true);
		debugger("SESSION-ADP-SECRET FROM TRANSFER IN FORM", session.getAttribute("ADP-SECRET"), true);

		try {

			if (!apiDocProtectorSecurity.burn(session, sessionData.getToken(), sessionId)) {

				debugger("NOT BURN FROM TRANSFER IN FORM", null, true);
				logger(INITIALIZER_SUCCESSFUL.getMessage(), "info");
				auditor(INITIALIZER_SUCCESSFUL, null, sessionId, 2);

				return apiDocProtectorViewer.form(session, sessionId);
			}

			debugger("OPS! WAS BURN FROM TRANSFER IN FORM", null, true);
			logger(INITIALIZER_BURNED.getMessage(), "info");
			auditor(INITIALIZER_BURNED, null, sessionId, 2);

			return apiDocProtectorViewer.error(
					BURN_ERROR.getMessage(),
					"The request is burned",
					BURN_ERROR.getStatusCode());

		} catch (RuntimeException re) {

			debugger("FORM INITIALIZER [EXCEPTION]", re.getMessage(), true);
			logger("FORM INITIALIZER [EXCEPTION]: " + re.getMessage(), "except");
			auditor(INITIALIZER_EXCEPTION, re.getMessage(), null, 2);

			return apiDocProtectorViewer.error(
					BURN_EXCEPTION.getMessage(),
					"The request has caused a exception",
					BURN_EXCEPTION.getStatusCode());
		}
	}

	@Operation(hidden = true)
	@GetMapping(path = "${apidocprotector.custom.uri-login:/doc-protect/login}")
	public String denied() {

		debugger("ACCESS DENIED", INITIALIZER_DENIED.getMessage(), true);
		logger(INITIALIZER_DENIED.getMessage(), "info");
		auditor(INITIALIZER_DENIED, null, null, 2);

		return apiDocProtectorErrorRedirect.redirectInitializerError("Missing_Token");
	}

	@Operation(hidden = true)
	@GetMapping(path = "/doc-protect/initializer/error/{data}")
	public ModelAndView error(@PathVariable(required = false) String data) {

		debugger(INITIALIZER_EXCEPTION.getMessage(), data, true);
		logger(INITIALIZER_EXCEPTION.getMessage(), "except");
		auditor(INITIALIZER_EXCEPTION, data, null, 2);

		return apiDocProtectorViewer.error(
				ApiDocProtectorLibraryEnum.INITIALIZER_ERROR.getMessage(),
				data.replace("_", " "),
				ApiDocProtectorLibraryEnum.INITIALIZER_ERROR.getStatusCode());
	}

}
