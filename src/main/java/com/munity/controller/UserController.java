package com.munity.controller;

import com.munity.common.R;
import com.munity.mapper.UserMapper;
import com.munity.pojo.entity.User;
import com.munity.pojo.model.PersonDetail;
import com.munity.pojo.model.alterPass;
import com.munity.service.FollowService;
import com.munity.service.LikeService;
import com.munity.service.UserService;
import com.munity.util.CommunityUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author Forrest
 * @since 2022-04-29 09:14:22
 */
@RestController
public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Value("${community.path.upload}")
    private String uploadPath;

    @Value("${community.path.domain}")
    private String domain;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Autowired
    UserService userService;
    @Autowired
    LikeService likeService;
    @Autowired
    FollowService followService;


    @RequestMapping(value = "/alterPass", method = RequestMethod.POST)
    public R<String> alterPass(@RequestBody alterPass alterPass) {
        return userService.alterPass(alterPass);
    }

    @RequestMapping(value = "/uploadHeader", method = RequestMethod.POST)
    public R<String> uploadHeader(@RequestBody MultipartFile file, HttpServletRequest request) {
        if (file == null) {
            return R.error("您还没有选择图片!");
        }
        String fileName = file.getOriginalFilename();
        String suffix = fileName.substring(fileName.lastIndexOf("."));
        if (StringUtils.isBlank(suffix)) {
            return R.error("文件的格式不正确!");
        }

        fileName = CommunityUtil.generateUUID() + suffix;
        // 确定文件存放的路径
        File dest = new File(uploadPath + "/" + fileName);
        try {
            // 存储文件
            file.transferTo(dest);
        } catch (IOException e) {
            logger.error("上传文件失败: " + e.getMessage());
            throw new RuntimeException("上传文件失败,服务器发生异常!", e);
        }

        String headerUrl = domain + contextPath + "/header/" + fileName;

        User u = (User) request.getSession().getAttribute("user");
        if (userService.uploadHeader(u.getUsername(), headerUrl) > 0) {
            return R.success("上传图片成功");
        } else {
            return R.error("上传图片失败");
        }
    }

    @RequestMapping(path = "/header/{fileName}", method = RequestMethod.GET)
    public void getHeader(@PathVariable("fileName") String fileName, HttpServletResponse response) {
        // 服务器存放路径
        fileName = uploadPath + "/" + fileName;
        // 文件后缀
        String suffix = fileName.substring(fileName.lastIndexOf("."));
        // 响应图片
        response.setContentType("image/" + suffix);
        try (
                FileInputStream fis = new FileInputStream(fileName);
                OutputStream os = response.getOutputStream();
        ) {
            byte[] buffer = new byte[1024];
            int b = 0;
            while ((b = fis.read(buffer)) != -1) {
                os.write(buffer, 0, b);
            }
        } catch (IOException e) {
            logger.error("读取头像失败: " + e.getMessage());
        }
    }

    @RequestMapping(value = "/profile/{userId}", method = RequestMethod.GET)
    public R<PersonDetail> profile(@PathVariable("userId") Integer userId, HttpServletRequest request) {
        User user = (User) request.getSession().getAttribute("user");
        if (user == null) {
            R.error("未登陆");
        }
        PersonDetail personDetail = new PersonDetail(user);
        personDetail.setLikeCount(likeService.findUserLikeCount(userId));
        personDetail.setFollowerCount(followService.findFollowerCount(3,userId));
        personDetail.setFolloweeCount(followService.findFolloweeCount(userId,3));

        return R.success(personDetail);
    }
}
