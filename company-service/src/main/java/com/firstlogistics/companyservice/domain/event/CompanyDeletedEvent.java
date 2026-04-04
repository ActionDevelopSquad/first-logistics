package com.firstlogistics.companyservice.domain.event;

import com.firstlogistics.companyservice.domain.exception.CompanyErrorCode;
import com.firstlogistics.companyservice.domain.exception.CompanyException;
import java.util.UUID;

public record CompanyDeletedEvent(

        UUID companyId
) {

    public CompanyDeletedEvent {
        if (companyId == null) {
            throw new CompanyException(CompanyErrorCode.INVALID_COMPANY_ID);
        }
    }
}
