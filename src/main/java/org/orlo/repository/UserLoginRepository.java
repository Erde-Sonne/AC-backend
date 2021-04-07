package org.orlo.repository;

import org.orlo.entity.User;
import org.orlo.entity.UserLogin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserLoginRepository extends JpaRepository<UserLogin, Integer> {
    @Query(value = "select u from user u where u.username = ?1 and u.password = ?2")
    UserLogin getUserByNameAndPassword (String userName, String password) ;
}
