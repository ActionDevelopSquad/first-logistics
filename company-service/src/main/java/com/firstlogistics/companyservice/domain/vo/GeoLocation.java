package com.firstlogistics.companyservice.domain.vo;

import com.firstlogistics.companyservice.domain.exception.CompanyErrorCode;
import com.firstlogistics.companyservice.domain.exception.CompanyException;

public record GeoLocation(
        double latitude,
        double longitude
) {

    public GeoLocation {
        validate(latitude, longitude);
    }

    public static GeoLocation of(double latitude, double longitude) {
        return new GeoLocation(latitude, longitude);
    }

    private static void validate(double latitude, double longitude) {
        if (Double.isNaN(latitude) || Double.isNaN(longitude)) {
            throw new CompanyException(CompanyErrorCode.INVALID_GEO_LOCATION);
        }

        if (Double.isInfinite(latitude) || Double.isInfinite(longitude)) {
            throw new CompanyException(CompanyErrorCode.INVALID_GEO_LOCATION);
        }

        if (latitude < -90.0 || latitude > 90.0) {
            throw new CompanyException(CompanyErrorCode.INVALID_GEO_LOCATION);
        }

        if (longitude < -180.0 || longitude > 180.0) {
            throw new CompanyException(CompanyErrorCode.INVALID_GEO_LOCATION);
        }
    }
}
