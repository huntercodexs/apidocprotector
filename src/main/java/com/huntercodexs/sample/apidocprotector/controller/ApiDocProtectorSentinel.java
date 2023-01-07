package com.huntercodexs.sample.apidocprotector.controller;

import com.huntercodexs.sample.apidocprotector.library.ApiDocProtectorLibrary;
import com.huntercodexs.sample.apidocprotector.model.ApiDocProtectorEntity;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.ServletException;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.huntercodexs.sample.apidocprotector.library.ApiDocProtectorErrorLibrary.SENTINEL_ERROR;

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

	@Operation(hidden = true)
	@GetMapping(path = "${apidocprotector.custom.server-uri-account-active:/doc-protect/account/active}/{token}")
	@ResponseBody
	public String activator(@PathVariable("token") String token) {

		logTerm("ACTIVATOR IS START", null, true);

		String tokenCrypt = dataEncrypt(token);
		ApiDocProtectorEntity result = findAccountByTokenAndActive(tokenCrypt, "no");
		logTerm("RESULT TOKEN", result, true);

		if (result == null) {

			/*Generic (HTML Page)*/
			String dataHtml = readFile("./src/main/resources/templates/apidocprotector/generic.html");

			if (alreadyActivated(token)) {
				logTerm("ACCOUNT ALREADY ACTIVATED IN ACTIVATOR", token, true);

				response.setStatus(HttpStatus.CONFLICT.value());
				return dataHtml
						.replace("@{apidoc_protector_title}", "Activation Failure")
						.replace("@{apidoc_protector_content}", "The account has been already activated");
			}

			logTerm("ACCOUNT NOT FOUND IN ACTIVATOR", null, true);

			response.setStatus(HttpStatus.NOT_FOUND.value());
			return dataHtml
					.replace("@{apidoc_protector_title}", "Activation Failure")
					.replace("@{apidoc_protector_content}", "The account was not found");
		}

		/*TODO: Create an alternative to solve this issue (Expired Time to Activate Account)*/
		if (activateExpired(token)) {
			logTerm("ACCOUNT HAS BEEN EXPIRED TO ACTIVE IN ACTIVATOR", token, true);

			/*Generic (HTML Page)*/
			String dataHtml = readFile("./src/main/resources/templates/apidocprotector/generic.html");
			response.setStatus(HttpStatus.NOT_ACCEPTABLE.value());
			return dataHtml
					.replace("@{apidoc_protector_title}", "Activation Failure")
					.replace("@{apidoc_protector_content}", "The time to active your account has been expired, please make a enrollment again");


		}

		result.setActive("yes");
		apiDocProtectorRepository.save(result);

		String emailTo = result.getEmail();
		String subject = apiDocProtectorMailSender.subjectMail(result.getUsername());
		String content = apiDocProtectorMailSender.contentMailActivatedUser(result.getName(), token);

		apiDocProtectorMailSender.sendMailAttached(emailTo, subject, content);

		logTerm("USER TOKEN HAS BEEN ACTIVATED IN ACTIVATOR", result, true);

		/*Activated (HTML Page)*/
		String dataHtml = readFile("./src/main/resources/templates/apidocprotector/activated.html");
		response.setStatus(HttpStatus.OK.value());
		return dataHtml.replace("@{apidoc_protector_username}", result.getName());
	}

}
