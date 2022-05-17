package com.example.community;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.munity.pojo.entity.DiscussPost;
import com.munity.pojo.entity.User;
import com.munity.service.DiscussPostService;
import com.munity.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class home {
    @Autowired
    UserService userService;

    @Autowired
    DiscussPostService discussPostService;

@Test
    public List<Map<String, Object>> listTable(@RequestParam(value = "pageNum", defaultValue = "0") int pageNum, @RequestParam(value = "pageSize", defaultValue = "5") int pageSize) {
        Map<String, Object> result = new HashMap<>();
        List<Map<String, Object>> discussPosts = new ArrayList<>();
        Page<DiscussPost> s = new Page<>(pageNum, pageSize);
        Page<DiscussPost> page = discussPostService.page(s);
        List<DiscussPost> records = page.getRecords();
        Long total = page.getTotal();
        result.put("total", total);
        for (DiscussPost post : records) {
            User user = userService.getById(post.getUserId());
            result.put("records", records);
            result.put("post", post);

        }
        discussPosts.add(result);
        System.out.println(discussPosts.toString());
        return discussPosts;
    }
}
