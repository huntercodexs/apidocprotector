package com.huntercodexs.sample.apidocprotector.library;

import com.huntercodexs.sample.apidocprotector.model.ApiDocProtectorEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

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

        logTerm("JAVA MAIL SENDER", message, true);

        try {
            javaMailSender.send(message);
        } catch (RuntimeException re) {
            logTerm("[ JAVA MAIL SENDER EXCEPTION ]", re.getMessage(), true);
            logTerm("[ EMAIL ]", message, true);
        }
    }

    public void sendMailAttached(String to, String subject, String content) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(content, true);
            helper.addAttachment("huntercodexs-name-white.png", new ClassPathResource("/templates/apidocprotector/files/huntercodexs-name-white.png"));

            logTerm("[ EMAIL ]", message, true);
            logTerm("[ HELPER ]", helper, true);

            javaMailSender.send(message);

        } catch (MessagingException me) {
            logTerm("JAVA MAIL SENDER [ATTACHED] [MESSAGING-EXCEPTION]", me.getMessage(), true);
            throw new RuntimeException(me.getMessage());
        }

        logTerm("JAVA MAIL SENDER [ATTACHED] IS OK", "OK", true);
    }

    public String subjectMail(String username) {
        return "[APIDOC PROTECTOR] Account Creating to " + username;
    }

    public String contentMailGeneratorOrRecoveryUser(String username, String token) {
        String domainServer = customUrlServerDomain.replaceFirst("/$", "");;
        String uriServer = customUriAccountActive.replaceFirst("/$", "");
        if (!uriServer.startsWith("/")) uriServer = "/" + uriServer;
        String link = domainServer + uriServer +"/" + token;

        /*Activate (HTML Mail)*/
        String dataHtml = readFile("./src/main/resources/templates/apidocprotector/mail/activate.html");
        String emailTime = String.valueOf(expireTimeEmail) + " minutes";

        return dataHtml
                .replace("@{apidoc_protector_username}", username)
                .replace("@{apidoc_protector_url_active}", link)
                .replace("@{apidoc_protector_email_expires_time}", emailTime);
    }

    public String contentMailPassword(ApiDocProtectorEntity user) {
        String domainServer = customUrlServerDomain.replaceFirst("/$", "");;
        String uriServer = customUriPasswordRecovery.replaceFirst("/$", "");
        if (!uriServer.startsWith("/")) uriServer = "/" + uriServer;
        String link = domainServer + uriServer +"/" + user.getToken();

        /*Password Recovery (HTML Mail)*/
        String dataHtml = readFile("./src/main/resources/templates/apidocprotector/mail/password.html");
        String emailTime = String.valueOf(expireTimeEmail) + " minutes";

        return dataHtml
                .replace("@{apidoc_protector_username}", user.getName())
                .replace("@{apidoc_protector_url_password_recovery}", link)
                .replace("@{apidoc_protector_email_expires_time}", emailTime);
    }

    public String contentMailPasswordRecovery(ApiDocProtectorEntity user) {
        String domainServer = customUrlServerDomain.replaceFirst("/$", "");;
        String uriServer = customUriLogin.replaceFirst("/$", "");
        if (!uriServer.startsWith("/")) uriServer = "/" + uriServer;
        String link = domainServer + uriServer +"/" + user.getToken();

        /*Password Recovery (HTML Mail)*/
        String dataHtml = readFile("./src/main/resources/templates/apidocprotector/mail/password-recovery.html");

        return dataHtml
                .replace("@{apidoc_protector_username}", user.getName())
                .replace("@{apidoc_protector_url_password_recovery}", link);
    }

    public String contentMailActivatedUser(String username, String token) {
        String urlUser = customUrlServerDomain.replaceFirst("/$", "");
        String uriUser = customUriLogin.replaceFirst("/$", "") + "/" + token;
        if (!uriUser.startsWith("/")) uriUser = "/" + uriUser;
        String urlToken = urlUser + uriUser;

        /*Welcome (HTML Mail)*/
        String dataHtml = readFile("./src/main/resources/templates/apidocprotector/mail/welcome.html");

        return dataHtml
                .replace("@{apidoc_protector_username}", username)
                .replace("@{apidoc_protector_user_token}", token)
                .replace("@{apidoc_protector_url_access}", urlToken);
    }

}
