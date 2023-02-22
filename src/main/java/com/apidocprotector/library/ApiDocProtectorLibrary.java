package com.apidocprotector.library;

import com.apidocprotector.dto.ApiDocProtectorAuditDto;
import com.apidocprotector.dto.ApiDocProtectorDto;
import com.apidocprotector.enumerator.ApiDocProtectorAuditEnum;
import com.apidocprotector.model.ApiDocProtectorAuditEntity;
import com.apidocprotector.model.ApiDocProtectorEntity;
import com.apidocprotector.repository.ApiDocProtectorAuditRepository;
import com.apidocprotector.repository.ApiDocProtectorRepository;
import com.apidocprotector.secure.ApiDocProtectorErrorRedirect;
import com.apidocprotector.secure.ApiDocProtectorRedirect;
import com.apidocprotector.secure.ApiDocProtectorSecurity;
import com.apidocprotector.secure.ApiDocProtectorViewer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import static com.apidocprotector.enumerator.ApiDocProtectorAuditEnum.*;

@Slf4j
@Service
public abstract class ApiDocProtectorLibrary extends ApiDocProtectorDataLibrary {

    protected ApiDocProtectorDto transfer;

    protected static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm:ss");

    @Value("${api.prefix:/error}")
    protected String apiPrefix;

    @Value("${apidocprotector.custom.uri-generator:/doc-protect/generator}")
    protected String customUriGenerator;

    @Value("${apidocprotector.custom.uri-recovery:/doc-protect/recovery}")
    protected String customUriRecovery;

    @Value("${apidocprotector.custom.uri-password:/doc-protect/password}")
    protected String customUriPassword;

    @Value("${apidocprotector.custom.uri-login:/doc-protect/login}")
    protected String customUriLogin;

    @Value("${apidocprotector.custom.uri-form:/doc-protect/protector/form}")
    protected String customUriForm;

    @Value("${apidocprotector.custom.uri-logout:/doc-protect/logout}")
    protected String customUriLogout;

    @Value("${apidocprotector.custom.uri-user-generator:/doc-protect/generator/user}")
    protected String customUriUserGenerator;

    @Value("${apidocprotector.custom.uri-form-recovery:/doc-protect/recovery/form}")
    protected String customUriUserFormRecovery;

    @Value("${apidocprotector.custom.uri-user-recovery:/doc-protect/recovery/user}")
    protected String customUriUserRecovery;

    @Value("${apidocprotector.custom.uri-user-password:/doc-protect/password/user}")
    protected String customUriUserPassword;

    @Value("${apidocprotector.custom.uri-user-password-recovery:/doc-protect/password/recovery/user}")
    protected String customUriUserPasswordRecovery;

    @Value("${apidocprotector.server-name:localhost}")
    protected String apiDocServerName;

    @Value("${springdoc.swagger-ui.path:/swagger-ui}/protector")
    protected String swaggerUIPath;

    @Value("${apidocprotector.type:swagger}")
    protected String apiDocProtectorType;

    @Value("${apidocprotector.data.crypt.type:md5}")
    protected String dataCryptTpe;

    @Value("${springdoc.api-docs.path:/api-docs-guard}")
    protected String apiDocsPath;

    @Value("${apidocprotector.url.show:true}")
    protected String showUrlApiDocs;

    @Value("${springdoc.swagger-ui.layout:StandaloneLayout}")
    protected String swaggerLayout;

    @Value("${apidocprotector.session.expire-time:0}")
    protected int expireTimeSession;

    @Value("${apidocprotector.email.expire-time:1}")
    protected int expireTimeEmail;

    @Value("${apidocprotector.custom.server-domain:http://localhost}")
    protected String customUrlServerDomain;

    @Value("${apidocprotector.audit.enabled:false}")
    protected boolean apiDocAuditor;

    @Value("${apidocprotector.audit.level:0}")
    protected int apiDocAuditorLevel;

    @Value("${apidocprotector.logging.enabled:false}")
    protected boolean apiDocLogging;

    @Value("${apidocprotector.debugger.enabled:false}")
    protected boolean apiDocDebugger;

    @Value("${apidocprotector.custom.uri-account-active:/doc-protect/account/active}")
    protected String customUriAccountActive;

    @Value("${apidocprotector.custom.uri-password-recovery:/doc-protect/password/recovery}")
    protected String customUriPasswordRecovery;

