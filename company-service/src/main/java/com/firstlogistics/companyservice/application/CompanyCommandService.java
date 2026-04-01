package com.firstlogistics.companyservice.application;

import com.firstlogistics.companyservice.application.dto.command.CreateCompanyCommand;
import com.firstlogistics.companyservice.application.dto.result.CompanyResult;
import com.firstlogistics.companyservice.application.port.CompanyEventPublisher;
import com.firstlogistics.companyservice.application.port.HubPort;
import com.firstlogistics.companyservice.domain.entity.Company;
import com.firstlogistics.companyservice.domain.entity.CompanyType;
import com.firstlogistics.companyservice.domain.entity.Receiver;
import com.firstlogistics.companyservice.domain.entity.Supplier;
import com.firstlogistics.companyservice.domain.event.CompanyCreatedEvent;
import com.firstlogistics.companyservice.domain.exception.CompanyErrorCode;
import com.firstlogistics.companyservice.domain.exception.CompanyException;
import com.firstlogistics.companyservice.domain.repository.CompanyRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CompanyCommandService {
    private final CompanyRepository companyRepository;
    private final HubPort hubPort;
    private final CompanyEventPublisher eventPublisher;

    @Transactional
    public CompanyResult register(CreateCompanyCommand command) {
        UUID hubId = hubPort.getHubId(command.latitude(), command.longitude());

        CompanyType type = resolveCompanyType(command.type());

        Company company = Company.create(
                hubId,
                command.userId(),
                command.name(),
                type,
                command.roadAddress(),
                command.detailAddress(),
                command.latitude(),
                command.longitude()
        );

        Company saved = companyRepository.save(company);

        publishAfterCommit(saved);

        return CompanyResult.from(saved);
    }

    private void publishAfterCommit(Company company) {
        eventPublisher.publish(
                new CompanyCreatedEvent(
                        company.getId(),
                        company.getName()
                )
        );
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
