package com.munity.pojo.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class addComment implements Serializable {
    int discussPostId;
    String content;
}
