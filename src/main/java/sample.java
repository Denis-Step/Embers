import dagger.DaggerPlaidComponent;
import dagger.DaggerTwilioComponent;
import lambda.processors.ItemProcessor;
import lambda.processors.LoadTransactionsProcessor;
import lambda.processors.MessageProcessor;
import lambda.requests.transactions.GetTransactionsRequest;

import java.io.IOException;

public class sample {
    private static final String SAMPLE_ACCESS_TOKEN = "access-development-e0744ae4-f524-4b97-b710-5949fdd58d3b";

    public static void main(String[] args) {
        testProxy();
    }

    private static void testProxy() {
        System.out.print("Not used anymore");
    }

    private static void testMessages() {
        MessageProcessor messageProcessor = DaggerTwilioComponent.create().buildMessageProcessor();
        System.out.println(messageProcessor.sendMessage("+12148865506", "This is your fazha"));
    }

    public static void testTransactionsProcessor() throws IOException, ItemProcessor.ItemException {
        LoadTransactionsProcessor processor = DaggerPlaidComponent.create().buildLoadTransactionsProcessor();
        GetTransactionsRequest request = new GetTransactionsRequest();
        request.setStartDate("2020-08-01T10:15:30.00Z");
        request.setEndDate("2020-08-31T10:15:30.00Z");
        request.setUser("Denny");
        request.setInstitutionName("Discover");
        //List<Transaction> transactions = processor.pullNewTransactions(request);
    }
}
