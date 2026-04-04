package com.firstlogistics.companyservice.domain.entity;

public sealed interface CompanyType permits Supplier, Receiver {

    String getTypeName();

    boolean canRegisterProduct();

    boolean canHoldInventory();
}
