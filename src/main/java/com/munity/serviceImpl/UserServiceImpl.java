package com.munity.serviceImpl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.munity.common.R;
import com.munity.mapper.UserMapper;
import com.munity.pojo.entity.LoginTicket;
import com.munity.pojo.entity.User;
import com.munity.pojo.model.alterPass;
import com.munity.service.UserService;
import com.munity.util.CommunityUtil;
import com.munity.util.RedisKeyUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author Forrest
 * @since 2022-04-29 09:14:22
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public R<String> register(User user) {
        Map<String, Object> map = new HashMap<>();
//         空值处理
        if (user == null) {
            throw new IllegalArgumentException("参数不能为空!");
        }
        if (StringUtils.isBlank(user.getUsername())) {
            return R.error("账号不能为空!");

        }
        if (StringUtils.isBlank(user.getPassword())) {
            return R.error("密码不能为空!");

        }
        if (StringUtils.isBlank(user.getEmail())) {
            return R.error("邮箱不能为空!");

        }

        // 验证账号
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", user.getUsername());
        User u = userMapper.selectOne(queryWrapper);
        System.out.println(u);

        if (u != null) {
            return R.error("该账号已存在!");

        }

        // 验证邮箱
        queryWrapper.eq("email", user.getEmail());
        u = userMapper.selectOne(queryWrapper);
        if (u != null) {
            return R.error("该邮箱已被注册!");

        }

        // 注册用户
        user.setSalt(CommunityUtil.generateUUID().substring(0, 5));
        user.setPassword(CommunityUtil.md5(user.getPassword() + user.getSalt()));
        user.setType(0);
        user.setStatus(1);
        user.setActivationCode(CommunityUtil.generateUUID());
        user.setHeaderUrl(String.format("http://images.nowcoder.com/head/%dt.png", new Random().nextInt(1000)));
        user.setCreateTime(new Date());
        userMapper.insert(user);
        return R.success("注册成功");
    }

    @Override
    public R<User> login(User u) {
        R<String> r = new R();
        Map<String, Object> map = new HashMap<>();

        // 空值处理
        if (StringUtils.isBlank(u.getUsername())) {
            return R.error("账号不能为空!");

        }
        if (StringUtils.isBlank(u.getPassword())) {
            return R.error("密码不能为空!");

        }

        // 验证账号
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", u.getUsername());
        User user = userMapper.selectOne(queryWrapper);
        if (user == null) {
            return R.error("该账号不存在!");

        }
        if (user.getStatus() == 0) {
            return R.error("该账号未激活!");

        }
        String p = CommunityUtil.md5(u.getPassword() + user.getSalt());
        if (!user.getPassword().equals(p)) {
            return R.error("密码不正确!");
        }

        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setUserId(user.getId());
        loginTicket.setTicket(CommunityUtil.generateUUID());
        loginTicket.setStatus(0);
        loginTicket.setExpired(new Date(System.currentTimeMillis() + 24 * 60 * 6000));
        String redisKey = RedisKeyUtil.getTicketKey(loginTicket.getTicket());
        redisTemplate.opsForValue().set(redisKey, loginTicket);
        System.out.println(loginTicket.toString());
        return R.success(user).add("ticket", loginTicket.getTicket());

    }

    @Override
    public R<String> logout(String ticket) {
//        UpdateWrapper<LoginTicket> updateWrapper = new UpdateWrapper<>();
//        updateWrapper.eq("ticket",ticket).set("status",1);
//        loginTicketMapper.update(null, updateWrapper);
/**
 * 当请求参数传过来当时候要保证它是干净的，当参数放进sql语句中，不会出现问题的。
 * 在mybatis # {} 的预编译下参数将会变为字符串所以参数不用带引号，只需保证它正确干净就行，如果需要对于参数进行处理，那就再说。
 * */
//        loginTicketMapper.updateStatus(1, ticket);
        String redisKey = RedisKeyUtil.getTicketKey(ticket);
        LoginTicket loginTicket = (LoginTicket) redisTemplate.opsForValue().get(redisKey);
        loginTicket.setStatus(1);
        redisTemplate.opsForValue().set(redisKey, loginTicket);
        return R.success("退出成功");
    }

    @Override
    public LoginTicket findLoginTicket(String ticket) {
//        QueryWrapper<LoginTicket> queryWrapper = new QueryWrapper<>();
//        queryWrapper.eq("ticket", ticket);
//        return loginTicketMapper.selectOne(queryWrapper);
        //不再先去数据库中查询而是先去查询redis缓存中的信息，如果有信息直接用，没有再去数据库查询，再放到redis中。
        String redisKey = RedisKeyUtil.getTicketKey(ticket);
        return (LoginTicket) redisTemplate.opsForValue().get(redisKey);
    }

    @Override
    public User findUserById(int id) {
        User user = getCache(id);
        if (user == null) {
            user = initCache(id);
        }
        return user;
//        return userMapper.selectById(id);
    }

    @Override
    public R<String> alterPass(alterPass pass) {
        System.out.println(pass.toString());
        if (StringUtils.isBlank(pass.getUsername())) {
            return R.error("账号不能为空!");
        }
        if (StringUtils.isBlank(pass.getOldPass())) {
            return R.error("原密码不能为空!");

        }
        if (StringUtils.isBlank(pass.getPass())) {
            return R.error("新密码不能为空!");

        }
        if (StringUtils.isBlank(pass.getCheckPass())) {
            return R.error("check密码不能为空!");

        }
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", pass.getUsername());
        User user = userMapper.selectOne(queryWrapper);
        if (user == null) {
            return R.error("该账号不存在!");

        }
        if (user.getStatus() == 0) {
            return R.error("该账号未激活!");

        }
        String p = CommunityUtil.md5(pass.getOldPass() + user.getSalt());
        if (!user.getPassword().equals(p)) {
            return R.error("密码不正确!");
        }

        user.setPassword(CommunityUtil.md5(pass.getPass() + user.getSalt()));

        userMapper.updateById(user);
        return R.success("密码修改成功");


    }

    @Override
    public int uploadHeader(String username, String headerUrl) {
        UpdateWrapper<User> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("username", username).set("header_url", headerUrl);
        int rows = userMapper.update(null, updateWrapper);
        int userId = userMapper.selectIdByUsername(username);
        clearCache(userId);
        return rows;

    }

    // 1.优先从缓存中取值
    private User getCache(int userId) {
        String redisKey = RedisKeyUtil.getUserKey(userId);
        return (User) redisTemplate.opsForValue().get(redisKey);
    }

    // 2.取不到时初始化缓存数据
    private User initCache(int userId) {
        User user = userMapper.selectById(userId);
        String redisKey = RedisKeyUtil.getUserKey(userId);
        redisTemplate.opsForValue().set(redisKey, user, 3600, TimeUnit.SECONDS);
        return user;
    }

    // 3.数据变更时清除缓存数据
    private void clearCache(int userId) {
        String redisKey = RedisKeyUtil.getUserKey(userId);
        redisTemplate.delete(redisKey);
    }

    @Override
    public int findUserIdByUserName(String username) {
        int id = 0;
        id = userMapper.selectIdByUsername(username);
        return id;
    }


}
