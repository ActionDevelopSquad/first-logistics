package com.firstlogistics.companyservice.presentation.dto.response;

import com.firstlogistics.companyservice.application.dto.result.CompanyResult;
import java.util.UUID;

public record CreateCompanyResponse(
        UUID companyId
) {

    public static CreateCompanyResponse from(CompanyResult result) {
        return new CreateCompanyResponse(result.id());
    }
}
