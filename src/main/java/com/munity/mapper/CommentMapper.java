package com.munity.mapper;

import com.munity.pojo.entity.Comment;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author Forrest
 * @since 2022-04-29 09:14:21
 */
@Mapper
public interface CommentMapper extends BaseMapper<Comment> {

}
