package com.munity.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.munity.common.R;
import com.munity.event.EventProducer;
import com.munity.mapper.CommentMapper;
import com.munity.mapper.UserMapper;
import com.munity.pojo.entity.Comment;
import com.munity.pojo.entity.DiscussPost;
import com.munity.pojo.entity.Event;
import com.munity.pojo.entity.User;
import com.munity.pojo.model.CommentDetail;
import com.munity.pojo.model.Reply;
import com.munity.pojo.model.addComment;
import com.munity.service.CommentService;
import com.munity.service.DiscussPostService;
import com.munity.service.LikeService;
import com.munity.service.UserService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

import static com.munity.util.CommunityConstant.*;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author Forrest
 * @since 2022-04-29 09:14:21
 */
@Api
@RestController
@RequestMapping("/munity/comment")
public class CommentController {

    @Autowired
    CommentService commentService;

    @Autowired
    UserService userService;

    @Autowired
    UserMapper userMapper;

    @Autowired
    CommentMapper commentMapper;

    @Autowired
    LikeService likeService;

    @Autowired
    DiscussPostService discussPostService;

    @Autowired
    private EventProducer eventProducer;

    @RequestMapping(path = "/add", method = RequestMethod.POST)
    public R<List<Map<String, Object>>> addComment(@RequestBody addComment addComment, HttpServletRequest request) {
        R<List<Map<String, Object>>> r = new R<>();
        User user = (User) request.getSession().getAttribute("user");
        if (user == null) {
            return R.error("未登陆，无法评论");
        }
        Comment comment = new Comment();
        //修改
        comment.setUserId(user.getId());
        comment.setStatus(0);
        comment.setEntityType(1);
        comment.setTargetId(0);
        comment.setContent(addComment.getContent());
        comment.setEntityId(addComment.getDiscussPostId());
        comment.setCreateTime(new Date());

        Event event = new Event()
                .setTopic(TOPIC_COMMENT)
                .setUserId(user .getId())
                .setEntityType(comment.getEntityType())
                .setEntityId(comment.getEntityId())
                .setData("postId", addComment.getDiscussPostId());
        if (comment.getEntityType() == ENTITY_TYPE_POST) {
            DiscussPost target = discussPostService.selectById(comment.getEntityId());
            event.setEntityUserId(target.getUserId());
        } else if (comment.getEntityType() == ENTITY_TYPE_COMMENT) {
            Comment target = commentService.selectById(comment.getEntityId());
            event.setEntityUserId(target.getUserId());
        }
        eventProducer.fireEvent(event);

        if (commentService.addComment(comment) > 0) {
            List<Map<String, Object>> discussPostComment = new ArrayList<>();
            Page<Comment> s = new Page<>(0, 5);
            QueryWrapper<Comment> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("entity_type", ENTITY_TYPE_POST);
            queryWrapper.eq("entity_id", addComment.getDiscussPostId());
            Page<Comment> page = commentService.page(s, queryWrapper);
            List<Comment> records = page.getRecords();
            Map<String, Object> t = new HashMap<>();
            long total = page.getTotal();
            t.put("total", total);
            if (records != null) {
                for (Comment com : records) {
                    Map<String, Object> map = new HashMap<>();
                    CommentDetail c = new CommentDetail(com);
                    c.setHeaderUrl(user.getHeaderUrl());
                    c.setUsername(user.getUsername());
                    c.setEmail(user.getEmail());
                    map.put("comment", c);
                    discussPostComment.add(map);
                }
            }
            r.setData(discussPostComment);
            r.setMap(t);
            r.setMsg("评论成功");
            r.setCode(1);
            return r;
        } else {
            return R.error("评论失败");
        }


    }


    @RequestMapping(value = "/addReply", method = RequestMethod.POST)
    public R<String> addReply(@RequestBody Reply reply, HttpServletRequest request) {
        if (reply == null) {
            return R.error("回复失败");
        }
        Comment comment = new Comment();
        comment.setEntityId(reply.getEntity_id());
        if (reply.getEntity_type() == 3) {
            comment.setTargetId(commentMapper.selectById(reply.getId()).getTargetId());
        } else {
            comment.setTargetId(reply.getId());
        }
        comment.setContent(reply.getContent());
        User user = (User) request.getSession().getAttribute("user");
        if (user == null) {
            return R.error("您还未登陆");
        }
        comment.setUserId(userMapper.selectIdByUsername(user.getUsername()));
        comment.setEntityType(reply.getEntity_type());

        if (reply.getEntity_type() == 3) {
            comment.setTargetUserId(reply.getUser_id());
        }
        comment.setCreateTime(new Date());
        commentService.addReply(comment);
        return R.success("回复成功");
    }


    @RequestMapping(value = "/replyList", method = RequestMethod.GET)
    public R<List<Map<String, Object>>> getReplyList(@RequestParam("id") int id) {
        if (id == 0) {
            return R.error("获取失败");
        }
        R<List<Map<String, Object>>> r = new R<>();
        List<Map<String, Object>> replyList = new ArrayList<>();
        Page<Comment> s = new Page<>(0, Integer.MAX_VALUE);
        QueryWrapper<Comment> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("target_id", id);
        Page<Comment> page1 = commentService.page(s, queryWrapper);
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
                c.setCommentLikeCount(likeService.findEntityLikeCount(ENTITY_TYPE_COMMENT, c.getId()));
                c.setUsername(user.getUsername());
                map.put("comment", c);
                replyList.add(map);
            }
        }

        r.setData(replyList);
        r.setCode(1);
        r.setMsg("请求成功");


        return R.success(replyList);
    }


}
