package com.example.node.service;

import com.example.node.entity.UserMain;

public interface AddUserService {
    String registerUser(UserMain userMain);

    String setEmail(UserMain userMain, String email);
}
