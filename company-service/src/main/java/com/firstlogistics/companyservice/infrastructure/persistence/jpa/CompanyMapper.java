package com.firstlogistics.companyservice.infrastructure.persistence.jpa;

import com.firstlogistics.companyservice.domain.entity.Company;
import com.firstlogistics.companyservice.domain.entity.CompanyType;
import com.firstlogistics.companyservice.domain.entity.Receiver;
import com.firstlogistics.companyservice.domain.entity.Supplier;
import com.firstlogistics.companyservice.domain.exception.CompanyErrorCode;
import com.firstlogistics.companyservice.domain.exception.CompanyException;
import com.firstlogistics.companyservice.domain.vo.CompanyAddress;
import com.firstlogistics.companyservice.domain.vo.GeoLocation;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class CompanyMapper {

    public static CompanyJpaEntity toJpaEntity(Company company) {
        String typeStr = switch (company.getType()) {
            case Supplier s -> "SUPPLIER";
            case Receiver r -> "RECEIVER";
        };

        return new CompanyJpaEntity(
                company.getId(),
                company.getHubId(),
                company.getManagerId(),
                company.getName(),
                company.getStatus(),
                typeStr,
                company.getAddress().roadAddress(),
                company.getAddress().detailAddress(),
                company.getGeoLocation().latitude(),
                company.getGeoLocation().longitude()
        );
    }

    public static Company toDomain(CompanyJpaEntity entity) {
        CompanyType companyType = switch (entity.getType().toUpperCase()) {
            case "SUPPLIER" -> new Supplier();
            case "RECEIVER" -> new Receiver();
            default -> throw new CompanyException(CompanyErrorCode.INVALID_COMPANY_TYPE);
        };

        return Company.reconstitute(
                entity.getId(),
                entity.getHubId(),
                entity.getManagerId(),
                entity.getName(),
                companyType,
                entity.getStatus(),
                CompanyAddress.of(entity.getRoadAddress(), entity.getDetailAddress()),
                GeoLocation.of(entity.getLatitude(), entity.getLongitude())
        );
    }
}
