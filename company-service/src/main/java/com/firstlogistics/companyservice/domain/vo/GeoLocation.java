package com.firstlogistics.companyservice.domain.vo;

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
            throw new IllegalArgumentException("위도/경도는 NaN이 될 수 없습니다");
        }

        if (Double.isInfinite(latitude) || Double.isInfinite(longitude)) {
            throw new IllegalArgumentException("위도/경도는 무한대가 될 수 없습니다");
        }

        if (latitude < -90.0 || latitude > 90.0) {
            throw new IllegalArgumentException("위도는 -90과 90 사이여야 합니다");
        }

        if (longitude < -180.0 || longitude > 180.0) {
            throw new IllegalArgumentException("경도는 -180과 180 사이여야 합니다");
        }
    }
}
