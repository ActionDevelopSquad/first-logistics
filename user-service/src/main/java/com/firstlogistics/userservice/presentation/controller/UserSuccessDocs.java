package com.firstlogistics.userservice.presentation.controller;

public final class UserSuccessDocs {

    private UserSuccessDocs() {}

    public static final String LOGIN_SUCCESS = """
        {
          "success": true,
          "code": "LOGIN_SUCCESS",
          "message": "로그인에 성공했습니다.",
          "data": {
            "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
            "refreshToken": "eyJhbGciOiJIUzI1NiJ9..."
          }
        }
        """;

    public static final String LOGOUT_SUCCESS = """
        {
          "success": true,
          "code": "LOGOUT_SUCCESS",
          "message": "로그아웃에 성공했습니다.",
          "data": null
        }
        """;

    public static final String REFRESH_SUCCESS = """
        {
          "success": true,
          "code": "TOKEN_REFRESHED",
          "message": "토큰이 재발급되었습니다.",
          "data": {
            "accessToken": "new-access-token",
            "refreshToken": "new-refresh-token"
          }
        }
        """;

    public static final String SIGNUP_SUCCESS = """
        {
          "success": true,
          "code": "SIGNUP_SUCCESS",
          "message": "회원가입에 성공했습니다.",
          "data": {
            "userId": "123e4567-e89b-12d3-a456-426614174000"
          }
        }
        """;

    public static final String GET_USER_SUCCESS = """
        {
          "success": true,
          "code": "GET_USER",
          "message": "회원 조회에 성공했습니다.",
          "data": {
            "username": "user01",
            "name": "홍길동",
            "phone": "01012345678",
            "userRole": "HUB_MANAGER",
            "slackId": "hong",
            "organizationId": "123e4567-e89b-12d3-a456-426614174000",
            "lastLoginAt": "2026-04-05T12:00:00"
          }
        }
        """;

    public static final String GET_MY_PAGE_SUCCESS = GET_USER_SUCCESS;

    public static final String GET_USERS_SUCCESS = """
        {
          "success": true,
          "code": "GET_USERS",
          "message": "회원 목록 조회에 성공했습니다.",
          "data": {
            "content": [
              {
                "username": "user01",
                "name": "홍길동",
                "phone": "01012345678",
                "userRole": "HUB_MANAGER",
                "slackId": "hong",
                "organizationId": "123e4567-e89b-12d3-a456-426614174000",
                "lastLoginAt": "2026-04-05T12:00:00"
              }
            ],
            "page": 0,
            "size": 10,
            "totalElements": 1,
            "totalPages": 1
          }
        }
        """;

    public static final String UPDATE_ROLE_SUCCESS = """
        {
          "success": true,
          "code": "ROLE_UPDATED",
          "message": "회원 권한이 변경되었습니다.",
          "data": {
            "userId": "123e4567-e89b-12d3-a456-426614174000"
          }
        }
        """;

    public static final String UPDATE_STATUS_SUCCESS = """
        {
          "success": true,
          "code": "STATUS_UPDATED",
          "message": "회원 상태가 변경되었습니다.",
          "data": {
            "userId": "123e4567-e89b-12d3-a456-426614174000"
          }
        }
        """;

    public static final String UPDATE_USER_SUCCESS = """
        {
          "success": true,
          "code": "USER_UPDATED",
          "message": "회원 정보가 수정되었습니다.",
          "data": {
            "userId": "123e4567-e89b-12d3-a456-426614174000"
          }
        }
        """;

    public static final String DELETE_USER_SUCCESS = """
        {
          "success": true,
          "code": "USER_DELETED",
          "message": "회원이 삭제되었습니다.",
          "data": null
        }
        """;
}