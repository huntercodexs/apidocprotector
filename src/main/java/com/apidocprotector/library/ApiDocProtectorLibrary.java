package com.apidocprotector.library;

import com.apidocprotector.dto.ApiDocProtectorDto;
import com.apidocprotector.model.ApiDocProtectorEntity;
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

@Slf4j
@Service
public abstract class ApiDocProtectorLibrary extends ApiDocProtectorDataLibrary {

    protected ApiDocProtectorDto transfer;

    protected static final boolean APIDOC_PROTECTOR_DEBUG = true;

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
        transferDto.setSecret(guide());
        transferDto.setUsername(null);
        transferDto.setPassword(null);
        transferDto.setAuthorized(true);
        transferDto.setAuthenticate(false);
        transferDto.setKeypart(keypartVal);

        logTerm("KEYPART-VAL", keypartVal, true);

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
        logTerm("SESSION CREATED", session.getAttribute(sessionVal), true);
    }

    public void sessionRenew(ApiDocProtectorEntity result, LocalDateTime dateTimeNow) {
        result.setSessionCreatedAt(dateTimeNow.format(FORMATTER));
        apiDocProtectorRepository.save(result);
    }

    public boolean sessionExpired(String token) {

        /*Control Session is disabled*/
        if (expireTimeSession == 0) {
            logTerm("CONTROL EXPIRED SESSION IS DISABLE", expireTimeSession, true);
            return false;
        }

        /*Default/Minimum Time*/
        if (expireTimeSession < 1) {
            expireTimeSession = 1;
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

        logTerm("SESSION EXPIRED START LOG", null, true);
        logTerm("SESSION CREATED AT", sessionCreatedAt, false);
        logTerm("DATE TIME NOW", dateTimeNow, false);
        logTerm("DATE TIME NOW FORMATTER", dateTimeFormatter, false);
        logTerm("SESSION TIME PLUS", sessionTimePlus, false);
        logTerm("SESSION TIME PLUS FORMATTER", sessionTimePlus.format(FORMATTER), false);
        logTerm("DIFF TIME", diffTime, false);
        logTerm("EXPIRED TIME SESSION IS", expireTimeSession, false);
        logTerm("SESSION EXPIRED FINISH LOG", null, true);

        /*Check Expired Session*/
        if (diffTime > 0) {
            sessionRenew(result, dateTimeNow);
            logTerm("SESSION EXPIRED", result.getSessionVal(), true);
            return true;
        }

        sessionRenew(result, dateTimeNow);
        logTerm("SESSION REFRESHED", result.getSessionVal(), true);

        return false;
    }

    public boolean alreadyActivated(String clearToken) {
        String tokenCrypt = dataEncrypt(clearToken);
        ApiDocProtectorEntity result = apiDocProtectorRepository.findByTokenAndActive(tokenCrypt, "yes");
        if (result != null && result.getToken().equals(tokenCrypt)) {
            logTerm("ACCOUNT ALREADY ACTIVATED", clearToken, true);
            return true;
        }
        logTerm("ACCOUNT IS NOT ACTIVATED YET", clearToken, true);
        return false;
    }

    private int diffTime(String userDate) {

        LocalDateTime dateTimeNow = LocalDateTime.now();
        String dateTimeFormat = dateTimeNow.format(FORMATTER);
        LocalDateTime dateTimeFormatter = LocalDateTime.parse(dateTimeFormat, FORMATTER);

        try {
            LocalDateTime dateTimeAccount = LocalDateTime.parse(userDate, FORMATTER);
            LocalDateTime accountTimePlus = dateTimeAccount.plusMinutes(expireTimeEmail);

            logTerm("DATE TIME NOW", dateTimeNow, false);
            logTerm("DATE TIME NOW FORMATTER", dateTimeFormatter, false);
            logTerm("ACCOUNT TIME PLUS", accountTimePlus, false);
            logTerm("ACCOUNT TIME PLUS FORMATTER", accountTimePlus.format(FORMATTER), false);
            logTerm("EXPIRED TIME ACCOUNT IS", expireTimeEmail, false);

            return dateTimeFormatter.compareTo(accountTimePlus);
        } catch (Exception ex) {
            logTerm("DIFF TIME ACCOUNT[EXCEPTION]", ex.getMessage(), true);
            return 0;
        }
    }

    public boolean activateExpired(String tokenClear) {

        /*Default/Minimum Time to Activate Expire*/
        if (expireTimeEmail < 1) {
            expireTimeEmail = 1;
        }

        String tokenCrypt = dataEncrypt(tokenClear);
        ApiDocProtectorEntity user = apiDocProtectorRepository.findByTokenAndActive(tokenCrypt, "no");
        String accountCreatedAt = user.getCreatedAt();
        int diffTimeCreated = diffTime(user.getCreatedAt());
        int diffTimeUpdated = diffTime(user.getUpdatedAt());

        logTerm("ACCOUNT EXPIRED START LOG", null, true);
        logTerm("ACCOUNT CREATED AT", accountCreatedAt, false);
        logTerm("DIFF TIME CREATED", diffTimeCreated, false);
        logTerm("DIFF TIME UPDATED", diffTimeUpdated, false);
        logTerm("ACCOUNT EXPIRED FINISH LOG", null, true);

        /*Check Expired Account Time*/
        if (diffTimeCreated > 0 && diffTimeUpdated > 0) {
            logTerm("ACCOUNT TIME EXPIRED", user.getToken(), true);
            return true;
        }

        logTerm("ACCOUNT TIME IS OK", user.getToken(), true);

        return false;
    }

    public void logFile(String msg, String label) {
        if (APIDOC_PROTECTOR_DEBUG) {
            switch (label) {
                case "debug":
                    log.debug(msg);
                    break;
                case "info":
                    log.info(msg);
                    break;
                case "warn":
                    log.warn(msg);
                    break;
                case "except":
                    log.error(msg);
                    break;
            }
        }
    }

    public void logTerm(String title, Object data, boolean line) {
        if (APIDOC_PROTECTOR_DEBUG) {
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
                logTerm("MISSING DATA TO LOGIN", "ERROR", true);
                return false;
            }

            String passwordCrypt = dataEncrypt(password);
            String tokenCrypt = dataEncrypt(token);
            ApiDocProtectorEntity login = apiDocProtectorRepository
                    .findByUsernameAndPasswordAndTokenAndActive(username, passwordCrypt, tokenCrypt, "yes");

            if (login != null && login.getUsername().equals(username)) {
                logTerm("LOGIN SUCCESS: " + username, "INFO", true);
                return true;
            }

            logTerm("LOGIN FAILURE: " + login, "ERROR", true);
            return false;

        } catch (RuntimeException re) {
            logTerm("LOGIN FAILURE: " + re.getMessage(), "EXCEPTION", true);
            return false;
        }
    }

    public String userGenerator(Map<String, String> userBody) {

        try {

            String token = guide();
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

            return token;

        } catch (RuntimeException re) {
            return "Exception, " + re.getMessage();
        }

    }

    public String userRecovery(Map<String, String> userBody, ApiDocProtectorEntity user) {

        try {

            String token = guide();
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

            return token;

        } catch (RuntimeException re) {
            return "Exception, " + re.getMessage();
        }

    }

    public String userPasswordUpdate(Map<String, String> userBody, ApiDocProtectorEntity user) {

        try {

            String token = guide();
            String tokenCrypt = dataEncrypt(token);

            LocalDateTime dateTime = LocalDateTime.now();
            String currentDate = dateTime.format(FORMATTER);
            String passwordCrypt = dataEncrypt(userBody.get("password"));

            user.setToken(tokenCrypt);
            user.setPassword(passwordCrypt);
            user.setUpdatedAt(currentDate);

            apiDocProtectorRepository.save(user);

            return token;

        } catch (Exception ex) {
            logTerm("USER PASSWORD UPDATE [EXCEPTION]", ex.getMessage(), true);
            return null;
        }

    }

    public String dataEncrypt(String data) {
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

        logTerm("KEYPART FROM TRANSFER IN findDataSession", keypart, true);
        logTerm("SESSION-KEY FROM TRANSFER IN findDataSession", sessionKey, true);

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
            logTerm("READ-FILE [EXCEPTION]", e.getMessage(), true);
        }

        return dataHtml.toString();
    }

}
