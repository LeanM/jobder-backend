package com.jobder.app.authentication.models.users;

import com.jobder.app.authentication.dto.userdtos.ClientDTO;
import com.jobder.app.authentication.dto.userdtos.CommonDataDTO;
import com.jobder.app.authentication.dto.userdtos.WorkerDTO;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Document
public class User implements UserDetails {
    @Id
    private String id;
    private String name;
    @Indexed(unique = true)
    private String email;
    private String password;
    private String picture;
    private RoleName role;
    private String phoneNumber;
    private String address;
    private Double latitude;
    private Double longitude;
    private Date birthDate;

    private Boolean isGoogleUser;

    //Worker
    private String workSpecialization;
    private AvailabilityStatus availabilityStatus;
    private String description;
    private String workingHours;
    private String averageRating;
    private Integer worksFinished;
    private Integer totalReviews;

    //Client
    private SearchParameters searchParameters;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public WorkerDTO toWorker(){
        CommonDataDTO commonDataDTO = new CommonDataDTO(this.id,this.name,this.email,this.picture,this.phoneNumber,this.address,this.latitude,this.longitude,this.birthDate, this.isGoogleUser);
        WorkerDTO toReturn = new WorkerDTO(commonDataDTO,this.workSpecialization,this.availabilityStatus,this.description,this.workingHours,this.averageRating, this.worksFinished, this.totalReviews);

        return toReturn;
    }

    public ClientDTO toClient(){
        CommonDataDTO commonDataDTO = new CommonDataDTO(this.id,this.name,this.email,this.picture,this.phoneNumber,this.address,this.latitude,this.longitude,this.birthDate, this.isGoogleUser);
        ClientDTO toReturn = new ClientDTO(commonDataDTO, this.searchParameters);

        return toReturn;
    }
}