package org.orlo.util;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
//@ToString
public class RespBean {
    private long code;
    private String message;
    private Object obj;

    public static RespBean success() {
        return new RespBean(RespBeanEnum.SUCCESS.getCode(), RespBeanEnum.SUCCESS.getMessage(), null);
    }

    public static RespBean loginSuccess() {
        return new RespBean(RespBeanEnum.LOGIN_SUCCESS.getCode(), RespBeanEnum.LOGIN_SUCCESS.getMessage(), null);
    }

    public static RespBean logoutSuccess() {
        return new RespBean(RespBeanEnum.LOGOUT_SUCCESS.getCode(), RespBeanEnum.LOGOUT_SUCCESS.getMessage(), null);
    }

    public static RespBean verifySuccess() {
        return new RespBean(RespBeanEnum.VERIFY_SUCCESS.getCode(), RespBeanEnum.VERIFY_SUCCESS.getMessage(), null);
    }

    public static RespBean error() {
        return new RespBean(RespBeanEnum.ERROR.getCode(), RespBeanEnum.ERROR.getMessage(), null);
    }

    public static RespBean noUser() {
        return new RespBean(RespBeanEnum.NO_USER.getCode(), RespBeanEnum.NO_USER.getMessage(), null);
    }

    public static RespBean noAuthorize() {
        return new RespBean(RespBeanEnum.NO_AUTHORIZE.getCode(), RespBeanEnum.NO_AUTHORIZE.getMessage(), null);
    }

    public static RespBean verifyUser() {
        return new RespBean(RespBeanEnum.VERIFY_USER.getCode(), RespBeanEnum.VERIFY_USER.getMessage(), null);
    }

    public static RespBean passwdError() {
        return new RespBean(RespBeanEnum.PASSWD_ERROR.getCode(), RespBeanEnum.PASSWD_ERROR.getMessage(), null);
    }
}
