package com.firstlogistics.hubservice.hubconnection.domain.vo;

import com.firstlogistics.hubservice.hubconnection.domain.exception.HubConnectionErrorCode;
import com.firstlogistics.hubservice.hubconnection.domain.exception.HubConnectionException;

import java.util.UUID;

public record CompanyId(
        UUID id
) {
    public CompanyId{
        if(id== null)
            throw new HubConnectionException(HubConnectionErrorCode.INVALID_COMPANY_ID);
    }

    public static CompanyId of(UUID id){
        return new CompanyId(id);
    }

}
