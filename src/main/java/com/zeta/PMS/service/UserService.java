package com.zeta.PMS.service;

import com.zeta.PMS.dto.UserRequest;
import com.zeta.PMS.dto.UserResponse;

import java.util.List;

public interface UserService {
    public UserResponse createUser(UserRequest user);
    public List<UserResponse> getAllUsers();
}
