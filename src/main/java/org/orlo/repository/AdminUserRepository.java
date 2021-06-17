package org.orlo.repository;

import org.orlo.entity.AdminUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminUserRepository extends JpaRepository<AdminUser, Long> {

    AdminUser getAdminUserByPhone(long phone);
}
