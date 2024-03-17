package org.example.service.impl;

import org.example.entity.MailParams;
import org.example.service.AddUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.example.dao.NodeUserDAO;
import org.example.entity.NodeUser;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


@Log4j2
@RequiredArgsConstructor
@Service
public class AddUserServiceIml implements AddUserService {
    private final NodeUserDAO nodeUserDAO;
    private final RabbitTemplate rabbitTemplate;



    @Value("${spring.rabbitmq.queues.registration-mail}")
    private String registrationMailQueue;

    @Override
    public String registerUser(NodeUser nodeUser) {
        if (nodeUser.getIsActive()) {
            return "Вы уже зарегистрированы!";
        } else if (nodeUser.getEmail() != null) {
            return "Вам на почту уже было отправлено письмо. "
                    + "Перейдите по ссылке в письме для подтверждения регистрации.";
        }
     //   userMain.setState(WAIT_FOR_EMAIL_STATE);
        nodeUserDAO.save(nodeUser);
        return "Введите, пожалуйста, ваш email:";
    }


    private void sendRegistrationMail(String cryptoUserId, String email) {
        var mailParams = MailParams.builder()
                .id(cryptoUserId)
                .emailTo(email)
                .build();
        rabbitTemplate.convertAndSend(registrationMailQueue, mailParams);
    }
}
