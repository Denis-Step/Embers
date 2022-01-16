package testcontainers;

import org.testcontainers.containers.GenericContainer;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

import java.net.URI;
import java.net.URISyntaxException;

public class LocalDynamoDbClient {
    private static GenericContainer<?> localDynamoDbContainer;

    public static DynamoDbClient getDynamoClient() {

        if (localDynamoDbContainer == null) {
            localDynamoDbContainer = LocalDynamoDbContainer.getInstance();
        }

        try {
            return DynamoDbClient.builder()
                .credentialsProvider(DefaultCredentialsProvider.create())
                .endpointOverride(new URI("http://localhost:8000"))
                .build();
        } catch (URISyntaxException e) {
            throw new RuntimeException("URI is wrong");
        }
    }
}
