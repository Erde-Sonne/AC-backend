package org.orlo.repository;

import org.orlo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserRepository extends JpaRepository<User, Integer> {
    @Query(value = "select u from user u where u.username = ?1 and u.password = ?2")
    User getUserByNameAndPassword (String userName, String password) ;
}
