package org.orlo.repository;

import org.orlo.entity.UserUnVerify;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.web.bind.annotation.CrossOrigin;

@CrossOrigin
@RepositoryRestResource(path = "unverify")
public interface UserUnVerifyRepository extends JpaRepository<UserUnVerify, Long> {

    UserUnVerify getUserUnVerifyByPhone(long phone);
}
