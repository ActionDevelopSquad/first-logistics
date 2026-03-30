package com.firstlogistics.companyservice.domain.entity;

public sealed interface CompanyType permits Supplier, Receiver {

    boolean canRegisterProduct();

    boolean canHoldInventory();
}
