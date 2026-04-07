package com.firstlogistics.companyservice.presentation.docs;

public final class CompanyErrorDocs {

    private CompanyErrorDocs() {}

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

    public static final String COMPANY_NOT_FOUND = """
        {
          "code": "COM012",
          "message": "업체를 찾을 수 없습니다.",
          "data": null,
          "timestamp": "2026-04-07T10:00:00.000000"
        }
        """;

    public static final String INVALID_COMPANY_TYPE = """
        {
          "code": "COM004",
          "message": "회사 유형이 유효하지 않습니다.",
          "data": null,
          "timestamp": "2026-04-07T10:00:00.000000"
        }
        """;

    public static final String COMPANY_ALREADY_ACTIVE = """
        {
          "code": "COM005",
          "message": "회사가 이미 활성화 상태입니다.",
          "data": null,
          "timestamp": "2026-04-07T10:00:00.000000"
        }
        """;

    public static final String COMPANY_ALREADY_INACTIVE = """
        {
          "code": "COM006",
          "message": "회사가 이미 비활성화 상태입니다.",
          "data": null,
          "timestamp": "2026-04-07T10:00:00.000000"
        }
        """;

    public static final String DUPLICATE_MANAGER_ID = """
        {
          "code": "COM014",
          "message": "이미 업체를 관리하고 있는 담당자입니다.",
          "data": null,
          "timestamp": "2026-04-07T10:00:00.000000"
        }
        """;

    public static final String INVALID_HUB_ID = """
        {
          "code": "COM002",
          "message": "허브 ID가 유효하지 않습니다.",
          "data": null,
          "timestamp": "2026-04-07T10:00:00.000000"
        }
        """;
}
