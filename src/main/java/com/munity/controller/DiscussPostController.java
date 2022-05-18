package com.munity.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.munity.common.R;
import com.munity.mapper.UserMapper;
import com.munity.pojo.entity.Comment;
import com.munity.pojo.entity.DiscussPost;
import com.munity.pojo.entity.User;
import com.munity.pojo.model.CommentDetail;
import com.munity.pojo.model.Post;
import com.munity.service.CommentService;
import com.munity.service.DiscussPostService;
import com.munity.service.FollowService;
import com.munity.service.LikeService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

import static com.munity.util.CommunityConstant.ENTITY_TYPE_COMMENT;
import static com.munity.util.CommunityConstant.ENTITY_TYPE_POST;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author Forrest
 * @since 2022-04-29 09:14:22
 */
@Api
@RestController
@RequestMapping("/munity/discussPost")
public class DiscussPostController {

    @Autowired
    DiscussPostService discussPostService;
    @Autowired
    UserMapper userMapper;
    @Autowired
    CommentService commentService;
    @Autowired
    LikeService likeService;
    @Autowired
    FollowService followService;

    @RequestMapping(value = "add", method = RequestMethod.POST)
    public R<String> addPost(@RequestBody DiscussPost discussPost, HttpServletRequest request) {
        User u = (User) request.getSession().getAttribute("user");
        if (u == null) {
           return R.error("你未登陆");
        }
        DiscussPost post = new DiscussPost();
        int id = userMapper.selectIdByUsername(u.getUsername());
        post.setUserId(id);
        post.setTitle(discussPost.getTitle());
        post.setContent(discussPost.getContent());
        post.setCreateTime(new Date());

        if (post == null) {
            R.error("参数为空");
            throw new IllegalArgumentException("参数不能为空!");
        }


        if (discussPostService.addDiscussPost(post) > 0) {
            return R.success("发布成功");
        } else {
            return R.error("发布失败");
        }

    }

    //返回帖子详情
    @GetMapping(value = "/detail")
    public R<Post> postDetail(@RequestParam("discussPostId") int discussPostId,HttpServletRequest request) {
        if (discussPostId == 0) {
            return R.error("该帖子不存在");
        }
        User user = (User)request.getSession().getAttribute("user");
        if(user!=null){
        Post post = discussPostService.findDiscussPostById(discussPostId);
        post.setFollowStatus(followService.hasFollowed(user.getId(),1,discussPostId));
//        post.setLikeStatus(likeService.findEntityLikeStatus(user.getId(),1,discussPostId));
        return  R.success(post);
        }
        Post post = discussPostService.findDiscussPostById(discussPostId);
        return R.success(post);
    }

    //返回帖子评论，返回list类型
    @GetMapping(value = "/comment")
    public R<List<Map<String, Object>>> postCommentDetail(@RequestParam("discussPostId") int discussPostId, @RequestParam(value = "pageNum", defaultValue = "0") int pageNum, @RequestParam(value = "pageSize", defaultValue = "5") int pageSize) {
        if (discussPostId == 0) {
            return R.error("该帖子不存在");
        }
        Post post = discussPostService.findDiscussPostById(discussPostId);
        if (post == null) {
            return R.error("该帖子不存在");
        }

        List<Map<String, Object>> discussPostComment = new ArrayList<>();
        Page<Comment> s = new Page<>(pageNum, pageSize);
        QueryWrapper<Comment> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("entity_type", ENTITY_TYPE_POST);
        queryWrapper.eq("entity_id", post.getId());
        Page<Comment> page = commentService.page(s, queryWrapper);
        List<Comment> records = page.getRecords();
        if (records != null) {
            for (Comment comment : records) {
                Map<String, Object> map = new HashMap<>();
                CommentDetail c = new CommentDetail(comment);

                Page<Comment> s1 = new Page<>(pageNum, pageSize);
                QueryWrapper<Comment> queryWrapper1 = new QueryWrapper<>();
                queryWrapper1.eq("target_id",comment.getId());
                Page<Comment> page1 = commentService.page(s1, queryWrapper1);
                long replyTotal = page1.getTotal();
                c.setTotal(replyTotal);
                User user = userMapper.selectById(comment.getUserId());
                c.setHeaderUrl(user.getHeaderUrl());
                c.setUsername(user.getUsername());
                c.setEmail(user.getEmail());
                c.setCommentLikeCount(likeService.findEntityLikeCount(ENTITY_TYPE_COMMENT,c.getId()));
                map.put("comment", c);
                discussPostComment.add(map);
            }
        }
        return R.success(discussPostComment);
    }


}