    @Autowired
    protected ApiDocProtectorErrorRedirect apiDocProtectorErrorRedirect;

    @Autowired
    protected ApiDocProtectorRedirect apiDocProtectorRedirect;

    @Autowired
    protected ApiDocProtectorViewer apiDocProtectorViewer;

    @Autowired
    protected ApiDocProtectorSecurity apiDocProtectorSecurity;

    @Autowired
    protected ApiDocProtectorRepository apiDocProtectorRepository;

    @Autowired
    protected ApiDocProtectorAuditRepository apiDocProtectorAuditRepository;

    @Autowired
    protected ApiDocProtectorMailSender apiDocProtectorMailSender;

    @Autowired
    protected HttpServletRequest request;

    @Autowired
    protected HttpServletResponse response;

    @Autowired
    protected HttpSession session;

    public ApiDocProtectorDto initEnv(String token) {
        ApiDocProtectorDto transferDto = new ApiDocProtectorDto();
        String keypartVal = md5(now()).toUpperCase();
        transferDto.setId(session.getId());
        transferDto.setRequest(request);
        transferDto.setResponse(response);
        transferDto.setOrigin("redirectToLoginForm");
        transferDto.setToken(token);
        transferDto.setSecret(guide(null));
        transferDto.setUsername(null);
        transferDto.setPassword(null);
        transferDto.setAuthorized(true);
        transferDto.setAuthenticate(false);
        transferDto.setKeypart(keypartVal);

        debugger("KEYPART-VAL", keypartVal, true);
        auditor(LIBRARY_ENVIRONMENT_STARTED, null, null, 2);

        return transferDto;
    }

    public void sessionPrepare(HttpSession session, ApiDocProtectorDto transfer, ApiDocProtectorEntity result) {
        String sessionKey = md5(transfer.getKeypart() + transfer.getSecret()).toUpperCase();
        String sessionVal = md5(sessionKey).toUpperCase();
        result.setSessionKey(sessionKey);
        result.setSessionVal(sessionVal);
        result.setUpdatedAt(now());
        result.setSessionCreatedAt(now());
        apiDocProtectorRepository.save(result);

        /*This is a main session (security)*/
        session.setAttribute(sessionVal, transfer);
        debugger("SESSION CREATED", session.getAttribute(sessionVal), true);
        auditor(LIBRARY_SESSION_PREPARED_OK, null, null, 2);
    }

    public void sessionRenew(ApiDocProtectorEntity result, LocalDateTime dateTimeNow) {
        result.setSessionCreatedAt(dateTimeNow.format(FORMATTER));
        apiDocProtectorRepository.save(result);
        auditor(LIBRARY_SESSION_RENEW_OK, null, null, 2);
    }

    public boolean sessionExpired(String token) {

        /*Control Session is disabled*/
        if (expireTimeSession < 1) {
            debugger("CONTROL EXPIRED SESSION IS DISABLE", expireTimeSession, true);
            logger("CONTROL EXPIRED SESSION IS DISABLE: " + expireTimeSession, "info");
            auditor(LIBRARY_SESSION_CONTROL_DISABLED, null, null, 2);
            return false;
        }

        String tokenCrypt = dataEncrypt(token);
        ApiDocProtectorEntity result = apiDocProtectorRepository.findByTokenAndActive(tokenCrypt, "yes");
        String sessionCreatedAt = result.getSessionCreatedAt();

        LocalDateTime dateTimeSession = LocalDateTime.parse(result.getSessionCreatedAt(), FORMATTER);
        LocalDateTime sessionTimePlus = dateTimeSession.plusMinutes(expireTimeSession);

        LocalDateTime dateTimeNow = LocalDateTime.now();
        String dateTimeFormat = dateTimeNow.format(FORMATTER);
        LocalDateTime dateTimeFormatter = LocalDateTime.parse(dateTimeFormat, FORMATTER);

        int diffTime = dateTimeFormatter.compareTo(sessionTimePlus);

        debugger("SESSION EXPIRED START LOG", null, true);
        debugger("SESSION CREATED AT", sessionCreatedAt, false);
        debugger("DATE TIME NOW", dateTimeNow, false);
        debugger("DATE TIME NOW FORMATTER", dateTimeFormatter, false);
        debugger("SESSION TIME PLUS", sessionTimePlus, false);
        debugger("SESSION TIME PLUS FORMATTER", sessionTimePlus.format(FORMATTER), false);
        debugger("DIFF TIME", diffTime, false);
        debugger("EXPIRED TIME SESSION IS", expireTimeSession, false);
        debugger("SESSION EXPIRED FINISH LOG", null, true);

        /*Check Expired Session*/
        if (diffTime > 0) {
            sessionRenew(result, dateTimeNow);
            debugger("SESSION EXPIRED", result.getSessionVal(), true);
            auditor(LIBRARY_SESSION_EXPIRED, "Token " + tokenCrypt, null, 2);
            return true;
        }

        sessionRenew(result, dateTimeNow);
        debugger("SESSION REFRESHED", result.getSessionVal(), true);
        auditor(LIBRARY_SESSION_RENEWED, "Token " + tokenCrypt, null, 2);

        return false;
    }

