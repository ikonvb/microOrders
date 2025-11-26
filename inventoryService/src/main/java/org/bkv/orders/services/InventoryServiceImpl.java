package org.bkv.orders.services;

import inventory.Inventory;
import inventory.InventoryServiceGrpc;
import io.grpc.stub.StreamObserver;
import org.bkv.orders.entity.ProductEntity;
import org.bkv.orders.repo.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class InventoryServiceImpl extends InventoryServiceGrpc.InventoryServiceImplBase {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final ProductRepository productRepository;

    public InventoryServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public void checkProduct(Inventory.CheckProductRequest request, StreamObserver<Inventory.CheckProductResponse> responseObserver) {

        logger.info("Received checkProduct request: {}", request);

        long productId = request.getProductId();
        int quantity = request.getQuantity();

        Optional<ProductEntity> p = productRepository.findById(productId);

        if (p.isEmpty()) {

            logger.info("ProductEntity not found");

            Inventory.CheckProductResponse resp = Inventory.CheckProductResponse.newBuilder()
                    .setAvailable(false)
                    .setPrice(0)
                    .setDiscount(0)
                    .build();

            responseObserver.onNext(resp);
            responseObserver.onCompleted();
            return;
        }

        logger.info("ProductEntity has found");

        ProductEntity product = p.get();

        boolean available = product.getQuantity() >= quantity;

        Inventory.CheckProductResponse resp = Inventory.CheckProductResponse.newBuilder()
                .setAvailable(available)
                .setPrice(product.getPrice())
                .setDiscount(product.getDiscount())
                .build();

        responseObserver.onNext(resp);
        responseObserver.onCompleted();
    }
}
