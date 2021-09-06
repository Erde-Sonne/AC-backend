package org.orlo.repository;

import org.orlo.entity.Policy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.web.bind.annotation.CrossOrigin;

@CrossOrigin
@RepositoryRestResource(path = "policy")
public interface PolicyRepository extends JpaRepository<Policy, Integer> {
    @Query(value = "select u from policy u where u.dstIP = ?1")
    Policy getUserByName (String dstIP) ;

    @Modifying
    @Query(value = "update policy u set u.policycol = ?1 where u.dstIP = ?2")
    void updateByIP (String policyStr, String ipAddr);

    @Modifying
    @Query(value = "delete from policy u where u.dstIP = ?1")
    void deleteByDstIP(String dstIP);
}
