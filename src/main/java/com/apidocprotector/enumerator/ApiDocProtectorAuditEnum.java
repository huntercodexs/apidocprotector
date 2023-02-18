package com.apidocprotector.enumerator;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum ApiDocProtectorAuditEnum {

    GENERATOR_STARTED(1, "The GENERATOR form to new account has been started"),
    GENERATOR_GLASS_FORWARD(2, "forward: forwardToGeneratorGlass was running"),
    GENERATOR_GLASS_STARTED(3, "The GENERATOR glass has been started"),
    GENERATOR_EXCEPTION(5, "custom message"),
    GENERATOR_GLASS_EXCEPTION(6, "custom message"),
    GENERATOR_FORM_STARTED(7, "The GENERATOR form to new account has been loaded"),
    GENERATOR_FORM_INVALID_ACCCESS(8, "The APD-USER-GENERATOR session was not ok"),
    GENERATOR_VIEW_FORM(9, "View Form was called by GENERATOR"),
    GENERATOR_ACCOUNT_CREATED(10, "An account was created from GENERATOR"),
    GENERATOR_DATA_POST(11, "Was posted to create a new account from GENERATOR"),
    GENERATOR_FORM_INVALID_SESSION(12, "Invalid session to create a new acccount"),
    GENERATOR_FORM_USER_ALREADY_EXISTS(13, "The user or email already exists"),
    GENERATOR_FORM_USER_CREATED(14, "The user was created successful"),
    GENERATOR_MAIL_SENDER_OK(15, "The mail has been sended to user"),
    GENERATOR_FORM_REDIRECT(16, "redirect: redirecting to form was running"),

    ACTIVATOR_STARTED(17, "The ACTIVATOR account has been started"),
    ACTIVATOR_TOKEN_OK(18, "The current token was ok"),
    ACTIVATOR_MAIL_SUCCESSFUL(19, "An email has been sent to current user account"),
    ACTIVATOR_FINISHED(20, "The ACTIVATOR finished successful"),
    ACTIVATOR_EXPIRED_ACCOUNT(21, "Expired Account Token"),
    ACTIVATOR_ACCOUNT_ALREADY_ACTIVATED(22, "The account was already activated"),
    ACTIVATOR_ACCOUNT_NOT_FOUND(23, "custom message"),

    INITIALIZER_STARTED(24, "The INITIALIZER has been started"),
    INITIALIZER_TOKEN_OK(25, "custom message"),
    INITIALIZER_ERROR(26, "The current user is not activated"),
    INITIALIZER_ENVIRONMENT_OK(27, "The environment has been inialized"),
    INITIALIZER_SESSION_PREPARE_OK(27, "The session has been configured"),
    INITIALIZER_GLASS_STARTED(28, "The INITIALIZER glass was called"),
    INITIALIZER_EXCEPTION(29, "custom message"),
    INITIALIZER_DENIED(30, "Access denied, missing token"),
    INITIALIZER_BURNED(31, "The INITIALIZER was burned"),
    INITIALIZER_SUCCESSFUL(32, "The INITIALIZER has been finished"),
    INITIALIZER_SESSION_NOT_FOUND(33, "The session was not found to INITIALIZER"),
    INITIALIZER_FORM_STARTED(34, "The INITIALIZER form was called"),

    PROTECTOR_GLASS_FORWARD(10000, "forward: forwardToGlass was called"),
    GENERIC_MESSAGE(20000, ""),
    SESSION_PREPARED_OK(20001, "Session created successful"),
    SESSION_FOUNDED(20001, "Session founded successful"),
    VIEW_ERROR(50000, "");

    public int code;
    public String message;

}
