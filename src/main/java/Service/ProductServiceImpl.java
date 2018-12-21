package Service;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.proto.model.product.AddProductRequest;
import com.proto.model.product.AddProductResponse;
import com.proto.model.product.Product;
import com.proto.model.product.ProductServiceGrpc;
import io.grpc.stub.StreamObserver;
import org.bson.Document;

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
}
