package org.orlo.service;

import org.orlo.entity.IPConfidenceData;
import org.orlo.repository.IPConfidenceDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
public class IPConfidenceDataService {
    @Autowired
    IPConfidenceDataRepository ipConfidenceDataRepository;

    public IPConfidenceData getRow(long mac, long ip) {
        return ipConfidenceDataRepository.getRow(mac, ip);
    }

    public IPConfidenceData getIPConfidenceDataByMac(long mac) {
        return ipConfidenceDataRepository.getIPConfidenceDataByMac(mac);
    }

    public IPConfidenceData getIPConfidenceDataByIp(long ip) {
        return ipConfidenceDataRepository.getIPConfidenceDataByIp(ip);
    }

    @Transactional
    public void addRow(IPConfidenceData data) {
        ipConfidenceDataRepository.addnewRow(data.getMac(), data.getIp(), data.getConfidence(), data.getThreshold());
    }

    public void addRows(Iterable<IPConfidenceData> iterable) {
        ipConfidenceDataRepository.saveAll(iterable);
    }

    @Transactional
    public void updateRow(IPConfidenceData data) {
        ipConfidenceDataRepository.updateRow(data.getMac(), data.getIp(), data.getConfidence(), data.getThreshold());
    }
}
