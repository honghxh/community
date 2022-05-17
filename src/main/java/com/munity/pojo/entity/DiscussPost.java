package com.munity.pojo.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

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
@TableName("discuss_post")
@ApiModel(value = "DiscussPost对象", description = "")
public class DiscussPost implements Serializable {

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


}
