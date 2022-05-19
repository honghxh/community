package com.munity.serviceImpl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.munity.mapper.UserMapper;
import com.munity.pojo.entity.DiscussPost;
import com.munity.mapper.DiscussPostMapper;
import com.munity.pojo.entity.User;
import com.munity.pojo.model.Post;
import com.munity.service.DiscussPostService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.munity.service.FollowService;
import com.munity.service.LikeService;
import com.munity.util.SensitiveFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.munity.util.CommunityConstant.ENTITY_TYPE_POST;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author Forrest
 * @since 2022-04-29 09:14:22
 */
@Service
public class DiscussPostServiceImpl extends ServiceImpl<DiscussPostMapper, DiscussPost> implements DiscussPostService {
    @Autowired
    DiscussPostMapper discussPostMapper;

    @Autowired
    SensitiveFilter sensitiveFilter;

    @Autowired
    UserMapper userMapper;

    @Autowired
    LikeService likeService;
    @Autowired
    FollowService followService;
    @Override
    public int addDiscussPost(DiscussPost discussPost) {

        discussPost.setTitle(sensitiveFilter.filter(discussPost.getTitle()));
        discussPost.setContent(sensitiveFilter.filter(discussPost.getContent()));
        return discussPostMapper.insert(discussPost);
    }

    @Override
    public Post findDiscussPostById(int discussPostId) {
       DiscussPost post = discussPostMapper.selectById(discussPostId);
       if(post == null ){
           return null;
       }
       User user =  userMapper.selectById(post.getUserId());
       Post p = new Post(post);
       p.setUserId(user.getId());
       p.setHeaderUrl(user.getHeaderUrl());
       p.setUsername(user.getUsername());
       p.setEmail(user.getEmail());
       p.setLikeCount(likeService.findEntityLikeCount(ENTITY_TYPE_POST,p.getId()));
       p.setFollowCount(followService.findFollowerCount(ENTITY_TYPE_POST,discussPostId));
       return p;
    }

    @Override
    public List<DiscussPost> getUserPost(int userId) {
        QueryWrapper<DiscussPost> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        return discussPostMapper.selectList(queryWrapper);
    }

    @Override
    public DiscussPost selectById(int discussPostId) {
      return   discussPostMapper.selectById(discussPostId);
    }


}
