package com.munity.pojo.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class sendMessage implements Serializable {
    private String toName;
    private String content;
}