    public boolean alreadyActivated(String clearToken) {
        String tokenCrypt = dataEncrypt(clearToken);
        ApiDocProtectorEntity result = apiDocProtectorRepository.findByTokenAndActive(tokenCrypt, "yes");

        if (result != null && result.getToken().equals(tokenCrypt)) {
            debugger("ACCOUNT ALREADY ACTIVATED", clearToken, true);
            auditor(LIBRARY_ACCOUNT_ALREADY_ACTIVATED, null, null, 2);
            return true;
        }

        debugger("ACCOUNT IS NOT ACTIVATED YET", clearToken, true);
        auditor(LIBRARY_ACCOUNT_NOT_ACTIVATED_YET, null, null, 2);

        return false;
    }

    private int diffTime(String userDate) {

        LocalDateTime dateTimeNow = LocalDateTime.now();
        String dateTimeFormat = dateTimeNow.format(FORMATTER);
        LocalDateTime dateTimeFormatter = LocalDateTime.parse(dateTimeFormat, FORMATTER);

        try {
            LocalDateTime dateTimeAccount = LocalDateTime.parse(userDate, FORMATTER);
            LocalDateTime accountTimePlus = dateTimeAccount.plusMinutes(expireTimeEmail);

            debugger("DATE TIME NOW", dateTimeNow, false);
            debugger("DATE TIME NOW FORMATTER", dateTimeFormatter, false);
            debugger("ACCOUNT TIME PLUS", accountTimePlus, false);
            debugger("ACCOUNT TIME PLUS FORMATTER", accountTimePlus.format(FORMATTER), false);
            debugger("EXPIRED TIME ACCOUNT IS", expireTimeEmail, false);

            logger("DATE TIME NOW: " + dateTimeNow, "info");
            logger("DATE TIME NOW FORMATTER: " + dateTimeFormatter.format(FORMATTER), "info");
            logger("ACCOUNT TIME PLUS: " + accountTimePlus, "info");
            logger("ACCOUNT TIME PLUS FORMATTER: " + accountTimePlus.format(FORMATTER), "info");
            logger("EXPIRED TIME ACCOUNT IS: " + expireTimeEmail, "info");

            return dateTimeFormatter.format(FORMATTER).compareTo(accountTimePlus.format(FORMATTER));
        } catch (Exception ex) {
            debugger("DIFF TIME ACCOUNT[EXCEPTION]", ex.getMessage(), true);
            logger("DIFF TIME ACCOUNT[EXCEPTION]: " + ex.getMessage(), "except");
            return 0;
        }
    }

