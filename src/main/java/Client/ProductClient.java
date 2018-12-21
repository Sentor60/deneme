package Client;

import com.proto.model.product.AddProductRequest;
import com.proto.model.product.AddProductResponse;
import com.proto.model.product.Product;
import com.proto.model.product.ProductServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class ProductClient {

    public static void main(String[] args) {

        System.out.println("Hello from gRPC product client");
        ProductClient main = new ProductClient();

        main.run();

    }

    private void run() {

        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 50051)
                .usePlaintext()
                .build();

        ProductServiceGrpc.ProductServiceBlockingStub productClient = ProductServiceGrpc.newBlockingStub(channel);

        Product product = Product.newBuilder()
                .setName("Cay")
                .setPrice(1.0)
                .setExplanation("Bildigin cay")
                .build();

        AddProductResponse response = productClient.addProduct(AddProductRequest.newBuilder()
                .setProduct(product)
                .build());


        System.out.println("Received add Product response");
        System.out.println(response.toString());



    }


}
