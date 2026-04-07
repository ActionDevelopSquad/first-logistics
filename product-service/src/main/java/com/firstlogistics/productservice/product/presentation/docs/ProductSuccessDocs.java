package com.firstlogistics.productservice.product.presentation.docs;

public final class ProductSuccessDocs {

    private ProductSuccessDocs() {}

    public static final String REGISTER_SUCCESS = """
        {
          "code": "CREATED",
          "message": "생성되었습니다.",
          "data": {
            "productId": "a1b2c3d4-e5f6-7890-abcd-ef1234567890"
          },
          "timestamp": "2026-04-07T10:00:00.000000"
        }
        """;

    public static final String GET_PRODUCT_SUCCESS = """
        {
          "code": "OK",
          "message": "성공하였습니다.",
          "data": {
            "id": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
            "companyId": "b2c3d4e5-f6a7-8901-bcde-f12345678901",
            "hubId": "c3d4e5f6-a7b8-9012-cdef-123456789012",
            "name": "테스트 상품",
            "price": 10000,
            "status": "SELLING"
          },
          "timestamp": "2026-04-07T10:00:00.000000"
        }
        """;

    public static final String GET_PRODUCTS_SUCCESS = """
        {
          "code": "OK",
          "message": "성공하였습니다.",
          "data": {
            "content": [
              {
                "id": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
                "companyId": "b2c3d4e5-f6a7-8901-bcde-f12345678901",
                "hubId": "c3d4e5f6-a7b8-9012-cdef-123456789012",
                "name": "테스트 상품",
                "price": 10000,
                "status": "SELLING"
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

    public static final String UPDATE_SUCCESS = GET_PRODUCT_SUCCESS;

    public static final String CHANGE_STATUS_SUCCESS = """
        {
          "code": "OK",
          "message": "성공하였습니다.",
          "data": {
            "id": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
            "companyId": "b2c3d4e5-f6a7-8901-bcde-f12345678901",
            "hubId": "c3d4e5f6-a7b8-9012-cdef-123456789012",
            "name": "테스트 상품",
            "price": 10000,
            "status": "STOPPED"
          },
          "timestamp": "2026-04-07T10:00:00.000000"
        }
        """;

    public static final String DELETE_SUCCESS = """
        {
          "code": "OK",
          "message": "성공하였습니다.",
          "data": null,
          "timestamp": "2026-04-07T10:00:00.000000"
        }
        """;

    public static final String GET_STOCK_SUCCESS = """
        {
          "code": "OK",
          "message": "성공하였습니다.",
          "data": {
            "productId": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
            "available": 100,
            "reserved": 5
          },
          "timestamp": "2026-04-07T10:00:00.000000"
        }
        """;
}
