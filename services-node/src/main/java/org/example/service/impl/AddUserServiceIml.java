package org.example.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.dao.NodeUserDAO;
import org.example.entity.NodeUser;
import org.example.service.AddUserService;
import org.springframework.stereotype.Service;


@RequiredArgsConstructor
@Service
public class AddUserServiceIml implements AddUserService {
    private final NodeUserDAO nodeUserDAO;

    @Override
    public String  registerUser(NodeUser nodeUser) {
        if (nodeUser.getIsActive()) {
            return "Вы уже зарегистрированы!";
        } else if (nodeUser.getEmail() != null) {
            return "Вам на почту уже было отправлено письмо. "
                    + "Перейдите по ссылке в письме для подтверждения регистрации.";
        }
        nodeUserDAO.save(nodeUser);
        return "Введите, пожалуйста, ваш email:";
    }

}
