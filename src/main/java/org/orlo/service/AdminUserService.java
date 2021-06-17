package org.orlo.service;

import org.orlo.entity.AdminUser;
import org.orlo.repository.AdminUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AdminUserService {
    @Autowired
    AdminUserRepository adminUserRepository;

    public AdminUser getAdminByPhone(long phone) {
        return adminUserRepository.getAdminUserByPhone(phone);
    }

}
