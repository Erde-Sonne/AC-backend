package org.orlo.repository;

import org.orlo.entity.UserUnVerify;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.web.bind.annotation.CrossOrigin;

@CrossOrigin
@RepositoryRestResource(path = "unverify")
public interface UserUnVerifyRepository extends JpaRepository<UserUnVerify, Integer> {
    @Query(value = "select u from user_unverify u where u.username = ?1 and u.password = ?2")
    UserUnVerify getUserByNameAndPassword (String userName, String password);


}
