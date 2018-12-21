package Service;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.DeleteResult;
import com.proto.model.product.*;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import org.bson.Document;
import org.bson.types.ObjectId;

import static com.mongodb.client.model.Filters.eq;

public class ProductServiceImpl extends ProductServiceGrpc.ProductServiceImplBase {

    private MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017");
    private MongoDatabase database = mongoClient.getDatabase("mydb");
    private MongoCollection<Document> collection = database.getCollection("product");

    @Override
    public void addProduct(AddProductRequest request, StreamObserver<AddProductResponse> responseObserver) {

        Product product = request.getProduct();

        Document doc = new Document("name", product.getName())
                .append("price", product.getPrice())
                .append("explanation", product.getExplanation());

        System.out.println("Inserting product");

        collection.insertOne(doc);

        String id = doc.getObjectId("_id").toString();
        System.out.println("Inserted product : " + id);

        AddProductResponse response = AddProductResponse.newBuilder()
                .setProduct(product.toBuilder().setId(id).build())
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }



    @Override
    public void deleteProduct(DeleteProductRequest request, StreamObserver<DeleteProductResponse> responseObserver) {
        System.out.println("Received delete product request");

        String productId  = request.getProductId();

        DeleteResult result = null;

        try{
            result = collection.deleteOne(eq("_id", new ObjectId(productId)));
        }catch (Exception e){
            System.out.println("Product not found");
            responseObserver.onError(
                    Status.NOT_FOUND
                            .withDescription("The product with the corresponding id was not found")
                            .augmentDescription(e.getLocalizedMessage())
                            .asRuntimeException()
            );
        }

        if(result.getDeletedCount() == 0){
            System.out.println("Product not found");
            responseObserver.onError(
                    Status.NOT_FOUND
                            .withDescription("The product with the corresponding id was not found")
                            .asRuntimeException()
            );
        }else{
            System.out.println("Product deleted");
            responseObserver.onNext(DeleteProductResponse.newBuilder()
                    .setProductId(productId)
                    .build());
            responseObserver.onCompleted();
        }
    }

    @Override
    public void updateProduct(UpdateProductRequest request, StreamObserver<UpdateProductResponse> responseObserver) {

        System.out.println("Received update product request");

        Product product = request.getProduct();
        String productId = product.getId();

        Document result = null;

        try{
            result = collection.find(eq("_id", new ObjectId(productId)))
                    .first();
        }catch(Exception e){
            responseObserver.onError(
                    Status.NOT_FOUND
                        .withDescription("The product with the correspondigh id was not found")
                        .augmentDescription(e.getLocalizedMessage())
                        .asRuntimeException()
            );
        }

        if(result == null){
            System.out.println("Blog not found");
            responseObserver.onError(
                    Status.NOT_FOUND
                            .withDescription("The product with the correspondigh id was not found")
                            .asRuntimeException()
            );
        }else{
            Document replacement = new Document("name", product.getName())
                    .append("price", product.getPrice())
                    .append("explanation", product.getExplanation())
                    .append("_id", new ObjectId(productId));

            System.out.println("Replacing product in database...");

            collection.replaceOne(eq("_id", result.getObjectId("_id")), replacement);

            System.out.println("Replaced! Sending a response");

            responseObserver.onNext(
                    UpdateProductResponse.newBuilder()
                            .setProduct(documentToProduct(replacement))
                            .build()
            );

            responseObserver.onCompleted();
        }

    }

    @Override
    public void readProduct(ReadProductRequest request, StreamObserver<ReadProductResponse> responseObserver) {

        System.out.println("Received read product request");
        String productId = request.getProductId();

        Document result = null;

        try{
            result = collection.find(eq("_id", new ObjectId(productId)))
                    .first();
        }catch (Exception e){
            responseObserver.onError(
                    Status.NOT_FOUND
                        .withDescription("The product with the corresponding id was not found")
                        .augmentDescription(e.getLocalizedMessage())
                        .asRuntimeException()
            );
        }

        if(result == null){
            System.out.println("Product not found");
            responseObserver.onError(
                    Status.NOT_FOUND
                            .withDescription("The product with the corresponding id was not found")
                            .asRuntimeException()
            );
        } else{
            System.out.println("Product found, sending response");
            Product product = documentToProduct(result);
            responseObserver.onNext(ReadProductResponse.newBuilder()
                    .setProduct(product)
                    .build());

            responseObserver.onCompleted();
        }
    }

    @Override
    public void listProducts(ListProductRequest request, StreamObserver<ListProductsResponse> responseObserver) {
        System.out.println("Received List Blog Request");

        collection.find().iterator().forEachRemaining(document -> responseObserver.onNext(
                ListProductsResponse.newBuilder().setProduct(documentToProduct(document)).build()
        ));

        responseObserver.onCompleted();    }

    private Product documentToProduct(Document document){
        return Product.newBuilder()
                .setName(document.getString("name"))
                .setPrice(document.getDouble("price"))
                .setExplanation(document.getString("explanation"))
                .setId(document.getObjectId("_id").toString())
                .build();
    }

}
