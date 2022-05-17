package com.munity.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.munity.common.R;
import com.munity.pojo.entity.DiscussPost;
import com.munity.pojo.entity.User;
import com.munity.pojo.model.Post;
import com.munity.service.DiscussPostService;
import com.munity.service.LikeService;
import com.munity.service.UserService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.munity.util.CommunityConstant.ENTITY_TYPE_POST;

/**
 * @author forrest
 */
@Api
@RestController
public class HomeController {
    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private UserService userService;

    @Autowired
    private LikeService likeService;

    @RequestMapping(value = "/home", method = RequestMethod.GET)
    public R<List<Map<String, Object>>>listTable(@RequestParam(value = "pageNum", defaultValue = "0") int pageNum, @RequestParam(value = "pageSize", defaultValue = "5") int pageSize) {
        Page<DiscussPost> s = new Page<>(pageNum, pageSize);
        R<List<Map<String, Object>>> r = new R<>();
        Page<DiscussPost> page = discussPostService.page(s);
        Long total = page.getTotal();
        List<DiscussPost> records = page.getRecords();
        List<Map<String, Object>> discussPosts = new ArrayList<>();
        Map<String, Object> totals = new HashMap<>();
        totals.put("total",total);
        if (records != null) {
            for (DiscussPost post : records) {
                Map<String, Object> map = new HashMap<>();
                Post p = new Post(post);
                User user = userService.getById(post.getUserId());
                p.setUsername(user.getUsername());
                p.setHeaderUrl(user.getHeaderUrl());
                p.setLikeCount(likeService.findEntityLikeCount(ENTITY_TYPE_POST,p.getId()));

                map.put("p", p);
                discussPosts.add(map);
            }
        }
        r.setCode(1);
        r.setMsg("获取数据成功");
        r.setData(discussPosts);
        r.setMap(totals);
        return r;
    }
}

