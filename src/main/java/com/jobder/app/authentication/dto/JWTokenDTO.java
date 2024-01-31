package com.jobder.app.authentication.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JWTokenDTO {
    private String accessToken;
    private String role;
    private String refreshToken;
}