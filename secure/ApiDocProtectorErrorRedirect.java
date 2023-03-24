package com.apidocprotector.secure;

import org.springframework.stereotype.Service;

@Service
public class ApiDocProtectorErrorRedirect {

    public String redirectError(String data) {
        return "redirect:/doc-protect/redirect/error/"+data;
    }

}
