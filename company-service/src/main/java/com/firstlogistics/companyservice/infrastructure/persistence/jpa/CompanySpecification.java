package com.firstlogistics.companyservice.infrastructure.persistence.jpa;

import static com.firstlogistics.companyservice.infrastructure.persistence.jpa.QCompanyJpaEntity.companyJpaEntity;

import com.firstlogistics.companyservice.domain.specification.CompanySearchSpec;
import com.querydsl.core.BooleanBuilder;

public class CompanySpecification {

    private CompanySpecification() {}

    public static BooleanBuilder from(CompanySearchSpec spec) {
        BooleanBuilder builder = new BooleanBuilder();

        if (spec.keyword() != null && !spec.keyword().isBlank()) {
            builder.and(companyJpaEntity.name.containsIgnoreCase(spec.keyword()));
        }
        if (spec.type() != null && !spec.type().isBlank()) {
            builder.and(companyJpaEntity.type.equalsIgnoreCase(spec.type()));
        }
        if (spec.hubId() != null) {
            builder.and(companyJpaEntity.hubId.eq(spec.hubId()));
        }
        if (spec.status() != null) {
            builder.and(companyJpaEntity.status.eq(spec.status()));
        }

        return builder;
    }
}
