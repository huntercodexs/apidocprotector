package com.apidocprotector.secure;

import com.apidocprotector.library.ApiDocProtectorLibrary;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.io.IOException;

import static com.apidocprotector.enumerator.ApiDocProtectorAuditEnum.*;

@Service
public class ApiDocProtectorRedirect extends ApiDocProtectorLibrary {

    public String redirectToApiPrefix() {
        return "redirect:"+apiPrefix;
    }

    public String forwardToGeneratorGlass() {
        session.setAttribute("ADP-USER-GENERATOR", "1");
        logTerm("forwardToGeneratorGlass IS START", null, true);
        auditor(GENERATOR_GLASS_FORWARD, null, null);
        return "forward:/doc-protect/protector/generator/glass";
    }

    public String forwardToRecoveryGlass() {
        session.setAttribute("ADP-USER-RECOVERY", "1");
        logTerm("forwardToRecoveryGlass IS START", null, true);
        return "forward:/doc-protect/protector/recovery/glass";
    }

    public String forwardToPasswordGlass() {
        session.setAttribute("ADP-USER-PASSWORD", "1");
        logTerm("forwardToPasswordGlass IS START", null, true);
        return "forward:/doc-protect/protector/password/glass";
    }

    public String forwardToPasswordRecoveryGlass(String md5Token) {
        session.setAttribute("ADP-USER-PASSWORD-RECOVERY", "1");
        logTerm("forwardToPasswordRecoveryGlass IS START", null, true);
        return "forward:/doc-protect/protector/password/recovery/glass/"+md5Token;
    }

    public String forwardToGlass() {
        logTerm("forwardToGlass IS START", null, true);
        return "forward:/doc-protect/protector/glass";
    }

    public String redirectToGeneratorForm() {

        logTerm("REDIRECT TO GENERATOR FORM IS START", null, true);
        auditor(GENERATOR_FORM_REDIRECT, null, null);

        String uriTarget = customUriGenerator.replaceFirst("/$", "");
        if (!uriTarget.startsWith("/")) uriTarget = "/" + uriTarget;
        if (!uriTarget.endsWith("/form")) uriTarget = uriTarget.replaceFirst("/form$", "");
        uriTarget = uriTarget + "/form";

        logTerm("REDIRECT TO GENERATOR FORM", uriTarget, true);

        return "redirect:" + uriTarget;
    }

    public String redirectToRecoveryForm() {

        logTerm("REDIRECT TO RECOVERY FORM IS START", null, true);

        String uriTarget = customUriRecovery.replaceFirst("/$", "");
        if (!uriTarget.startsWith("/")) uriTarget = "/" + uriTarget;
        if (!uriTarget.endsWith("/form")) uriTarget = uriTarget.replaceFirst("/form$", "");
        uriTarget = uriTarget + "/form";

        logTerm("REDIRECT TO RECOVERY FORM", uriTarget, true);

        return "redirect:" + uriTarget;
    }

    public String redirectToPasswordForm() {

        logTerm("REDIRECT TO PASSWORD FORM IS START", null, true);

        String uriTarget = customUriPassword.replaceFirst("/$", "");
        if (!uriTarget.startsWith("/")) uriTarget = "/" + uriTarget;
        if (!uriTarget.endsWith("/form")) uriTarget = uriTarget.replaceFirst("/form$", "");
        uriTarget = uriTarget + "/form";

        logTerm("REDIRECT TO PASSWORD FORM", uriTarget, true);

        return "redirect:" + uriTarget;
    }

    public String redirectToPasswordRecoveryForm(String md5Token) {

        logTerm("REDIRECT TO PASSWORD RECOVERY FORM IS START", null, true);

        String uriTarget = customUriPasswordRecovery.replaceFirst("/$", "");
        if (!uriTarget.startsWith("/")) uriTarget = "/" + uriTarget;
        if (!uriTarget.endsWith("/form")) uriTarget = uriTarget.replaceFirst("/form$", "");
        uriTarget = uriTarget + "/form/" + md5Token;

        logTerm("REDIRECT TO PASSWORD RECOVERY FORM", uriTarget, true);

        return "redirect:" + uriTarget;
    }

    public String redirectToForm() {

        logTerm("REDIRECT TO FORM IS START", null, true);
        logTerm("ADP-EXPIRED-SESSION", session.getAttribute("ADP-EXPIRED-SESSION"), true);

        String uriTarget = customUriForm.replaceFirst("/$", "");
        if (!uriTarget.startsWith("/")) uriTarget = "/" + uriTarget;
        if (session.getAttribute("ADP-EXPIRED-SESSION") != null && session.getAttribute("ADP-EXPIRED-SESSION").equals(1)) {
            session.setAttribute("ADP-EXPIRED-SESSION", 0);
            return "redirect:" + uriTarget + "/x-expired-session";
        }
        return "redirect:" + uriTarget;
    }

    public void redirectExpiredSession(String token) throws IOException {

        logTerm("REDIRECT EXPIRED SESSION IS START", null, true);

        String uriTarget = customUriLogin.replaceFirst("/$", "") + "/" + token;
        if (!uriTarget.startsWith("/")) uriTarget = "/" + uriTarget;
        session.setAttribute("ADP-EXPIRED-SESSION", 1);
        response.setStatus(HttpStatus.REQUEST_TIMEOUT.value());
        response.sendRedirect(uriTarget);
    }

    public String captor(HttpSession session) {

        logTerm("CAPTOR IS START", null, true);

        if (session.getAttribute("ADP-KEYPART") == null) {
            logTerm("REDIRECT IN CAPTOR", "redirect:/doc-protect/protector", true);
            return "redirect:/doc-protect/protector";
        }

        logTerm("REDIRECT IN CAPTOR", "redirect:/doc-protect/router", true);

        /*Session is active*/
        return "redirect:/doc-protect/router";
    }

    public String router(HttpSession session) {

        logTerm("ROUTER IS START", null, true);

        /*Session is active*/
        if (session.getAttribute("ADP-KEYPART") != null) {
            session.setAttribute("ADP-KEYPART-REFRESH", "1");
            if (apiDocProtectorType.equals("swagger")) {
                logTerm("REDIRECT IN ROUTER", swaggerUIPath, true);
                return "redirect:"+swaggerUIPath;
            }
        }

        logTerm("REDIRECT IN ROUTER", "redirect:/doc-protect/protector", true);
        session.setAttribute("ADP-KEYPART-REFRESH", null);
        return "redirect:/doc-protect/protector";
    }

    public String logout(HttpSession session, String token) {
        response.setHeader("ApiDoc-Protector-Active-User", null);
        session.removeAttribute("ADP-KEYPART");
        session.removeAttribute("ADP-SECRET");
        session.removeAttribute("ADP-KEYPART-REFRESH");
        session.removeAttribute("APIDOC-AUDITOR");
        String uriTarget = customUriLogin.replaceFirst("/$", "") + "/" + token;
        if (!uriTarget.startsWith("/")) uriTarget = "/" + uriTarget;
        return "redirect:"+uriTarget;
    }
}
