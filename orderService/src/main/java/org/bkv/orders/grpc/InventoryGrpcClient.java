package org.bkv.orders.grpc;

import inventory.Inventory;
import inventory.InventoryServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.springframework.stereotype.Component;

@Component
public class InventoryGrpcClient {
    private final String HOST = "localhost";
    private final int PORT = 9090;
    private final InventoryServiceGrpc.InventoryServiceBlockingStub stub;

    public InventoryGrpcClient() {
        ManagedChannel channel = ManagedChannelBuilder
                .forAddress(HOST, PORT)
                .usePlaintext()
                .build();

        this.stub = InventoryServiceGrpc.newBlockingStub(channel);
    }

    public Inventory.CheckProductResponse checkProduct(long productId, int quantity) {

        Inventory.CheckProductRequest request = Inventory.CheckProductRequest.newBuilder()
                .setProductId(productId)
                .setQuantity(quantity)
                .build();

        return stub.checkProduct(request);
    }
}
