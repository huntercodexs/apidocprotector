package com.apidocprotector.library;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Base64;
import java.util.UUID;

@Service
public abstract class ApiDocProtectorDataLibrary {

    public Cookie cookie(HttpServletRequest request, String cookieName) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals( cookieName ))
                    return cookie;
            }
        }
        return new Cookie("COOKIE-NOT-FOUND", null);
    }

    public String now() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm:ss");
        LocalDateTime dateTime = LocalDateTime.now();
        return dateTime.format(formatter);
    }

    public String guide(String guide) {
        if (guide == null) {
            return UUID.randomUUID().toString();
        }
        return guide;
    }

    public String md5(String data) {
        return DigestUtils.md5DigestAsHex(data.getBytes());
    }

    public String bcrypt(String data) {
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        return bCryptPasswordEncoder.encode(data);
    }

    public String base64Encode(String input) {
        return Base64.getEncoder().encodeToString(input.getBytes());
    }

    public String base64Decode(String input) {
        byte[] result = Base64.getDecoder().decode(input);
        return new String(result);
    }

}
