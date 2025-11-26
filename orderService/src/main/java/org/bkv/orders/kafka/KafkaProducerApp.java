package org.bkv.orders.kafka;

import lombok.NonNull;
import org.bkv.orders.kafka.config.KafkaProducerConfig;
import org.bkv.orders.models.OrderDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducerApp {

    private final Logger logger = LoggerFactory.getLogger(KafkaProducerApp.class);

    private final KafkaTemplate<@NonNull Long, @NonNull OrderDto> kafkaTemplate;

    public KafkaProducerApp(KafkaTemplate<@NonNull Long, @NonNull OrderDto> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendMessage(OrderDto dto) {
        kafkaTemplate.send(KafkaProducerConfig.TOPIC, dto.orderId(), dto)
                .whenComplete((res, ex) -> {
                    if (ex != null) {
                        logger.error("Ошибка отправки: {}", dto, ex);
                    } else {
                        logger.info("Отправлено в partition={}, offset={}",
                                res.getRecordMetadata().partition(),
                                res.getRecordMetadata().offset());
                    }
                });
    }
}