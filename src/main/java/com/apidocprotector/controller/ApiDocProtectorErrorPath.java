package com.apidocprotector.controller;

import com.apidocprotector.library.ApiDocProtectorLibrary;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import static com.apidocprotector.enumerator.ApiDocProtectorLibraryEnum.GENERIC_ERROR;
import static com.apidocprotector.enumerator.ApiDocProtectorRegisterEnum.GENERIC_MESSAGE;
import static com.apidocprotector.enumerator.ApiDocProtectorRegisterEnum.NO_MAPPING_FOUND;

@Hidden
@Controller
public class ApiDocProtectorErrorPath extends ApiDocProtectorLibrary implements ErrorController {

    @Override
    @RequestMapping(path = "/error")
    public String getErrorPath() {
        register(NO_MAPPING_FOUND, null, "warn", 0, "No mapping found");
        return apiDocProtectorErrorRedirect.redirectError(base64Encode("No Mapping Found"));
    }

    @Operation(hidden = true)
    @GetMapping(path = "/doc-protect/redirect/error/{data}")
    public ModelAndView error(@PathVariable(value = "data", required = false) String data) {

        register(GENERIC_MESSAGE, null, "error", 1, data);

        return apiDocProtectorViewer.error(
                GENERIC_MESSAGE.getMessage(),
                data,
                GENERIC_ERROR.getStatusCode());
    }

}

