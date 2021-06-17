package org.orlo.entity;

import lombok.*;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class IPConfidenceUPK implements Serializable {
    private long mac;
    private long ip;
}
