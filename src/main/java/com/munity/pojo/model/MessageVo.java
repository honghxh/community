package com.munity.pojo.model;

import com.baomidou.mybatisplus.annotation.*;
import com.munity.pojo.entity.Message;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;


import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

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

@ApiModel(value = "传输的Message对象", description = "")
public class MessageVo implements Serializable {


    private Integer id;

    private Integer fromId;


    private Integer toId;


    private String conversationId;

    @TableField("content")
    private String content;

    @ApiModelProperty("0-未读;1-已读;2-删除;")
    private Integer status;

    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private Date createTime;

    private String toUserName;

    private String toUserHeader;
    @TableField("username")
    private String username;

    @TableField("header_url")
    private String headerUrl;

    private int letterCount;

    private int letterUnreadCount;


    public MessageVo(Message mes) {
        this.content = mes.getContent();
        this.createTime=mes.getCreateTime();
        this.id = mes.getId();
        this.fromId = mes.getFromId();
        this.toId = mes.getToId();
        this.conversationId =mes.getConversationId();
        this.status=mes.getStatus();
    }
}
