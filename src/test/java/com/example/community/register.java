package com.example.community;

import com.munity.CommunityApplication;
import com.munity.pojo.entity.User;
import com.munity.mapper.UserMapper;
import com.munity.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class register {
@Autowired
   private UserMapper userMapper;
@Autowired
   private UserService userService;
@Test
    public void main (){
User user = new User();
user.setUsername("文文");
user.setPassword("123");
user.setEmail("3@qq.com");
userService.register(user);
}

}
