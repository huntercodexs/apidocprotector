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

		logTerm("INITIALIZER IS START", null, true);
		auditor(INITIALIZER_STARTED, null, null);

		String tokenCrypt = dataEncrypt(token);
		ApiDocProtectorEntity result = findAccountByTokenAndActive(tokenCrypt, "yes");

		logTerm("RESULT TOKEN", result, true);
		auditor(INITIALIZER_TOKEN_OK, tokenCrypt, null);

		if (result != null && result.getToken().equals(tokenCrypt)) {

			this.transfer = initEnv(token);
			logTerm("INIT TRANSFER", this.transfer, true);
			auditor(INITIALIZER_ENVIRONMENT_OK, null, null);

			sessionPrepare(session, this.transfer, result);
			logTerm("SESSION CONFIG", session, true);
			auditor(INITIALIZER_SESSION_PREPARE_OK, null, null);

			return apiDocProtectorRedirect.forwardToGlass();
		}

		String error = token;
		if (findAccountByTokenAndActive(tokenCrypt, "no") != null) {
			error = "user_is_not_active";
		}

		auditor(INITIALIZER_ERROR, null, null);
		return apiDocProtectorErrorRedirect.redirectInitializerError(error);
	}

	@Operation(hidden = true)
	@GetMapping(path = "/doc-protect/protector/glass")
	public String glass() {

		logTerm("ACTIVATOR GLASS START", null, true);
		auditor(INITIALIZER_GLASS_STARTED, null, null);

		try {
			return apiDocProtectorRedirect.redirectToForm();
		} catch (RuntimeException re) {
			logTerm("GLASS IN INITIALIZER [EXCEPTION]", re.getMessage(), true);
			auditor(INITIALIZER_EXCEPTION, re.getMessage(), null);
			return apiDocProtectorErrorRedirect.redirectInitializerError("sessionId");
		}
	}

	@Operation(hidden = true)
	@GetMapping(path = {
			"${apidocprotector.custom.uri-form:/doc-protect/protector/form}",
			"${apidocprotector.custom.uri-form:/doc-protect/protector/form}/{expired}"
	})
	public ModelAndView form(@PathVariable(value = "expired", required = false) String expired) {

		logTerm("TRANSFER IN FORM", this.transfer, true);
		auditor(INITIALIZER_FORM_STARTED, null, null);

		ApiDocProtectorEntity sessionData = findDataSession(this.transfer.getKeypart(), this.transfer.getSecret());
		logTerm("SESSION-DATA FROM TRANSFER IN FORM", sessionData, true);

		if (sessionData == null) {

			auditor(INITIALIZER_SESSION_NOT_FOUND, null, null);

			return apiDocProtectorViewer.error(
					INVALID_SESSION.getMessage(),
					"The request is not valid",
					INVALID_SESSION.getStatusCode());
		}

		String sessionId = sessionData.getSessionVal();
		logTerm("SESSION-ID FROM TRANSFER IN FORM", sessionId, true);
		logTerm("SESSION-CURRENT FROM TRANSFER IN FORM", session.getAttribute(sessionId), true);

		session.setAttribute("ADP-KEYPART", this.transfer.getKeypart());
		logTerm("SESSION-ADP-KEYPART FROM TRANSFER IN FORM", session.getAttribute("ADP-KEYPART"), true);

		session.setAttribute("ADP-SECRET", this.transfer.getSecret());
		logTerm("SESSION-ADP-SECRET FROM TRANSFER IN FORM", session.getAttribute("ADP-SECRET"), true);

		try {

			if (!apiDocProtectorSecurity.burn(session, sessionData.getToken(), sessionId)) {

				logTerm("NOT BURN FROM TRANSFER IN FORM", null, true);
				auditor(INITIALIZER_SUCCESSFUL, null, sessionId);

				return apiDocProtectorViewer.form(session, sessionId);
			}

			logTerm("OPS! WAS BURN FROM TRANSFER IN FORM", null, true);
			auditor(INITIALIZER_BURNED, null, sessionId);

			return apiDocProtectorViewer.error(
					BURN_ERROR.getMessage(),
					"The request is burned",
					BURN_ERROR.getStatusCode());

		} catch (RuntimeException re) {

			logTerm("FORM INITIALIZER [EXCEPTION]", re.getMessage(), true);
			auditor(INITIALIZER_EXCEPTION, re.getMessage(), null);

			return apiDocProtectorViewer.error(
					BURN_EXCEPTION.getMessage(),
					"The request has caused a exception",
					BURN_EXCEPTION.getStatusCode());
		}
	}

	@Operation(hidden = true)
	@GetMapping(path = "${apidocprotector.custom.uri-login:/doc-protect/login}")
	public String denied() {
		auditor(INITIALIZER_DENIED, null, null);
		return apiDocProtectorErrorRedirect.redirectInitializerError("Missing_Token");
	}

	@Operation(hidden = true)
	@GetMapping(path = "/doc-protect/initializer/error/{data}")
	public ModelAndView error(@PathVariable(required = false) String data) {
		auditor(INITIALIZER_EXCEPTION, data, null);
		return apiDocProtectorViewer.error(
				ApiDocProtectorLibraryEnum.INITIALIZER_ERROR.getMessage(),
				data.replace("_", " "),
				ApiDocProtectorLibraryEnum.INITIALIZER_ERROR.getStatusCode());
	}

}
