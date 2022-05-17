package com.munity.pojo.model;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.munity.pojo.entity.User;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @author forrest
 */
@Data
public class PersonDetail {

    public PersonDetail(User user){
        this.id = user.getId();
        this.username = user.getUsername();
        this.password = user.getPassword();
        this.salt = user.getSalt();
        this.email = user.getEmail();
        this.type = user.getType();
        this.status = user.getStatus();
        this.activationCode = user.getActivationCode();
        this.headerUrl = user.getHeaderUrl();
        this.createTime = user.getCreateTime();

    }


    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @TableField("username")
    private String username;

    @TableField("password")
    private String password;

    @TableField("salt")
    private String salt;

    @TableField("email")
    private String email;

    @ApiModelProperty("0-普通用户; 1-超级管理员; 2-版主;")
    @TableField("type")
    private Integer type;

    @ApiModelProperty("0-未激活; 1-已激活;")
    @TableField("status")
    private Integer status;

    @TableField("activation_code")
    private String activationCode;

    @TableField("header_url")
    private String headerUrl;

    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private Date createTime;

    private long likeCount;

    private long followeeCount;

    private long followerCount;

    private boolean hasFollowed;









}
