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
        session.setAttribute("ADP-USER-GENERATOR", "1");
        debugger("forwardToGeneratorGlass IS START", null, true);
        auditor(FORWARD_TO_GENERATOR_GLASS, null, null, 1);
        return "forward:/doc-protect/protector/generator/glass";
    }

    public String forwardToRecoveryGlass() {
        session.setAttribute("ADP-USER-RECOVERY", "1");
        debugger("forwardToRecoveryGlass IS START", null, true);
        auditor(FORWARD_TO_RECOVERY_GLASS, null, null, 1);
        return "forward:/doc-protect/protector/recovery/glass";
    }

    public String forwardToPasswordGlass() {
        session.setAttribute("ADP-USER-PASSWORD", "1");
        debugger("forwardToPasswordGlass IS START", null, true);
        auditor(FORWARD_TO_PASSWORD_GLASS, null, null, 1);
        return "forward:/doc-protect/protector/password/glass";
    }

    public String forwardToPasswordRecoveryGlass(String md5Token) {
        session.setAttribute("ADP-USER-PASSWORD-RECOVERY", "1");
        debugger("forwardToPasswordRecoveryGlass IS START", null, true);
        auditor(FORWARD_TO_PASSWORD_RECOVERY_GLASS, null, null, 1);
        return "forward:/doc-protect/protector/password/recovery/glass/"+md5Token;
    }

    public String forwardToGlass() {
        debugger("forwardToGlass IS START", null, true);
        auditor(FORWARD_TO_GLASS, null, null, 1);
        return "forward:/doc-protect/protector/glass";
    }

    public String redirectToGeneratorForm() {

        debugger("REDIRECT TO GENERATOR FORM IS START", null, true);
        auditor(REDIRECT_TO_GENERATOR_FORM, null, null, 1);

        String uriTarget = customUriGenerator.replaceFirst("/$", "");
        if (!uriTarget.startsWith("/")) uriTarget = "/" + uriTarget;
        if (!uriTarget.endsWith("/form")) uriTarget = uriTarget.replaceFirst("/form$", "");
        uriTarget = uriTarget + "/form";

        debugger("REDIRECT TO GENERATOR FORM", uriTarget, true);
        auditor(REDIRECT_TO_GENERATOR_FORM_TARGET, uriTarget, null, 1);

        return "redirect:" + uriTarget;
    }

    public String redirectToRecoveryForm() {

        debugger("REDIRECT TO RECOVERY FORM IS START", null, true);
        auditor(REDIRECT_TO_RECOVERY_FORM, null, null, 1);

        String uriTarget = customUriRecovery.replaceFirst("/$", "");
        if (!uriTarget.startsWith("/")) uriTarget = "/" + uriTarget;
        if (!uriTarget.endsWith("/form")) uriTarget = uriTarget.replaceFirst("/form$", "");
        uriTarget = uriTarget + "/form";

        debugger("REDIRECT TO RECOVERY FORM", uriTarget, true);
        auditor(REDIRECT_TO_RECOVERY_FORM_TARGET, uriTarget, null, 1);

        return "redirect:" + uriTarget;
    }

    public String redirectToPasswordForm() {

        debugger("REDIRECT TO PASSWORD FORM IS START", null, true);
        auditor(REDIRECT_TO_PASSWORD_FORM, null, null, 1);

        String uriTarget = customUriPassword.replaceFirst("/$", "");
        if (!uriTarget.startsWith("/")) uriTarget = "/" + uriTarget;
        if (!uriTarget.endsWith("/form")) uriTarget = uriTarget.replaceFirst("/form$", "");
        uriTarget = uriTarget + "/form";

        debugger("REDIRECT TO PASSWORD FORM", uriTarget, true);
        auditor(REDIRECT_TO_PASSWORD_FORM_TARGET, uriTarget, null, 1);

        return "redirect:" + uriTarget;
    }

    public String redirectToPasswordRecoveryForm(String md5Token) {

        debugger("REDIRECT TO PASSWORD RECOVERY FORM IS START", null, true);
        auditor(REDIRECT_TO_PASSWORD_RECOVERY_FORM, null, null, 1);

        String uriTarget = customUriPasswordRecovery.replaceFirst("/$", "");
        if (!uriTarget.startsWith("/")) uriTarget = "/" + uriTarget;
        if (!uriTarget.endsWith("/form")) uriTarget = uriTarget.replaceFirst("/form$", "");
        uriTarget = uriTarget + "/form/" + md5Token;

        debugger("REDIRECT TO PASSWORD RECOVERY FORM", uriTarget, true);
        auditor(REDIRECT_TO_PASSWORD_RECOVERY_FORM_TARGET, null, null, 1);

        return "redirect:" + uriTarget;
    }

    public String redirectToForm() {

        debugger("REDIRECT TO FORM IS START", null, true);
        debugger("ADP-EXPIRED-SESSION", session.getAttribute("ADP-EXPIRED-SESSION"), true);
        auditor(REDIRECT_TO_FORM_STARTED, null, null, 2);

        String uriTarget = customUriForm.replaceFirst("/$", "");
        if (!uriTarget.startsWith("/")) uriTarget = "/" + uriTarget;
        if (session.getAttribute("ADP-EXPIRED-SESSION") != null && session.getAttribute("ADP-EXPIRED-SESSION").equals(1)) {

            session.setAttribute("ADP-EXPIRED-SESSION", 0);
            auditor(REDIRECT_TO_FORM_EXPIRED, null, null, 2);

            return "redirect:" + uriTarget + "/x-expired-session";
        }

        auditor(REDIRECT_TO_FORM_FINISHED, null, null, 2);
        return "redirect:" + uriTarget;
    }

    public void redirectExpiredSession(String token) throws IOException {

        debugger("REDIRECT EXPIRED SESSION IS START", null, true);
        auditor(REDIRECT_EXPIRED_SESSION_STARTED, null, null, 2);

        String uriTarget = customUriLogin.replaceFirst("/$", "") + "/" + token;
        if (!uriTarget.startsWith("/")) uriTarget = "/" + uriTarget;
        session.setAttribute("ADP-EXPIRED-SESSION", 1);
        response.setStatus(HttpStatus.REQUEST_TIMEOUT.value());
        auditor(REDIRECT_EXPIRED_SESSION_FINISHED, uriTarget, null, 2);
        response.sendRedirect(uriTarget);
    }

    public String captor(HttpSession session) {

        debugger("CAPTOR IS START", null, true);
        auditor(CAPTOR_STARTED, null, null, 2);

        if (session.getAttribute("ADP-KEYPART") == null) {
            debugger("REDIRECT IN CAPTOR", "redirect:/doc-protect/protector", true);
            auditor(CAPTOR_REDIRECT_TO_PROTECTOR, null, null, 2);
            return "redirect:/doc-protect/protector";
        }

        debugger("REDIRECT IN CAPTOR", "redirect:/doc-protect/router", true);
        auditor(CAPTOR_REDIRECT_TO_ROUTER, null, null, 2);

        /*Session is active*/
        return "redirect:/doc-protect/router";
    }

    public String router(HttpSession session) {

        debugger("ROUTER IS START", null, true);
        auditor(ROUTER_STARTED, null, null, 2);

        /*Session is active*/
        if (session.getAttribute("ADP-KEYPART") != null) {
            session.setAttribute("ADP-KEYPART-REFRESH", "1");
            if (apiDocProtectorType.equals("swagger")) {
                debugger("REDIRECT IN ROUTER", swaggerUIPath, true);
                auditor(ROUTER_REDIRECT_TO_SWAGGER_UI, null, null, 2);
                return "redirect:"+swaggerUIPath;
            }
        }

        debugger("REDIRECT IN ROUTER", "redirect:/doc-protect/protector", true);
        session.setAttribute("ADP-KEYPART-REFRESH", null);
        auditor(ROUTER_REDIRECT_TO_PROTECTOR, null, null, 2);

        return "redirect:/doc-protect/protector";
    }

    public String logout(HttpSession session, String token) {
        auditor(REDIRECT_LOGOUT_STARTED, null, null, 2);
        response.setHeader("ApiDoc-Protector-Active-User", null);
        seesionDestroy();
        String uriTarget = customUriLogin.replaceFirst("/$", "") + "/" + token;
        if (!uriTarget.startsWith("/")) uriTarget = "/" + uriTarget;
        auditor(REDIRECT_LOGOUT_TO_LOGIN, null, null, 2);
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
