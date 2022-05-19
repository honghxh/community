package com.munity.service;

import com.munity.pojo.entity.Message;
import com.baomidou.mybatisplus.extension.service.IService;
import com.munity.pojo.model.MessageVo;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author Forrest
 * @since 2022-04-29 09:14:22
 */
public interface MessageService extends IService<Message> {

    public List<MessageVo> getMessageList(List<Message> message);

    public List<MessageVo> getNoticeList(List<Message> message);

    public List<Message> findConversations(int userId, int offset, int limit);

    public int findConversationCount(int userId);

    public List<Message> findLetters(String conversationId, int offset, int limit);


    public int findLetterCount(String conversationId);

    public int findLetterUnreadCount(int userId, String conversationId);

    public int addMessage(Message message);

    public int readMessage(List<Integer> ids);

    public Message findLatestNotice(int userId, String topic);

    public int findNoticeCount(int userId, String topic);

    public int findNoticeUnreadCount(int userId, String topic);

    public List<Message> findNotices(int userId, String topic, int offset, int limit);

    public int deleteMessage(int id);

}
