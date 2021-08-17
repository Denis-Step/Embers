package lambda.handlers;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import dagger.DaggerAwsComponent;
import dynamo.TransactionsDAO;
import lambda.requests.CreateItemRequest;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class ItemHandler implements RequestHandler<CreateItemRequest, String> {
    DynamoDBMapper dynamoDBMapper =  DaggerAwsComponent.create().buildDynamo();

    public ItemHandler() {
    }

    @Override
    public String handleRequest(CreateItemRequest event, Context context) {
        LambdaLogger logger = context.getLogger();
        logger.log(event.getUser());
        logger.log(event.getPlaidItem());
        String logTemplate = String.format("Storing item %s for user %s.", event.getPlaidItem(), event.getUser());
        logger.log(logTemplate);

        TransactionsDAO transactionsDAO = mapRequest(event);
        dynamoDBMapper.save(transactionsDAO);
        logger.log("Item stored:" + event.getPlaidItem());

        return "Status code: 200.";

    }

    // Time now and empty transactions list stored.
    // @TODO: Change time implementation.
    private TransactionsDAO mapRequest(CreateItemRequest request) {
        List<String> transactions = new ArrayList<String>();
        String timeNow = Instant.now().toString();

        TransactionsDAO transactionsDAO = new TransactionsDAO();
        transactionsDAO.setUser(request.getUser());
        transactionsDAO.setPlaidItem(request.getPlaidItem());
        transactionsDAO.setTransactions(transactions);
        transactionsDAO.setDate(timeNow);

        return transactionsDAO;
    }
}
