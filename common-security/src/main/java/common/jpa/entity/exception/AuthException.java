package common.jpa.entity.exception;

import common.exception.BaseException;

public class AuthException extends BaseException {

    public AuthException(AuthErrorCode errorCode) {
        super(errorCode);
    }
}
