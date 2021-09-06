package org.orlo.config;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.orlo.util.MD5Util;

public class UserRealm extends AuthorizingRealm {
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        System.out.println("执行了授权。。。");
        return null;
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        System.out.println("执行了认证。。。。");
        String phone = "18848497745";
        String password = "123456";
        UsernamePasswordToken userToken = (UsernamePasswordToken) authenticationToken;
        if(!phone.equals(userToken.getUsername())) {
            return null;
        }
        return new SimpleAuthenticationInfo("", password, "");
    }
}
