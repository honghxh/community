package com.munity.controller;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.munity.common.R;
import com.munity.mapper.MessageMapper;
import com.munity.mapper.UserMapper;
import com.munity.pojo.entity.Message;
import com.munity.pojo.entity.User;
import com.munity.pojo.model.MessageVo;
import com.munity.pojo.model.sendMessage;
import com.munity.service.MessageService;
import com.munity.service.UserService;
import com.munity.util.CommunityConstant;
import com.munity.util.SensitiveFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.HtmlUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author Forrest
 * @since 2022-04-29 09:14:22
 */
@RestController
@RequestMapping("/munity/message")
public class MessageController implements CommunityConstant {

    @Autowired
    private MessageService messageService;
    @Autowired
    private UserService userService;
    @Autowired
    UserMapper userMapper;
    @Autowired
    SensitiveFilter sensitiveFilter;
    @Autowired
    MessageMapper messageMapper;

    // 私信列表
    @RequestMapping(path = "list", method = RequestMethod.GET)
    public R<List<MessageVo>> getLetterList(HttpServletRequest request, @RequestParam(value = "pageNum", defaultValue = "0") int pageNum, @RequestParam(value = "pageSize", defaultValue = "5") int pageSize) {
        User user = (User) request.getSession().getAttribute("user");
        if (user == null) {
            return R.error("您未登陆");
        }
        R<List<MessageVo>> r = new R<>();
        //修改
        List<Message> messageList = messageService.findConversations(user.getId(), pageNum, pageSize);
        List<MessageVo> messageVoList = messageService.getMessageList(messageList);
        int total = messageService.findConversationCount(user.getId());
        int letterUnreadCount = messageService.findLetterUnreadCount(user.getId(), null);
        int noticeUnreadCount = messageService.findNoticeUnreadCount(user.getId(), null);
        Map<String, Object> map = new HashMap<>();
        map.put("total", total);
        map.put("letterUnreadCount", letterUnreadCount);
        map.put("noticeUnreadCount", noticeUnreadCount);
        r.setMap(map);
        r.setData(messageVoList);
        r.setMsg("获取信息成功");
        r.setCode(1);
        return r;

    }

    @RequestMapping(path = "/letter/send", method = RequestMethod.POST)
    @ResponseBody
    public R<List<MessageVo>> sendLetter(@RequestBody sendMessage sendMessage, HttpServletRequest request) {

        User sendUser = (User) request.getSession().getAttribute("user");
        int sendUserId = userService.findUserIdByUserName(sendUser.getUsername());
        int targetId = userService.findUserIdByUserName(sendMessage.getToName());
        if (targetId == 0) {
            return R.error("目标用户不存在!");
        }
        User target = userMapper.selectById(targetId);
        Message message = new Message();
        message.setFromId(sendUserId);
        message.setToId(target.getId());

        if (message.getFromId() < message.getToId()) {
            message.setConversationId(message.getFromId() + "_" + message.getToId());
        } else {
            message.setConversationId(message.getToId() + "_" + message.getFromId());
        }
        message.setContent(sensitiveFilter.filter(sendMessage.getContent()));
        message.setStatus(0);
        message.setCreateTime(new Date());
        if (messageService.addMessage(message) > 0) {
            R<List<MessageVo>> r = new R<>();
            List<Message> messageList = messageService.findConversations(sendUserId, 0, 5);
            List<MessageVo> messageVoList = messageService.getMessageList(messageList);
            int total = messageService.findConversationCount(sendUserId);
            Map<String, Object> map = new HashMap<>();
            map.put("total", total);
            r.setMap(map);
            r.setCode(1);
            r.setData(messageVoList);
            r.setMsg("发送成功");
            return r;
        } else return R.error("发送失败");
    }


