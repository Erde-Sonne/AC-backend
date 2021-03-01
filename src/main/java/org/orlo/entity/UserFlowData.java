package org.orlo.entity;
import lombok.Data;

import javax.persistence.*;


@Entity()
@Data
public class UserFlowData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private  int id;
    private  String keyValue;
    private  String startTime;
    @Lob
    private  String jsonData;
}
