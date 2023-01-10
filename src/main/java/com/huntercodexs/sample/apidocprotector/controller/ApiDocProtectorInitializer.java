package com.huntercodexs.sample.apidocprotector.controller;

import com.huntercodexs.sample.apidocprotector.library.ApiDocProtectorLibrary;
import com.huntercodexs.sample.apidocprotector.model.ApiDocProtectorEntity;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.ModelAndView;

import static com.huntercodexs.sample.apidocprotector.library.ApiDocProtectorErrorLibrary.*;

@Hidden
@Controller
@CrossOrigin(origins = "*")
public class ApiDocProtectorInitializer extends ApiDocProtectorLibrary {

	@Operation(hidden = true)
	@GetMapping(path = "${apidocprotector.custom.uri-login:/doc-protect/login}/{token}")
	public String initializer(@PathVariable("token") String token) {

		String tokenCrypt = dataEncrypt(token);
		ApiDocProtectorEntity result = findAccountByTokenAndActive(tokenCrypt, "yes");
		logTerm("RESULT TOKEN", result, true);

		if (result != null && result.getToken().equals(tokenCrypt)) {

			this.transfer = initEnv(token);
			logTerm("INIT TRANSFER", this.transfer, true);

			sessionPrepare(session, this.transfer, result);
			logTerm("SESSION CONFIG", session, true);

			return apiDocProtectorRedirect.forwardToGlass();
		}

		String error = token;
		if (findAccountByTokenAndActive(tokenCrypt, "no") != null) {
			error = "user_is_not_active";
		}
		return apiDocProtectorErrorRedirect.redirectInitializerError(error);
	}

	@Operation(hidden = true)
	@GetMapping(path = "/doc-protect/protector/glass")
	public String glass() {
		try {
			return apiDocProtectorRedirect.redirectToForm();
		} catch (RuntimeException re) {
			logTerm("GLASS IN INITIALIZER [EXCEPTION]", re.getMessage(), true);
		}
		return apiDocProtectorErrorRedirect.redirectInitializerError("sessionId");
	}

	@Operation(hidden = true)
	@GetMapping(path = {
			"${apidocprotector.custom.uri-form:/doc-protect/protector/form}",
			"${apidocprotector.custom.uri-form:/doc-protect/protector/form}/{expired}"
	})
	public ModelAndView form(@PathVariable(value = "expired", required = false) String expired) {

		logTerm("TRANSFER IN FORM", this.transfer, true);

		ApiDocProtectorEntity sessionData = findDataSession(this.transfer.getKeypart(), this.transfer.getSecret());
		logTerm("SESSION-DATA FROM TRANSFER IN FORM", sessionData, true);
		/*TODO: Check if DATA SESSION is ok*/

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

				return apiDocProtectorViewer.form(session, sessionId);
			}

			logTerm("OPS! WAS BURN FROM TRANSFER IN FORM", null, true);

			return apiDocProtectorViewer.error(
					BURN_ERROR.getMessage(),
					"The request is burned",
					BURN_ERROR.getStatusCode());

		} catch (RuntimeException re) {
			logTerm("FORM INITIALIZER [EXCEPTION]", re.getMessage(), true);
			return apiDocProtectorViewer.error(
					BURN_EXCEPTION.getMessage(),
					"The request has caused a exception",
					BURN_EXCEPTION.getStatusCode());
		}
	}

	@Operation(hidden = true)
	@GetMapping(path = "${apidocprotector.custom.uri-login:/doc-protect/login}")
	public String denied() {
		return apiDocProtectorErrorRedirect.redirectInitializerError("Missing_Token");
	}

	@Operation(hidden = true)
	@GetMapping(path = "/doc-protect/initializer/error/{data}")
	public ModelAndView error(@PathVariable(required = false) String data) {
		return apiDocProtectorViewer.error(
				INITIALIZE_ERROR.getMessage(),
				data.replace("_", " "),
				INITIALIZE_ERROR.getStatusCode());
	}

}
