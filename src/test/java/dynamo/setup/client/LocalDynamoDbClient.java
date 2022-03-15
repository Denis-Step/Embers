package dynamo.setup.client;

import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * LDB Client for using real DynamoDB instance in tests.
 * Uses static methods to encapsulate and hide internal dependency on LocalDynamoDb Docker container.
 */
public class LocalDynamoDbClient {
    /**
     * Singleton class for Docker Container running local DynamoDb instance.
     */
    private static LocalDynamoDbContainer localDynamoDbContainer;

    /**
     * For regular dynamoDb client to query local DynamoDb.
     * @return dynamo client
     */
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

    /**
     * For enhanced DynamoDb client to query local DynamoDb.
     * @return enhanced DynamoDb client
     */
    public static DynamoDbEnhancedClient getEnhancedDynamoClient() {
        DynamoDbClient dynamoDbClient = getDynamoClient();
        return DynamoDbEnhancedClient.builder()
                .dynamoDbClient(dynamoDbClient)
                .build();
    }
}
