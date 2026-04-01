package com.firstlogistics.companyservice.domain.event;

import com.firstlogistics.companyservice.domain.exception.CompanyErrorCode;
import com.firstlogistics.companyservice.domain.exception.CompanyException;
import java.util.UUID;

public record CompanyCreatedEvent(

        UUID companyId,
        String companyName
) {

    public CompanyCreatedEvent {
        if (companyId == null) {
            throw new CompanyException(CompanyErrorCode.INVALID_COMPANY_ID);
        }
    }
}
