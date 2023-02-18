package com.apidocprotector.enumerator;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum ApiDocProtectorAuditEnum {

    GENERATOR_STARTED(1, "The generator form to new account has been started"),
    GENERATOR_GLASS_FORWARD(2, "forward: forwardToGeneratorGlass was running"),
    GENERATOR_GLASS_STARTED(3, "The generator glass has been started"),
    GENERATOR_EXCEPTION(5, "custom message"),
    GENERATOR_GLASS_EXCEPTION(6, "custom message"),
    GENERATOR_FORM_STARTED(7, "The generator form to new account has been loaded"),
    GENERATOR_FORM_INVALID_ACCCESS(8, "The APD-USER-GENERATOR session was not ok"),
    GENERATOR_VIEW_FORM(9, "View Form was called by GENERATOR"),
    GENERATOR_ACCOUNT_CREATED(10, "An account was created from GENERATOR"),
    GENERATOR_DATA_POST(11, "Was posted to create a new account from GENERATOR"),
    GENERATOR_FORM_INVALID_SESSION(12, "Invalid session to create a new acccount"),
    GENERATOR_FORM_USER_ALREADY_EXISTS(13, "The user or email already exists"),
    GENERATOR_FORM_USER_CREATED(14, "The user was created successful"),
    GENERATOR_MAIL_SENDER_OK(15, "The mail has been sended to user"),
    GENERATOR_FORM_REDIRECT(16, "redirect: redirecting to form was running"),

    GENERIC_MESSAGE(20000, ""),
    VIEW_ERROR(50000, "");

    public int code;
    public String message;

}
