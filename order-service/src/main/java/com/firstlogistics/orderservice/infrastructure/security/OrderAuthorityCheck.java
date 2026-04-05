package com.firstlogistics.orderservice.infrastructure.security;

import com.firstlogistics.orderservice.application.port.UserContextPort;
import com.firstlogistics.orderservice.application.port.OrderAuthorityCheckPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderAuthorityCheck implements OrderAuthorityCheckPort {

    private final UserContextPort userContext;

    @Override
    public boolean canRequestCancel(UUID receiverManagerId, UUID myUserId) {
        if (!userContext.isCompanyManager()) return false;
        return Objects.equals(receiverManagerId, myUserId);
    }

    @Override
    public boolean canAcceptOrCancel(UUID supplierHubId, UUID supplierManagerId, UUID myHubId, UUID myUserId) {
        if (userContext.isMaster()) return true;
        if (userContext.isHubManager()) {
            return Objects.equals(supplierHubId, myHubId);
        }
        if (userContext.isCompanyManager()) {
            return Objects.equals(supplierManagerId, myUserId);
        }
        return false;
    }

    @Override
    public boolean canView(UUID supplierHubId, UUID supplierManagerId, UUID receiverManagerId, UUID myHubId, UUID myUserId) {
        if (userContext.isMaster()) return true;

        if (userContext.isHubManager()) {
            return supplierHubId.equals(myHubId);
        }

        if (userContext.isCompanyManager()) {
            return Objects.equals(supplierManagerId, myUserId) || Objects.equals(receiverManagerId, myUserId);
        }

        return false;
    }

    @Override
    public boolean canDelete(UUID supplierHubId, UUID myHubId) {
        if (userContext.isMaster()) return true;

        if (userContext.isHubManager()) {
            return supplierHubId.equals(myHubId);
        }

        return false;
    }
}