package com.firstlogistics.userservice.presentation.controller;

public final class UserErrorDocs {

    private UserErrorDocs() {}

    public static final String DUPLICATED_USERNAME = """
        {
          "success": false,
          "code": "USER_400_001",
          "message": "이미 존재하는 아이디입니다.",
          "data": null
        }
        """;

    public static final String DUPLICATED_EMAIL = """
        {
          "success": false,
          "code": "USER_400_002",
          "message": "이미 존재하는 이메일입니다.",
          "data": null
        }
        """;

    public static final String NOT_APPROVED_USER = """
        {
          "success": false,
          "code": "USER_400_003",
          "message": "승인된 사용자만 로그인할 수 있습니다.",
          "data": null
        }
        """;

    public static final String ALREADY_APPROVE = """
        {
          "success": false,
          "code": "USER_400_004",
          "message": "이미 승인된 사용자입니다.",
          "data": null
        }
        """;

    public static final String ALREADY_REJECTED = """
        {
          "success": false,
          "code": "USER_400_005",
          "message": "이미 거부된 사용자입니다.",
          "data": null
        }
        """;

    public static final String ALREADY_DELETED = """
        {
          "success": false,
          "code": "USER_400_006",
          "message": "탈퇴 회원 정보는 수정이 불가능합니다.",
          "data": null
        }
        """;

    public static final String SAME_ROLE_SELECTED = """
        {
          "success": false,
          "code": "USER_400_007",
          "message": "현재 권한과 수정된 권한이 동일합니다.",
          "data": null
        }
        """;

    public static final String REFRESH_TOKEN_NOT_FOUND = """
        {
          "success": false,
          "code": "USER_400_008",
          "message": "refresh token이 없습니다.",
          "data": null
        }
        """;

    public static final String UNAUTHORIZED = """
        {
          "success": false,
          "code": "USER_401_001",
          "message": "인증이 필요합니다.",
          "data": null
        }
        """;

    public static final String FORBIDDEN = """
        {
          "success": false,
          "code": "USER_403_001",
          "message": "권한이 필요합니다.",
          "data": null
        }
        """;

    public static final String ID_PASSWORD_NOT_MATCH = """
        {
          "success": false,
          "code": "USER_404_001",
          "message": "아이디 또는 비밀번호가 올바르지 않습니다.",
          "data": null
        }
        """;

    public static final String USER_NOT_FOUND = """
        {
          "success": false,
          "code": "USER_404_002",
          "message": "존재하지 않는 회원입니다.",
          "data": null
        }
        """;

    public static final String ORGANIZATION_ID_NOT_FOUND = """
        {
          "success": false,
          "code": "USER_404_003",
          "message": "존재하지 않는 소속 ID 입니다.",
          "data": null
        }
        """;

    public static final String AUTH_SERVER_REQUEST_ERROR = """
        {
          "success": false,
          "code": "AUTH_502_001",
          "message": "인증 서버 요청 중 오류가 발생했습니다.",
          "data": null
        }
        """;

    public static final String AUTH_SERVER_INTERNAL_ERROR = """
        {
          "success": false,
          "code": "AUTH_502_002",
          "message": "인증 서버에 일시적인 오류가 발생했습니다.",
          "data": null
        }
        """;

    public static final String AUTH_SERVER_CONNECTION_ERROR = """
        {
          "success": false,
          "code": "AUTH_503_003",
          "message": "인증 서버와 통신할 수 없습니다.",
          "data": null
        }
        """;

    public static final String FEIGN_SERVICE_UNAVAILABLE = """
        {
          "success": false,
          "code": "AUTH_503_004",
          "message": "해당 서비스를 이용할 수 없습니다.",
          "data": null
        }
        """;

    public static final String INVALID_INPUT = """
        {
          "success": false,
          "code": "COMMON_001",
          "message": "입력값을 확인해주세요.",
          "data": null
        }
        """;
}