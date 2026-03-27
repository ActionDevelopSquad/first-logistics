package com.firstlogistics.companyservice.domain.entity;

public final class Receiver implements CompanyType {

    @Override
    public boolean canRegisterProduct() {
        return false;
    }

    @Override
    public boolean canHoldInventory() {
        return false;
    }
}
