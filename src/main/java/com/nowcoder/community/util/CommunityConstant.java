package com.nowcoder.community.util;

public interface CommunityConstant {

    // success activation
    int ACTIVATION_SUCCESS = 0;

    // duplicated activation
    int ACTIVATION_REPEAT = 1;

    // fail activation
    int ACTIVATION_FAILURE = 2;

    // default expiration time for login ticket
    int DEFAULT_EXPIRED_SECONDS = 3600 * 12;

    // expiration time for login ticket when remembering me
    int REMEMBER_EXPIRED_SECONDS = 3600 * 24 * 100;

    // entity type for post
    int ENTITY_TYPE_POST = 1;

    // entity type for comment
    int ENTITY_TYPE_COMMENT = 2;

    // entity type for user
    int ENTITY_TYPE_USER = 3;

    // topics: comment
    String TOPIC_COMMENT = "comment";

    // topic: like
    String TOPIC_LIKE = "like";

    // topic: follow
    String TOPIC_FOLLOW = "follow";

    // userId for the system
    int SYSTEM_USER_ID = 1;
}
