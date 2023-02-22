package com.apidocprotector.controller;

import com.apidocprotector.enumerator.ApiDocProtectorAuditEnum;
import com.apidocprotector.library.ApiDocProtectorLibrary;
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

        debugger("ERROR PATH", "No mapping found", true);
        logger("No mapping found", "warn");
        auditor(ApiDocProtectorAuditEnum.NO_MAPPING_FOUND, null, null, 0);

        return apiDocProtectorErrorRedirect.forwardSentinelError("No Mapping Found");
    }
}

