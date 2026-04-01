package common.jpa.security.aop;

import common.jpa.entity.enums.UserRole;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface OnlyMaster {
    UserRole value();
}