package com.firstlogistics.productservice.product.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ProductStatus {
    SELLING("판매 중"),
    STOPPED("판매 중지")
    ;

    private final String description;
}
