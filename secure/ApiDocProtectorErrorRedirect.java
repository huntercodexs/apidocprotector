package com.apidocprotector.secure;

import org.springframework.stereotype.Service;

@Service
public class ApiDocProtectorErrorRedirect {

    public String redirectGeneratorError(String data) {
        return "redirect:/doc-protect/generator/error/"+data;
    }

    public String redirectRecoveryError(String data) {
        return "redirect:/doc-protect/recovery/error/"+data;
    }

    public String redirectPasswordError(String data) {
        return "redirect:/doc-protect/password/error/"+data;
    }

    public String redirectPasswordRecoveryError(String data) {
        return "redirect:/doc-protect/password/recovery/error/"+data;
    }

    public String redirectInitializerError(String data) {
        return "redirect:/doc-protect/initializer/error/"+data;
    }

    public String forwardSentinelError(String error) {
        return "forward:/doc-protect/sentinel/error/"+error.replaceAll(" ", "_");
    }

    public String redirectSentinelError(String data) {
        return "redirect:/doc-protect/sentinel/error/"+data;
    }

    public String redirectLoginError(String username) {
        return "redirect:/doc-protect/login/error/"+username;
    }

}
