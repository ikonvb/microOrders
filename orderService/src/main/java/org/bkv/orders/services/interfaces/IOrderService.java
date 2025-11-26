package org.bkv.orders.services.interfaces;

import org.antlr.v4.runtime.misc.Pair;
import org.bkv.orders.entity.OrderEntity;

public interface IOrderService {
    OrderEntity saveOrder(OrderEntity order);

    Pair<Boolean, Double> canCreateOrder(OrderEntity order);
}
