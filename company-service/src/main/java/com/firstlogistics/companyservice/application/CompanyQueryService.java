package com.firstlogistics.companyservice.application;

import com.firstlogistics.companyservice.application.dto.query.CompanySearchQuery;
import com.firstlogistics.companyservice.application.dto.result.CompanyResult;
import com.firstlogistics.companyservice.domain.exception.CompanyErrorCode;
import com.firstlogistics.companyservice.domain.exception.CompanyException;
import com.firstlogistics.companyservice.domain.repository.CompanyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CompanyQueryService {

    private final CompanyRepository companyRepository;

    public Page<CompanyResult> search(CompanySearchQuery query, Pageable pageable) {
        return companyRepository.findAll(query.toCondition(), pageable)
                .map(CompanyResult::from);
    }

    public CompanyResult getById(UUID companyId) {
        return companyRepository.findById(companyId)
                .map(CompanyResult::from)
                .orElseThrow(() -> new CompanyException(CompanyErrorCode.COMPANY_NOT_FOUND));
    }

    public CompanyResult getByManagerId(UUID managerId) {
        return companyRepository.findByManagerId(managerId)
                .map(CompanyResult::from)
                .orElseThrow(() -> new CompanyException(CompanyErrorCode.COMPANY_NOT_FOUND));
    }
}
