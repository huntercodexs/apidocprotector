package com.apidocprotector.secure;

import com.apidocprotector.dto.ApiDocProtectorDto;
import com.apidocprotector.library.ApiDocProtectorLibrary;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static com.apidocprotector.enumerator.ApiDocProtectorAuditEnum.*;
import static com.apidocprotector.enumerator.ApiDocProtectorLibraryEnum.*;

@Service
public class ApiDocProtectorViewer extends ApiDocProtectorLibrary {

    public ModelAndView error(String title, String info, HttpStatus statusCode) {
        auditor(VIEW_ERROR_STARTED, null, null, 1);
        response.setStatus(statusCode.value());
        ModelAndView modelAndView = new ModelAndView("apidocprotector/error");
        modelAndView.addObject("apidoc_protector_error_title", title);
        modelAndView.addObject("apidoc_protector_error_info", info);
        auditor(VIEW_ERROR_FINISHED, null, null, 1);
        return modelAndView;
    }

    public ModelAndView generator(boolean userCreatedSuccessful) {

        auditor(VIEW_GENERATOR_STARTED, null, null, 1);

        try {

            if (apiDocProtectorType.equals("swagger")) {
                ModelAndView modelAndView = new ModelAndView("apidocprotector/generator");
                modelAndView.addObject("apidoc_protector_target", customUriUserGenerator);
                modelAndView.addObject("apidoc_protector_form_recovery", customUriRecovery);

                if (userCreatedSuccessful) {
                    modelAndView.addObject("apidoc_protector_created", "Account Created Successful");
                }

                auditor(VIEW_GENERATOR_FINISHED, null, null, 1);
                return modelAndView;
            }

            auditor(VIEW_GENERATOR_ERROR, "Occurs an critical error on form", null, 1);
            return error(
                    FORM_VIEW_ERROR.getMessage(),
                    "Occurs an critical error on form",
                    FORM_VIEW_ERROR.getStatusCode());

        } catch (RuntimeException re) {

            auditor(VIEW_GENERATOR_EXCEPTION, re.getMessage(), null, 1);
            return error(
                    FORM_ERROR.getMessage(),
                    re.getMessage(),
                    FORM_ERROR.getStatusCode());
        }
    }

    public ModelAndView recovery(boolean userRecoverySuccessful) {

        auditor(VIEW_RECOVERY_STARTED, null, null, 1);

        try {

            if (apiDocProtectorType.equals("swagger")) {
                ModelAndView modelAndView = new ModelAndView("apidocprotector/recovery");
                modelAndView.addObject("apidoc_protector_target", customUriUserRecovery);

                if (userRecoverySuccessful) {
                    modelAndView.addObject("apidoc_protector_recovery", "Check your email !");
                }

                auditor(VIEW_RECOVERY_FINISHED, null, null, 1);

                return modelAndView;
            }

            auditor(VIEW_RECOVERY_ERROR, "Occurs an critical error on form", null, 1);
            return error(
                    FORM_VIEW_ERROR.getMessage(),
                    "Occurs an critical error on form recovery",
                    FORM_VIEW_ERROR.getStatusCode());

        } catch (RuntimeException re) {
            auditor(VIEW_RECOVERY_EXCEPTION, re.getMessage(), null, 1);
            return error(
                    FORM_ERROR.getMessage(),
                    re.getMessage(),
                    FORM_ERROR.getStatusCode());
        }
    }

    public ModelAndView password(boolean userRecoverySuccessful) {

        auditor(VIEW_PASSWORD_STARTED, null, null, 1);

        try {

            if (apiDocProtectorType.equals("swagger")) {
                ModelAndView modelAndView = new ModelAndView("apidocprotector/password");
                modelAndView.addObject("apidoc_protector_target", customUriUserPassword);

                if (userRecoverySuccessful) {
                    modelAndView.addObject("apidoc_protector_password", "Check your email !");
                }

                auditor(VIEW_PASSWORD_FINISHED, null, null, 1);
                return modelAndView;
            }

            auditor(VIEW_PASSWORD_ERROR, "Occurs an critical error on form", null, 1);
            return error(
                    FORM_VIEW_ERROR.getMessage(),
                    "Occurs an critical error on form password",
                    FORM_VIEW_ERROR.getStatusCode());

        } catch (RuntimeException re) {
            auditor(VIEW_PASSWORD_EXCEPTION, re.getMessage(), null, 1);
            return error(
                    FORM_ERROR.getMessage(),
                    re.getMessage(),
                    FORM_ERROR.getStatusCode());
        }
    }

