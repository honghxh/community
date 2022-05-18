package com.munity.service;

import com.munity.pojo.entity.Comment;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author Forrest
 * @since 2022-04-29 09:14:21
 */
public interface CommentService extends IService<Comment> {
    public int addComment(Comment comment);
    public int addReply(Comment comment);
    public List<Map<String, Object>> getReplyList(int id);
    public List<Comment> getUserComment(int userId);

}