    public boolean activateExpired(String tokenClear) {

        /*0 = not expires email to account activate*/
        if (expireTimeEmail < 1) {
            return false;
        }

        String tokenCrypt = dataEncrypt(tokenClear);
        ApiDocProtectorEntity user = apiDocProtectorRepository.findByTokenAndActive(tokenCrypt, "no");
        String accountCreatedAt = user.getCreatedAt();
        int diffTimeCreated = diffTime(user.getCreatedAt());
        int diffTimeUpdated = diffTime(user.getUpdatedAt());

        logger("USER", "info");
        logger(user.toString(), "info");

        debugger("ACCOUNT EXPIRED START LOG", null, true);
        debugger("ACCOUNT CREATED AT", accountCreatedAt, false);
        debugger("DIFF TIME CREATED", diffTimeCreated, false);
        debugger("DIFF TIME UPDATED", diffTimeUpdated, false);
        debugger("ACCOUNT EXPIRED FINISH LOG", null, true);

        logger("ACCOUNT EXPIRED START LOG", "info");
        logger("ACCOUNT CREATED AT: " + accountCreatedAt, "info");
        logger("DIFF TIME CREATED: " + diffTimeCreated, "info");
        logger("DIFF TIME UPDATED: " + diffTimeUpdated, "info");
        logger("ACCOUNT EXPIRED FINISH LOG", "info");

        /*Check Expired Account Time*/
        if (diffTimeCreated > 0 && diffTimeUpdated == 0 || diffTimeCreated > 0 && diffTimeUpdated > 0) {
            debugger("ACCOUNT TIME EXPIRED", user.getToken(), true);
            logger("ACCOUNT TIME EXPIRED: " + user.getToken(), "info");
            return true;
        }

        debugger("ACCOUNT TIME IS OK", user.getToken(), true);
        logger("ACCOUNT TIME IS OK: " + user.getToken(), "info");
        return false;
    }

    public void logger(String msg, String label) {
        if (apiDocLogging) {
            switch (label) {
                case "debug": log.debug(msg); break;
                case "info": log.info(msg); break;
                case "warn": log.warn(msg); break;
                case "trace": log.trace(msg); break;
                default: log.error(msg);
            }
        }
    }

    public void debugger(String title, Object data, boolean line) {
        if (apiDocDebugger) {
            System.out.println(title);
            if (data != null && !data.equals("")) {
                System.out.println(data);
            }
            if (line) {
                for (int i = 0; i < 120; i++) System.out.print("-");
            }
            System.out.println("\n");
        }
    }

    public void doAuthorized(HttpSession session, String sessionId, boolean flag) {
        ApiDocProtectorDto sessionTransfer = (ApiDocProtectorDto) session.getAttribute(sessionId);
        sessionTransfer.setAuthorized(flag);
        session.setAttribute(sessionId, sessionTransfer);
    }

    public void doAuthenticated(HttpSession session, String sessionId, boolean flag) {
        ApiDocProtectorDto sessionTransfer = (ApiDocProtectorDto) session.getAttribute(sessionId);
        sessionTransfer.setAuthenticate(flag);
        session.setAttribute(sessionId, sessionTransfer);
    }

    public boolean loginChecker(String username, String password, String token) {

        try {

            if (username.equals("") || password.equals("")) {
                debugger("MISSING DATA TO LOGIN", "ERROR", true);
                return false;
            }

            String passwordCrypt = dataEncrypt(password);
            String tokenCrypt = dataEncrypt(token);
            ApiDocProtectorEntity login = apiDocProtectorRepository
                    .findByUsernameAndPasswordAndTokenAndActive(username, passwordCrypt, tokenCrypt, "yes");

            if (login != null && login.getUsername().equals(username)) {
                debugger("LOGIN SUCCESS: " + username, "INFO", true);
                auditor(LIBRARY_LOGIN_SUCCESSFUL, "Login successful to user " + username, null, 2);
                return true;
            }

            debugger("LOGIN FAILURE: " + login, "ERROR", true);
            auditor(LIBRARY_LOGIN_ERROR, "Login not ok to user " + username, null, 2);

            return false;

        } catch (RuntimeException re) {
            debugger("LOGIN FAILURE: " + re.getMessage(), "EXCEPTION", true);
            return false;
        }
    }

