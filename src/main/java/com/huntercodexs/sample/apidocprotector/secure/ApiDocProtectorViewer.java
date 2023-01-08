package com.huntercodexs.sample.apidocprotector.secure;

import com.huntercodexs.sample.apidocprotector.dto.ApiDocProtectorDto;
import com.huntercodexs.sample.apidocprotector.library.ApiDocProtectorLibrary;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static com.huntercodexs.sample.apidocprotector.library.ApiDocProtectorErrorLibrary.*;

@Service
public class ApiDocProtectorViewer extends ApiDocProtectorLibrary {

    public ModelAndView error(String title, String info, HttpStatus statusCode) {
        response.setStatus(statusCode.value());
        ModelAndView modelAndView = new ModelAndView("apidocprotector/error");
        modelAndView.addObject("apidoc_protector_error_title", title);
        modelAndView.addObject("apidoc_protector_error_info", info);
        return modelAndView;
    }

    public ModelAndView generator(boolean userCreatedSuccessful) {

        try {

            if (apiDocProtectorType.equals("swagger")) {
                ModelAndView modelAndView = new ModelAndView("apidocprotector/generator");
                modelAndView.addObject("apidoc_protector_target", customUriUserGenerator);
                modelAndView.addObject("apidoc_protector_form_recovery", customUriRecovery);

                if (userCreatedSuccessful) {
                    modelAndView.addObject("apidoc_protector_created", "Account Created Successful");
                }

                return modelAndView;
            }

            return error(
                    FORM_VIEW_ERROR.getMessage(),
                    "Occurs an critical error on form",
                    FORM_VIEW_ERROR.getStatusCode());

        } catch (RuntimeException re) {
            return error(
                    FORM_ERROR.getMessage(),
                    re.getMessage(),
                    FORM_ERROR.getStatusCode());
        }
    }

    public ModelAndView recovery(boolean userRecoverySuccessful) {

        try {

            if (apiDocProtectorType.equals("swagger")) {
                ModelAndView modelAndView = new ModelAndView("apidocprotector/recovery");
                modelAndView.addObject("apidoc_protector_target", customUriUserRecovery);

                if (userRecoverySuccessful) {
                    modelAndView.addObject("apidoc_protector_recovery", "Check your email !");
                }

                return modelAndView;
            }

            return error(
                    FORM_VIEW_ERROR.getMessage(),
                    "Occurs an critical error on form recovery",
                    FORM_VIEW_ERROR.getStatusCode());

        } catch (RuntimeException re) {
            return error(
                    FORM_ERROR.getMessage(),
                    re.getMessage(),
                    FORM_ERROR.getStatusCode());
        }
    }

    public ModelAndView password(boolean userRecoverySuccessful) {

        try {

            if (apiDocProtectorType.equals("swagger")) {
                ModelAndView modelAndView = new ModelAndView("apidocprotector/password");
                modelAndView.addObject("apidoc_protector_target", customUriUserPassword);

                if (userRecoverySuccessful) {
                    modelAndView.addObject("apidoc_protector_password", "Check your email !");
                }

                return modelAndView;
            }

            return error(
                    FORM_VIEW_ERROR.getMessage(),
                    "Occurs an critical error on form password",
                    FORM_VIEW_ERROR.getStatusCode());

        } catch (RuntimeException re) {
            return error(
                    FORM_ERROR.getMessage(),
                    re.getMessage(),
                    FORM_ERROR.getStatusCode());
        }
    }

    public ModelAndView passwordRecovery(boolean userRecoverySuccessful, String currentToken) {

        try {

            if (apiDocProtectorType.equals("swagger")) {
                ModelAndView modelAndView = new ModelAndView("apidocprotector/password-recovery");
                modelAndView.addObject("apidoc_protector_target", customUriUserPasswordRecovery);

                if (userRecoverySuccessful) {
                    modelAndView.addObject("apidoc_protector_password", "Password changed successful !");
                } else {
                    modelAndView.addObject("apidoc_protector_token", currentToken);
                }

                return modelAndView;
            }

            return error(
                    FORM_VIEW_ERROR.getMessage(),
                    "Occurs an critical error on form password recovery",
                    FORM_VIEW_ERROR.getStatusCode());

        } catch (RuntimeException re) {
            return error(
                    FORM_ERROR.getMessage(),
                    re.getMessage(),
                    FORM_ERROR.getStatusCode());
        }
    }

