package com.munity.serviceImpl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.munity.mapper.UserMapper;
import com.munity.pojo.entity.DiscussPost;
import com.munity.pojo.entity.Message;
import com.munity.mapper.MessageMapper;
import com.munity.pojo.entity.User;
import com.munity.pojo.model.MessageVo;
import com.munity.service.MessageService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.munity.util.SensitiveFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author Forrest
 * @since 2022-04-29 09:14:22
 */
@Service
public class MessageServiceImpl extends ServiceImpl<MessageMapper, Message> implements MessageService {

    @Autowired
    MessageMapper messageMapper;

    @Autowired
    UserMapper userMapper;
    @Autowired
    SensitiveFilter sensitiveFilter;

    @Override
    public List<MessageVo> getMessageList(List<Message> records) {
        List<MessageVo> messageVoList = new ArrayList<>();
        if (records != null) {
            for (Message mes : records) {
                MessageVo messageVo = new MessageVo(mes);
                User user = userMapper.selectById(mes.getFromId());
                messageVo.setHeaderUrl(user.getHeaderUrl());
                messageVo.setUsername(user.getUsername());
                messageVo.setToUserName(userMapper.selectById(mes.getToId()).getUsername());
                messageVo.setLetterCount(messageMapper.selectLetterCount(mes.getConversationId()));
                messageVo.setLetterUnreadCount(messageMapper.selectLetterUnreadCount(mes.getToId(), mes.getConversationId()));
                messageVo.setToUserHeader(userMapper.selectById(mes.getToId()).getHeaderUrl());
                messageVoList.add(messageVo);

            }
        }
        return messageVoList;
    }

    public List<Message> findConversations(int userId, int offset, int limit) {
        return messageMapper.selectConversations(userId, offset, limit);
    }

    public int findConversationCount(int userId) {
        return messageMapper.selectConversationCount(userId);
    }

    public List<Message> findLetters(String conversationId, int offset, int limit) {
        return messageMapper.selectLetters(conversationId, offset, limit);
    }

    public int findLetterCount(String conversationId) {
        return messageMapper.selectLetterCount(conversationId);
    }

    public int findLetterUnreadCount(int userId, String conversationId) {
        return messageMapper.selectLetterUnreadCount(userId, conversationId);
    }

    public int addMessage(Message message) {
        message.setContent(HtmlUtils.htmlEscape(message.getContent()));
        message.setContent(sensitiveFilter.filter(message.getContent()));
        return messageMapper.insertMessage(message);
    }

    public int readMessage(List<Integer> ids) {
        return messageMapper.updateStatus(ids, 1);
    }

    public Message findLatestNotice(int userId, String topic) {
        return messageMapper.selectLatestNotice(userId, topic);
    }

    public int findNoticeCount(int userId, String topic) {
        return messageMapper.selectNoticeCount(userId, topic);
    }

    public int findNoticeUnreadCount(int userId, String topic) {
        return messageMapper.selectNoticeUnreadCount(userId, topic);
    }

    public List<Message> findNotices(int userId, String topic, int offset, int limit) {
        return messageMapper.selectNotices(userId, topic, offset, limit);
    }

    @Override
    public int deleteMessage(int id) {
        UpdateWrapper<Message> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", id).set("status", 2);
        return messageMapper.update(null, updateWrapper);

    }

}
