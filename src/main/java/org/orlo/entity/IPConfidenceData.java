package org.orlo.entity;


import lombok.Data;

import javax.persistence.*;

@Entity()
@Data
public class IPConfidenceData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private  int id;
    private String ip;
    private String mac;
    private String confidence;
    private String threshold;
}