    public ModelAndView form(HttpSession session, String sessionId) {

        Map<String, String> body = new HashMap<>();
        body.put("condition", "--init-login-form");

        try {
            apiDocProtectorSecurity.firewall(session, body, sessionId);
            logTerm("FIREWALL IS DONE IN FORM", null, true);

            String secret = ((ApiDocProtectorDto) session.getAttribute(sessionId)).getSecret();
            logTerm("SECRET IN FORM", secret, true);

            String token = ((ApiDocProtectorDto) session.getAttribute(sessionId)).getToken();
            logTerm("TOKEN IN FORM", token, true);

            if (apiDocProtectorType.equals("swagger")) {
                ModelAndView modelAndView = new ModelAndView("apidocprotector/login");
                modelAndView.addObject("apidoc_protector_target", swaggerUIPath);
                modelAndView.addObject("apidoc_protector_sec", secret);
                modelAndView.addObject("apidoc_protector_token", token);
                modelAndView.addObject("apidoc_protector_form_password", customUriPassword);
                return modelAndView;
            }

            return error(
                    FORM_VIEW_ERROR.getMessage(),
                    "Occurs an critical error on form",
                    FORM_VIEW_ERROR.getStatusCode());

        } catch (RuntimeException re) {
            return error(
                    FORM_ERROR.getMessage(),
                    re.getMessage(),
                    FORM_ERROR.getStatusCode());
        }
    }

    public ModelAndView index(String token) {

        logTerm("INDEX VIEWER START", null, true);
        logTerm("PROTECTOR-TYPE VIEWER IN INDEX", apiDocProtectorType, true);

        if (apiDocProtectorType.equals("swagger")) {
            ModelAndView modelAndView = new ModelAndView("apidocprotector/swagger-ui/index");
            modelAndView.addObject("api_docs_path", apiDocsPath);
            modelAndView.addObject("swagger_layout", swaggerLayout);
            modelAndView.addObject("show_url_api_docs", showUrlApiDocs);

            String uriLogout = customUriLogout.replaceFirst("/$", "") + "/";
            if (!uriLogout.startsWith("/")) uriLogout = "/" + uriLogout;

            modelAndView.addObject("apidoc_protector_logout_token", uriLogout+token);
            return modelAndView;
        }

        return error(
                INVALID_PROTECTOR.getMessage(),
                apiDocProtectorType,
                INVALID_PROTECTOR.getStatusCode());
    }

    public ModelAndView refresh(HttpSession session, String sessionId, String flag) {

        logTerm("REFRESH VIEWER START", session.getAttribute("ADP-KEYPART-REFRESH"), true);

        Map<String, String> body = new HashMap<>();
        body.put("condition", flag);

        try {

            String username = ((ApiDocProtectorDto) session.getAttribute(sessionId)).getUsername();
            response.setHeader("ApiDoc-Protector-Active-User", md5(username));
            logTerm("USERNAME CURRENT IN REFRESH", username, true);

            apiDocProtectorSecurity.firewall(session, body, sessionId);

            String token = ((ApiDocProtectorDto) session.getAttribute(sessionId)).getToken();
            logTerm("TOKEN CURRENT IN REFRESH", token, true);

            if (sessionExpired(token)) {
                try {
                    apiDocProtectorRedirect.redirectExpiredSession(token);
                    return null;
                } catch (IOException e) {
                    logTerm("EXCEPTION", e.getMessage(), true);
                }
                return apiDocProtectorViewer.error(
                        EXPIRED_SESSION.getMessage(),
                        "session expired",
                        EXPIRED_SESSION.getStatusCode());
            }

            if (apiDocProtectorType.equals("swagger")) {
                return index(token);
            }

            return error(
                    INVALID_PROTECTOR.getMessage(),
                    apiDocProtectorType,
                    INVALID_PROTECTOR.getStatusCode());

        } catch (RuntimeException re) {
            String error = re.getMessage();
            if (error == null) error = "Maybe invalid/expired session";
            return error(
                    REFRESH_ERROR.getMessage(),
                    error,
                    REFRESH_ERROR.getStatusCode());
        }
    }

    public ModelAndView protector(HttpSession session, String sessionId) {

        logTerm("PROTECTOR VIEWER START", session.getAttribute(sessionId), true);

        Map<String, String> body = new HashMap<>();
        body.put("condition", "--login-success");

        try {
            apiDocProtectorSecurity.firewall(session, body, sessionId);

            logTerm("FIREWALL IS DONE", null, true);
            logTerm("SESSION CURRENT IN PROTECTOR", session.getAttribute(sessionId), true);
            logTerm("KEYPART CURRENT IN PROTECTOR", session.getAttribute("ADP-KEYPART"), true);

            String secret = ((ApiDocProtectorDto) session.getAttribute(sessionId)).getSecret();
            logTerm("SECRET CURRENT IN PROTECTOR", secret, true);

            String token = ((ApiDocProtectorDto) session.getAttribute(sessionId)).getToken();
            logTerm("SECRET CURRENT IN PROTECTOR", token, true);

            return index(token);

        } catch (RuntimeException re) {
            logTerm("[EXCEPTION] PROTECTOR VIEWER", re.getMessage(), true);
            return error(
                    PROTECTOR_ERROR.getMessage(),
                    re.getMessage(),
                    PROTECTOR_ERROR.getStatusCode());
        }
    }

}
