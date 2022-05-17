//package com.munity.interceptor;
//
//
//import com.alibaba.fastjson.JSONObject;
//import com.munity.pojo.entity.LoginTicket;
//import com.munity.pojo.entity.User;
//import com.munity.service.UserService;
//import com.munity.util.CookieUtil;
//import com.munity.util.HostHolder;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.lang.Nullable;
//import org.springframework.stereotype.Component;
//import org.springframework.web.bind.annotation.RestController;
//import org.springframework.web.servlet.HandlerInterceptor;
//import org.springframework.web.servlet.ModelAndView;
//
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import java.io.IOException;
//import java.io.PrintWriter;
//import java.util.*;
//
//
//@Component
//
//public class LoginTicketInterceptor implements HandlerInterceptor {
//
//    @Autowired
//    private UserService userService;
//
//    @Autowired
//    private HostHolder hostHolder;
//
//    private Logger logger = LoggerFactory.getLogger(this.getClass());
//
//    @Override
//    public boolean preHandle(HttpServletRequest request,
//                             HttpServletResponse response, Object handler) throws Exception {
//        // 从cookie中获取凭证
//        String ticket = CookieUtil.getValue(request, "ticket");
//
//        if (ticket != null) {
//            // 查询凭证
//            LoginTicket loginTicket = userService.findLoginTicket(ticket);
//            // 检查凭证是否有效
//            if (loginTicket != null && loginTicket.getStatus() == 0
//                    && loginTicket.getExpired().after(new Date())) {
//                // 根据凭证查询用户
//                User user = userService.findUserById(loginTicket.getUserId());
//                hostHolder.setUser(user);
//
//            }
//        }
//        return true;
//    }
//
//    @Override
//    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable ModelAndView modelAndView) throws Exception {
//        // 从ThreadLocal 中得到当前线程持有的user
//        Map<String, Object> map = new HashMap<>();
//        User user = hostHolder.getUser();
//        if (user != null) {
//
//            String u = JSONObject.toJSONString(user);
//
//        }
//    }
//
//
//    private void returnJson(HttpServletResponse response, String json) throws Exception {
//
//        response.setCharacterEncoding("UTF-8");
//        response.setContentType("text/html; charset=utf-8");
//        response.getWriter().println(json);
//
//
//    }
//
//
//    @Override
//    public void afterCompletion(HttpServletRequest request,
//                                HttpServletResponse response, Object handler, Exception ex) throws Exception {
//        // 从ThreadLocal清除数据
//        hostHolder.clear();
//        System.out.println("after");
//    }
//}
