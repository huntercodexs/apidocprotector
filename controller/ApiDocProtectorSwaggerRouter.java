package com.apidocprotector.controller;

import com.apidocprotector.dto.ApiDocProtectorDto;
import com.apidocprotector.library.ApiDocProtectorLibrary;
import com.apidocprotector.model.ApiDocProtectorEntity;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

import static com.apidocprotector.enumerator.ApiDocProtectorLibraryEnum.*;
import static com.apidocprotector.enumerator.ApiDocProtectorRegisterEnum.*;

@Hidden
@Controller
@CrossOrigin(origins = "*")
public class ApiDocProtectorSwaggerRouter extends ApiDocProtectorLibrary {

	@Operation(hidden = true)
	@PostMapping(
			path = "${springdoc.swagger-ui.path:/swagger-ui}/protector",
			consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
			produces = MediaType.TEXT_HTML_VALUE
	)
	@ResponseBody
	public ModelAndView sign(@RequestParam Map<String, String> body) {

		register(SWAGGER_ROUTER_SIGN, null, "info", 0, "");

		if (!apiDocProtectorSecurity.shield(session)) {

			register(SWAGGER_ROUTER_STOPPED_BY_SHIELD, null, "error", 2, "Shield BLocked");

			return apiDocProtectorViewer.error(
					INVALID_ACCESS.getMessage(),
					base64Encode("session required"),
					INVALID_ACCESS.getStatusCode());
		}

		String keypart = session.getAttribute("ADP-KEYPART").toString();
		String username = body.get("username");
		String password = body.get("password");
		String secret = body.get("apidocprotector_sec");
		String token64 = body.get("apidocprotector_token");

		register(SWAGGER_ROUTER_DETAILS, null, "info", 2, "POST BODY: "+ body);

		if (username == null || username.equals("")) {

			register(SWAGGER_ROUTER_MISSING_USERNAME, null, "error", 2, "Missing username");

			return apiDocProtectorViewer.error(
					INVALID_ACCESS.getMessage(),
					base64Encode("missing username"),
					INVALID_ACCESS.getStatusCode());
		}

		if (password == null || password.equals("")) {

			register(SWAGGER_ROUTER_MISSING_PASSWORD, null, "error", 2, "Missing password");

			return apiDocProtectorViewer.error(
					INVALID_ACCESS.getMessage(),
					base64Encode("missing password"),
					INVALID_ACCESS.getStatusCode());
		}

		if (sessionExpired(token64)) {

			register(SWAGGER_ROUTER_EXPIRED_SESSION, null, "error", 2, "Expired session");

			try {
				apiDocProtectorRedirect.redirectExpiredSession(token64);
				return null;
			} catch (IOException e) {

				register(SWAGGER_ROUTER_EXCEPTION, null, "except", 2, e.getMessage());

				return apiDocProtectorViewer.error(
						EXPIRED_SESSION.getMessage(),
						base64Encode("session expired"),
						EXPIRED_SESSION.getStatusCode());
			}
		}

		String sessionKey = md5(keypart + secret).toUpperCase();

		ApiDocProtectorEntity sessionData = apiDocProtectorRepository.findBySessionKeyAndActive(sessionKey, "yes");
		String sessionId = sessionData.getSessionVal();
		ApiDocProtectorDto sessionTransfer = (ApiDocProtectorDto) session.getAttribute(sessionId);

		register(SWAGGER_ROUTER_SESSION_FOUNDED, sessionId, "info", 2, "Session Transfer: " + sessionTransfer.getUsername());

		if (loginChecker(username, password, token64)) {

			sessionTransfer.setUsername(username);
			sessionTransfer.setPassword("0x"+md5(password).replaceAll("[^0-9]", ""));
			sessionTransfer.setAuthenticate(true);

			session.setAttribute(sessionId, sessionTransfer);
			response.setHeader("ApiDoc-Protector-Active-User", md5(username));

			register(SWAGGER_ROUTER_LOGIN_OK, sessionId, "info", 2, "User: " + username);

			return apiDocProtectorViewer.protector(session, sessionId);

		}

		sessionTransfer.setAuthenticate(false);

		register(SWAGGER_ROUTER_LOGIN_ERROR, sessionId, "error", 2, "The login was invalid to " + username);

		return apiDocProtectorViewer.error(
				INVALID_ACCESS.getMessage(),
				base64Encode("login failure to "+username),
				INVALID_ACCESS.getStatusCode());

	}

	@Operation(hidden = true)
	@GetMapping(path = "${springdoc.swagger-ui.path:/swagger-ui}/protector")
	public ModelAndView refresh() {

		register(SWAGGER_ROUTER_REFRESH_STARTED, null, "info", 2, "Refresh page started");

		String keypart = session.getAttribute("ADP-KEYPART").toString();
		String secret = session.getAttribute("ADP-SECRET").toString();
		String sessionKey = md5(keypart + secret).toUpperCase();
		ApiDocProtectorEntity sessionData = apiDocProtectorRepository.findBySessionKeyAndActive(sessionKey, "yes");
		String sessionId = sessionData.getSessionVal();

		register(SWAGGER_ROUTER_REFRESH_OK, sessionId, "info", 2, "Refresh page finished");

		return apiDocProtectorViewer.refresh(session, sessionId,"--refresh-page");
	}

	@Operation(hidden = true)
	@GetMapping(path = {
			/*Swagger*/
			"/swagger",
			"/swagger/",
			"/swagger/login",
			"/swagger/sign",
			"/swagger/viewer",
			"/swagger/logout",
			"/swagger/doc-protected",
			"/swagger/index",

			/*Swagger-UI*/
			"/swagger-ui",
			"/swagger-ui/",
			"/swagger-ui/login",
			"/swagger-ui/sign",
			"/swagger-ui/viewer",
			"/swagger-ui/logout",
			"/swagger-ui/doc-protected",
			"/swagger-ui/index"
	})
	public String routes() {
		register(SWAGGER_ROUTER_ROUTES_STARTED, null, "info", 2, "");
		return apiDocProtectorRedirect.captor(session);
	}

	@Operation(hidden = true)
	@RequestMapping(value = "${springdoc.swagger-ui.path:/swagger-ui-path}/index.html")
	public String denied(HttpServletResponse httpResponse) throws IOException {

		register(SWAGGER_ROUTER_DENIED_STARTED, null, "info", 2, "");

		httpResponse.setStatus(HttpStatus.NOT_ACCEPTABLE.value());
		return apiDocProtectorErrorRedirect.redirectError(base64Encode("Operation not allowed"));
	}

}
