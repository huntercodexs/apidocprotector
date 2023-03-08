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

import static com.apidocprotector.enumerator.ApiDocProtectorRegisterEnum.*;

@Hidden
@Controller
@CrossOrigin(origins = "*")
public class ApiDocProtectorSentinel extends ApiDocProtectorLibrary {

	@Operation(hidden = true)
	@GetMapping(path = {"/"})
	public void allowed(ServletResponse servletResponse) throws IOException, ServletException {

		register(SENTINEL_ALLOWED_STARTED, null, "info", 0, "URI: "+request.getRequestURI());

		if (apiDocEnabled) {

			HttpServletResponse httpResponse = (HttpServletResponse) servletResponse;
			httpResponse.sendRedirect("/error");

		} else {
			String prefix = apiPrefix.replaceFirst("/$", "") + "/";
			if (!prefix.startsWith("/")) prefix = "/" + prefix;

			HttpServletResponse httpResponse = (HttpServletResponse) servletResponse;
			httpResponse.sendRedirect(prefix);
		}

		register(SENTINEL_ALLOWED_FINISHED, null, "info", 0, "");
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

		register(SENTINEL_DOCS_STARTED, null, "info", 0, "URI: " + request.getRequestURI());

		if (session.getAttribute("ADP-KEYPART") != null && !session.getAttribute("ADP-KEYPART").equals("")) {

			register(SENTINEL_DOCS_FINISHED_FORM, null, "info", 2, "");

			return apiDocProtectorRedirect.redirectToForm();
		}

		register(SENTINEL_DOCS_FINISHED_CAPTOR, null, "info", 0, "");
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
		register(SENTINEL_PROTECT_STARTED, null, "info", 0, "");
		return apiDocProtectorRedirect.captor(session);
	}

	@Operation(hidden = true)
	@GetMapping(path = "/doc-protect/router")
	public String router() {
		register(SENTINEL_ROUTER_STARTED, null, "info", 0, "");
		return apiDocProtectorRedirect.router(session);
	}

	@Operation(hidden = true)
	@GetMapping(path = "${apidocprotector.custom.uri-logout:/doc-protect/logout}/{token}")
	public String logout(@PathVariable("token") String token) {
		register(SENTINEL_LOGOUT_STARTED, null, "info", 0,"");
		return apiDocProtectorRedirect.logout(session, token);
	}

	@Operation(hidden = true)
	@GetMapping(path = "/doc-protect/protector")
	public ModelAndView protector() {
		register(SENTINEL_PROTECTOR_STARTED, null, "info", 0, "");
		return apiDocProtectorViewer.protector(session, null);
	}

}
