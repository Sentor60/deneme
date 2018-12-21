package Client;

import com.proto.model.product.*;
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

        //addProduct(channel);
        //readProduct(channel);
        //updateProduct(channel);
        //deleteProduct(channel);
        listProducts(channel);

    }


    private void addProduct(ManagedChannel channel){

        ProductServiceGrpc.ProductServiceBlockingStub productClient = ProductServiceGrpc.newBlockingStub(channel);

        Product product = Product.newBuilder()
                .setName("Oralet")
                .setPrice(1.5)
                .setExplanation("Kivili")
                .build();

        AddProductResponse response = productClient.addProduct(AddProductRequest.newBuilder()
                .setProduct(product)
                .build());


        System.out.println("Received add Product response");
        System.out.println(response.toString());

    }


    private Product readProduct(ManagedChannel channel){

        ProductServiceGrpc.ProductServiceBlockingStub productClient = ProductServiceGrpc.newBlockingStub(channel);

        ReadProductResponse response = productClient.readProduct(ReadProductRequest.newBuilder()
                .setProductId("5c1cb6034bd23422303531d6")
                .build());

       // System.out.println(response.getProduct().toString());

        return response.getProduct();

    }

    private void updateProduct(ManagedChannel channel){

        ProductServiceGrpc.ProductServiceBlockingStub productClient = ProductServiceGrpc.newBlockingStub(channel);

        Product product = readProduct(channel);

        product = product.toBuilder().setPrice(1.5).build();

        UpdateProductResponse response = productClient.updateProduct(UpdateProductRequest.newBuilder().setProduct(product).build());

        System.out.println(response.getProduct());
    }

    private void deleteProduct(ManagedChannel channel){

        ProductServiceGrpc.ProductServiceBlockingStub productClient = ProductServiceGrpc.newBlockingStub(channel);

        DeleteProductResponse response = productClient.deleteProduct(
                DeleteProductRequest.newBuilder().setProductId("5c1cb6034bd23422303531d6").build());

        System.out.println("Product deleted.");

    }

    private void listProducts(ManagedChannel channel){

        ProductServiceGrpc.ProductServiceBlockingStub productClient = ProductServiceGrpc.newBlockingStub(channel);

        productClient.listProducts(ListProductRequest.newBuilder().build()).forEachRemaining(
                listProductsResponse -> System.out.println(listProductsResponse.getProduct().toString())
        );

    }


}
