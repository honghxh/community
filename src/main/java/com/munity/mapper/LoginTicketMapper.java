package com.munity.mapper;

import com.munity.pojo.entity.LoginTicket;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.*;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *当sql语句正确时，如果执行结果不在预期之内，要求看传进来的参数格式是否正确。
 * 在#{} 预编译的情况下，会讲参数转为字符串的形式。
 * @author Forrest
 * @since 2022-04-29 09:14:22
 */
@Mapper
public interface LoginTicketMapper extends BaseMapper<LoginTicket> {

    @Update({"update login_ticket set status = #{status} where ticket = #{ticket}"})
    int updateStatus(@Param("status")Integer status, @Param("ticket") String ticket);

}
