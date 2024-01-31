package com.jobder.app.authentication.models;

import com.jobder.app.authentication.dto.ClientDTO;
import com.jobder.app.authentication.dto.WorkerDTO;
import lombok.*;
import org.springframework.data.annotation.Id;
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
    private String email;
    private String password;
    private String picture;
    private RoleName role;

    //Common client and worker
    private String phoneNumber;
    private String address;
    private String latitude;
    private String longitude;
    private Date birthDate;

    //Worker
    private String workSpecialization;
    private String availabilityStatus;

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
        WorkerDTO toReturn = new WorkerDTO(this.id,this.name,this.email,this.picture,this.phoneNumber,this.address,this.latitude,this.longitude,this.birthDate,this.workSpecialization,this.availabilityStatus);

        return toReturn;
    }

    public ClientDTO toClient(){
        ClientDTO toReturn = new ClientDTO(this.id,this.name,this.email,this.picture,this.phoneNumber,this.address,this.latitude,this.longitude,this.birthDate);

        return toReturn;
    }
}