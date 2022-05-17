package com.munity.pojo.model;

import com.baomidou.mybatisplus.annotation.*;
import com.munity.pojo.entity.Comment;
import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@TableName("commentDetail")
@ApiModel(value = "CommentDetail对象", description = "传输评论详情的实体类")
public class CommentDetail implements Serializable {
    public CommentDetail(Comment comment) {
        this.id = comment.getId();
        this.userId = comment.getUserId();
        this.entityType = comment.getEntityType();
        this.entityId = comment.getEntityId();
        this.targetId = comment.getTargetId();
        this.content = comment.getContent();
        this.status = comment.getStatus();
        this.createTime = comment.getCreateTime();
        this.targetUserId = comment.getTargetUserId();
    }

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @TableField("user_id")
    private Integer userId;

    @TableField("username")
    private String username;

    @TableField("email")
    private String email;

    @TableField("header_url")
    private String headerUrl;

    @TableField("entity_type")
    private Integer entityType;

    @TableField("entity_id")
    private Integer entityId;

    @TableField("target_id")
    private Integer targetId;

    @TableField("content")
    private String content;

    @TableField("status")
    private Integer status;

    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private Date createTime;

    @TableField("target_userid")
    private Integer targetUserId;

    private String replyName;

    private long commentLikeCount;

    private Long total;

}


