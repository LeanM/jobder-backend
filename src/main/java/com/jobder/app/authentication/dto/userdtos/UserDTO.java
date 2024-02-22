package com.jobder.app.authentication.dto.userdtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {
    private String id;
    private String name;
    private String email;
    private String picture;
    private String phoneNumber;
    private String address;
    private Double latitude;
    private Double longitude;
    @JsonFormat(pattern="yyyy-MM-dd")
    private Date birthDate;

    private Boolean isGoogleUser;

    public UserDTO(CommonDataDTO commonDataDTO) {
        this.id = commonDataDTO.getId();
        this.name = commonDataDTO.getName();
        this.email = commonDataDTO.getEmail();
        this.picture = commonDataDTO.getPicture();
        this.phoneNumber = commonDataDTO.getPhoneNumber();
        this.address = commonDataDTO.getAddress();
        this.latitude = commonDataDTO.getLatitude();
        this.longitude = commonDataDTO.getLongitude();
        this.birthDate = commonDataDTO.getBirthDate();
        this.isGoogleUser = commonDataDTO.getIsGoogleUser();
    }
}
