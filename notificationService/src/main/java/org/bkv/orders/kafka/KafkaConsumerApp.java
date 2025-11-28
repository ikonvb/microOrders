package org.bkv.orders.kafka;

import org.bkv.orders.kafka.config.KafkaConfig;
import org.bkv.orders.models.OrderDto;
import org.bkv.orders.services.impls.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumerApp {


    private final OrderService orderService;

    public KafkaConsumerApp(OrderService orderService) {
        this.orderService = orderService;
    }

    private final Logger logger = LoggerFactory.getLogger(KafkaConsumerApp.class);

    @KafkaListener(topics = KafkaConfig.TOPIC, groupId = KafkaConfig.CONSUMER_GROUP_ID)
    public void listen(OrderDto message) {
        logger.info("Received message: {}", message);
        orderService.saveOrder(message);
    }
}
