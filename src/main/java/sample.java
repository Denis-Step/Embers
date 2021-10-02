import dagger.DaggerPlaidComponent;
import dagger.DaggerTwilioComponent;
import lambda.processors.ItemProcessor;
import lambda.processors.MessageProcessor;
import lambda.processors.TransactionProcessor;
import lambda.requests.CreateLinkTokenRequest;
import lambda.requests.GetTransactionsRequest;
import lambda.requests.RequestInvocationHandler;
import plaid.entities.Transaction;

import java.io.IOException;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;

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
        TransactionProcessor processor = DaggerPlaidComponent.create().buildTransactionProcessor();
        GetTransactionsRequest request = new GetTransactionsRequest();
        request.setStartDate("2020-08-01T10:15:30.00Z");
        request.setEndDate("2020-08-31T10:15:30.00Z");
        request.setUser("Denny");
        request.setInstitutionName("Discover");
        List<Transaction> transactions = processor.pullFromPlaid(request);
    }
}
