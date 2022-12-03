package com.huntercodexs.sample.apidocprotector.library;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ApiDocProtectorMailSender {

    @Value("${apidocprotector.custom.server-domain:http://localhost}")
    String customServerDomain;

    @Value("${apidocprotector.custom.server-uri-account-active:/apidoc-protector/account/active}")
    String customServerUriAccountActive;

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
        System.out.println("JAVA MAIL SENDER: "+message);
        try {
            javaMailSender.send(message);
        } catch (RuntimeException re) {
            System.out.println("[ JAVA MAIL SENDER EXCEPTION ]");
            System.out.println(re.getMessage());
            System.out.println("------------------------------------------------------------------------");
            System.out.println("[ EMAIL ]");
            System.out.println(message);
            System.out.println("------------------------------------------------------------------------");
        }
    }

    public String subjectMail(String username) {
        return "APIDOC PROTECTOR: active your account " + username;
    }

    public String contentMail(String username, String token) {
        String domainServer = customServerDomain.replaceFirst("/$", "");;
        String uriServer = customServerUriAccountActive.replaceFirst("/$", "");
        if (!uriServer.startsWith("/")) uriServer = "/" + uriServer;
        String link = domainServer + uriServer +"/" + token;
        return "Hi " + username + "\n" +
                "Active your account now: " + link;
    }

}
