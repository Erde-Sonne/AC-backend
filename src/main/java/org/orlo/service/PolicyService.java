package org.orlo.service;

import org.orlo.entity.Policy;
import org.orlo.repository.PolicyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional
public class PolicyService {
    @Autowired
    PolicyRepository policyRepository;

    public Policy getPolicyByIP(String dstIP) {
        return policyRepository.getUserByName(dstIP);
    }

    public void setPolicy(Policy policy){
        policyRepository.save(policy);
    }

    public void deletePolicyByIP(String dstIP){
        policyRepository.deleteByDstIP(dstIP);
    }

    public void updatePolicy(String policyStr, String ipAddr){
        policyRepository.updateByIP(policyStr, ipAddr);
    }
}