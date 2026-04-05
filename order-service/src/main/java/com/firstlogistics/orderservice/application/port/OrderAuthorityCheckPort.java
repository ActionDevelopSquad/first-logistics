package com.firstlogistics.orderservice.application.port;

import java.util.UUID;

public interface OrderAuthorityCheckPort {


    boolean canRequestCancel(UUID receiverManagerId, UUID myUserId);

    boolean canAcceptOrCancel(UUID supplierHubId, UUID supplierManagerId, UUID myHubId, UUID myUserId);

    boolean canView(UUID supplierHubId, UUID supplierManagerId, UUID receiverManagerId, UUID myHubId, UUID myUserId);

    boolean canDelete(UUID supplierHubId, UUID myHubId);
}