    public String userGenerator(Map<String, String> userBody) {

        try {

            String token = guide(null);
            String tokenCrypt = dataEncrypt(token);

            if (userBody.get("role") == null || userBody.get("role").equals("")) {
                userBody.put("role", "viewer");
            }

            if (
                    userBody.get("name") == null || userBody.get("name").equals("") ||
                    userBody.get("username") == null || userBody.get("username").equals("") ||
                    userBody.get("password") == null || userBody.get("password").equals("") ||
                    userBody.get("email") == null || userBody.get("email").equals("")
            ) {
                return "Missing data, check your request";
            }

            LocalDateTime dateTime = LocalDateTime.now();
            String currentDate = dateTime.format(FORMATTER);
            String passwordCrypt = dataEncrypt(userBody.get("password"));

            if (apiDocProtectorRepository.findByUsernameOrEmail(userBody.get("username"), userBody.get("email")) != null) {
                auditor(LIBRARY_GENERATOR_USER_ALREADY_EXISTS, userBody.get("username"), null, 2);
                return "User already exists";
            }

            ApiDocProtectorEntity newUser = new ApiDocProtectorEntity();
            newUser.setName(userBody.get("name"));
            newUser.setUsername(userBody.get("username"));
            newUser.setEmail(userBody.get("email"));
            newUser.setRole(userBody.get("role"));
            newUser.setPassword(passwordCrypt);
            newUser.setToken(tokenCrypt);
            newUser.setActive("no");
            newUser.setSessionKey(null);
            newUser.setSessionVal(null);
            newUser.setSessionCreatedAt(null);
            newUser.setCreatedAt(currentDate);
            newUser.setUpdatedAt(null);
            newUser.setDeletedAt(null);

            apiDocProtectorRepository.save(newUser);
            auditor(LIBRARY_GENERATOR_USER_SUCCESSFUL, "The user was created " + userBody.get("username"), null, 2);

            return token;

        } catch (RuntimeException re) {
            return "Exception, " + re.getMessage();
        }

    }

    public String userRecovery(Map<String, String> userBody, ApiDocProtectorEntity user) {

        try {

            String token = guide(null);
            String tokenCrypt = dataEncrypt(token);
            LocalDateTime dateTime = LocalDateTime.now();
            String currentDate = dateTime.format(FORMATTER);

            user.setToken(tokenCrypt);
            user.setActive("no");
            user.setSessionKey(null);
            user.setSessionVal(null);
            user.setSessionCreatedAt(null);
            user.setUpdatedAt(currentDate);

            apiDocProtectorRepository.save(user);
            auditor(LIBRARY_USER_RECOVERY_OK, user.getUsername(), null, 2);

            return token;

        } catch (RuntimeException re) {
            return "Exception, " + re.getMessage();
        }

    }

    public String userPasswordUpdate(Map<String, String> userBody, ApiDocProtectorEntity user) {

        try {

            String token = guide(null);
            String tokenCrypt = dataEncrypt(token);

            LocalDateTime dateTime = LocalDateTime.now();
            String currentDate = dateTime.format(FORMATTER);
            String passwordCrypt = dataEncrypt(userBody.get("password"));

            user.setToken(tokenCrypt);
            user.setPassword(passwordCrypt);
            user.setUpdatedAt(currentDate);

            apiDocProtectorRepository.save(user);
            auditor(LIBRARY_PASSWORD_UPDATED, user.getUsername(), null, 2);

            return token;

        } catch (Exception ex) {
            debugger("USER PASSWORD UPDATE [EXCEPTION]", ex.getMessage(), true);
            return null;
        }

    }

    public String dataEncrypt(String data) {
        if (data == null) return null;
        if (dataCryptTpe.equals("md5")) {
            return md5(data);
        } else if (dataCryptTpe.equals("bcrypt")) {
            return bcrypt(data);
        }
        return data;
    }

    public boolean findPrivilegedAdmin(String token) {
        String tokenCrypt = dataEncrypt(token);
        ApiDocProtectorEntity result = apiDocProtectorRepository.findByTokenAndRoleAndActive(tokenCrypt, "admin", "yes");
        return result != null && result.getToken().equals(tokenCrypt);
    }

    public ApiDocProtectorEntity findAccountByTokenAndActive(String tokenCrypt, String active) {
        return apiDocProtectorRepository.findByTokenAndActive(tokenCrypt, active);
    }

    public ApiDocProtectorEntity findDataSession(String keypart, String secret) {
        String sessionKey = md5(keypart + secret).toUpperCase();

        debugger("KEYPART FROM TRANSFER IN findDataSession", keypart, true);
        debugger("SESSION-KEY FROM TRANSFER IN findDataSession", sessionKey, true);
        auditor(LIBRARY_SESSION_FOUNDED, null, null, 2);

        return apiDocProtectorRepository.findBySessionKeyAndActive(sessionKey, "yes");
    }

