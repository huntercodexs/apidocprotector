package com.huntercodexs.sample.apidocprotector.controller;

import com.huntercodexs.sample.apidocprotector.dto.ApiDocProtectorDto;
import com.huntercodexs.sample.apidocprotector.library.ApiDocProtectorLibrary;
import com.huntercodexs.sample.apidocprotector.model.ApiDocProtectorEntity;
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

import static com.huntercodexs.sample.apidocprotector.library.ApiDocProtectorErrorLibrary.*;

@Hidden
@Controller
@CrossOrigin(origins = "*")
public class ApiDocProtectorSwaggerRouter extends ApiDocProtectorLibrary {

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
		logTerm("ROUTES FROM SWAGGER", null, true);
		return apiDocProtectorRedirect.captor(session);
	}

	@Operation(hidden = true)
	@GetMapping(path = "${springdoc.swagger-ui.path:/swagger-ui}/protector")
	public ModelAndView refresh() {
		logTerm("REFRESH FROM MODEL-AND-VIEW", null, true);
		logTerm("ADP-KEYPART SESSION", session.getAttribute("ADP-KEYPART"), true);
		logTerm("ADP-SECRET SESSION", session.getAttribute("ADP-SECRET"), true);
		logTerm("ADP-KEYPART-REFRESH SESSION", session.getAttribute("ADP-KEYPART-REFRESH"), true);

		String keypart = session.getAttribute("ADP-KEYPART").toString();
		logTerm("KEYPART IN REFRESH", keypart, true);

		String secret = session.getAttribute("ADP-SECRET").toString();
		logTerm("SECRET IN REFRESH", secret, true);

		String sessionKey = md5(keypart + secret).toUpperCase();
		logTerm("SESSION-KEY IN REFRESH", sessionKey, true);

		ApiDocProtectorEntity sessionData = apiDocProtectorRepository.findBySessionKeyAndActive(sessionKey, "yes");
		logTerm("SESSION-DATA IN REFRESH", sessionData, true);

		String sessionId = sessionData.getSessionVal();
		logTerm("SESSION-ID IN REFRESH", sessionId, true);

		return apiDocProtectorViewer.refresh(session, sessionId,"--refresh-page");
	}

	@Operation(hidden = true)
	@GetMapping(path = "/doc-protect/login/error/{username}")
	public ModelAndView error(@PathVariable(required = false) String username) {
		return apiDocProtectorViewer.error(
				INVALID_LOGIN.getMessage(),
				username,
				INVALID_LOGIN.getStatusCode());
	}

	@Operation(hidden = true)
	@RequestMapping(value = "${springdoc.swagger-ui.path:/swagger-ui-path}/index.html")
	public String denied(HttpServletResponse httpResponse) throws IOException {
		httpResponse.setStatus(HttpStatus.NOT_ACCEPTABLE.value());
		return apiDocProtectorErrorRedirect.forwardSentinelError("Operation not allowed");
	}

	@Operation(hidden = true)
	@PostMapping(
			path = "${springdoc.swagger-ui.path:/swagger-ui}/protector",
			consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
			produces = MediaType.TEXT_HTML_VALUE
	)
	@ResponseBody
	public ModelAndView sign(@RequestParam Map<String, String> body) {

		if (!apiDocProtectorSecurity.shield(session)) {
			logTerm("OPS! SIGN ERROR FROM POST (MISSING ADP-KEYPART SESSION)", "ERROR", true);
			return apiDocProtectorViewer.error(
					INVALID_ACCESS.getMessage(),
					"session is needed",
					INVALID_ACCESS.getStatusCode());
		}

		String keypart = session.getAttribute("ADP-KEYPART").toString();
		logTerm("KEYPART FROM POST IN SESSION", session.getAttribute("ADP-KEYPART"), true);

		String username = body.get("username");
		String password = body.get("password");
		String secret = body.get("apidocprotector_sec");
		String token = body.get("apidocprotector_token");
		logTerm("POST BODY", body, true);

		if (username == null || username.equals("")) {
			return apiDocProtectorViewer.error(
					INVALID_ACCESS.getMessage(),
					"missing username",
					INVALID_ACCESS.getStatusCode());
		}

		if (password == null || password.equals("")) {
			return apiDocProtectorViewer.error(
					INVALID_ACCESS.getMessage(),
					"missing password",
					INVALID_ACCESS.getStatusCode());
		}

		if (sessionExpired(token)) {
			try {
				apiDocProtectorRedirect.redirectExpiredSession(token);
				return null;
			} catch (IOException e) {
				logTerm("EXCEPTION", e.getMessage(), true);
			}
			return apiDocProtectorViewer.error(
					EXPIRED_SESSION.getMessage(),
					"session expired",
					EXPIRED_SESSION.getStatusCode());
		}

		String sessionKey = md5(keypart + secret).toUpperCase();
		logTerm("SESSION-KEY FROM POST", sessionKey, true);

		ApiDocProtectorEntity sessionData = apiDocProtectorRepository.findBySessionKeyAndActive(sessionKey, "yes");
		String sessionId = sessionData.getSessionVal();
		logTerm("SESSION-ID FROM POST", sessionId, true);

		logTerm("SESSION-CURRENT FROM POST", session.getAttribute(sessionId), true);

		ApiDocProtectorDto sessionTransfer = (ApiDocProtectorDto) session.getAttribute(sessionId);
		logTerm("SESSION-TRANSFER FROM POST", sessionTransfer, true);

		if (loginChecker(username, password, token)) {

			sessionTransfer.setUsername(username);
			sessionTransfer.setPassword("0x"+md5(password).replaceAll("[^0-9]", ""));
			sessionTransfer.setAuthenticate(true);

			session.setAttribute(sessionId, sessionTransfer);
			logTerm("SESSION-UPDATED FROM POST", session.getAttribute(sessionId), true);

			response.setHeader("ApiDoc-Protector-Active-User", md5(username));
			return apiDocProtectorViewer.protector(session, sessionId);
		}

		sessionTransfer.setAuthenticate(false);
		logTerm("OPS! SIGN ERROR FROM POST", "ERROR", true);

		return apiDocProtectorViewer.error(
				INVALID_ACCESS.getMessage(),
				"login failure to "+username,
				INVALID_ACCESS.getStatusCode());

	}
}
