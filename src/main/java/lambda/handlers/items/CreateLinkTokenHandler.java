package lambda.handlers.items;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import dagger.DaggerProcessorComponent;
import lambda.processors.items.CreateLinkTokenProcessor;
import lambda.requests.items.CreateLinkTokenRequest;
import lambda.requests.items.ImmutableCreateLinkTokenRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

public class CreateLinkTokenHandler
        implements RequestHandler<CreateLinkTokenHandler.LambdaCreateLinkTokenRequest, String> {

    private final CreateLinkTokenProcessor processor;
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final Logger LOGGER = LoggerFactory.getLogger(CreateLinkTokenHandler.class);

    public CreateLinkTokenHandler() {this.processor = DaggerProcessorComponent.create().buildLinkTokenProcessor();}

    public CreateLinkTokenHandler(CreateLinkTokenProcessor processor) {
        this.processor = processor;
    }

    @Override
    public String handleRequest(LambdaCreateLinkTokenRequest request, Context context) {
        return handleRequest(request.build(), context);
    }

    public String handleRequest(CreateLinkTokenRequest event, Context context) {
        LOGGER.info("Getting link token for " + event.getUser() +
                " products " + event.getProducts() + " and webhook?: " + event.getWebhookEnabled());

        try {
            return processor.createLinkToken(event);
        } catch (IOException e) {
            // Rethrow Exception to prevent Lambda from succeeding.
            LOGGER.error("Exception" + e.getMessage() + System.currentTimeMillis());
            throw new RuntimeException(String.format("Exception: %s", e.getMessage()));
        }
    }

    public static class LambdaCreateLinkTokenRequest {
        private final ImmutableCreateLinkTokenRequest.Builder builder;

        private String user;
        private List<String> products;
        private boolean webhookEnabled;

        public LambdaCreateLinkTokenRequest() {
            this.builder = ImmutableCreateLinkTokenRequest.builder();
        }

        public void setUser(String user) {
            this.user = user;
            this.builder.user(user);

        }
        public void setProducts(List<String> products) {
            this.products = products;
            this.builder.products(products);
        }

        public void setWebhookEnabled(boolean webhookEnabled) {
            this.webhookEnabled = webhookEnabled;
            this.builder.webhookEnabled(webhookEnabled);
        }

        public String getUser() { return user; }
        public List<String> getProducts() { return products; }
        public boolean isWebhookEnabled() { return webhookEnabled; }

        public ImmutableCreateLinkTokenRequest build() {return this.builder.build();}
    }

}
