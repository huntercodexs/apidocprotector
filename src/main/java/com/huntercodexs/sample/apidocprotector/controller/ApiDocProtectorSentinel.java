package com.huntercodexs.sample.apidocprotector.controller;

import com.huntercodexs.sample.apidocprotector.dto.ApiDocProtectorUserGeneratorRequestDto;
import com.huntercodexs.sample.apidocprotector.library.ApiDocProtectorLibrary;
import com.huntercodexs.sample.apidocprotector.model.ApiDocProtectorEntity;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.ServletException;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;

import static com.huntercodexs.sample.apidocprotector.library.ApiDocProtectorErrorLibrary.SENTINEL_ERROR;

@Hidden
@Controller
@CrossOrigin(origins = "*")
public class ApiDocProtectorSentinel extends ApiDocProtectorLibrary {

	@Operation(hidden = true)
	@GetMapping(path = "${apidocprotector.custom.server-uri-account-active:/doc-protect/account/active}/{token}")
	@ResponseBody
	public String activator(@PathVariable("token") String token) {

		String tokenCrypt = dataEncrypt(token);
		ApiDocProtectorEntity result = findUserForm(tokenCrypt, "no");
		logTerm("RESULT TOKEN", result, true);

		if (result != null && result.getToken().equals(tokenCrypt)) {
			result.setActive("yes");
			apiDocProtectorRepository.save(result);
			response.setStatus(HttpStatus.OK.value());

			/*Activated (HTML)*/
			String dataHtml = readFile("./src/main/resources/templates/apidocprotector/activated.html");

			String emailTo = result.getEmail();
			String subject = apiDocProtectorMailSender.subjectMail(result.getUsername());
			String content = apiDocProtectorMailSender.contentMailActivatedUser(result.getName(), token);

			apiDocProtectorMailSender.sendMailAttached(emailTo, subject, content);

			logTerm("USER TOKEN IS ACTIVATED", result, true);

			return dataHtml.replace("@{apidoc_protector_username}", result.getName());

		}
		response.setStatus(HttpStatus.NOT_FOUND.value());
		return apiDocProtectorErrorRedirect.initializerError("user_not_found");
	}

	@Operation(hidden = true)
	@GetMapping(path = {"/"})
	public void allowed(ServletResponse servletResponse) throws IOException, ServletException {

		logTerm("ALLOWED", null, true);
		logTerm("API-PREFIX", apiPrefix, true);
		logTerm("CURRENT-URI", request.getRequestURI(), true);
		logTerm("REFERER", request.getHeader("Referer"), true);

		String prefix = apiPrefix.replaceFirst("/$", "") + "/";
		if (!prefix.startsWith("/")) prefix = "/" + prefix;

		HttpServletResponse httpResponse = (HttpServletResponse) servletResponse;
		httpResponse.sendRedirect(prefix);
	}

	@Operation(hidden = true)
	@GetMapping(path = {
			"${springdoc.api-docs.path:/api/docs}",
			"${springdoc.api-docs.path:/api/docs}/swagger-config",
			"/api-docs",
			"/api-docs/swagger-config",
			"/api-doc-guard",
			"/api-doc-guard/swagger-config",
			"/api-docs-guard",
			"/api-docs-guard/swagger-config"
	})
	public String docs() {
		logTerm("API-DOCS FROM SENTINEL", null, true);
		logTerm("SESSION-ADP-KEYPART IN API-DOCS FROM SENTINEL", session.getAttribute("ADP-KEYPART"), true);
		logTerm("URI", request.getRequestURI(), true);
		logTerm("REFERER", request.getHeader("Referer"), true);

		if (session.getAttribute("ADP-KEYPART") != null && !session.getAttribute("ADP-KEYPART").equals("")) {
			return apiDocProtectorRedirect.redirectToForm();
		}

		return apiDocProtectorRedirect.captor(session);
	}

	@Operation(hidden = true)
	@GetMapping(path = {
			"/doc-protect",
			"/doc-protect/",
			"/doc-protect/sign",
			"/doc-protect/viewer",
			"/doc-protect/doc-protected",
			"/doc-protect/index.html",
			"${apidocprotector.custom.uri-user-generator:/doc-protect/generator/user}", /*HTTP METHOD GET IS NOT ACCEPTED*/
			/*Swagger Routes*/
			"/doc-protect/swagger",
			"/doc-protect/swagger/index.html",
			"/doc-protect/swagger-ui",
			"/doc-protect/swagger-ui/index.html"
	})
	public String protect() {
		logTerm("SESSION-ADP-KEYPART IN API-DOCS FROM SENTINEL", session.getAttribute("ADP-KEYPART"), true);
		return apiDocProtectorRedirect.captor(session);
	}

	@Operation(hidden = true)
	@GetMapping(path = "/doc-protect/router")
	public String router() {
		logTerm("ROUTER FROM SENTINEL", null, true);
		return apiDocProtectorRedirect.router(session);
	}

	@Operation(hidden = true)
	@GetMapping(path = "${apidocprotector.custom.uri-logout:/doc-protect/logout}/{token}")
	public String logout(@PathVariable("token") String token) {
		logTerm("LOGOUT FROM SENTINEL", null, true);
		return apiDocProtectorRedirect.logout(session, token);
	}

	@Operation(hidden = true)
	@GetMapping(path = "/doc-protect/protector")
	public ModelAndView protector() {
		logTerm("PROTECTOR FROM SENTINEL", null, true);
		return apiDocProtectorViewer.protector(session, null);
	}

	@Operation(hidden = true)
	@GetMapping(path = "/doc-protect/sentinel/error/{error}")
	public ModelAndView error(@PathVariable("error") String error) {
		return apiDocProtectorViewer.error(
				SENTINEL_ERROR.getMessage(),
				error.replaceAll("_", " "),
				SENTINEL_ERROR.getStatusCode());
	}

}
