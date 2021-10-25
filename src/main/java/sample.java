import dagger.DaggerPlaidComponent;
import dagger.DaggerTwilioComponent;
import dynamo.PlaidItemDAO;
import lambda.processors.transactions.LoadTransactionsProcessor;
import lambda.processors.messages.MessageProcessor;
import lambda.requests.transactions.GetTransactionsRequest;

import java.io.IOException;

public class sample {
    private static final String SAMPLE_ACCESS_TOKEN = "access-development-e0744ae4-f524-4b97-b710-5949fdd58d3b";

    public static void main(String[] args) {
    }

    private static void testProxy() {
        System.out.print("Not used anymore");
    }

    private static void testMessages() {
        MessageProcessor messageProcessor = DaggerTwilioComponent.create().buildMessageProcessor();
    }
}
