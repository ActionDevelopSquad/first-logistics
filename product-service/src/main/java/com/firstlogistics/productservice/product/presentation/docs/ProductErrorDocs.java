package com.firstlogistics.productservice.product.presentation.docs;

public final class ProductErrorDocs {

    private ProductErrorDocs() {}

    public static final String INVALID_INPUT = """
        {
          "code": "INVALID_INPUT_VALUE",
          "message": "입력값이 올바르지 않습니다.",
          "data": null,
          "timestamp": "2026-04-07T10:00:00.000000"
        }
        """;

    public static final String FORBIDDEN = """
        {
          "code": "FORBIDDEN",
          "message": "접근 권한이 없습니다.",
          "data": null,
          "timestamp": "2026-04-07T10:00:00.000000"
        }
        """;

    public static final String PRODUCT_NOT_FOUND = """
        {
          "code": "PRO006",
          "message": "상품을 찾을 수 없습니다.",
          "data": null,
          "timestamp": "2026-04-07T10:00:00.000000"
        }
        """;

    public static final String COMPANY_NOT_FOUND = """
        {
          "code": "PRO008",
          "message": "업체를 찾을 수 없습니다.",
          "data": null,
          "timestamp": "2026-04-07T10:00:00.000000"
        }
        """;

    public static final String UNAUTHORIZED_COMPANY_ACCESS = """
        {
          "code": "PRO009",
          "message": "본인 업체의 상품만 등록할 수 있습니다.",
          "data": null,
          "timestamp": "2026-04-07T10:00:00.000000"
        }
        """;

    public static final String PRODUCT_ALREADY_STOPPED = """
        {
          "code": "PRO007",
          "message": "이미 판매 중지된 상품입니다.",
          "data": null,
          "timestamp": "2026-04-07T10:00:00.000000"
        }
        """;

    public static final String PRODUCT_ALREADY_SELLING = """
        {
          "code": "PRO012",
          "message": "이미 판매 중인 상품입니다.",
          "data": null,
          "timestamp": "2026-04-07T10:00:00.000000"
        }
        """;

    public static final String INVENTORY_NOT_FOUND = """
        {
          "code": "INV005",
          "message": "재고 정보를 찾을 수 없습니다.",
          "data": null,
          "timestamp": "2026-04-07T10:00:00.000000"
        }
        """;
}
