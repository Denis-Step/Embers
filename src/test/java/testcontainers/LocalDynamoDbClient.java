package testcontainers;

import org.testcontainers.containers.GenericContainer;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

import javax.inject.Singleton;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * LDB Client for using real DynamoDB instance in tests.
 */
public class LocalDynamoDbClient {
    private static LocalDynamoDbContainer localDynamoDbContainer;

    public static DynamoDbClient getDynamoClient() {
        // Ensure LDB running.
        LocalDynamoDbContainer localDynamoDbContainer = LocalDynamoDbContainer.getInstance();

        try {
            return DynamoDbClient.builder()
                .credentialsProvider(DefaultCredentialsProvider.create())
                .endpointOverride(new URI(String.format("http://localhost:%d", localDynamoDbContainer.getExposedPort())))
                .build();
        } catch (URISyntaxException e) {
            throw new RuntimeException("URI is wrong");
        }
    }
}
