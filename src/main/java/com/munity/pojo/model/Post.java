package com.munity.pojo.model;

import com.baomidou.mybatisplus.annotation.*;
import com.munity.pojo.entity.DiscussPost;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 *
 * </p>
 *
 * @author Forrest
 * @since 2022-04-29 09:14:22
 */
@Getter
@Setter
@ApiModel(value = "Post对象", description = "")
public class Post implements Serializable {

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @TableField("user_id")
    private Integer userId;

    @TableField("title")
    private String title;

    @TableField("content")
    private String content;

    @ApiModelProperty("0-普通; 1-置顶;")
    @TableField("type")
    private Integer type;

    @ApiModelProperty("0-正常; 1-精华; 2-拉黑;")
    @TableField("status")
    private Integer status;

    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private Date createTime;

    @TableField("comment_count")
    private Integer commentCount;

    @TableField("score")
    private Double score;

    @TableField("username")
    private String username;


    @TableField("email")
    private String email;

    @ApiModelProperty("0-普通用户; 1-超级管理员; 2-版主;")
    @TableField("userType")
    private Integer usertype;

    @ApiModelProperty("0-未激活; 1-已激活;")
    @TableField("userStatus")
    private Integer userStatus;

    @TableField("activation_code")
    private String activationCode;

    @TableField("header_url")
    private String headerUrl;

    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private Date userCreateTime;

    private long likeCount;

    private int likeStatus;

    private long followCount;

    private boolean followStatus = false;

    private Date followTime;

    public Post(DiscussPost post) {
        this.id = post.getId();
        this.title=post.getTitle();
        this.content = post.getContent();
        this.score= post.getScore();
        this.type  =post.getType();
        this.userId = post.getUserId();
        this.score = post.getScore();
        this.commentCount = post.getCommentCount();
        this.createTime = post.getCreateTime();
        this.status = post.getStatus();
    }
}
