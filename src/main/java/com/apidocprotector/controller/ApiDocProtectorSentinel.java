package com.apidocprotector.controller;

import com.apidocprotector.library.ApiDocProtectorLibrary;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.ServletException;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.apidocprotector.enumerator.ApiDocProtectorAuditEnum.*;
import static com.apidocprotector.enumerator.ApiDocProtectorLibraryEnum.SENTINEL_ERROR;

@Hidden
@Controller
@CrossOrigin(origins = "*")
public class ApiDocProtectorSentinel extends ApiDocProtectorLibrary {

	@Operation(hidden = true)
	@GetMapping(path = {"/"})
	public void allowed(ServletResponse servletResponse) throws IOException, ServletException {

		logTerm("ALLOWED", null, true);
		logTerm("API-PREFIX", apiPrefix, true);
		logTerm("CURRENT-URI", request.getRequestURI(), true);
		logTerm("REFERER", request.getHeader("Referer"), true);
		auditor(SENTINEL_ALLOWED_STARTED, null, null, 0);

		String prefix = apiPrefix.replaceFirst("/$", "") + "/";
		if (!prefix.startsWith("/")) prefix = "/" + prefix;

		HttpServletResponse httpResponse = (HttpServletResponse) servletResponse;
		httpResponse.sendRedirect(prefix);
		auditor(SENTINEL_ALLOWED_FINISHED, null, null, 0);
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
		auditor(SENTINEL_DOCS_STARTED, null, null, 0);

		if (session.getAttribute("ADP-KEYPART") != null && !session.getAttribute("ADP-KEYPART").equals("")) {
			auditor(SENTINEL_DOCS_FINISHED_FORM, null, null, 2);
			return apiDocProtectorRedirect.redirectToForm();
		}

		auditor(SENTINEL_DOCS_FINISHED_CAPTOR, null, null, 0);
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
		auditor(SENTINEL_PROTECT_STARTED, null, null, 0);
		return apiDocProtectorRedirect.captor(session);
	}

	@Operation(hidden = true)
	@GetMapping(path = "/doc-protect/router")
	public String router() {
		logTerm("ROUTER FROM SENTINEL", null, true);
		auditor(SENTINEL_ROUTER_STARTED, null, null, 0);
		return apiDocProtectorRedirect.router(session);
	}

	@Operation(hidden = true)
	@GetMapping(path = "${apidocprotector.custom.uri-logout:/doc-protect/logout}/{token}")
	public String logout(@PathVariable("token") String token) {
		logTerm("LOGOUT FROM SENTINEL", null, true);
		auditor(SENTINEL_LOGOUT_STARTED, null, null, 0);
		return apiDocProtectorRedirect.logout(session, token);
	}

	@Operation(hidden = true)
	@GetMapping(path = "/doc-protect/protector")
	public ModelAndView protector() {
		logTerm("PROTECTOR FROM SENTINEL", null, true);
		auditor(SENTINEL_PROTECTOR_STARTED, null, null, 0);
		return apiDocProtectorViewer.protector(session, null);
	}

	@Operation(hidden = true)
	@GetMapping(path = "/doc-protect/sentinel/error/{error}")
	public ModelAndView error(@PathVariable("error") String error) {
		auditor(SENTINEL_ERROR_STARTED, null, null, 1);
		return apiDocProtectorViewer.error(
				SENTINEL_ERROR.getMessage(),
				error.replaceAll("_", " "),
				SENTINEL_ERROR.getStatusCode());
	}

}
