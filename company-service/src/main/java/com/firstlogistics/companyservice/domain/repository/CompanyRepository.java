package com.firstlogistics.companyservice.domain.repository;

import com.firstlogistics.companyservice.application.dto.query.CompanyQueryCondition;
import com.firstlogistics.companyservice.domain.entity.Company;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CompanyRepository {

    Company save(Company company);

    Page<Company> findAll(CompanyQueryCondition condition, Pageable pageable);
}
