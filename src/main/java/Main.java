import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.plaid.client.request.LinkTokenCreateRequest;
import com.plaid.client.response.LinkTokenCreateResponse;
import dagger.DaggerAwsComponent;
import dagger.DaggerPlaidComponent;
import dynamo.TransactionsDAO;
import lambda.handlers.ItemHandler;
import lambda.requests.CreateItemRequest;
import plaid.LinkGrabber;
import retrofit2.Call;
import retrofit2.Response;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main {

    public static void main(String[] args){
       testDynamoDbUpload();
    }

    public static void testItemHandler() {
        CreateItemRequest createItemRequest = new CreateItemRequest();
        createItemRequest.setPlaidItem("public-development-32d715cf-252e-44cb-a230-95267d9e85fa");
        createItemRequest.setUser("Dan");
        ItemHandler itemHandler = new ItemHandler();
    }

    public static void testDynamoDbUpload() {
        DynamoDBMapper dynamoDBMapper = DaggerAwsComponent.create().buildDynamo();
        TransactionsDAO testTx = new TransactionsDAO();
        testTx.setUser("Denis");
        testTx.setPlaidItem("public-development-32d715cf-252e-44cb-a230-95267d9e85fa");
        List<String> plaidItems = new ArrayList<String>();
        testTx.setTransactions(plaidItems);
        testTx.setDate(Instant.now().toString());

        dynamoDBMapper.save(testTx);

    }

    private void testLinkCreateTokenResponse() {
        LinkGrabber linkGrabber = DaggerPlaidComponent.create().buildPlaidGrabber();
        System.out.println(Main.class.getPackage());
        List<String> products = Arrays.asList("transactions");
        List<String> countryCodes = Arrays.asList("US");

        LinkTokenCreateRequest linkTokenCreateRequest = new LinkTokenCreateRequest(new LinkTokenCreateRequest.User("Denis"),
                "PlaidJava",
                products,
                countryCodes,
                "en"
        );
        Call<LinkTokenCreateResponse> response =  linkGrabber.plaidClient.service().linkTokenCreate(linkTokenCreateRequest);
        try {
            Response<LinkTokenCreateResponse> resp = response.execute();
            System.out.println(resp);
            System.out.println(resp.body().getLinkToken());
        } catch (IOException e) {
            System.out.println(e);
        }
    }
}
