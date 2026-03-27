package com.firstlogistics.companyservice.domain.entity;

import com.firstlogistics.companyservice.domain.enums.CompanyStatus;
import com.firstlogistics.companyservice.domain.vo.CompanyAddress;
import java.util.Objects;
import java.util.UUID;

public class Company {
    private UUID id;
    private UUID hubId;
    private String name;
    private CompanyType type;
    private CompanyStatus status;
    private CompanyAddress address;

    private Company(UUID id, UUID hubId, String name, CompanyType type, CompanyStatus status, CompanyAddress address) {
        this.id = id;
        this.hubId = hubId;
        this.name = name;
        this.type = type;
        this.status = status;
        this.address = address;
    }

    public static Company of(UUID hubId, String name, CompanyType type, String address) {
        validate(hubId, name, type);

        return new Company(
                UUID.randomUUID(),
                hubId,
                name,
                type,
                CompanyStatus.ACTIVE,
                CompanyAddress.of(address)
        );
    }

    private static void validate(UUID hubId, String name, CompanyType type) {
        if (hubId == null) {
            throw new IllegalArgumentException("허브 ID는 null일 수 없습니다.");
        }
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("이름은 null이거나 빈 문자열일 수 없습니다.");
        }
        if (type == null) {
            throw new IllegalArgumentException("타입은 null일 수 없습니다.");
        }
    }

    public void changeAddress(String address) {
        if (this.status == CompanyStatus.INACTIVE) {
            throw new IllegalStateException("비활성화 상태의 회사는 주소를 변경할 수 없습니다.");
        }

        if (address == null || address.isBlank()) {
            throw new IllegalArgumentException("주소는 null이거나 빈 문자열일 수 없습니다.");
        }

        this.address = new CompanyAddress(address);
    }

    public void deactivate() {
        if (this.status == CompanyStatus.INACTIVE) {
            throw new IllegalStateException("이미 비활성화 상태입니다.");
        }
        this.status = CompanyStatus.INACTIVE;
    }

    public void activate() {
        if (this.status == CompanyStatus.ACTIVE) {
            throw new IllegalStateException("이미 활성화 상태입니다.");
        }
        this.status = CompanyStatus.ACTIVE;
    }

    public boolean canRegisterProduct() {
        return type.canRegisterProduct();
    }

    public boolean canHoldInventory() {
        return type.canHoldInventory();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Company)) return false;
        Company company = (Company) o;
        return Objects.equals(id, company.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Company{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
