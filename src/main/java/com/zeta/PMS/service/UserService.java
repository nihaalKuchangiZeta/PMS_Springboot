package com.zeta.PMS.service;

import com.zeta.PMS.dto.UserRequest;
import com.zeta.PMS.entity.User;

import java.util.List;

public interface UserService {
    public User createUser(UserRequest user);
    public List<User> getAllUsers();
}
