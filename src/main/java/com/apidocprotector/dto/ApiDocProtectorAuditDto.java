package com.apidocprotector.dto;

import io.swagger.v3.oas.annotations.Hidden;
import lombok.*;

@Hidden
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ApiDocProtectorAuditDto {
    String username;
    String level;
    String token;
    String detail;
    String message;
    String tracker;
    String ip;
}
