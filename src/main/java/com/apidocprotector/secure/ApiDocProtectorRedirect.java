package com.apidocprotector.secure;

import com.apidocprotector.library.ApiDocProtectorLibrary;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.io.IOException;

import static com.apidocprotector.enumerator.ApiDocProtectorRegisterEnum.*;

@Service
public class ApiDocProtectorRedirect extends ApiDocProtectorLibrary {

    public String redirectToApiPrefix() {
        return "redirect:"+apiPrefix;
    }

    public String forwardToGeneratorGlass() {

        register(FORWARD_TO_GENERATOR_GLASS, null, "info", 1, "forwardToGeneratorGlass IS START");

        session.setAttribute("ADP-USER-GENERATOR", "1");
        return "forward:/doc-protect/protector/generator/glass";
    }

    public String forwardToRecoveryGlass() {

        register(FORWARD_TO_RECOVERY_GLASS, null, "info", 1, "forwardToRecoveryGlass IS START");

        session.setAttribute("ADP-USER-RECOVERY", "1");
        return "forward:/doc-protect/protector/recovery/glass";
    }

    public String forwardToPasswordGlass() {

        register(FORWARD_TO_PASSWORD_GLASS, null, "info", 1, "forwardToPasswordGlass IS START");

        session.setAttribute("ADP-USER-PASSWORD", "1");
        return "forward:/doc-protect/protector/password/glass";
    }

    public String forwardToPasswordRecoveryGlass(String md5Token) {

        register(FORWARD_TO_PASSWORD_RECOVERY_GLASS, null, "info", 1, "forwardToPasswordRecoveryGlass IS START");

        session.setAttribute("ADP-USER-PASSWORD-RECOVERY", "1");
        return "forward:/doc-protect/protector/password/recovery/glass/"+md5Token;
    }

    public String forwardToGlass() {

        register(FORWARD_TO_GLASS, null, "info", 1, "forwardToGlass IS START");

        return "forward:/doc-protect/protector/glass";
    }

    public String redirectToGeneratorForm() {

        register(REDIRECT_TO_GENERATOR_FORM, null, "info", 1, "REDIRECT TO GENERATOR FORM IS START");

        String uriTarget = customUriGenerator.replaceFirst("/$", "");
        if (!uriTarget.startsWith("/")) uriTarget = "/" + uriTarget;
        if (!uriTarget.endsWith("/form")) uriTarget = uriTarget.replaceFirst("/form$", "");
        uriTarget = uriTarget + "/form";

        register(REDIRECT_TO_GENERATOR_FORM_TARGET, null, "info", 1, "REDIRECT TO GENERATOR FORM");

        return "redirect:" + uriTarget;
    }

    public String redirectToRecoveryForm() {

        register(REDIRECT_TO_RECOVERY_FORM, null, "info", 1, "REDIRECT TO RECOVERY FORM IS START");

        String uriTarget = customUriRecovery.replaceFirst("/$", "");
        if (!uriTarget.startsWith("/")) uriTarget = "/" + uriTarget;
        if (!uriTarget.endsWith("/form")) uriTarget = uriTarget.replaceFirst("/form$", "");
        uriTarget = uriTarget + "/form";

        register(REDIRECT_TO_RECOVERY_FORM_TARGET, null, "info", 1, "REDIRECT TO RECOVERY FORM");

        return "redirect:" + uriTarget;
    }

    public String redirectToPasswordForm() {

        register(REDIRECT_TO_PASSWORD_FORM, null, "info", 1, "REDIRECT TO PASSWORD FORM IS START");

        String uriTarget = customUriPassword.replaceFirst("/$", "");
        if (!uriTarget.startsWith("/")) uriTarget = "/" + uriTarget;
        if (!uriTarget.endsWith("/form")) uriTarget = uriTarget.replaceFirst("/form$", "");
        uriTarget = uriTarget + "/form";

        register(REDIRECT_TO_PASSWORD_FORM_TARGET, null, "info", 1, "REDIRECT TO PASSWORD FORM");

        return "redirect:" + uriTarget;
    }

