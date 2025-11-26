package org.bkv.orders.kafka.deserializer;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Deserializer;
import org.bkv.orders.models.OrderDto;

public class OrderDtoDeserializer implements Deserializer<OrderDto> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public OrderDto deserialize(String topic, byte[] data) {
        if (data == null) {
            return null;
        }
        try {
            return objectMapper.readValue(data, OrderDto.class);
        } catch (Exception e) {
            throw new RuntimeException("Error deserializing JSON to OrderDto", e);
        }
    }
}