    @RequestMapping(path = "/letter/detail", method = RequestMethod.GET)
    public R<List<MessageVo>> getLetterDetail(@RequestParam("conversationId") String conversationId, @RequestParam(value = "pageNum", defaultValue = "0") int pageNum, @RequestParam(value = "pageSize", defaultValue = "5") int pageSize, HttpServletRequest request) {
        User user = (User) request.getSession().getAttribute("user");
        if (user == null) {
            return R.error("您未登陆");
        }
        R<List<MessageVo>> r = new R<>();
        List<Message> letterList = messageService.findLetters(conversationId, pageNum, pageSize);
        List<MessageVo> messageVoList = new ArrayList<>();
        int letterCount = messageService.findLetterCount(conversationId);
        Map<String, Object> map = new HashMap<>();
        map.put("letterCount", letterCount);
        if (letterList != null) {
            for (Message mes : letterList) {
                MessageVo messageVo = new MessageVo(mes);
                User u = userMapper.selectById(mes.getFromId());
                messageVo.setHeaderUrl(u.getHeaderUrl());
                messageVo.setUsername(u.getUsername());

                messageVoList.add(messageVo);

            }
        }
        List<Integer> ids = getLetterIds(letterList, user);
        if (!ids.isEmpty()) {
            messageService.readMessage(ids);
        }
        User toUser = getLetterTarget(conversationId, user.getId());
        map.put("toUser", toUser);

        r.setMap(map);
        r.setData(messageVoList);
        r.setCode(1);
        r.setMsg("获取私信列表成功");
        return r;
    }

    private List<Integer> getLetterIds(List<Message> letterList, User use) {
        List<Integer> ids = new ArrayList<>();

        if (letterList != null) {
            for (Message message : letterList) {

                if (use.getId().equals(message.getToId()) && message.getStatus() == 0) {
                    ids.add(message.getId());
                }
            }
        }

        return ids;
    }

    private User getLetterTarget(String conversationId, int userId) {
        String[] ids = conversationId.split("_");
        int id0 = Integer.parseInt(ids[0]);
        int id1 = Integer.parseInt(ids[1]);

        if (userId == id0) {
            return userService.findUserById(id1);
        } else {
            return userService.findUserById(id0);
        }
    }

    @RequestMapping(path = "/letter/delete/{id}", method = RequestMethod.GET)
    public R<List<MessageVo>> deleteLetter(@PathVariable("id") int id) {
        if (messageService.deleteMessage(id) > 0) {
            R<List<MessageVo>> r = new R<>();
            QueryWrapper<Message> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("id", id);
            Message message = messageMapper.selectOne(queryWrapper);
            List<Message> letterList = messageService.findLetters(message.getConversationId(), 0, 5);
            List<MessageVo> messageVoList = new ArrayList<>();
            int letterCount = messageService.findLetterCount(message.getConversationId());
            Map<String, Object> map = new HashMap<>();
            map.put("letterCount", letterCount);
            if (letterList != null) {
                for (Message mes : letterList) {
                    MessageVo messageVo = new MessageVo(mes);
                    User u = userMapper.selectById(mes.getFromId());
                    messageVo.setHeaderUrl(u.getHeaderUrl());
                    messageVo.setUsername(u.getUsername());

                    messageVoList.add(messageVo);

                }
            }
            return R.success(messageVoList);
        } else {
            return R.error("删除失败");
        }
    }

