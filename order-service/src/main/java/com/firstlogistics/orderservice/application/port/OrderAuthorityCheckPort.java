package com.firstlogistics.orderservice.application.port;

import java.util.UUID;

public interface OrderAuthorityCheckPort {


    boolean canRequestCancel(UUID receiverManagerId);

    boolean canAcceptOrCancel(UUID supplierHubId, UUID supplierManagerId, UUID myHubId);

    boolean canView(UUID supplierHubId, UUID supplierManagerId, UUID receiverManagerId, UUID myHubId);

    boolean canDelete(UUID supplierHubId, UUID myHubId);
}
