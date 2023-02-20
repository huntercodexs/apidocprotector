package com.apidocprotector.dto;

import io.swagger.v3.oas.annotations.Hidden;
import lombok.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Hidden
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ApiDocProtectorDto {
    String id;
    HttpServletRequest request;
    HttpServletResponse response;
    String origin;
    String token;
    String secret;
    String keypart;
    String username;
    String level;
    String password;
    Boolean authorized;
    Boolean authenticate;
}
