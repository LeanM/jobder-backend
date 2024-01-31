package com.jobder.app.authentication.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserGoogleDTO {
    private String name;
    private String picture;
    private String email;
}
