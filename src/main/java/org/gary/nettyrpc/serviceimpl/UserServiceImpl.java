package org.gary.nettyrpc.serviceimpl;

import org.gary.nettyrpc.pojo.User;
import org.gary.nettyrpc.service.UserService;

public class UserServiceImpl implements UserService {

    @Override
    public User getUser() {
        User user = new User();
        user.setName("hello");
        user.setPassword("nico");
        return user;
    }

    @Override
    public String getPasswordByName(String s) {
        if(s.equals("hello"))
            return "nico";
        return null;
    }
}
