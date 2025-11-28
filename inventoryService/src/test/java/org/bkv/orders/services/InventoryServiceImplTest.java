package org.bkv.orders.services;

import inventory.Inventory;
import io.grpc.stub.StreamObserver;
import org.bkv.orders.entity.ProductEntity;
import org.bkv.orders.repo.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class InventoryServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private StreamObserver<Inventory.CheckProductResponse> responseObserver;

    private InventoryServiceImpl inventoryService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        inventoryService = new InventoryServiceImpl(productRepository);
    }

    @Test
    void checkProduct_productNotFound() {

        long productId = 1L;
        int quantity = 5;

        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        Inventory.CheckProductRequest request = Inventory.CheckProductRequest.newBuilder()
                .setProductId(productId)
                .setQuantity(quantity)
                .build();

        ArgumentCaptor<Inventory.CheckProductResponse> captor = ArgumentCaptor.forClass(Inventory.CheckProductResponse.class);

        inventoryService.checkProduct(request, responseObserver);

        verify(responseObserver).onNext(captor.capture());
        verify(responseObserver).onCompleted();

        Inventory.CheckProductResponse response = captor.getValue();
        assertFalse(response.getAvailable());
        assertEquals(0, response.getPrice());
        assertEquals(0, response.getDiscount());
    }

    @Test
    void checkProduct_productFoundAvailable() {

        long productId = 2L;
        int quantity = 3;
        ProductEntity product = new ProductEntity();
        product.setProductId(productId);
        product.setQuantity(10.0);
        product.setPrice(100.0);
        product.setDiscount(10.0);

        when(productRepository.findById(productId)).thenReturn(Optional.of(product));

        Inventory.CheckProductRequest request = Inventory.CheckProductRequest.newBuilder()
                .setProductId(productId)
                .setQuantity(quantity)
                .build();

        ArgumentCaptor<Inventory.CheckProductResponse> captor = ArgumentCaptor.forClass(Inventory.CheckProductResponse.class);

        inventoryService.checkProduct(request, responseObserver);

        verify(responseObserver).onNext(captor.capture());
        verify(responseObserver).onCompleted();

        Inventory.CheckProductResponse response = captor.getValue();
        assertTrue(response.getAvailable());
        assertEquals(100, response.getPrice());
        assertEquals(10, response.getDiscount());
    }

    @Test
    void checkProduct_productFoundNotAvailable() {

        long productId = 3L;
        int quantity = 15;
        ProductEntity product = new ProductEntity();
        product.setProductId(productId);
        product.setQuantity(10.0);
        product.setPrice(200.0);
        product.setDiscount(20.0);

        when(productRepository.findById(productId)).thenReturn(Optional.of(product));

        Inventory.CheckProductRequest request = Inventory.CheckProductRequest.newBuilder()
                .setProductId(productId)
                .setQuantity(quantity)
                .build();

        ArgumentCaptor<Inventory.CheckProductResponse> captor = ArgumentCaptor.forClass(Inventory.CheckProductResponse.class);

        inventoryService.checkProduct(request, responseObserver);

        verify(responseObserver).onNext(captor.capture());
        verify(responseObserver).onCompleted();

        Inventory.CheckProductResponse response = captor.getValue();
        assertFalse(response.getAvailable());
        assertEquals(200, response.getPrice());
        assertEquals(20, response.getDiscount());
    }
}