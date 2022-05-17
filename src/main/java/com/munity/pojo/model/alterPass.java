package com.munity.pojo.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class alterPass implements Serializable {
    String username;
    String oldPass;
    String Pass;
    String checkPass;
}
