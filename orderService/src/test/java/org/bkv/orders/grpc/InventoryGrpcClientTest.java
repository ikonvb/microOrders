package org.bkv.orders.grpc;

import inventory.Inventory;
import inventory.InventoryServiceGrpc;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@SpringBootTest
class InventoryGrpcClientTest {

    @Mock
    private InventoryServiceGrpc.InventoryServiceBlockingStub stub;

    @InjectMocks
    private InventoryGrpcClient client;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Заменяем реальный stub на мок
        client = new InventoryGrpcClient() {
            @Override
            public Inventory.CheckProductResponse checkProduct(long productId, int quantity) {
                return stub.checkProduct(
                        Inventory.CheckProductRequest.newBuilder()
                                .setProductId(productId)
                                .setQuantity(quantity)
                                .build()
                );
            }
        };
    }

    @Test
    void checkProduct_success() {
        long productId = 1L;
        int quantity = 5;

        Inventory.CheckProductResponse responseMock = Inventory.CheckProductResponse.newBuilder()
                .setAvailable(true)
                .build();

        when(stub.checkProduct(any())).thenReturn(responseMock);

        Inventory.CheckProductResponse response = client.checkProduct(productId, quantity);

        assertTrue(response.getAvailable());
        verify(stub, times(1)).checkProduct(any());
    }

    @Test
    void checkProduct_notAvailable() {
        Inventory.CheckProductResponse responseMock = Inventory.CheckProductResponse.newBuilder()
                .setAvailable(false)
                .build();

        when(stub.checkProduct(any())).thenReturn(responseMock);

        Inventory.CheckProductResponse response = client.checkProduct(2L, 10);

        assertFalse(response.getAvailable());
        verify(stub, times(1)).checkProduct(any());
    }
}