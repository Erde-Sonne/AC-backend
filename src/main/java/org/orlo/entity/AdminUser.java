package org.orlo.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity()
@Data
public class AdminUser {
    @Id
    private long phone;
    private  String username;
    private  String password;
    private  String salt;
}
