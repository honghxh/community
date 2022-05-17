//package com.munity.interceptor;
//
//import com.munity.util.HostHolder;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//import org.springframework.web.method.HandlerMethod;
//import org.springframework.web.servlet.HandlerInterceptor;
////
////import javax.servlet.http.HttpServletRequest;
////import javax.servlet.http.HttpServletResponse;
////import java.lang.reflect.Method;
////
////@Component
////
////public class LoginRequiredInterceptor implements HandlerInterceptor {
////
////    @Autowired
////    private HostHolder hostHolder;
////
////
////
////    @Override
////    public boolean preHandle(HttpServletRequest request,
////                             HttpServletResponse response, Object handler) throws Exception {
////        // 判断handler 是否是 HandlerMethod 类型
////        if (handler instanceof HandlerMethod) {
////            HandlerMethod handlerMethod = (HandlerMethod) handler;
////            // 获取到方法实例
////            Method method = handlerMethod.getMethod();
////            // 从方法实例中获得其 LoginRequired 注解
////            LoginRequired loginRequired = method.getAnnotation(LoginRequired.class);
////            // 如果方法实例上标注有 LoginRequired 注解,但 hostHandler中没有 用户信息则拦截
////            if (loginRequired != null && hostHolder.getUser() == null) {
////                response.sendRedirect(request.getContextPath() + "/login");
////                return false;
////            }
////        }
////        return true;
////    }
