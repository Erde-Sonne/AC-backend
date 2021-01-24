package org.orlo.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity(name = "user_unverify")
@Data
public class UserUnVerify {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private  int id;
    private  String username;
    private  String password;
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
