package com.firstlogistics.companyservice.infrastructure.persistence.jpa;

import com.firstlogistics.companyservice.domain.entity.Company;
import com.firstlogistics.companyservice.domain.repository.CompanyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CompanyRepositoryImpl implements CompanyRepository {
    private final CompanyJpaRepository companyJpaRepository;

    @Override
    public Company save(Company company) {
        CompanyJpaEntity jpaEntity = CompanyMapper.toJpaEntity(company);
        CompanyJpaEntity savedEntity = companyJpaRepository.save(jpaEntity);
        return CompanyMapper.toDomain(savedEntity);
    }
}
