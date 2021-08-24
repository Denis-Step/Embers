package lambda.handlers;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import dagger.DaggerAwsComponent;
import dagger.DaggerPlaidComponent;
import lambda.requests.CreateLinkTokenRequest;
import lambda.requests.GetTransactionsRequest;
import plaid.clients.LinkGrabber;

import java.util.List;

public class GetTransactionsHandler implements RequestHandler<GetTransactionsRequest, String> {
    DynamoDBMapper dynamoDBMapper;

    public GetTransactionsHandler() {
        dynamoDBMapper = DaggerAwsComponent.create().buildDynamo();
    }

    @Override
    public String handleRequest(GetTransactionsRequest event, Context context) {
        return "";
    }

    /**
     * @param user JP user.
     * @return accessToken to use for grabbing transactions.
     */
    private String getAccessToken(String user) {

        return "";
    }

}
