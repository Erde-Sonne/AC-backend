package org.orlo.repository;

import org.orlo.entity.UserVerify;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.web.bind.annotation.CrossOrigin;

@CrossOrigin
@RepositoryRestResource(path = "verify")
public interface UserVerifyRepository extends JpaRepository<UserVerify, Integer> {
    @Query(value = "select u from user_verify u where u.username = ?1 and u.password = ?2")
    UserVerify getUserByNameAndPassword (String userName, String password) ;
    @Query(value = "select u from user_verify u where u.MAC = ?1 and u.switcher = ?2")
    UserVerify getUserByMacAndSwitcher (String mac, String switcher) ;
}
