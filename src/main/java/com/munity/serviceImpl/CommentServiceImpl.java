package com.munity.serviceImpl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.munity.common.R;
import com.munity.mapper.DiscussPostMapper;
import com.munity.mapper.UserMapper;
import com.munity.pojo.entity.Comment;
import com.munity.mapper.CommentMapper;
import com.munity.pojo.entity.DiscussPost;
import com.munity.pojo.entity.User;
import com.munity.pojo.model.CommentDetail;
import com.munity.pojo.model.Post;
import com.munity.service.CommentService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.munity.util.SensitiveFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.munity.util.CommunityConstant.*;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author Forrest
 * @since 2022-04-29 09:14:21
 */
@Service
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment> implements CommentService {

    @Autowired
    SensitiveFilter sensitiveFilter;
    @Autowired
    CommentMapper commentMapper;
    @Autowired
    DiscussPostMapper discussPostMapper;
    @Autowired
    UserMapper userMapper;

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public int addComment(Comment comment) {
        comment.setContent(sensitiveFilter.filter(comment.getContent()));
        int row = commentMapper.insert(comment);
        QueryWrapper<Comment> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("entity_id", comment.getEntityId());
        queryWrapper.eq("entity_type", comment.getEntityType());
        long count = commentMapper.selectCount(queryWrapper);
        UpdateWrapper<DiscussPost> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", comment.getEntityId()).set("comment_count", count);
        discussPostMapper.update(null, updateWrapper);

        return row;
    }

    @Override
    public int addReply(Comment comment) {
        comment.setContent(sensitiveFilter.filter(comment.getContent()));
        int row = commentMapper.insert(comment);
        return row;
    }

    @Override
    public List<Map<String, Object>> getReplyList(int id) {
        List<Map<String, Object>> replyList = new ArrayList<>();
        Page<Comment> s = new Page<>(0, Integer.MAX_VALUE);
        QueryWrapper<Comment> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("target_id", id);
        Page<Comment> page1 = commentMapper.selectPage(s, queryWrapper);
        List<Comment> records1 = page1.getRecords();
        if (records1 != null) {
            for (Comment comment : records1) {
                Map<String, Object> map = new HashMap<>();
                CommentDetail c = new CommentDetail(comment);
                User user = userMapper.selectById(comment.getUserId());
                User replyUser = userMapper.selectById(comment.getTargetUserId());
                if (replyUser != null) {
                    c.setReplyName(replyUser.getUsername());

                }
                c.setUsername(user.getUsername());
                map.put("comment", c);
                replyList.add(map);
            }
        }

        return replyList;
    }

    public List<Comment> getUserComment(int userId) {
        QueryWrapper<Comment> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);

        return commentMapper.selectList(queryWrapper);
    }
}