    public ModelAndView passwordRecovery(boolean userRecoverySuccessful, String currentToken) {

        auditor(VIEW_PASSWORD_RECOVERY_STARTED, null, null, 1);

        try {

            if (apiDocProtectorType.equals("swagger")) {
                ModelAndView modelAndView = new ModelAndView("apidocprotector/password-recovery");
                modelAndView.addObject("apidoc_protector_target", customUriUserPasswordRecovery);

                if (userRecoverySuccessful) {
                    modelAndView.addObject("apidoc_protector_password", "Password changed successful !");
                } else {
                    modelAndView.addObject("apidoc_protector_token", currentToken);
                }

                auditor(VIEW_PASSWORD_RECOVERY_FINISHED, null, null, 1);
                return modelAndView;
            }

            auditor(VIEW_PASSWORD_RECOVERY_ERROR, "Occurs an critical error on form", null, 1);
            return error(
                    FORM_VIEW_ERROR.getMessage(),
                    "Occurs an critical error on form password recovery",
                    FORM_VIEW_ERROR.getStatusCode());

        } catch (RuntimeException re) {
            auditor(VIEW_PASSWORD_RECOVERY_EXCEPTION, re.getMessage(), null, 1);
            return error(
                    FORM_ERROR.getMessage(),
                    re.getMessage(),
                    FORM_ERROR.getStatusCode());
        }
    }

    public ModelAndView form(HttpSession session, String sessionId) {

        auditor(VIEW_FORM_STARTED, null, null, 1);

        Map<String, String> body = new HashMap<>();
        body.put("condition", "--init-login-form");

        try {
            apiDocProtectorSecurity.firewall(session, body, sessionId);
            debugger("FIREWALL IS DONE IN FORM", null, true);

            String secret = ((ApiDocProtectorDto) session.getAttribute(sessionId)).getSecret();
            debugger("SECRET IN FORM", secret, true);

            String token = ((ApiDocProtectorDto) session.getAttribute(sessionId)).getToken();
            debugger("TOKEN IN FORM", token, true);

            if (apiDocProtectorType.equals("swagger")) {
                ModelAndView modelAndView = new ModelAndView("apidocprotector/login");
                modelAndView.addObject("apidoc_protector_target", swaggerUIPath);
                modelAndView.addObject("apidoc_protector_sec", secret);
                modelAndView.addObject("apidoc_protector_token", token);
                modelAndView.addObject("apidoc_protector_form_password", customUriPassword);

                auditor(VIEW_FORM_FINISHED, null, null, 1);
                return modelAndView;
            }

            auditor(VIEW_FORM_ERROR, "Occurs an critical error on form", null, 1);
            return error(
                    FORM_VIEW_ERROR.getMessage(),
                    "Occurs an critical error on form",
                    FORM_VIEW_ERROR.getStatusCode());

        } catch (RuntimeException re) {
            auditor(VIEW_FORM_EXCEPTION, re.getMessage(), null, 1);
            return error(
                    FORM_ERROR.getMessage(),
                    re.getMessage(),
                    FORM_ERROR.getStatusCode());
        }
    }

    public ModelAndView index(String token) {

        auditor(VIEW_FORM_STARTED, null, null, 1);

        debugger("INDEX VIEWER START", null, true);
        debugger("PROTECTOR-TYPE VIEWER IN INDEX", apiDocProtectorType, true);

        if (apiDocProtectorType.equals("swagger")) {
            ModelAndView modelAndView = new ModelAndView("apidocprotector/swagger-ui/index");
            modelAndView.addObject("api_docs_path", apiDocsPath);
            modelAndView.addObject("swagger_layout", swaggerLayout);
            modelAndView.addObject("show_url_api_docs", showUrlApiDocs);

            String uriLogout = customUriLogout.replaceFirst("/$", "") + "/";
            if (!uriLogout.startsWith("/")) uriLogout = "/" + uriLogout;

            modelAndView.addObject("apidoc_protector_logout_token", uriLogout+token);
            auditor(VIEW_INDEX_FINISHED, null, null, 1);

            return modelAndView;
        }

        auditor(VIEW_INDEX_ERROR, "Occurs an critical error on form", null, 1);
        return error(
                INVALID_PROTECTOR.getMessage(),
                apiDocProtectorType,
                INVALID_PROTECTOR.getStatusCode());
    }

