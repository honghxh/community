package com.munity.pojo.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class Like implements Serializable {
    private long likeCount;
    private int likeStatus;

}
