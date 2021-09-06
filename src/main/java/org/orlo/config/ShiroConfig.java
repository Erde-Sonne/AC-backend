package org.orlo.config;

import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.LinkedHashMap;
import java.util.Map;

@Configuration
public class ShiroConfig {
    //shiroFilterFactoryBean
    @Bean
    public ShiroFilterFactoryBean getShiroFilterFactoryBean(@Qualifier("securityManager") DefaultWebSecurityManager defaultWebSecurityManager) {
        ShiroFilterFactoryBean factoryBean = new ShiroFilterFactoryBean();
        //设置安全管理器
        factoryBean.setSecurityManager(defaultWebSecurityManager);
        //添加shiro的内置过滤器
        /**
         * anon 无需认证
         * authc 必须认证
         * user  必须有记住我功能
         * perms 拥有对某个资源的权限
         * role  拥有某个角色
         */
        Map<String, String> filter = new LinkedHashMap<>();
        filter.put("/user/login", "anon");
        filter.put("/admin/login", "anon");
//        filter.put("/administrator/*", "authc");
//        filter.put("/policy", "authc");
        factoryBean.setFilterChainDefinitionMap(filter);
        return factoryBean;
    }


    //DefaultWebSecurityManager
    @Bean("securityManager")
    public DefaultWebSecurityManager getDefaultWebSecurityManager(@Qualifier("userRealm") UserRealm userRealm) {
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        securityManager.setRealm(userRealm);
        return securityManager;
    }

    //realm
    @Bean
    public UserRealm userRealm() {
        return new UserRealm();
    }

}
