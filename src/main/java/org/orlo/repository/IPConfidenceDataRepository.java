package org.orlo.repository;

import org.orlo.entity.IPConfidenceData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface IPConfidenceDataRepository extends JpaRepository<IPConfidenceData, Integer> {

    IPConfidenceData getIPConfidenceDataByMacAndIp(long mac, long ip);

    IPConfidenceData getIPConfidenceDataByMac(long mac);

    IPConfidenceData getIPConfidenceDataByIp(long ip);

    @Query(value = "select * from ipconfidence_data d where d.mac=?1 and d.ip=?2", nativeQuery = true)
    IPConfidenceData getRow(long mac, long ip);


    @Modifying
    @Query(value = "insert into ipconfidence_data(mac, ip, confidence, threshold) values(?1, ?2, ?3, ?4)", nativeQuery = true)
    void addnewRow(long mac, long ip, double confidence, double threshold);

    @Modifying
    @Query(value = "update ipconfidence_data d set d.confidence = ?3, d.threshold = ?4 where (d.mac = ?1 AND d.ip = ?2)", nativeQuery = true)
    int updateRow(long mac, long ip, double confidence, double threshold);


}
