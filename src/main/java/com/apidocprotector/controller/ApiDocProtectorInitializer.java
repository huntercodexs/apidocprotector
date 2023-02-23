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

import static com.apidocprotector.enumerator.ApiDocProtectorLibraryEnum.*;
import static com.apidocprotector.enumerator.ApiDocProtectorRegisterEnum.INITIALIZER_ERROR;
import static com.apidocprotector.enumerator.ApiDocProtectorRegisterEnum.*;

@Hidden
@Controller
@CrossOrigin(origins = "*")
public class ApiDocProtectorInitializer extends ApiDocProtectorLibrary {

	@Operation(hidden = true)
	@GetMapping(path = "${apidocprotector.custom.uri-login:/doc-protect/login}/{token}")
	public String initializer(@PathVariable("token") String token) {

		register(INITIALIZER_STARTED, null, "info", 2, "");

		String tokenCrypt = dataEncrypt(token);
		ApiDocProtectorEntity result = findAccountByTokenAndActive(tokenCrypt, "yes");

		register(INITIALIZER_TOKEN_OK, null, "info", 2, "Result token: " + result);

		if (result != null && result.getToken().equals(tokenCrypt)) {

			this.transfer = initEnv(token);
			register(INITIALIZER_ENVIRONMENT_OK, null, "info", 2, "Init Transfer: " + this.transfer);

			sessionPrepare(session, this.transfer, result);
			register(INITIALIZER_SESSION_PREPARE_OK, null, "info", 2, "Session configured: " + session);

			return apiDocProtectorRedirect.forwardToGlass();
		}

		String error = token;
		if (findAccountByTokenAndActive(tokenCrypt, "no") != null) {
			error = "user_is_not_active";
		}

		register(INITIALIZER_ERROR, null, "info", 2, "Initializer Error");

		return apiDocProtectorErrorRedirect.redirectInitializerError(error);
	}

	@Operation(hidden = true)
	@GetMapping(path = "/doc-protect/protector/glass")
	public String glass() {

		register(INITIALIZER_GLASS_STARTED, null, "info", 2, "");

		try {
			return apiDocProtectorRedirect.redirectToForm();
		} catch (RuntimeException re) {

			register(INITIALIZER_EXCEPTION, null, "except", 2, "Initializer glass: " + re.getMessage());

			return apiDocProtectorErrorRedirect.redirectInitializerError("sessionId");
		}
	}

	@Operation(hidden = true)
	@GetMapping(path = {
			"${apidocprotector.custom.uri-form:/doc-protect/protector/form}",
			"${apidocprotector.custom.uri-form:/doc-protect/protector/form}/{expired}"
	})
	public ModelAndView form(@PathVariable(value = "expired", required = false) String expired) {

		register(INITIALIZER_FORM_STARTED, null, "info", 2, "");

		ApiDocProtectorEntity sessionData = findDataSession(this.transfer.getKeypart(), this.transfer.getSecret());

		register(NO_AUDITOR, null, "info", 2, "Session Data: " + sessionData);

		if (sessionData == null) {

			register(INITIALIZER_SESSION_NOT_FOUND, null, "info", 2, "Session Error");

			return apiDocProtectorViewer.error(
					INVALID_SESSION.getMessage(),
					"The request is not valid",
					INVALID_SESSION.getStatusCode());
		}

		String sessionId = sessionData.getSessionVal();
		session.setAttribute("ADP-KEYPART", this.transfer.getKeypart());
		session.setAttribute("ADP-SECRET", this.transfer.getSecret());

		register(NO_AUDITOR, sessionId, "info", 2, "");
		register(NO_AUDITOR, sessionId, "info", 2, session.getAttribute(sessionId).toString());

		try {

			if (!apiDocProtectorSecurity.burn(session, sessionData.getToken(), sessionId)) {
				register(INITIALIZER_SUCCESSFUL, sessionId, "info", 2, "Not Burn");
				return apiDocProtectorViewer.form(session, sessionId);
			}

			register(INITIALIZER_BURNED, sessionId, "info", 2, "Ops! Was Burn");

			return apiDocProtectorViewer.error(
					BURN_ERROR.getMessage(),
					"The request is burned",
					BURN_ERROR.getStatusCode());

		} catch (RuntimeException re) {

			register(INITIALIZER_EXCEPTION, sessionId, "except", 2, re.getMessage());

			return apiDocProtectorViewer.error(
					BURN_EXCEPTION.getMessage(),
					"The request has caused a exception",
					BURN_EXCEPTION.getStatusCode());
		}
	}

	@Operation(hidden = true)
	@GetMapping(path = "${apidocprotector.custom.uri-login:/doc-protect/login}")
	public String denied() {
		register(INITIALIZER_DENIED, null, "info", 2, "Access Denied");
		return apiDocProtectorErrorRedirect.redirectInitializerError("Missing_Token");
	}

	@Operation(hidden = true)
	@GetMapping(path = "/doc-protect/initializer/error/{data}")
	public ModelAndView error(@PathVariable(required = false) String data) {

		register(INITIALIZER_EXCEPTION, null, "error", 2, "Error: "+data);

		return apiDocProtectorViewer.error(
				ApiDocProtectorLibraryEnum.INITIALIZER_ERROR.getMessage(),
				data.replace("_", " "),
				ApiDocProtectorLibraryEnum.INITIALIZER_ERROR.getStatusCode());
	}

}
