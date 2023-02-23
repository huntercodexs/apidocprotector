package com.apidocprotector.controller;

import com.apidocprotector.library.ApiDocProtectorLibrary;
import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import static com.apidocprotector.enumerator.ApiDocProtectorRegisterEnum.NO_MAPPING_FOUND;

@Hidden
@Controller
public class ApiDocProtectorErrorPath extends ApiDocProtectorLibrary implements ErrorController {
    @Override
    @RequestMapping(path = "/error")
    public String getErrorPath() {
        register(NO_MAPPING_FOUND, null, "warn", 0, "No mapping found");
        return apiDocProtectorErrorRedirect.forwardSentinelError("No Mapping Found");
    }
}

