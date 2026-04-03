package com.firstlogistics.deliverservice.application.permission.strategy;

import com.firstlogistics.deliverservice.application.permission.DeliveryAccessContext;

import com.firstlogistics.deliverservice.application.port.CompanyPort;
import com.firstlogistics.deliverservice.domain.enums.UserRole;
import com.firstlogistics.deliverservice.domain.exception.DeliveryErrorCode;
import com.firstlogistics.deliverservice.domain.exception.DeliveryException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class CompanyManagerPermissionStrategy implements RolePermissionStrategy {

	private final CompanyPort companyPort;

	@Override
	public UserRole supportedRole() {
		return UserRole.COMPANY_MANAGER;
	}

	@Override
	public void validate(DeliveryAccessContext context, UUID userId) {
		UUID companyId = companyPort.getCompanyByUserId(userId).companyId();
		if (!companyId.equals(context.receiverCompanyId())) {
			throw new DeliveryException(DeliveryErrorCode.DELIVERY_ACCESS_DENIED);
		}
	}
}
