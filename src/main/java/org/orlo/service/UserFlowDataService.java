package org.orlo.service;

import org.orlo.entity.UserFlowData;
import org.orlo.repository.UserFlowDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserFlowDataService {
    @Autowired
    UserFlowDataRepository userFlowDataRepository;

    public void saveData(UserFlowData userFlowData) {
        userFlowDataRepository.save(userFlowData);
    }
}
