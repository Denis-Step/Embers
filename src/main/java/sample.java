import com.fasterxml.jackson.databind.JsonNode;
import dagger.DaggerProcessorComponent;
import dagger.DaggerTwilioComponent;
import lambda.processors.messages.MessageProcessor;

public class sample {
    private static final String SAMPLE_ACCESS_TOKEN = "access-development-e0744ae4-f524-4b97-b710-5949fdd58d3b";

    public static void main(String[] args) {
        JsonNode testNode = DaggerProcessorComponent.create().buildJsonNode();
        System.out.println(testNode.get("PLAID_DEVELOPMENT_SECRET").textValue());
    }

    private static void testProxy() {
        System.out.print("Not used anymore");
    }

    private static void testMessages() {
        MessageProcessor messageProcessor = DaggerTwilioComponent.create().buildMessageProcessor();
    }
}
