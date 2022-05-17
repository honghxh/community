package com.munity.service;

import com.munity.common.R;
import com.munity.pojo.entity.LoginTicket;
import com.munity.pojo.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;
import com.munity.pojo.model.alterPass;

import java.util.Map;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author Forrest
 * @since 2022-04-29 09:14:22
 */
public interface UserService extends IService<User> {
    public R<String> register(User user);
    public R<User> login (User user);
    public R<String> logout(String ticket);
    public LoginTicket findLoginTicket(String ticket);
    public  User findUserById (int id);
    public R<String> alterPass( alterPass pass);
    public int uploadHeader(String username,String headerUrl);
    public int findUserIdByUserName(String username);

}
