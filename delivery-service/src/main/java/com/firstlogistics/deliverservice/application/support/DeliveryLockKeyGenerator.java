package com.firstlogistics.deliverservice.application.support;

import java.util.UUID;

public final class DeliveryLockKeyGenerator {

    private static final String HUB_MANAGER_ASSIGN_PREFIX = "delivery:assign:hub:";

    private DeliveryLockKeyGenerator() {
    }

    public static String hubManagerAssignKey(UUID hubId) {
        return HUB_MANAGER_ASSIGN_PREFIX + hubId;
    }
}