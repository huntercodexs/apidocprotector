package com.apidocprotector.library;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
        PasswordEncoder encoder = new BCryptPasswordEncoder();
        return encoder.encode(data);
    }

    public String base64Encode(String input) {
        return Base64.getEncoder().encodeToString(input.getBytes());
    }

    public String base64Decode(String input) {
        byte[] result = Base64.getDecoder().decode(input);
        return new String(result);
    }

    public boolean passwordCheck(String passwordRequest, String passwordDatabase, String alg) {
        switch (alg) {
            case "md5":
                return md5(passwordDatabase).equals(passwordDatabase);
            case "bcrypt":
                PasswordEncoder encoder = new BCryptPasswordEncoder();
                return encoder.matches(passwordRequest, passwordDatabase);
            case "custom": //TODO
                return true;
            default:
                return false;
        }
    }

    public static boolean cpfValidator(String cpf) {

        if (cpf.length() > 11) return false;

        cpf = cpf.replace(".", "");
        cpf = cpf.replace("-", "");

        try {
            Long.parseLong(cpf);
        } catch(NumberFormatException e){
            return false;
        }

        int d1, d2;
        int digit1, digit2, rest;
        int cpfDigit;
        String nDigResult;

        d1 = d2 = 0;
        digit1 = digit2 = rest = 0;

        for (int nCount = 1; nCount < cpf.length() - 1; nCount++) {
            cpfDigit = Integer.valueOf(cpf.substring(nCount - 1, nCount)).intValue();
            d1 = d1 + (11 - nCount) * cpfDigit;
            d2 = d2 + (12 - nCount) * cpfDigit;
        };

        rest = (d1 % 11);

        if (rest < 2)
            digit1 = 0;
        else
            digit1 = 11 - rest;

        d2 += 2 * digit1;

        rest = (d2 % 11);

        if (rest < 2)
            digit2 = 0;
        else
            digit2 = 11 - rest;

        String digitVerify = cpf.substring(cpf.length() - 2, cpf.length());

        nDigResult = String.valueOf(digit1) + String.valueOf(digit2);

        return digitVerify.equals(nDigResult);
    }

    public static boolean mailValidator(String email) {
        boolean isValidMail = false;
        if (email != null && email.length() > 0) {
            String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[a-zA-Z]{2,4}$";
            Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(email);
            if (matcher.matches()) {
                isValidMail = true;
            }
        }
        return isValidMail;
    }

    public static boolean phoneValidator(String phoneNumber) {
        boolean isValidPhone = false;
        if (phoneNumber != null && phoneNumber.length() > 0) {
            String expression = "^[0-9]{13}$";
            Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(phoneNumber);
            if (matcher.matches()) {
                isValidPhone = true;
            }
        }
        return isValidPhone;
    }

}
