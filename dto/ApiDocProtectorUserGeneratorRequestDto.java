package com.apidocprotector.dto;

import io.swagger.v3.oas.annotations.Hidden;
import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Hidden
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ApiDocProtectorUserGeneratorRequestDto {
    @NotBlank @NotEmpty @NotNull
    String name;
    @NotBlank @NotEmpty @NotNull
    String username;
    @NotBlank @NotEmpty @NotNull
    String email;
    @NotBlank @NotEmpty @NotNull
    String password;
    String role;
}
