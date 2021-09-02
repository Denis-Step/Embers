import dagger.DaggerPlaidComponent;
import dagger.DaggerTwilioComponent;
import lambda.processors.ItemProcessor;
import lambda.processors.MessageProcessor;
import lambda.processors.TransactionProcessor;
import lambda.requests.GetTransactionsRequest;
import plaid.entities.Transaction;
import twilio.MessageClient;

import java.io.IOException;
import java.util.List;

public class sample {
    private static final String SAMPLE_ACCESS_TOKEN = "access-development-e0744ae4-f524-4b97-b710-5949fdd58d3b";

    public static void main(String[] args) throws  IOException, ItemProcessor.ItemException {
        //new MessageClient().sendMessage("+19175478272",  "Bankers HATE this 1 weird trick!!");
        MessageProcessor messageProcessor = DaggerTwilioComponent.create().buildMessageProcessor();
        System.out.println(messageProcessor.sendMessage("+19175478272", "anothertest"));
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
