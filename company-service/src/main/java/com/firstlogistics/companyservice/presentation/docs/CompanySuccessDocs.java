package com.firstlogistics.companyservice.presentation.docs;

public final class CompanySuccessDocs {

    private CompanySuccessDocs() {}

    public static final String REGISTER_SUCCESS = """
        {
          "code": "CREATED",
          "message": "생성되었습니다.",
          "data": {
            "companyId": "a1b2c3d4-e5f6-7890-abcd-ef1234567890"
          },
          "timestamp": "2026-04-07T10:00:00.000000"
        }
        """;

    public static final String GET_COMPANY_SUCCESS = """
        {
          "code": "OK",
          "message": "성공하였습니다.",
          "data": {
            "id": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
            "hubId": "b2c3d4e5-f6a7-8901-bcde-f12345678901",
            "managerId": "c3d4e5f6-a7b8-9012-cdef-123456789012",
            "name": "테스트 업체",
            "type": "SUPPLIER",
            "status": "ACTIVE",
            "roadAddress": "서울특별시 강남구 테헤란로 123",
            "detailAddress": "456호",
            "latitude": 37.5025,
            "longitude": 127.0255
          },
          "timestamp": "2026-04-07T10:00:00.000000"
        }
        """;

    public static final String GET_COMPANIES_SUCCESS = """
        {
          "code": "OK",
          "message": "성공하였습니다.",
          "data": {
            "content": [
              {
                "id": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
                "hubId": "b2c3d4e5-f6a7-8901-bcde-f12345678901",
                "managerId": "c3d4e5f6-a7b8-9012-cdef-123456789012",
                "name": "테스트 업체",
                "type": "SUPPLIER",
                "status": "ACTIVE",
                "roadAddress": "서울특별시 강남구 테헤란로 123",
                "detailAddress": "456호"
              }
            ],
            "page": 0,
            "size": 10,
            "totalElements": 1,
            "totalPages": 1
          },
          "timestamp": "2026-04-07T10:00:00.000000"
        }
        """;

    public static final String UPDATE_SUCCESS = GET_COMPANY_SUCCESS;

    public static final String DEACTIVATE_SUCCESS = """
        {
          "code": "OK",
          "message": "성공하였습니다.",
          "data": {
            "id": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
            "hubId": "b2c3d4e5-f6a7-8901-bcde-f12345678901",
            "managerId": "c3d4e5f6-a7b8-9012-cdef-123456789012",
            "name": "테스트 업체",
            "type": "SUPPLIER",
            "status": "INACTIVE",
            "roadAddress": "서울특별시 강남구 테헤란로 123",
            "detailAddress": "456호",
            "latitude": 37.5025,
            "longitude": 127.0255
          },
          "timestamp": "2026-04-07T10:00:00.000000"
        }
        """;

    public static final String ACTIVATE_SUCCESS = GET_COMPANY_SUCCESS;

    public static final String DELETE_SUCCESS = """
        {
          "code": "OK",
          "message": "성공하였습니다.",
          "data": null,
          "timestamp": "2026-04-07T10:00:00.000000"
        }
        """;
}
