package com.munity.pojo.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;
import io.swagger.annotations.ApiModel;
import lombok.*;

/**
 * <p>
 * 
 * </p>
 *
 * @author Forrest
 * @since 2022-04-29 09:14:21
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@TableName("comment")
@ApiModel(value = "Comment对象", description = "")
public class Comment implements Serializable {

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @TableField("user_id")
    private Integer userId;

    @TableField("entity_type")
    private Integer entityType;

    @TableField("entity_id")
    private Integer entityId;

    @TableField("target_id")
    private Integer targetId;

    @TableField("target_userid")
    private Integer targetUserId;

    @TableField("content")
    private String content;

    @TableField("status")
    private Integer status;


    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private Date createTime;




}
