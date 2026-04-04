package com.firstlogistics.orderservice.domain.specification;

public enum OrderSearchType {
    SENT,
    RECEIVED;

    public static OrderSearchType from(String value) {
        if (value == null) return null;
        try {
            return OrderSearchType.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
