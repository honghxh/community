package com.munity.pojo.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class AddLike {
    @ApiModelProperty("被关注的用户to//点赞的实体id")
    private int entityId;
    @ApiModelProperty("关注的类型//点赞的实体类型")
    private int entityType;
    @ApiModelProperty("关注的用户from用登陆用户id//收到赞的用户点赞指向的用户")
    private int entityUserId;
}
