import com.plaid.client.PlaidClient;
import dagger.DaggerPlaidComponent;
import dynamo.PlaidItemDAO;
import lambda.processors.LoadTransactionsProcessor;
import plaid.clients.ItemRequester;
import plaid.entities.Transaction;
import plaid.responses.PublicTokenExchangeResponse;

import java.io.IOException;
import java.time.Instant;
import java.util.Date;
import java.util.List;

public class sample {
    private static final String SAMPLE_ACCESS_TOKEN = "access-development-e0744ae4-f524-4b97-b710-5949fdd58d3b";

    public static void main(String[] args) throws  IOException{
        //testItemQuery();

    }

   /* public static void  testLoadTransactionsProcessor() throws IOException, LoadTransactionsProcessor.MultipleItemsFoundException {
        LoadTransactionsProcessor processor = new LoadTransactionsProcessor();
        List<Transaction> transactions = processor.pullFromPlaid("Derek", Date.from(Instant.parse("2020-08-01T10:15:30.00Z")) );
        System.out.println(transactions);
    }

    public static void testItemQuery() {
        System.out.println(new PlaidItemDAO().queryAccessTokens("Derek", "Disco").get(0));
    }

    public static void testItemCreator() throws IOException {
        PlaidClient plaidClient = DaggerPlaidComponent.create().buildPLaidClient();
        ItemRequester itemRequester = new ItemRequester(plaidClient);

        PublicTokenExchangeResponse response = itemRequester.requestItem("public-development-32d715cf-252e-44cb-a230-95267d9e85fa");
        System.out.println(response);
    }


    public static void testTransactionsGrabber() throws IOException {
        PlaidClient plaidClient = DaggerPlaidComponent.create().buildPLaidClient();
        TransactionsGrabber txGrabber = new  TransactionsGrabber(plaidClient, SAMPLE_ACCESS_TOKEN);

        Date startDate = Date.from(Instant.parse("2020-08-01T10:15:30.00Z"));
        List<Transaction> transactions = txGrabber.requestTransactions(startDate );
        System.out.println(transactions);

        for (Transaction transaction: transactions) {
            System.out.println(transaction);
        }
    }

    public static void testDynamoDbUpload() {
        DynamoDBMapper dynamoDBMapper = DaggerAwsComponent.create().buildDynamo();
        PlaidItemDAO testTx = new PlaidItemDAO();
        testTx.setUser("Denis");
        testTx.setItemAccessToken("public-development-32d715cf-252e-44cb-a230-95267d9e85fa");
        List<String> plaidItems = new ArrayList<String>();
        testTx.setDate(Instant.now().toString());

        dynamoDBMapper.save(testTx);

    }

    private void testLinkCreateTokenResponse() {
        LinkGrabber linkGrabber = DaggerPlaidComponent.create().buildPlaidGrabber();
        System.out.println(sample.class.getPackage());
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
    }*/
}
