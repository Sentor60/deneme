package Service;

import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.IOException;

public class MainServer {

    public static void main(String[] args) throws InterruptedException, IOException {

        System.out.println("Hello gRPC");
        Server server = ServerBuilder.forPort(50051)
                .addService(new ProductServiceImpl())
                .build();

        server.start();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Received Shutdown Request");
            server.shutdown();
            System.out.println("Succesfully stopped the servers");
        }));

        server.awaitTermination();

    }



}
