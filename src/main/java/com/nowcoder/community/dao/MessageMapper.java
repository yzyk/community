package com.nowcoder.community.dao;

import com.nowcoder.community.entity.Message;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MessageMapper {

    // select list of conversations for this current user, only the latest msg for every conversation
    List<Message> selectConversations(int userId, int offset, int limit);

    // select count of conversation for this current user
    int selectConversationCount(int userId);

    // select list of msg for a certain conversation
    List<Message> selectLetters(String conversationId, int offset, int limit);

    // select count of msg for a certain conversation
    int selectLetterCount(String conversationId);

    // select count of unread msg for this current user
    int selectLetterUnreadCount(int userId, String conversationId);

    // add a new message
    int insertMessage(Message message);

    // update status for certain messages
    int updateStatus(List<Integer> ids, int status);

    // select latest system msg for a certain topic
    Message selectLatestNotice(int userId, String topic);

    // select count of system msg for a certain topic
    int selectNoticeCount(int userId, String topic);

    // select count of unread system msg for a certain topic
    int selectNoticeUnreadCount(int userId, String topic);

    // select list of notices for a certain topic
    List<Message> selectNotices(int userId, String topic, int offset, int limit);
}