    public String redirectToPasswordRecoveryForm(String token64) {

        register(REDIRECT_TO_PASSWORD_RECOVERY_FORM, null, "info", 1, "REDIRECT TO PASSWORD RECOVERY FORM IS START");

        String uriTarget = customUriPasswordRecovery.replaceFirst("/$", "");
        if (!uriTarget.startsWith("/")) uriTarget = "/" + uriTarget;
        if (!uriTarget.endsWith("/form")) uriTarget = uriTarget.replaceFirst("/form$", "");
        uriTarget = uriTarget + "/form/" + token64;

        register(REDIRECT_TO_PASSWORD_RECOVERY_FORM_TARGET, null, "info", 1, "REDIRECT TO PASSWORD RECOVERY FORM");

        return "redirect:" + uriTarget;
    }

    public String redirectToForm() {

        register(REDIRECT_TO_FORM_STARTED, null, "info", 1, "REDIRECT TO FORM IS START");

        String uriTarget = customUriForm.replaceFirst("/$", "");
        if (!uriTarget.startsWith("/")) uriTarget = "/" + uriTarget;
        if (session.getAttribute("ADP-EXPIRED-SESSION") != null && session.getAttribute("ADP-EXPIRED-SESSION").equals(1)) {

            session.setAttribute("ADP-EXPIRED-SESSION", 0);
            register(REDIRECT_TO_FORM_EXPIRED, null, "info", 2, "");

            return "redirect:" + uriTarget + "/x-expired-session";
        }

        register(REDIRECT_TO_FORM_FINISHED, null, "info", 2, "");

        return "redirect:" + uriTarget;
    }

    public void redirectExpiredSession(String token) throws IOException {

        register(REDIRECT_EXPIRED_SESSION_STARTED, null, "info", 2, "REDIRECT EXPIRED SESSION IS START");

        String uriTarget = customUriLogin.replaceFirst("/$", "") + "/" + token;
        if (!uriTarget.startsWith("/")) uriTarget = "/" + uriTarget;
        session.setAttribute("ADP-EXPIRED-SESSION", 1);
        response.setStatus(HttpStatus.REQUEST_TIMEOUT.value());

        register(REDIRECT_EXPIRED_SESSION_FINISHED, null, "info", 2, "");

        response.sendRedirect(uriTarget);
    }

    public String captor(HttpSession session) {

        register(CAPTOR_STARTED, null, "info", 2, "CAPTOR IS START");

        if (session.getAttribute("ADP-KEYPART") == null) {
            register(CAPTOR_REDIRECT_TO_PROTECTOR, null, "info", 2, "redirect:/doc-protect/protector");
            return "redirect:/doc-protect/protector";
        }

        register(CAPTOR_REDIRECT_TO_ROUTER, null, "info", 1, "redirect:/doc-protect/router");

        /*Session is active*/
        return "redirect:/doc-protect/router";
    }

    public String router(HttpSession session) {

        register(ROUTER_STARTED, null, "info", 2, "ROUTER IS START");

        /*Session is active*/
        if (session.getAttribute("ADP-KEYPART") != null) {
            session.setAttribute("ADP-KEYPART-REFRESH", "1");
            if (apiDocProtectorType.equals("swagger")) {

                register(ROUTER_REDIRECT_TO_SWAGGER_UI, null, "info", 2, "redirect:/doc-protect/swagger-ui");

                return "redirect:"+swaggerUIPath;
            }
        }

        register(ROUTER_REDIRECT_TO_PROTECTOR, null, "info", 2, "redirect:/doc-protect/protector");

        return "redirect:/doc-protect/protector";
    }

    public String logout(HttpSession session, String token64) {

        register(REDIRECT_LOGOUT_STARTED, null, "info", 1, "");

        seesionDestroy();
        response.setHeader("ApiDoc-Protector-Active-User", null);
        String uriTarget = customUriLogin.replaceFirst("/$", "") + "/" + token64;
        if (!uriTarget.startsWith("/")) uriTarget = "/" + uriTarget;

        register(REDIRECT_LOGOUT_TO_LOGIN, null, "info", 1, "");

        return "redirect:"+uriTarget;
    }

    public void seesionDestroy() {
        session.removeAttribute("ADP-KEYPART");
        session.removeAttribute("ADP-SECRET");
        session.removeAttribute("ADP-KEYPART-REFRESH");
        session.removeAttribute("APIDOC-AUDITOR");
        session.removeAttribute("APIDOC-AUDITOR-GET-USERNAME");
        session.removeAttribute("APIDOC-AUDITOR-GET-ROLE");
    }
}
