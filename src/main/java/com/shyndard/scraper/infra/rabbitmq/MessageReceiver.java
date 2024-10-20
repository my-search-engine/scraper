package com.shyndard.scraper.infra.rabbitmq;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.shyndard.scraper.service.ScraperService;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@AllArgsConstructor
public class MessageReceiver {

    private ScraperService service;

    @RabbitListener(queues = "url-to-process", concurrency = "100")
    public void receive(String message) throws InterruptedException {
        service.process(message);
    }

}
