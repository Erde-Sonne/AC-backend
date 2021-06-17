package org.orlo.entity;


import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.*;

@Entity(name = "ipconfidence_data")
@Data
@IdClass(IPConfidenceUPK.class)
@Accessors(chain = true)
public class IPConfidenceData {
    @Id
    private long mac;
    @Id
    private long ip;
    private double confidence;
    private double threshold;
}
