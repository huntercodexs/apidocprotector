package com.apidocprotector.library;

import com.apidocprotector.model.ApiDocProtectorEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import static com.apidocprotector.enumerator.ApiDocProtectorRegisterEnum.*;

@Slf4j
@Service
public class ApiDocProtectorMailSender extends ApiDocProtectorLibrary {

    @Autowired
    private final JavaMailSender javaMailSender;

    public ApiDocProtectorMailSender(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    public void sendMail(String to, String subject, String content) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(content);

        register(MAILSENDER_STARTED, null, "info", 0, "");

        try {
            javaMailSender.send(message);
        } catch (RuntimeException re) {
            register(NO_AUDITOR, null, "except", 0, re.getMessage());
        }
    }

    public void sendMailAttached(String to, String subject, String content) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(content, true);
            helper.addAttachment("logo.png", new ClassPathResource("/templates/apidocprotector/files/logo.png"));

            register(MAILSENDER_STARTED, null, "info", 0, "");

            javaMailSender.send(message);

        } catch (MessagingException me) {
            register(NO_AUDITOR, null, "except", 0, me.getMessage());
        }
    }

    public String subjectMail(String info, String username) {
        return "[APIDOC PROTECTOR] " + info + " " + username;
    }

    public String contentMailGeneratorUser(String username, String md5Token) {
        String domainServer = customUrlServerDomain.replaceFirst("/$", "");;
        String uriServer = customUriAccountActive.replaceFirst("/$", "");
        if (!uriServer.startsWith("/")) uriServer = "/" + uriServer;
        String link = domainServer + uriServer +"/" + base64Encode(md5Token);

        /*Activate (HTML Mail)*/
        String dataHtml = readFile("./src/main/resources/templates/apidocprotector/mail/activate.html");
        String dataCss = readFile("./src/main/resources/static/apidocprotector/css/mail.css");

        String emailTime = String.valueOf(expireTimeEmail) + " minutes";

        register(MAILSENDER_CONTENT, null, "info", 2, "mail to " + username);

        return dataHtml
                .replace("@{apidoc_protector_mail_css}", dataCss)
                .replace("@{apidoc_protector_username}", username)
                .replace("@{apidoc_protector_url_active}", link)
                .replace("@{apidoc_protector_email_expires_time}", emailTime);
    }

    public String contentMailRecoveryUser(String username, String md5TokenCrypt) {
        String domainServer = customUrlServerDomain.replaceFirst("/$", "");;
        String uriServer = customUriAccountActive.replaceFirst("/$", "");
        if (!uriServer.startsWith("/")) uriServer = "/" + uriServer;
        String link = domainServer + uriServer +"/" + base64Encode(md5TokenCrypt);

        /*Activate (HTML Mail)*/
        String dataHtml = readFile("./src/main/resources/templates/apidocprotector/mail/activate.html");
        String dataCss = readFile("./src/main/resources/static/apidocprotector/css/mail.css");

        String emailTime = String.valueOf(expireTimeEmail) + " minutes";

        register(MAILSENDER_CONTENT, null, "info", 2, "mail to " + username);

        return dataHtml
                .replace("@{apidoc_protector_mail_css}", dataCss)
                .replace("@{apidoc_protector_username}", username)
                .replace("@{apidoc_protector_url_active}", link)
                .replace("@{apidoc_protector_email_expires_time}", emailTime);
    }

    public String contentMailPassword(ApiDocProtectorEntity user) {
        String domainServer = customUrlServerDomain.replaceFirst("/$", "");;
        String uriServer = customUriPasswordRecovery.replaceFirst("/$", "");
        if (!uriServer.startsWith("/")) uriServer = "/" + uriServer;
        String link = domainServer + uriServer +"/" + base64Encode(user.getToken());

        /*Password Recovery (HTML Mail)*/
        String dataHtml = readFile("./src/main/resources/templates/apidocprotector/mail/password.html");
        String dataCss = readFile("./src/main/resources/static/apidocprotector/css/mail.css");

        String emailTime = String.valueOf(expireTimeEmail) + " minutes";

        register(MAILSENDER_CONTENT, null, "info", 2, "mail to " + user.getUsername());

        return dataHtml
                .replace("@{apidoc_protector_mail_css}", dataCss)
                .replace("@{apidoc_protector_username}", user.getName())
                .replace("@{apidoc_protector_url_password_recovery}", link)
                .replace("@{apidoc_protector_email_expires_time}", emailTime);
    }

    public String contentMailPasswordRecovery(String updatedMd5TokenCrypt, ApiDocProtectorEntity user) {
        String domainServer = customUrlServerDomain.replaceFirst("/$", "");;
        String uriServer = customUriLogin.replaceFirst("/$", "");
        if (!uriServer.startsWith("/")) uriServer = "/" + uriServer;
        String link = domainServer + uriServer +"/" + base64Encode(updatedMd5TokenCrypt);

        /*Password Recovery (HTML Mail)*/
        String dataHtml = readFile("./src/main/resources/templates/apidocprotector/mail/password-recovery.html");
        String dataCss = readFile("./src/main/resources/static/apidocprotector/css/mail.css");

        register(MAILSENDER_CONTENT, null, "info", 2, "mail to " + user.getUsername());

        return dataHtml
                .replace("@{apidoc_protector_mail_css}", dataCss)
                .replace("@{apidoc_protector_username}", user.getName())
                .replace("@{apidoc_protector_url_password_recovery}", link);
    }

    public String contentMailActivatedUser(String name, String username, String md5TokenCrypt) {
        String urlUser = customUrlServerDomain.replaceFirst("/$", "");
        String uriUser = customUriLogin.replaceFirst("/$", "") + "/" + base64Encode(md5TokenCrypt);
        if (!uriUser.startsWith("/")) uriUser = "/" + uriUser;
        String urlToken = urlUser + uriUser;

        /*Welcome (HTML Mail)*/
        String dataHtml = readFile("./src/main/resources/templates/apidocprotector/mail/welcome.html");
        String dataCss = readFile("./src/main/resources/static/apidocprotector/css/mail.css");

        register(MAILSENDER_CONTENT, null, "info", 2, "mail to " + username);

        return dataHtml
                .replace("@{apidoc_protector_mail_css}", dataCss)
                .replace("@{apidoc_protector_name}", name)
                .replace("@{apidoc_protector_username}", username)
                .replace("@{apidoc_protector_url_access}", urlToken);
    }

}