    public ModelAndView refresh(HttpSession session, String sessionId, String flag) {

        debugger("REFRESH VIEWER START", session.getAttribute("ADP-KEYPART-REFRESH"), true);
        auditor(VIEW_REFRESHED_STARTED, null, null, 1);

        Map<String, String> body = new HashMap<>();
        body.put("condition", flag);

        try {

            String username = ((ApiDocProtectorDto) session.getAttribute(sessionId)).getUsername();
            response.setHeader("ApiDoc-Protector-Active-User", md5(username));
            debugger("USERNAME CURRENT IN REFRESH", username, true);

            apiDocProtectorSecurity.firewall(session, body, sessionId);

            String token = ((ApiDocProtectorDto) session.getAttribute(sessionId)).getToken();
            debugger("TOKEN CURRENT IN REFRESH", token, true);

            if (sessionExpired(token)) {
                try {
                    apiDocProtectorRedirect.redirectExpiredSession(token);
                    return null;
                } catch (IOException e) {
                    debugger("EXCEPTION", e.getMessage(), true);
                }
                return apiDocProtectorViewer.error(
                        EXPIRED_SESSION.getMessage(),
                        "session expired",
                        EXPIRED_SESSION.getStatusCode());
            }

            if (apiDocProtectorType.equals("swagger")) {
                auditor(VIEW_REFRESHED_FINISHED, null, null, 1);
                return index(token);
            }

            auditor(VIEW_REFRESHED_ERROR, null, null, 1);
            return error(
                    INVALID_PROTECTOR.getMessage(),
                    apiDocProtectorType,
                    INVALID_PROTECTOR.getStatusCode());

        } catch (RuntimeException re) {
            String error = re.getMessage();
            if (error == null) error = "Maybe invalid/expired session";

            auditor(VIEW_REFRESHED_EXCEPTION, re.getMessage(), null, 1);

            return error(
                    REFRESH_ERROR.getMessage(),
                    error,
                    REFRESH_ERROR.getStatusCode());
        }
    }

    public ModelAndView protector(HttpSession session, String sessionId) {

        debugger("PROTECTOR VIEWER START", session.getAttribute(sessionId), true);
        auditor(VIEW_PROTECTOR_STARTED, null, sessionId, 1);

        Map<String, String> body = new HashMap<>();
        body.put("condition", "--login-success");

        try {
            apiDocProtectorSecurity.firewall(session, body, sessionId);

            debugger("FIREWALL IS DONE", null, true);
            debugger("SESSION CURRENT IN PROTECTOR", session.getAttribute(sessionId), true);
            debugger("KEYPART CURRENT IN PROTECTOR", session.getAttribute("ADP-KEYPART"), true);

            String secret = ((ApiDocProtectorDto) session.getAttribute(sessionId)).getSecret();
            debugger("SECRET CURRENT IN PROTECTOR", secret, true);

            String token = ((ApiDocProtectorDto) session.getAttribute(sessionId)).getToken();
            debugger("SECRET CURRENT IN PROTECTOR", token, true);

            auditor(VIEW_PROTECTOR_FINISHED, null, null, 1);
            return index(token);

        } catch (RuntimeException re) {

            debugger("[EXCEPTION] PROTECTOR VIEWER", re.getMessage(), true);
            auditor(VIEW_PROTECTOR_EXCEPTION, re.getMessage(), null, 1);

            return error(
                    PROTECTOR_ERROR.getMessage(),
                    re.getMessage(),
                    PROTECTOR_ERROR.getStatusCode());
        }
    }

}
