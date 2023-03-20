package com.apidocprotector.config.crypt;

import com.apidocprotector.secure.ApiDocProtectorCrypt;

import java.util.Base64;

import static org.apache.commons.lang3.StringUtils.reverse;

public class ApiDocProtectorCustomCrypt implements ApiDocProtectorCrypt {

    @Override
    public String customEncrypt(String input) {
        String data = Base64.getEncoder().encodeToString(input.getBytes());
        String full = reverse(data)+reverse(data+data);
        return Base64.getEncoder().encodeToString(full.getBytes());
    }

    @Override
    public String customDecrypt(String output) {
        //TODO: Change this function to correct result
        byte[] decode64 = Base64.getDecoder().decode(output);
        String data = Base64.getEncoder().encodeToString(output.getBytes());
        String full = reverse(data)+reverse(data+data);
        return Base64.getEncoder().encodeToString(full.getBytes());
    }
}
