package com.firstlogistics.companyservice.domain.repository;

import com.firstlogistics.companyservice.domain.entity.Company;

public interface CompanyRepository {

    Company save(Company company);
}
