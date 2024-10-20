package com.shyndard.scraper.infra.rabbitmq;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@AllArgsConstructor
@Slf4j
public class MessageSender {

    private final RabbitTemplate rabbitTemplate;

    public void sendMessage(final String url) {
        rabbitTemplate.convertAndSend("scraper", "url", url);
    }
}
