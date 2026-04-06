package com.firstlogistics.userservice.presentation.controller;

public final class UserSuccessDocs {

    private UserSuccessDocs() {}

    public static final String LOGIN_SUCCESS = """
        {
          "code": "USER_001",
          "message": "로그인 되었습니다.",
          "data": {
            "accessToken": "d2a1d6ee-4c3b-4ef0-9b9a-1d2f3a4b5c6d",
            "refreshToken": "d2a1d6ee-4c3b-4ef0-9b9a-1d2f3a4b5c6d"
          },
          "timestamp": "2026-04-02T20:13:34.3109013"
        }
        """;

    public static final String LOGOUT_SUCCESS = """
        {
          "code": "USER_002",
          "message": "로그아웃 되었습니다.",
          "data": {
          },
          "timestamp": "2026-04-02T20:13:34.3109013"
        }
        """;

    public static final String REFRESH_SUCCESS = """
        {
          "code": "USER_010",
          "message": "토큰이 재발급 되었습니다.",
          "data": {
            "accessToken": "d2a1d6ee-4c3b-4ef0-9b9a-1d2f3a4b5c6d",
            "refreshToken": "d2a1d6ee-4c3b-4ef0-9b9a-1d2f3a4b5c6d"
          },
          "timestamp": "2026-04-02T20:13:34.3109013"
        }
        """;

    public static final String SIGNUP_SUCCESS = """
        {
          "code": "USER_003",
          "message": "승인 대기중입니다.",
          "data": {
            "userId": "d2a1d6ee-4c3b-4ef0-9b9a-1d2f3a4b5c6d"
          },
          "timestamp": "2026-04-02T20:13:34.3109013"
        }
        """;

    public static final String GET_USER_SUCCESS = """
        {
            "code": "USER_005",
            "status": "OK",
            "message": "회원이 조회되었습니다.",
            "data": {
                "username": "test12345",
                "name": "홍길동",
                "phone": "010-1357-2468",
                "userRole": "HUB_MANAGER",
                "status": "PENDING",
                "slackId": "U04F7ABCD12",
                "organizationId": "3f9a2c8e-7b6f-4c1e-9d2a-5f6b1e8a3c7d",
                "lastLoginAt": null
            },
            "timestamp": "2026-04-02T20:13:34.3109013"
        }
        """;

    public static final String GET_MY_PAGE_SUCCESS = GET_USER_SUCCESS;

    public static final String GET_USERS_SUCCESS = """
        {
            "code": "USER_006",
            "status": "OK",
            "message": "회원 목록이 조회되었습니다.",
            "data": {
                "users": [
                    {
                        "username": "test12345",
                        "name": "홍길동",
                        "phone": "010-1357-2468",
                        "userRole": "HUB_MANAGER",
                        "status": "PENDING",
                        "slackId": "U04F7ABCD12",
                        "organizationId": "3f9a2c8e-7b6f-4c1e-9d2a-5f6b1e8a3c7d",
                        "lastLoginAt": null
                    },
                    {
                        "username": "test12345",
                        "name": "아무개",
                        "phone": "010-1357-2468",
                        "userRole": "DELIVERY_MANAGER",
                        "status": "APPROVED",
                        "slackId": "U04F7ABCD12",
                        "organizationId": "3f9a2c8e-7b6f-4c1e-9d2a-5f6b1e8a3c7d",
                        "lastLoginAt": null
                    }
                ],
                "page": 0,
                "size": 10,
                "totalElements": 1,
                "totalPages": 1,
                "first": true,
                "last": true
            },
            "timestamp": "2026-04-02T20:17:13.2052295"
        }
        """;

    public static final String UPDATE_ROLE_SUCCESS = """
        {
          "code": "USER_004",
          "message": "권한이 변경되었습니다.",
          "data": {
              "userId": "d2a1d6ee-4c3b-4ef0-9b9a-1d2f3a4b5c6d"
          },
          "timestamp": "2026-04-02T20:13:34.3109013"
        }
        """;

    public static final String UPDATE_STATUS_SUCCESS = """
        {
          "code": "STATUS_MODIFIED",
          "message": "회원이 승인되었습니다.",
          "data": {
              "userId": "d2a1d6ee-4c3b-4ef0-9b9a-1d2f3a4b5c6d"
          },
          "timestamp": "2026-04-02T20:13:34.3109013"
        }
        """;

    public static final String UPDATE_USER_SUCCESS = """
        {
          "code": "USER_MODIFIED",
          "message": "회원정보가 수정되었습니다.",
          "data": {
              "userId": "d2a1d6ee-4c3b-4ef0-9b9a-1d2f3a4b5c6d"
          },
          "timestamp": "2026-04-02T20:13:34.3109013"
        }
        """;

    public static final String DELETE_USER_SUCCESS = """
        {
          "code": "USER_DELETED",
          "message": "회원 탈퇴 되었습니다.",
          "data": {
          },
          "timestamp": "2026-04-02T20:13:34.3109013"
        }
        """;
}