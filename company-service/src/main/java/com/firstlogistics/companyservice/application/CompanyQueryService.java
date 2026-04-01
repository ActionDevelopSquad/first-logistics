package com.firstlogistics.companyservice.application;

import com.firstlogistics.companyservice.application.dto.query.CompanySearchQuery;
import com.firstlogistics.companyservice.application.dto.result.CompanyResult;
import com.firstlogistics.companyservice.domain.repository.CompanyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CompanyQueryService {

    private final CompanyRepository companyRepository;

    public Page<CompanyResult> search(CompanySearchQuery query) {
        return companyRepository.findAll(query.toCondition(), query.toPageable())
                .map(CompanyResult::from);
    }
}