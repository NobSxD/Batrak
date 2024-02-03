package com.example.node.service.impl;

import com.example.node.dao.UserMainDAO;
import com.example.node.entity.MailParams;
import com.example.node.entity.UserMain;
import com.example.node.service.AddUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import static com.example.node.entity.enums.UserState.BASIC_STATE;

@Log4j
@RequiredArgsConstructor
@Service
public class AddUserServiceIml implements AddUserService {
    private final UserMainDAO userMainDAO;
    private final RabbitTemplate rabbitTemplate;

    @Value("${spring.rabbitmq.queues.registration-mail}")
    private String registrationMailQueue;

    @Override
    public String registerUser(UserMain userMain) {
        if (userMain.getIsActive()) {
            return "Вы уже зарегистрированы!";
        } else if (userMain.getEmail() != null) {
            return "Вам на почту уже было отправлено письмо. "
                    + "Перейдите по ссылке в письме для подтверждения регистрации.";
        }
     //   userMain.setState(WAIT_FOR_EMAIL_STATE);
        userMainDAO.save(userMain);
        return "Введите, пожалуйста, ваш email:";
    }

    @Override
    public String setEmail(UserMain userMain, String email) {
        try {
            var emailAddr = new InternetAddress(email);
            emailAddr.validate();
        } catch (AddressException e) {
            return "Введите, пожалуйста, корректный email. Для отмены команды введите /cancel";
        }
        var appUserOpt = userMainDAO.findByEmail(email);
        if (appUserOpt.isEmpty()) {
            userMain.setEmail(email);
            userMain.setState(BASIC_STATE);
            userMain = userMainDAO.save(userMain);


         //   sendRegistrationMail(cryptoUserId, email);

            return "Вам на почту было отправлено письмо."
                    + "Перейдите по ссылке в письме для подтверждения регистрации.";
        } else {
            return "Этот email уже используется. Введите корректный email."
                    + " Для отмены команды введите /cancel";
        }
    }
    private void sendRegistrationMail(String cryptoUserId, String email) {
        var mailParams = MailParams.builder()
                .id(cryptoUserId)
                .emailTo(email)
                .build();
        rabbitTemplate.convertAndSend(registrationMailQueue, mailParams);
    }
}
