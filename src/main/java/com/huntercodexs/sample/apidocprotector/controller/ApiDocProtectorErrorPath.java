package com.huntercodexs.sample.apidocprotector.controller;

import com.huntercodexs.sample.apidocprotector.library.ApiDocProtectorLibrary;
import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Hidden
@Controller
public class ApiDocProtectorErrorPath extends ApiDocProtectorLibrary implements ErrorController {
    @Override
    @RequestMapping(path = "/error")
    public String getErrorPath() {
        return apiDocProtectorErrorRedirect.forwardSentinelError("No Mapping Found");
    }
}

