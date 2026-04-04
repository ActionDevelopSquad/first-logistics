package com.firstlogistics.companyservice.application;

import com.firstlogistics.companyservice.application.dto.command.CreateCompanyCommand;
import com.firstlogistics.companyservice.application.dto.command.UpdateCompanyCommand;
import com.firstlogistics.companyservice.application.dto.result.CompanyResult;
import com.firstlogistics.companyservice.application.port.HubPort;
import com.firstlogistics.companyservice.domain.entity.Company;
import com.firstlogistics.companyservice.domain.entity.CompanyType;
import com.firstlogistics.companyservice.domain.entity.Receiver;
import com.firstlogistics.companyservice.domain.entity.Supplier;
import com.firstlogistics.companyservice.domain.event.CompanyActivatedEvent;
import com.firstlogistics.companyservice.domain.event.CompanyDeactivatedEvent;
import com.firstlogistics.companyservice.domain.event.CompanyDeletedEvent;
import com.firstlogistics.companyservice.domain.exception.CompanyErrorCode;
import com.firstlogistics.companyservice.domain.exception.CompanyException;
import com.firstlogistics.companyservice.domain.repository.CompanyRepository;
import com.firstlogistics.companyservice.domain.vo.GeoLocation;
import common.event.Events;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CompanyCommandService {
    private final CompanyRepository companyRepository;
    private final HubPort hubPort;

    @Transactional
    public CompanyResult register(CreateCompanyCommand command) {
        if (companyRepository.existsByManagerId(command.managerId())) {
            throw new CompanyException(CompanyErrorCode.DUPLICATE_MANAGER_ID);
        }

        UUID hubId = hubPort.getHubId(GeoLocation.of(command.latitude(), command.longitude()));

        CompanyType type = resolveCompanyType(command.type());

        Company company = Company.create(
                hubId,
                command.managerId(),
                command.name(),
                type,
                command.roadAddress(),
                command.detailAddress(),
                command.latitude(),
                command.longitude()
        );

        Company saved = companyRepository.save(company);

        return CompanyResult.from(saved);
    }

    @Transactional
    public CompanyResult update(UpdateCompanyCommand command) {
        Company company = companyRepository.findById(command.companyId())
                .orElseThrow(() -> new CompanyException(CompanyErrorCode.COMPANY_NOT_FOUND));

        UUID hubId = hubPort.getHubId(GeoLocation.of(command.latitude(), command.longitude()));
        CompanyType type = resolveCompanyType(command.type());

        company.update(command.name(), type, hubId,
                command.roadAddress(), command.detailAddress(),
                command.latitude(), command.longitude());

        return CompanyResult.from(companyRepository.save(company));
    }

    @Transactional
    public CompanyResult deactivate(UUID companyId) {
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new CompanyException(CompanyErrorCode.COMPANY_NOT_FOUND));

        company.deactivate();

        CompanyResult result = CompanyResult.from(companyRepository.save(company));
        Events.trigger(new CompanyDeactivatedEvent(companyId));
        return result;
    }

    @Transactional
    public CompanyResult activate(UUID companyId) {
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new CompanyException(CompanyErrorCode.COMPANY_NOT_FOUND));

        company.activate();

        CompanyResult result = CompanyResult.from(companyRepository.save(company));
        Events.trigger(new CompanyActivatedEvent(companyId));
        return result;
    }

    @Transactional
    public void delete(UUID companyId, UUID deletedBy) {
        companyRepository.findById(companyId)
                .orElseThrow(() -> new CompanyException(CompanyErrorCode.COMPANY_NOT_FOUND));
        companyRepository.delete(companyId, deletedBy);
        Events.trigger(new CompanyDeletedEvent(companyId));
    }

    private CompanyType resolveCompanyType(String typeStr) {
        if (typeStr == null) {
            throw new CompanyException(CompanyErrorCode.INVALID_COMPANY_TYPE);
        }

        return switch (typeStr.toUpperCase()) {
            case "SUPPLIER" -> new Supplier();
            case "RECEIVER" -> new Receiver();
            default -> throw new CompanyException(CompanyErrorCode.INVALID_COMPANY_TYPE);
        };
    }
}
