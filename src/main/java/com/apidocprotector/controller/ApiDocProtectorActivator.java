package com.apidocprotector.controller;

import com.apidocprotector.library.ApiDocProtectorLibrary;
import com.apidocprotector.model.ApiDocProtectorEntity;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import static com.apidocprotector.enumerator.ApiDocProtectorAuditEnum.*;

@Hidden
@Controller
@CrossOrigin(origins = "*")
public class ApiDocProtectorActivator extends ApiDocProtectorLibrary {

	@Operation(hidden = true)
	@GetMapping(path = "${apidocprotector.custom.uri-account-active:/doc-protect/account/active}/{token}")
	@ResponseBody
	public String activator(@PathVariable("token") String token) {

		logTerm("ACTIVATOR IS START", null, true);
		auditor(ACTIVATOR_STARTED, null, null);

		String tokenCrypt = dataEncrypt(token);
		ApiDocProtectorEntity result = findAccountByTokenAndActive(tokenCrypt, "no");
		logTerm("RESULT TOKEN", result, true);

		if (result == null) {

			/*Generic (HTML Page)*/
			String dataHtml = readFile("./src/main/resources/templates/apidocprotector/generic.html");

			if (alreadyActivated(token)) {
				logTerm("ACCOUNT ALREADY ACTIVATED IN ACTIVATOR", token, true);
				auditor(ACTIVATOR_ACCOUNT_ALREADY_ACTIVATED, "The token was expired: " + token, null);

				response.setStatus(HttpStatus.CONFLICT.value());
				return dataHtml
						.replace("@{apidoc_protector_title}", "Activation Failure")
						.replace("@{apidoc_protector_content}", "The account has been already activated");
			}

			logTerm("ACCOUNT NOT FOUND IN ACTIVATOR", null, true);
			auditor(ACTIVATOR_ACCOUNT_NOT_FOUND, token, null);

			response.setStatus(HttpStatus.NOT_FOUND.value());
			return dataHtml
					.replace("@{apidoc_protector_title}", "Activation Failure")
					.replace("@{apidoc_protector_content}", "The account was not found");
		}

		if (activateExpired(token)) {
			logTerm("ACCOUNT HAS BEEN EXPIRED TO ACTIVE IN ACTIVATOR", token, true);
			auditor(ACTIVATOR_EXPIRED_ACCOUNT, "The token was expired: " + token, null);

			/*Generic (HTML Page)*/
			String dataHtml = readFile("./src/main/resources/templates/apidocprotector/generic.html");
			response.setStatus(HttpStatus.NOT_ACCEPTABLE.value());
			return dataHtml
					.replace("@{apidoc_protector_title}", "Activation Failure")
					.replace("@{apidoc_protector_content}", "" +
							"The time to active your account has been expired, you need recovery the account<br />" +
							"<i>Please goto in the Account Creation page and click in Account Recovery</i>" +
							"");
		}

		result.setActive("yes");
		apiDocProtectorRepository.save(result);
		auditor(ACTIVATOR_TOKEN_OK, null, null);

		String emailTo = result.getEmail();
		String subject = apiDocProtectorMailSender.subjectMail(result.getUsername());
		String content = apiDocProtectorMailSender.contentMailActivatedUser(result.getName(), token);

		apiDocProtectorMailSender.sendMailAttached(emailTo, subject, content);
		logTerm("USER TOKEN HAS BEEN ACTIVATED IN ACTIVATOR", result, true);
		auditor(ACTIVATOR_MAIL_SUCCESSFUL, null, null);

		/*Activated (HTML Page)*/
		String dataHtml = readFile("./src/main/resources/templates/apidocprotector/activated.html");
		response.setStatus(HttpStatus.OK.value());
		auditor(ACTIVATOR_FINISHED, null, null);

		return dataHtml.replace("@{apidoc_protector_username}", result.getName());
	}

}
