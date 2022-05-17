package com.munity.controller;

import com.munity.common.R;
import com.munity.pojo.entity.User;
import com.munity.pojo.model.AddLike;
import com.munity.pojo.model.Like;
import com.munity.service.LikeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("munity")
public class LikeController {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private LikeService likeService;

    @RequestMapping(value = ("/like"),method = RequestMethod.POST)
    public R<Like> like(@RequestBody AddLike addLike, HttpServletRequest request){
        R<Like> r = new R<>();
        User user = (User)request.getSession().getAttribute("user");
        if(user ==null){
            return R.error("未登陆");
        }
        likeService.like(user.getId(),addLike.getEntityType(),addLike.getEntityId(),addLike.getEntityUserId());
        Like like = new Like();
        long likeCount = likeService.findEntityLikeCount(addLike.getEntityType(), addLike.getEntityId());
        int likeStatus = likeService.findEntityLikeStatus(user.getId(), addLike.getEntityType(),addLike.getEntityId());
        like.setLikeCount(likeCount);
        like.setLikeStatus(likeStatus);
        r.setCode(1);
        r.setMsg("点赞成功");
        r.setData(like);
        return r;
    }


}