    @RequestMapping(value = "/notice/list", method = RequestMethod.GET)
    public R<List<MessageVo>> getNotice( HttpServletRequest request) {
        User user = (User) request.getSession().getAttribute("user");
        if (user == null) {
            return R.error("未登陆");
        }
        List<MessageVo> messageVoList = new ArrayList<>();
        Message commentMessage = messageService.findLatestNotice(user.getId(), TOPIC_COMMENT);
        if (commentMessage != null) {
            MessageVo messageVo = new MessageVo(commentMessage);
            String content = HtmlUtils.htmlUnescape(commentMessage.getContent());
            Map<String, Object> data = JSONObject.parseObject(content, HashMap.class);
            int uId = (Integer) data.get("userId");
            String username = userService.findUserById(uId).getUsername();
            int entityType = (Integer) data.get("entityType");
            String type = (entityType == 1) ? "帖子" : "评论";
            content = "用户" + username + "回复了你的" + type;
            messageVo.setContent(content);
            int count = messageService.findNoticeCount(user.getId(), TOPIC_COMMENT);
            messageVo.setLetterCount(count);
            int unread = messageService.findNoticeUnreadCount(user.getId(), TOPIC_COMMENT);
            messageVo.setLetterUnreadCount(unread);
            messageVoList.add(messageVo);
        }

        Message likeMessage = messageService.findLatestNotice(user.getId(), TOPIC_LIKE);
        if (likeMessage != null) {
            MessageVo messageVo = new MessageVo(likeMessage);
            String content = HtmlUtils.htmlUnescape(likeMessage.getContent());
            Map<String, Object> data = JSONObject.parseObject(content, HashMap.class);
            int uId = (Integer) data.get("userId");
            String username = userService.findUserById(uId).getUsername();
            int entityType = (Integer) data.get("entityType");
            String type = (entityType == 1) ? "帖子" : "评论";
            content = "用户" + username + "点赞了你的" + type;
            messageVo.setContent(content);
            int count = messageService.findNoticeCount(user.getId(), TOPIC_LIKE);
            messageVo.setLetterCount(count);
            int unread = messageService.findNoticeUnreadCount(user.getId(), TOPIC_LIKE);
            messageVo.setLetterUnreadCount(unread);
            messageVoList.add(messageVo);
        }

        Message followMessage = messageService.findLatestNotice(user.getId(), TOPIC_FOLLOW);
        if (followMessage != null) {
            MessageVo messageVo = new MessageVo(followMessage);
            String content = HtmlUtils.htmlUnescape(followMessage.getContent());
            Map<String, Object> data = JSONObject.parseObject(content, HashMap.class);
            int uId = (Integer) data.get("userId");
            String username = userService.findUserById(uId).getUsername();
            int entityType = (Integer) data.get("entityType");
            String type = (entityType == 1) ? "的帖子" : "";
            content = "用户" + username + "关注了你" + type;
            messageVo.setContent(content);
            int count = messageService.findNoticeCount(user.getId(), TOPIC_FOLLOW);
            messageVo.setLetterCount(count);
            int unread = messageService.findNoticeUnreadCount(user.getId(), TOPIC_FOLLOW);
            messageVo.setLetterUnreadCount(unread);
            messageVoList.add(messageVo);
        }

        Map<String, Object> map = new HashMap<>();
        int noticeUnreadCount = messageService.findNoticeUnreadCount(user.getId(), null);
        map.put("noticeUnreadCount", noticeUnreadCount);

        R<List<MessageVo>> r = new R<>();

        r.setMap(map);
        r.setData(messageVoList);
        r.setCode(1);
        r.setMsg("成功获取通知信息");

        return r;
    }

    @RequestMapping(path = "/notice/detail/{topic}", method = RequestMethod.GET)
    public R<List<MessageVo>> getNoticeList(HttpServletRequest request,@PathVariable("topic") String topic,@RequestParam(value = "pageNum", defaultValue = "0") int pageNum, @RequestParam(value = "pageSize", defaultValue = "5") int pageSize){
        User user = (User) request.getSession().getAttribute("user");
        if (user == null) {
            return R.error("未登陆");
        }
        R<List<MessageVo>> r = new R<>();
        List<MessageVo> noticeVoList = new ArrayList<>();
        List<Message> noticeList = messageService.findNotices(user.getId(), topic, pageNum,pageSize);
        int total = messageService.findNoticeCount(user.getId(), topic);
        noticeVoList = messageService.getNoticeList(noticeList);
        Map<String, Object> map = new HashMap<>();
        map.put("total", total);

        r.setData(noticeVoList);
        r.setCode(1);
        r.setMap(map);
        r.setMsg("成功获取通知信息列表");

        List<Integer> ids = getLetterIds(noticeList,user);
        if (!ids.isEmpty()) {
            messageService.readMessage(ids);
        }

        return r;
    }
}