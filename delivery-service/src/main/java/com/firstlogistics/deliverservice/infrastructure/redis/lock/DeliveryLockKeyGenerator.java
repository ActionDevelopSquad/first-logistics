package com.firstlogistics.deliverservice.infrastructure.redis.lock;

import java.util.UUID;

public final class DeliveryLockKeyGenerator {

    private static final String HUB_STAFF_ASSIGN_PREFIX = "delivery:assign:hub:";

    private DeliveryLockKeyGenerator() {
    }

    public static String hubStaffAssignKey(UUID hubId) {
        return HUB_STAFF_ASSIGN_PREFIX + hubId;
    }
}