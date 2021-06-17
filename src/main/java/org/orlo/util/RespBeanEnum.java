package org.orlo.util;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@ToString
@AllArgsConstructor
@Getter
public enum RespBeanEnum {
    SUCCESS(200, "SUCCESS"),
    ERROR(500, "服务端异常"),
    LOGIN_SUCCESS(2001, "登录成功"),
    VERIFY_SUCCESS(2002, "认证成功"),
    LOGOUT_SUCCESS(2003, "登出成功"),
    NO_USER(5001, "没有用户请注册"),
    NO_AUTHORIZE(5002, "失败，未得到授权"),
    VERIFY_USER(5003, "用户正在审核，请联系管理员"),
    PASSWD_ERROR(5004, "密码错误"),
    ;

    private final Integer code;
    private final String message;
}
