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
}
