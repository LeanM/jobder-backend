package com.jobder.app.authentication.dto.userdtos;

import com.jobder.app.authentication.models.SearchParameters;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Date;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClientDTO extends UserDTO{
    private SearchParameters searchParameters;

    public ClientDTO(CommonDataDTO commonDataDTO, SearchParameters searchParameters){
        super(commonDataDTO);
        this.searchParameters = searchParameters;
    }
}
