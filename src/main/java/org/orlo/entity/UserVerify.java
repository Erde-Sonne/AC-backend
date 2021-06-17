package org.orlo.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity(name = "user_verify")
@Data
public class UserVerify {
    @Id
    private long phone;
    private  String username;
    private  String password;
    private String salt;
    private  String safe;
    private  String device;
    private  String time;
    private  String department;
    private  String type;
    private  String ip;
    private  String MAC;
    private  String switcher;
    private  String port;
}
