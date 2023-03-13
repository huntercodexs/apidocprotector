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

import static com.apidocprotector.enumerator.ApiDocProtectorRegisterEnum.*;

@Hidden
@Controller
@CrossOrigin(origins = "*")
public class ApiDocProtectorActivator extends ApiDocProtectorLibrary {

	@Operation(hidden = true)
	@GetMapping(path = "${apidocprotector.custom.uri-account-active:/doc-protect/account/active}/{token64}")
	@ResponseBody
	public String activator(@PathVariable("token64") String token64) {

		register(ACTIVATOR_STARTED, null, "info", 2, "");

		String md5TokenCrypt = base64Decode(token64);
		ApiDocProtectorEntity result = findAccountByTokenAndActive(md5TokenCrypt, "no");

		register(NO_AUDITOR, null, "info", 2, "RESULT TOKEN " + result.getName());

		if (result.getToken() == null) {

			/*Generic (HTML Page)*/
			String dataHtml = readFile("./src/main/resources/templates/apidocprotector/generic.html");
			String dataCss = readFile("./src/main/resources/static/apidocprotector/css/mail.css");

			if (alreadyActivated(md5TokenCrypt)) {

				register(ACTIVATOR_ACCOUNT_ALREADY_ACTIVATED, null, "info", 2, "Account token: " + md5TokenCrypt);
				response.setStatus(HttpStatus.CONFLICT.value());

				return dataHtml
						.replace("@{apidoc_protector_mail_css}", dataCss)
						.replace("@{apidoc_protector_title}", "Activation Failure")
						.replace("@{apidoc_protector_content}", "The account has been already activated");
			}

			register(ACTIVATOR_ACCOUNT_NOT_FOUND,  null, "info", 2, "Account not found in activator: " + md5TokenCrypt);

			response.setStatus(HttpStatus.NOT_FOUND.value());

			return dataHtml
					.replace("@{apidoc_protector_mail_css}", dataCss)
					.replace("@{apidoc_protector_title}", "Activation Failure")
					.replace("@{apidoc_protector_content}", "The account was not found");
		}

		if (activateExpired(md5TokenCrypt)) {

			register(ACTIVATOR_EXPIRED_ACCOUNT, null, "info", 2, "Account token has been expired: " + md5TokenCrypt);

			/*Generic (HTML Page)*/
			String dataHtml = readFile("./src/main/resources/templates/apidocprotector/generic.html");
			String dataCss = readFile("./src/main/resources/static/apidocprotector/css/mail.css");

			response.setStatus(HttpStatus.NOT_ACCEPTABLE.value());

			return dataHtml
					.replace("@{apidoc_protector_mail_css}", dataCss)
					.replace("@{apidoc_protector_title}", "Activation Failure")
					.replace("@{apidoc_protector_content}", "" +
							"The time to active your account has been expired, you need recovery the account<br />" +
							"<i>Please goto in the Account Creation page and click in Account Recovery</i>" +
							"");
		}

		result.setActive("yes");
		apiDocProtectorRepository.save(result);

		String emailTo = result.getEmail();
		String subject = apiDocProtectorMailSender.subjectMail("User activated", result.getUsername());
		String content = apiDocProtectorMailSender.contentMailActivatedUser(result.getName(), result.getUsername(), md5TokenCrypt);
		apiDocProtectorMailSender.sendMailAttached(emailTo, subject, content);

		register(ACTIVATOR_MAIL_SUCCESSFUL, null, "info", 2, "User activated ok: " + result.getName());

		/*Activated (HTML Page)*/
		String dataHtml = readFile("./src/main/resources/templates/apidocprotector/mail/activated.html");
		String dataCss = readFile("./src/main/resources/static/apidocprotector/css/mail.css");

		response.setStatus(HttpStatus.OK.value());

		register(ACTIVATOR_FINISHED, null, "info", 2, "");

		return dataHtml
				.replace("@{apidoc_protector_mail_css}", dataCss)
				.replace("@{apidoc_protector_username}", result.getName());
	}

}
