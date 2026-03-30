package com.firstlogistics.companyservice.domain.entity;

public final class Supplier implements CompanyType {

    @Override
    public boolean canRegisterProduct() {
        return true;
    }

    @Override
    public boolean canHoldInventory() {
        return true;
    }
}
