package org.bkv.orders.services.impls;


import inventory.Inventory;
import lombok.AllArgsConstructor;
import org.antlr.v4.runtime.misc.Pair;
import org.bkv.orders.entity.OrderEntity;
import org.bkv.orders.grpc.InventoryGrpcClient;
import org.bkv.orders.repo.OrderRepository;
import org.bkv.orders.services.interfaces.IOrderService;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class OrderService implements IOrderService {

    private final InventoryGrpcClient grpcClient;
    private OrderRepository orderRepository;

    public Pair<Boolean, Double> canCreateOrder(OrderEntity order) {

        Inventory.CheckProductResponse response = grpcClient.checkProduct(order.getProductId(), order.getQuantity());

        if (!response.getAvailable()) {
            return new Pair<>(false, 0.0);
        }

        System.out.println("Price: " + response.getPrice() + ", Discount: " + response.getDiscount());

        return new Pair<>(true, response.getPrice());
    }

    @Override
    public OrderEntity saveOrder(OrderEntity order) {
        return orderRepository.save(order);
    }

}
