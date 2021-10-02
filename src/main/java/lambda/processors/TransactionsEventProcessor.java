package lambda.processors;


import com.amazonaws.services.eventbridge.AmazonEventBridge;
import dagger.DaggerAwsComponent;

// This is where events for new transactions are processed.
public class TransactionsEventProcessor {
    private AmazonEventBridge eventBridge;

    public TransactionsEventProcessor() {
        this.eventBridge = DaggerAwsComponent.create().buildAmazonEventBridge();
    }

    public TransactionsEventProcessor(AmazonEventBridge eventBridge) {
        this.eventBridge = eventBridge;
    }
}
