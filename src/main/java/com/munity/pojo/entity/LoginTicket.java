package com.munity.pojo.entity;

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
@TableName("login_ticket")
@ApiModel(value = "LoginTicket对象", description = "")
public class LoginTicket implements Serializable {

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @TableField("user_id")
    private Integer userId;

    @TableField("ticket")
    private String ticket;

    @ApiModelProperty("0-有效; 1-无效;")
    @TableField("status")
    private Integer status;

    @TableField("expired")
    private Date expired;


}
