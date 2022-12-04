package com.huntercodexs.sample.apidocprotector.rule;

import org.springframework.stereotype.Service;

@Service
public class ApiDocProtectorErrorRedirect {

    public String generatorError(String data) {
        return "redirect:/doc-protect/generator/error/"+data;
    }

    public String initializerError(String data) {
        return "redirect:/doc-protect/initializer/error/"+data;
    }

    public String sentinelError(String error) {
        return "forward:/doc-protect/sentinel/error/"+error.replaceAll(" ", "_");
    }

    public String loginError(String username) {
        return "redirect:/doc-protect/login/error/"+username;
    }

}
