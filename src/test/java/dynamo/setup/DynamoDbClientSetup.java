package dynamo.setup;

import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

import java.net.URI;
import java.net.URISyntaxException;

@RunWith(MockitoJUnitRunner.class)
public class DynamoDbClientSetup {

    public static DynamoDbClient getDefaultDdbClient() {
        DynamoDbClient dynamoDbClient = DynamoDbClient.builder()
                .credentialsProvider(DefaultCredentialsProvider.create())
                .build();

        try {
            return DynamoDbClient.builder()
                    .credentialsProvider(DefaultCredentialsProvider.create())
                    .endpointOverride(new URI("http://localhost:8000"))
                    .build();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e.getCause());
        }
    }
}
