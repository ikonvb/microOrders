package org.bkv.orders.kafka.serializer;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Serializer;
import org.bkv.orders.models.OrderDto;

public class OrderSerializer implements Serializer<OrderDto> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public byte[] serialize(String topic, OrderDto data) {
        try {
            return objectMapper.writeValueAsBytes(data);
        } catch (Exception e) {
            throw new RuntimeException("Error serializing OrderDto to JSON", e);
        }
    }

}
