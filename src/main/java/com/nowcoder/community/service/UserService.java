package com.nowcoder.community.service;

import com.nowcoder.community.dao.LoginTicketMapper;
import com.nowcoder.community.dao.UserMapper;
import com.nowcoder.community.entity.LoginTicket;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.util.CommunityConstant;
import com.nowcoder.community.util.CommunityUtil;
import com.nowcoder.community.util.MailClient;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
public class UserService implements CommunityConstant {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private MailClient mailClient;

    @Autowired
    private TemplateEngine templateEngine;

    @Value("${community.path.domain}")
    private String domain;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Autowired
    private LoginTicketMapper loginTicketMapper;

    public User findUserById(int id) {
        return userMapper.selectById(id);
    }

    public Map<String, Object> register(User user, String confirmPassword) {
        Map<String, Object> map = new HashMap<>();

        // handle null
        if (user == null) {
            throw new IllegalArgumentException("No Null Parameter!");
        }
        if (StringUtils.isBlank(user.getUsername())) {
            map.put("usernameMsg", "No Null Username!");
            return map;
        }
        if (StringUtils.isBlank(user.getPassword())) {
            map.put("passwordMsg", "No Null Password!");
            return map;
        }
        if (!user.getPassword().equals(confirmPassword)) {
            map.put("confirmPasswordMsg", "Password should match!");
            return map;
        }
        if (StringUtils.isBlank(user.getEmail())) {
            map.put("emailMsg", "No Null Email!");
            return map;
        }

        // validate username
        User u = userMapper.selectByName(user.getUsername());
        if (u != null) {
            map.put("usernameMsg", "This username already exists!");
            return map;
        }

        // validate email
        u = userMapper.selectByEmail(user.getEmail());
        if (u != null) {
            map.put("emailMsg", "This email already exists!");
            return map;
        }

        // register user
        user.setSalt(CommunityUtil.generateUUID().substring(0, 5));
        user.setPassword(CommunityUtil.md5(user.getPassword() + user.getSalt()));
        user.setType(0);
        user.setStatus(0);
        user.setActivationCode(CommunityUtil.generateUUID());
        user.setHeaderUrl(String.format("images.nowcoder.com/head/%dt.png", new Random().nextInt(1000)));
        user.setCreateTime(new Date());
        userMapper.insertUser(user);

        // send activation mail
        Context context = new Context();
        context.setVariable("email", user.getEmail());
        // http://localhost:63777/community/activation/101/code
        String url = domain + contextPath + "/activation/" + user.getId() + "/" + user.getActivationCode();
        context.setVariable("url", url);
        String content = templateEngine.process("/mail/activation", context);
        mailClient.sendMail(user.getEmail(), "Activate your account", content);

        return map;
    }

    public int activation(int userId, String code) {
        User user = userMapper.selectById(userId);
        if (user.getStatus() == 1) {
            return ACTIVATION_REPEAT;
        } else if (user.getActivationCode().equals(code)) {
            userMapper.updateStatus(userId, 1);
            return ACTIVATION_SUCCESS;
        } else {
            return ACTIVATION_FAILURE;
        }
    }

    public Map<String, Object> login(String username, String password, int expiredSeconds) {
        Map<String, Object> map = new HashMap<>();

        // handle null values
        if (StringUtils.isBlank(username)) {
            map.put("usernameMsg", "No null username!");
            return map;
        }
        if (StringUtils.isBlank(password)) {
            map.put("passwordMsg", "No null password!");
            return map;
        }

        // validate username
        User user = userMapper.selectByName(username);
        if (user == null) {
            map.put("usernameMsg", "Invalid username!");
            return map;
        }
        // validate status
        if (user.getStatus() == 0) {
            map.put("usernameMsg", "Has not activated!");
            return map;
        }
        // validate password
        password = CommunityUtil.md5(password + user.getSalt());
        if (!user.getPassword().equals(password)) {
            map.put("passwordMsg", "Incorrect password!");
            return map;
        }
        // generate login ticket
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setUserId(user.getId());
        loginTicket.setTicket(CommunityUtil.generateUUID());
        loginTicket.setStatus(0);
        loginTicket.setExpired(new Date(System.currentTimeMillis() + expiredSeconds * 1000));
        loginTicketMapper.insertLoginTicket(loginTicket);

        map.put("ticket", loginTicket.getTicket());
        return map;
    }

    public void logout(String ticket) {
        loginTicketMapper.updateStatus(ticket, 1);
    }

    public LoginTicket findLoginTicket(String ticket) {
        return loginTicketMapper.selectByTicket(ticket);
    }

    public int updateHeader(int userId, String headerUrl) {
        return userMapper.updateHeader(userId, headerUrl);
    }

    public Map<String, Object> changePassword(int userId, String oldPassword, String newPassword, String confirmPassword) {
        Map<String, Object> mp = new HashMap<>();
        User user = userMapper.selectById(userId);
        String salt = user.getSalt();
        if (!CommunityUtil.md5(oldPassword + salt).equals(user.getPassword())) {
            mp.put("oldPasswordMsg", "Incorrect Password!");
            return mp;
        }
        if (StringUtils.isBlank(newPassword)) {
            mp.put("newPasswordMsg", "Null Password!");
            return mp;
        }
        if (!newPassword.equals(confirmPassword)) {
            mp.put("confirmPasswordMsg", "Passwords should match!");
            return mp;
        }
        newPassword = CommunityUtil.md5(newPassword + salt);
        userMapper.updatePassword(userId, newPassword);
        return mp;
    }

}
