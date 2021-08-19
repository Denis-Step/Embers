package lambda.handlers;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.EnvironmentVariableCredentialsProvider;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import dynamo.ItemsDAO;
import lambda.requests.CreateItemRequest;

import java.time.Instant;

public class ItemHandler implements RequestHandler<CreateItemRequest, String> {
    AWSCredentialsProvider environmentVariableCredentialsProvider = new EnvironmentVariableCredentialsProvider();
    DynamoDBMapper dynamoDBMapper =  new DynamoDBMapper(AmazonDynamoDBClientBuilder
            .standard()
            .withRegion("us-east-2")
            .withCredentials(environmentVariableCredentialsProvider)
            .build());

    public ItemHandler() {
    }

    @Override
    public String handleRequest(CreateItemRequest event, Context context) {
        LambdaLogger logger = context.getLogger();
        logger.log(event.getUser());
        logger.log(event.getAccessToken());
        String logTemplate = String.format("Storing item %s for user %s.", event.getAccessToken(), event.getUser());
        logger.log(logTemplate);

        ItemsDAO transactionsDAO = mapRequest(event);
        dynamoDBMapper.save(transactionsDAO);
        logger.log("Item stored:" + event.getAccessToken());

        return "Status code: 200.";

    }

    // Time now and empty transactions list stored.
    // @TODO: Change time implementation.
    private ItemsDAO mapRequest(CreateItemRequest request) {
        String timeNow = Instant.now().toString();
        ItemsDAO itemsDAO = new ItemsDAO();

        // Set sort key
        String itemAccessToken = request.getItemId() + "#" + request.getAccessToken();
        itemsDAO.setItemAccessToken(itemAccessToken);
        itemsDAO.setUser(request.getUser());
        itemsDAO.setDate(timeNow);
        return itemsDAO;
    }
}
