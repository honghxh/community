package com.munity.controller;

import com.munity.common.R;
import com.munity.event.EventProducer;
import com.munity.pojo.entity.Event;
import com.munity.pojo.entity.User;
import com.munity.pojo.model.AddLike;
import com.munity.pojo.model.Followees;
import com.munity.pojo.model.Post;
import com.munity.service.DiscussPostService;
import com.munity.service.FollowService;
import com.munity.service.UserService;
import com.munity.util.CommunityConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author forrest
 */
@RestController
public class FollowController implements CommunityConstant {

    @Autowired
    private FollowService followService;

    @Autowired
    private UserService userService;

    @Autowired
    private EventProducer eventProducer;

    @Autowired
    private DiscussPostService discussPostService;


    @RequestMapping(path = "/follow", method = RequestMethod.POST)
    public R<String> follow(@RequestBody AddLike addLike, HttpServletRequest request) {
        User user = (User) request.getSession().getAttribute("user");
        if(user == null) {
            return R.error("未登陆");
        }
        if(followService.hasFollowed(user.getId(),addLike.getEntityType(),addLike.getEntityId())){
            followService.unfollow(user.getId(), addLike.getEntityType(), addLike.getEntityId());
            R.success("取消关注");
        }
        else{
        followService.follow(user.getId(), addLike.getEntityType(), addLike.getEntityId());
        if(addLike.getEntityType()==1){
            Event event = new Event()
                    .setTopic(TOPIC_FOLLOW)
                    .setUserId(user.getId())
                    .setEntityType(addLike.getEntityType())
                    .setEntityId(addLike.getEntityId())
                    .setEntityUserId(discussPostService.selectById(addLike.getEntityId()).getUserId());
            eventProducer.fireEvent(event);
        }
        else{
            Event event = new Event()
                    .setTopic(TOPIC_FOLLOW)
                    .setUserId(user.getId())
                    .setEntityType(addLike.getEntityType())
                    .setEntityId(addLike.getEntityId())
                    .setEntityUserId(addLike.getEntityId());
            eventProducer.fireEvent(event);}
        }
        return R.success("关注成功");
    }

    @RequestMapping(path = "/unfollow", method = RequestMethod.POST)
    public R<String> unfollow(@RequestBody AddLike addLike, HttpServletRequest request) {
        User user = (User) request.getSession().getAttribute("user");
        if(user == null) {
            return R.error("未登陆");
        }
        followService.unfollow(user.getId(), addLike.getEntityType(), addLike.getEntityId());

        return R.success("success");
    }

    @RequestMapping(path = "/followees/{userId}", method = RequestMethod.GET)
    public R<List<Followees>> getFollowees(@PathVariable("userId") int userId,@RequestParam(value = "pageNum", defaultValue = "0") int pageNum, @RequestParam(value = "pageSize", defaultValue = "5") int pageSize) {
        User user = userService.findUserById(userId);
        if (user == null) {
            return R.error("该用户不存在");
        }
       long total = followService.findFolloweeCount(userId, ENTITY_TYPE_USER);
        List<Followees> followeesList = followService.findFollowees(userId,pageNum,pageSize);
        R<List<Followees>> r = new R<>();
        r.setData(followeesList);
        r.setCode(1);
        r.setMsg("获取关注列表成功");
        return r;
    }

    @RequestMapping(path = "/followers/{userId}", method = RequestMethod.GET)
    public R<List<Followees>> getFollowers(@PathVariable("userId") int userId,@RequestParam(value = "pageNum", defaultValue = "0") int pageNum, @RequestParam(value = "pageSize", defaultValue = "5") int pageSize) {
        User user = userService.findUserById(userId);
        if (user == null) {
            return R.error("该用户不存在");
        }
        long total = followService.findFolloweeCount(userId, ENTITY_TYPE_USER);
        List<Followees> followeesList = followService.findFollowers(userId,pageNum,pageSize);
        R<List<Followees>> r = new R<>();
        r.setData(followeesList);
        r.setCode(1);
        r.setMsg("获取关注列表成功");
        return r;
    }


    @RequestMapping(path = "/followerPost/{id}", method = RequestMethod.GET)
    public R<List<Post>> getFollowerPost(@PathVariable("id") int id, HttpServletRequest request, @RequestParam(value = "pageNum", defaultValue = "0") int pageNum, @RequestParam(value = "pageSize", defaultValue = "5") int pageSize) {
        User user = (User) request.getSession().getAttribute("user");
        if (user == null) {
            return R.error("未登陆");
        }
        long total = followService.findFolloweeCount(id, ENTITY_TYPE_POST);
        List<Post> postList = followService.findFolloweesPost(id,pageNum,pageSize);
        R<List<Post>> r = new R<>();
        r.setData(postList);
        r.setCode(1);
        r.setMsg("获取关注列表成功");
        return r;
    }

}
