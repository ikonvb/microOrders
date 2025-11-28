package org.bkv.orders.services;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.io.IOException;

import static org.mockito.Mockito.*;

class GrpcServerTest {

    @Mock
    private InventoryServiceImpl inventoryService;

    @Mock
    private ServerBuilder<?> serverBuilder;

    @Mock
    private Server server;

    private GrpcServer grpcServer;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        grpcServer = new GrpcServer(inventoryService);
    }

    @Test
    void start_shouldBuildAndStartServer_fixed() throws IOException {
        setPrivateField(grpcServer, "port", 8080);

        try (var mockedStatic = mockStatic(ServerBuilder.class)) {
            mockedStatic.when(() -> ServerBuilder.forPort(8080)).thenReturn(serverBuilder);
            Mockito.<ServerBuilder<?>>when(serverBuilder.addService(inventoryService))
                    .thenReturn(serverBuilder);
            when(serverBuilder.build()).thenReturn(server);
            when(server.start()).thenReturn(server);

            grpcServer.start();

            verify(serverBuilder).addService(inventoryService);
            verify(server).start();
        }
    }

    @Test
    void start_shouldBuildAndStartServerV2() throws IOException {
        setPrivateField(grpcServer, "port", 8080);

        try (var mockedStatic = mockStatic(ServerBuilder.class)) {
            mockedStatic.when(() -> ServerBuilder.forPort(8080)).thenReturn(serverBuilder);
            Mockito.<ServerBuilder<?>>when(serverBuilder.addService(inventoryService))
                    .thenReturn(serverBuilder);
            when(serverBuilder.build()).thenReturn(server);
            when(server.start()).thenReturn(server);

            grpcServer.start();

            verify(serverBuilder, times(1)).addService(inventoryService);
            verify(server, times(1)).start();
        }
    }

    @Test
    void stop_shouldShutdownServer() {

        setPrivateField(grpcServer, "server", server);
        grpcServer.stop();
        verify(server, times(1)).shutdown();
    }

    private void setPrivateField(Object target, String fieldName, Object value) {
        try {
            var field = target.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(target, value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}