    public String readFile(String filepath) {
        StringBuilder dataHtml = new StringBuilder();

        try {
            FileReader activateFile = null;
            try {
                activateFile = new FileReader(filepath);
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
            BufferedReader readActivateFile = new BufferedReader(activateFile);

            String lineFile = "";
            try {
                lineFile = readActivateFile.readLine();
                dataHtml = new StringBuilder(lineFile);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            while (lineFile != null) {
                dataHtml.append(lineFile);
                lineFile = readActivateFile.readLine();
            }

            activateFile.close();

        } catch (IOException e) {
            debugger("READ-FILE [EXCEPTION]", e.getMessage(), true);
        }

        return dataHtml.toString();
    }

    public void auditor(ApiDocProtectorAuditEnum auditEnum, String customMessage, String sessionId, int auditLevel) {

        if (apiDocAuditor && auditLevel <= apiDocAuditorLevel) {

            String username = null;
            String level = "auditor";
            String token = null;
            ApiDocProtectorAuditDto auditDto = new ApiDocProtectorAuditDto();

            if (sessionId != null && !sessionId.equals("")) {
                ApiDocProtectorDto sessionData = ((ApiDocProtectorDto) session.getAttribute(sessionId));
                username = sessionData.getUsername();
                token = sessionData.getToken();
            }

            if (session.getAttribute("APIDOC-AUDITOR") == null || session.getAttribute("APIDOC-AUDITOR").equals("")) {

                auditDto.setUsername(username);
                auditDto.setLevel(level);
                auditDto.setToken(token);
                auditDto.setDetail(auditEnum.name());
                auditDto.setIp(request.getRemoteAddr());

                if (customMessage == null) {
                    auditDto.setMessage(auditEnum.getMessage());
                } else {
                    auditDto.setMessage(customMessage);
                }

                auditDto.setTracker(guide(null));
                session.setAttribute("APIDOC-AUDITOR", auditDto);

                debugger("NEW SESSION APIDOC-AUDITOR", auditDto, true);

            } else {

                auditDto = ((ApiDocProtectorAuditDto) session.getAttribute("APIDOC-AUDITOR"));
                auditDto.setUsername(username);
                auditDto.setLevel(level);
                auditDto.setToken(token);
                auditDto.setDetail(auditEnum.name());
                auditDto.setIp(request.getRemoteAddr());

                if (customMessage == null) {
                    auditDto.setMessage(auditEnum.getMessage());
                } else {
                    auditDto.setMessage(customMessage);
                }

                debugger("EXISTS SESSION APIDOC-AUDITOR", auditDto, true);

            }

            if (token != null && session.getAttribute("APIDOC-GET-ROLE") == null) {
                ApiDocProtectorEntity user = apiDocProtectorRepository.findByToken(dataEncrypt(token));
                session.setAttribute("APIDOC-AUDITOR-GET-USERNAME", user.getUsername());
                session.setAttribute("APIDOC-AUDITOR-GET-ROLE", user.getRole());
                auditDto.setUsername(user.getUsername());
                auditDto.setLevel(user.getRole());
            } else if (session.getAttribute("APIDOC-GET-ROLE") != null) {
                auditDto.setUsername(session.getAttribute("APIDOC-AUDITOR-GET-USERNAME").toString());
                auditDto.setLevel(session.getAttribute("APIDOC-AUDITOR-GET-ROLE").toString());
            } else {
                auditDto.setLevel(level);
            }

            LocalDateTime dateTime = LocalDateTime.now();
            String currentDate = dateTime.format(FORMATTER);

            ApiDocProtectorAuditEntity apiDocProtectorAuditEntity = new ApiDocProtectorAuditEntity();
            apiDocProtectorAuditEntity.setUsername(auditDto.getUsername());
            apiDocProtectorAuditEntity.setLevel(auditDto.getLevel());
            apiDocProtectorAuditEntity.setToken(dataEncrypt(auditDto.getToken()));
            apiDocProtectorAuditEntity.setTracker(auditDto.getTracker());
            apiDocProtectorAuditEntity.setDetail(auditDto.getDetail());
            apiDocProtectorAuditEntity.setMessage(auditDto.getMessage());
            apiDocProtectorAuditEntity.setCode(auditEnum.getCode());
            apiDocProtectorAuditEntity.setIp(auditDto.getIp());
            apiDocProtectorAuditEntity.setCreatedAt(currentDate);

            debugger("ApiDocProtectorAuditEntity IS", apiDocProtectorAuditEntity, true);

            apiDocProtectorAuditRepository.save(apiDocProtectorAuditEntity);
            logger("Auditor on APIDOC PROTECTOR is ok", "info");
            logger(apiDocProtectorAuditEntity.toString(), "info");

        }

    }

}
