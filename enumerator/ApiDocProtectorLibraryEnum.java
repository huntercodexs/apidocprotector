package com.apidocprotector.enumerator;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum ApiDocProtectorLibraryEnum {
    BURN_ERROR(
            HttpStatus.BAD_REQUEST,
            10000,
            "Process error"),

    BURN_EXCEPTION(
            HttpStatus.INTERNAL_SERVER_ERROR,
            10001,
            "Process error"),

    INITIALIZER_ERROR(
            HttpStatus.BAD_REQUEST,
            10002,
            "Initial process error"),

    INVALID_LOGIN(
            HttpStatus.UNAUTHORIZED,
            10003,
            "Invalid Login"),

    INVALID_ACCESS(
            HttpStatus.NOT_ACCEPTABLE,
            10004,
            "Invalid Access"),

    PROTECTOR_ERROR(
            HttpStatus.BAD_REQUEST,
            10005,
            "Protector found an error"),

    FORM_VIEW_ERROR(
            HttpStatus.INTERNAL_SERVER_ERROR,
            10006,
            "Protector type"),

    INVALID_PROTECTOR(
            HttpStatus.INTERNAL_SERVER_ERROR,
            10007,
            "Error on application, invalid Protector Type"),

    SENTINEL_ERROR(
            HttpStatus.INTERNAL_SERVER_ERROR,
            10008,
            "Request error"),

    REFRESH_ERROR(
            HttpStatus.BAD_REQUEST,
            10009,
            "Refresh page found an error"),

    FORM_ERROR(
            HttpStatus.UNAUTHORIZED,
            10010,
            "Form page found an error"),

    EXPIRED_SESSION(
            HttpStatus.REQUEST_TIMEOUT,
            10011,
            "Request timeout"),

    GENERATOR_ERROR(
            HttpStatus.INTERNAL_SERVER_ERROR,
            10012,
            "Generator process error"),

    GENERATOR_UNAUTHORIZED(
            HttpStatus.UNAUTHORIZED,
            10013,
            "Generator process error"),

    INVALID_SESSION(
            HttpStatus.REQUEST_TIMEOUT,
            10014,
            "Access Violation"),

    PASSWORD_ERROR(
            HttpStatus.INTERNAL_SERVER_ERROR,
            10015,
            "Password process error"),

    PASSWORD_RECOVERY_ERROR(
            HttpStatus.INTERNAL_SERVER_ERROR,
            10016,
            "Password Recovery process error"),

    RECOVERY_ERROR(
            HttpStatus.INTERNAL_SERVER_ERROR,
            10017,
            "Recovery process error"),

    SWAGGER_ROUTE_ERROR(
            HttpStatus.INTERNAL_SERVER_ERROR,
            10017,
            "Swagger Route process error"),

    UNAUTHORIZED_FIREWALL(
            HttpStatus.UNAUTHORIZED,
            50001,
            "Unauthorized"),

    NOT_ACCEPTED_FIREWALL(
            HttpStatus.NOT_ACCEPTABLE,
            50002,
            "Not Accepted"),

    WRONG_REQUEST_FIREWALL(
            HttpStatus.NOT_ACCEPTABLE,
            50003,
            "Not Accepted"),

    INVALID_FORM_FIREWALL(
            HttpStatus.BAD_REQUEST,
            50004,
            "Invalid Form"),

    GENERIC_ERROR(
            HttpStatus.INTERNAL_SERVER_ERROR,
            99999,
                    "Occurs an error"),;

    public HttpStatus statusCode;
    public int errorCode;
    public String message;

}
