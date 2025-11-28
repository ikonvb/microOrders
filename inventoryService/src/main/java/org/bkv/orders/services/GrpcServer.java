package org.bkv.orders.services;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class GrpcServer {

    @Value("${grpc.port}")
    private int port;
    private Server server;
    private final InventoryServiceImpl inventoryService;

    public GrpcServer(InventoryServiceImpl inventoryService) {
        this.inventoryService = inventoryService;
    }

    @PostConstruct
    public void start() throws IOException {
        server = ServerBuilder.forPort(port)
                .addService(inventoryService)
                .build()
                .start();

        System.out.println("gRPC server started on port " + port);

        new Thread(() -> {
            try {
                server.awaitTermination();
            } catch (InterruptedException ignored) {}
        }).start();
    }

    @PreDestroy
    public void stop() {
        if (server != null) {
            System.out.println("Stopping gRPC server...");
            server.shutdown();
        }
    }
}
