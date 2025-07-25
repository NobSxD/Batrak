package org.example.configuration;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.example.model.RabbitQueue.*;

@Configuration
public class RabbitConfiguration {
    @Bean
    public MessageConverter jsonMessageConverter(){
        return new Jackson2JsonMessageConverter();
    }
    @Bean
    public Queue textMessageQueue(){
        return new Queue(TEXT_MESSAGE_UPDATE);
    }
    @Bean
    public Queue answerMessageQueue(){
        return new Queue(ANSWER_MESSAGE);
    }
    @Bean
    public Queue listCustomMessage(){return new Queue(LIST_CUSTOM_MESSAGE);}
    @Bean
    public Queue EnumCustomMessage(){return new Queue(ENUM_CUSTOM_MESSAGE);}
    @Bean
    public Queue tradeStart(){return new Queue(TRADE_MESSAGE);}
    @Bean
    public Queue tradeCancelOrder(){return new Queue(TRADE_CANCEL_ORDER);}
    @Bean Queue rate(){return new Queue(RATE);}
    @Bean Queue infoAccount(){return new Queue(INFO_ACCOUNT);}
    @Bean Queue trade_stop(){return new Queue(TRADE_STOP);}
    @Bean Queue trade_state(){return new Queue(TRADE_STATE);}
    @Bean Queue rebut_telegram(){return new Queue(REBUT_TELEGRAM);}
    @Bean Queue rebut_trade(){return new Queue(REBUT_TRADE);}

}
