package com.munity.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.munity.common.R;
import com.munity.mapper.UserMapper;
import com.munity.pojo.entity.Comment;
import com.munity.pojo.entity.LoginTicket;
import com.munity.pojo.entity.Message;
import com.munity.pojo.entity.User;
import com.munity.service.UserService;
import com.munity.util.CookieUtil;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.Map;

/**
 * @author forrest
 */
@RestController
@Api
public class LoginController {

    @Autowired
    private UserService userService;
    @Autowired
    private UserMapper userMapper;

    @GetMapping(value = "/person")
    public R<User> person(HttpServletRequest request) {
        String sessionId = CookieUtil.getValue(request, "JSESSIONID");
        String ticket = (String) request.getSession().getAttribute("ticket");
        System.out.println(sessionId);
        System.out.println(ticket);
        if (ticket != null) {
            // 查询凭证
            LoginTicket loginTicket = userService.findLoginTicket(ticket);
            // 检查凭证是否有效
            if (loginTicket != null && loginTicket.getStatus() == 0
                    && loginTicket.getExpired().after(new Date())) {
                // 根据凭证查询用户
                User user = userService.findUserById(loginTicket.getUserId());
                return R.success(user);
            }
        }
        return R.error("未登陆");
    }

    @ApiOperation(value = "登陆接口")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successful — 请求已完成"),
            @ApiResponse(code = 400, message = "请求中有语法问题，或不能满足请求"),
            @ApiResponse(code = 403, message = "服务器拒绝请求"),
            @ApiResponse(code = 401, message = "未授权客户机访问数据"),
            @ApiResponse(code = 404, message = "服务器找不到给定的资源；文档不存在"),
            @ApiResponse(code = 500, message = "服务器不能完成请求")}
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "username", value = "用户名"),
            @ApiImplicitParam(name = "password", value = "密码")
    })
    @RequestMapping(value = "login", method = RequestMethod.POST)
    public R<User> login(@RequestBody User user, HttpServletRequest request,HttpServletResponse response) throws IOException {
        R<User> r = userService.login(user);
        if (r.getCode() == 1) {
            request.getSession().setAttribute("ticket", r.getMap().get("ticket").toString());
            QueryWrapper<User> queryWrapper1 = new QueryWrapper<>();
            queryWrapper1.eq("username",user.getUsername());
            user = userMapper.selectOne(queryWrapper1);
            request.getSession().setAttribute("user",user);
            return r;
        } else {
            return r;
        }
    }

    @ApiOperation(value = "注册接口")
    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public R<String> register(@RequestBody User user) {
        return userService.register(user);

    }

    @ApiOperation(value = "退出接口")
    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    public R<String> logout(HttpServletRequest request, @CookieValue("JSESSIONID") String JSESSIONID) {
        request.getSession().removeAttribute("ticket");
        request.getSession().removeAttribute("user");
        return userService.logout(JSESSIONID);


    }
}