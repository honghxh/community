package com.munity.mapper;

import com.munity.pojo.entity.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author Forrest
 * @since 2022-04-29 09:14:22
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {
    @Select("select id from user where username = #{username}")
    public int selectIdByUsername(@Param("username") String username);
}
