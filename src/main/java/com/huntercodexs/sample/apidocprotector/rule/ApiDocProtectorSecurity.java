package com.huntercodexs.sample.apidocprotector.rule;

import com.huntercodexs.sample.apidocprotector.dto.ApiDocProtectorDto;
import com.huntercodexs.sample.apidocprotector.library.ApiDocProtectorLibrary;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.util.Map;

import static com.huntercodexs.sample.apidocprotector.library.ApiDocProtectorErrorLibrary.*;

@Service
public class ApiDocProtectorSecurity extends ApiDocProtectorLibrary {

    public void firewall(HttpSession session, Map<String, String> body, String sessionId) {

        logTerm("FIREWALL IS RUNNING", "INFO", true);
        logTerm("FIREWALL IS RUNNING", response.getHeader("ApiDoc-Protector-Active-User"), true);

        ApiDocProtectorDto sessionTransfer = (ApiDocProtectorDto) session.getAttribute(sessionId);
        logTerm("SESSION TRANSFER IN FIREWALL", sessionTransfer, true);

        String secretForm;

        String headerRequestHost = null; //localhost:31303, 192.168.15.14:31303
        String headerRequestUserAgent = null; //Mozilla Firefox, PostmanRuntime/7.28.4
        String headerSecFetchDest = null; //document
        String headerSecFetchMode = null; //navigate
        String headerRequestContentType = null; //application/x-www-form-urlencoded
        String headerRequestOrigin = null; //http://localhost:31303, http://192.168.15.14:31303
        String headerRequestReferer = null; //http://localhost:31303/doc-protect/uri, http://192.168.15.14:31303/doc-protect/uri

        String requestMethod = null; //POST, GET, PUT, DELETE, PATCH, OPTIONS, HEAD
        String remoteAddr = null; //127.0.0.1, 0:0:0:0:0:0:0:1, 192.168.15.13
        String remoteHost = null; //127.0.0.1, 0:0:0:0:0:0:0:1, 192.168.15.13
        String serverName = null; //localhost, 192.168.15.14
        String servletPath = null; //swagger-ui/protector
        String authorization = null; //Bearer F1F2F3F34F4F5F6F7FF8...
        String userAgent = null; //Mozilla Firefox, PostmanRuntime/7.28.4, MSIE-Edge etc..
        String postmanToken = null; //96f17e89-cacf-41d2-be8d-96aafd870b3a

        try {
            headerRequestHost = request.getHeader("Host");
            logTerm("(HEADER) Host: " + headerRequestHost, "INFO", true);
        } catch (RuntimeException re) {
            logTerm("(HEADER) Host " + re.getMessage(), "ERROR", true);
        }

        try {
            headerRequestUserAgent = request.getHeader("User-Agent");
            logTerm("(HEADER) User-Agent: " + headerRequestUserAgent, "INFO", true);
        } catch (RuntimeException re) {
            logTerm("(HEADER) User-Agent " + re.getMessage(), "ERROR", true);
        }

        try {
            headerSecFetchDest = request.getHeader("Sec-Fetch-Dest");
            logTerm("(HEADER) Sec-Fetch-Dest: " + headerSecFetchDest, "INFO", true);
        } catch (RuntimeException re) {
            logTerm("(HEADER) Sec-Fetch-Dest " + re.getMessage(), "ERROR", true);
        }

        try {
            headerSecFetchMode = request.getHeader("Sec-Fetch-Mode");
            logTerm("(HEADER) Sec-Fetch-Mode: " + headerSecFetchMode, "INFO", true);
        } catch (RuntimeException re) {
            logTerm("(HEADER) Sec-Fetch-Mode " + re.getMessage(), "ERROR", true);
        }

        try {
            headerRequestContentType = request.getHeader("Content-Type");
            logTerm("(HEADER) Content-Type: " + headerRequestContentType, "INFO", true);
        } catch (RuntimeException re) {
            logTerm("(HEADER) Content-Type " + re.getMessage(), "ERROR", true);
        }

        try {
            headerRequestOrigin = request.getHeader("Origin");
            logTerm("(HEADER) Origin: " + headerRequestOrigin, "INFO", true);
        } catch (RuntimeException re) {
            logTerm("(HEADER) Origin " + re.getMessage(), "ERROR", true);
        }

        try {
            headerRequestReferer = request.getHeader("Referer");
            logTerm("(HEADER) Referer: " + headerRequestReferer, "INFO", true);
        } catch (RuntimeException re) {
            logTerm("(HEADER) Referer " + re.getMessage(), "ERROR", true);
        }

        try {
            requestMethod = request.getMethod();
            logTerm("getMethod: "+requestMethod, "INFO", true);
        } catch (RuntimeException re) {
            logTerm("(SERVLET) getMethod " + re.getMessage(), "ERROR", true);
        }

        try {
            remoteAddr = request.getRemoteAddr();
            logTerm("getRemoteAddr: "+remoteAddr, "INFO", true);
        } catch (RuntimeException re) {
            logTerm("(SERVLET) getRemoteAddr " + re.getMessage(), "ERROR", true);
        }

        try {
            remoteHost = request.getRemoteHost();
            logTerm("getRemoteHost: "+remoteHost, "INFO", true);
        } catch (RuntimeException re) {
            logTerm("(SERVLET) getRemoteHost " + re.getMessage(), "ERROR", true);
        }

        try {
            serverName = request.getServerName();
            logTerm("getServerName: "+serverName, "INFO", true);
        } catch (RuntimeException re) {
            logTerm("(SERVLET) getServerName" + re.getMessage(), "ERROR", true);
        }

        try {
            servletPath = request.getServletPath();
            logTerm("getServletPath: "+servletPath, "INFO", true);
        } catch (RuntimeException re) {
            logTerm("(SERVLET) getServletPath " + re.getMessage(), "ERROR", true);
        }

        try {
            authorization = request.getHeader("Authorization");
            logTerm("Authorization: " + authorization, "INFO", true);
        } catch (RuntimeException re) {
            logTerm("(SERVLET) Authorization " + re.getMessage(), "ERROR", true);
        }

        try {
            userAgent = request.getHeader("User-Agent");
            logTerm("User-Agent: " + userAgent, "INFO", true);
        } catch (RuntimeException re) {
            logTerm("(SERVLET) User-Agent " + re.getMessage(), "ERROR", true);
        }

        try {
            postmanToken = request.getHeader("Postman-Token");
            logTerm("Postman-Token: " + postmanToken, "INFO", true);
        } catch (RuntimeException re) {
            logTerm("(SERVLET) Postman-Token " + re.getMessage(), "ERROR", true);
        }

        logTerm("REQUEST-METHOD", requestMethod, true);

        if (requestMethod != null && requestMethod.equals("GET")) {

            if (
                    headerSecFetchDest != null &&
                    !headerSecFetchDest.equals("document") &&
                    !headerSecFetchDest.equals("style") &&
                    !headerSecFetchDest.equals("script")
            ) {
                throw new RuntimeException(NOT_ACCEPTED_FIREWALL.getMessage());
            }

            if (
                    headerSecFetchMode != null &&
                    !headerSecFetchMode.equals("navigate") &&
                    !headerSecFetchMode.equals("no-cors")
            ) {
                throw new RuntimeException(NOT_ACCEPTED_FIREWALL.getMessage());
            }

        } else {

            try {
                secretForm = body.get("apidocprotector_sec");
                logTerm("[FORM] Secret: " + secretForm, "INFO", true);

                if (!secretForm.equals(sessionId)) {
                    throw new RuntimeException(INVALID_FORM_FIREWALL.getMessage());
                }

            } catch (RuntimeException re) {
                logTerm("[FORM] Secret: " + re.getMessage(), "ERROR", true);
            }

            if (headerRequestContentType != null && !headerRequestContentType.equals("application/x-www-form-urlencoded")) {
                throw new RuntimeException(WRONG_REQUEST_FIREWALL.getMessage());
            }

            if (servletPath != null && !servletPath.contains("/protector")) {
                if (!servletPath.equals(swaggerUIPath)) {
                    throw new RuntimeException(WRONG_REQUEST_FIREWALL.getMessage());
                }
            }

            if (headerRequestHost != null && !headerRequestHost.contains("localhost") && !headerRequestHost.contains(apiDocServerName)) {
                throw new RuntimeException(NOT_ACCEPTED_FIREWALL.getMessage());
            }
            if (headerRequestUserAgent != null && headerRequestUserAgent.contains("PostmanRuntime")) {
                throw new RuntimeException(NOT_ACCEPTED_FIREWALL.getMessage());
            }

            if (
                    headerRequestOrigin != null &&
                    !headerRequestOrigin.contains("http://localhost") &&
                    !headerRequestOrigin.contains("http://"+apiDocServerName) &&
                    !headerRequestOrigin.contains("https://"+apiDocServerName)
            ) {
                throw new RuntimeException(NOT_ACCEPTED_FIREWALL.getMessage());
            }

            if (
                    headerRequestReferer != null &&
                    !headerRequestReferer.contains("http://localhost") &&
                    !headerRequestReferer.contains("http://"+apiDocServerName) &&
                    !headerRequestReferer.contains("https://"+apiDocServerName)
            ) {
                throw new RuntimeException(NOT_ACCEPTED_FIREWALL.getMessage());
            }

            if (remoteAddr != null && remoteAddr.equals("0:0:0:0:0:0:0:1")) {
                if (headerRequestUserAgent != null && headerRequestUserAgent.contains("PostmanRuntime")) {
                    throw new RuntimeException(NOT_ACCEPTED_FIREWALL.getMessage());
                }
            }

            if (remoteHost != null && remoteHost.equals("0:0:0:0:0:0:0:1")) {
                if (headerRequestUserAgent != null && headerRequestUserAgent.contains("PostmanRuntime")) {
                    throw new RuntimeException(NOT_ACCEPTED_FIREWALL.getMessage());
                }
            }

            if (serverName != null && !serverName.equals("localhost") && !serverName.equals(apiDocServerName)) {
                throw new RuntimeException(NOT_ACCEPTED_FIREWALL.getMessage());
            }

            if (userAgent != null && userAgent.contains("PostmanRuntime")) {
                throw new RuntimeException(NOT_ACCEPTED_FIREWALL.getMessage());
            }

            if (postmanToken != null) {
                throw new RuntimeException(NOT_ACCEPTED_FIREWALL.getMessage());
            }
            if (authorization == null && session.getId() == null) {
                throw new RuntimeException(NOT_ACCEPTED_FIREWALL.getMessage());
            }

        }

        logTerm("ACTIVE USER IN HEADER", response.getHeader("ApiDoc-Protector-Active-User"), true);

        if(response.getHeader("ApiDoc-Protector-Active-User") == null) {
            if (!body.get("condition").equals("--init-login-form")) {
                throw new RuntimeException(UNAUTHORIZED_FIREWALL.getMessage());
            }
        } else {
            if (!body.get("condition").equals("--init-login-form")) {
                if (!md5(sessionTransfer.getUsername()).equals(response.getHeader("ApiDoc-Protector-Active-User"))) {
                    throw new RuntimeException(UNAUTHORIZED_FIREWALL.getMessage());
                }
            }
        }

        if (!sessionTransfer.getAuthorized()) {
            throw new RuntimeException(UNAUTHORIZED_FIREWALL.getMessage());
        }

        if (session.getAttribute(sessionId).equals("") || !((ApiDocProtectorDto) session.getAttribute(sessionId)).getToken().equals(sessionTransfer.getToken())) {
            if (body.get("condition") != null && !body.get("condition").equals("--login-fail")) {
                throw new RuntimeException(UNAUTHORIZED_FIREWALL.getMessage());
            }
        }

        logTerm("FIREWALL PASSED", "INFO", true);
        logTerm("JSESSIONID IS: "+session.getId(), "INFO", true);
        logTerm("TOKEN IS: "+sessionTransfer.getToken(), "INFO", true);
    }

    public boolean burn(HttpSession session, String tokenCrypt, String sessionId) {
        ApiDocProtectorDto sessionTransfer = (ApiDocProtectorDto) session.getAttribute(sessionId);
        if (
                !sessionTransfer.getOrigin().equals("redirectToLoginForm") ||
                !sessionTransfer.getId().equals(session.getId()) ||
                !tokenCrypt.equals(dataEncrypt(sessionTransfer.getToken())) ||
                !session.getAttribute("ADP-KEYPART").equals(sessionTransfer.getKeypart())
        ) {
            doAuthorized(session, sessionId, false);
            return true;
        }

        return false;
    }

    public boolean shield(HttpSession session) {
        try {
            if (session.getAttribute("ADP-KEYPART").toString().equals("")) {
                logTerm("MISSING ADP-KEYPART SESSION IN SHIELD", null, true);
                return false;
            }
            if (session.getAttribute("ADP-SECRET").toString().equals("")) {
                logTerm("MISSING ADP-SECRET SESSION IN SHIELD", null, true);
                return false;
            }
            return true;
        } catch (RuntimeException re) {
            logTerm("EXCEPTION IN SHIELD", re.getMessage(), true);
            return false;
        }
    }

}
