package org.orlo.repository;

import org.orlo.entity.UserData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserDataRepository  extends JpaRepository<UserData, Integer> {

    @Query(value = "select u from user_data u where u.userName = ?1")
    UserData getUserByName (String userName) ;
}
