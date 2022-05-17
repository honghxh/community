package com.munity.pojo.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class Reply implements Serializable {
    int id;
    int user_id;
    int entity_id;
    int entity_type;
    int target_id;
    int target_userid;
    String content;
}
