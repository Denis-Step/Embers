package lambda.handlers.items;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import dagger.DaggerProcessorComponent;
import lambda.processors.items.ItemProcessor;
import lambda.requests.items.CreateItemRequest;
import external.plaid.entities.PlaidItem;
import lambda.requests.items.ImmutableCreateItemRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.io.IOException;
import java.util.List;

public class CreateItemHandler
        implements RequestHandler<CreateItemHandler.LambdaCreateItemLambdaRequest, PlaidItem> {
    private final ItemProcessor processor;
    private static final Logger LOGGER = LoggerFactory.getLogger(CreateItemHandler.class);


    public CreateItemHandler() {this.processor = DaggerProcessorComponent.create().buildItemProcessor();}

    @Inject
    public CreateItemHandler(ItemProcessor processor) {this.processor = processor;}

    @Override
    public PlaidItem handleRequest(LambdaCreateItemLambdaRequest request, Context context) {
        return handleRequest(request.build(), context);
    }

    public PlaidItem handleRequest(CreateItemRequest event, Context context) {
        LOGGER.info("Getting access token for {}", event.getPublicToken());
        LOGGER.info(event.toString());

        try {
            PlaidItem item = processor.createPlaidItem(event);
            LOGGER.info("Created item:" + item.getId());
            return item;
        } catch (IOException e){
            // Rethrow Exception to prevent Lambda from succeeding.
            LOGGER.info("Exception" + e.getMessage() + System.currentTimeMillis());
            throw new RuntimeException(String.format("Exception: %s", e.getMessage()));
        }
    }

    public static class LambdaCreateItemLambdaRequest {
        private final ImmutableCreateItemRequest.Builder builder;

        private String user;
        private String publicToken;
        private String institutionId;
        private List<String> availableProducts;
        private List<String> accounts;
        private String dateCreated;
        private String metadata; // Remaining metadata. Rarely used.
        private boolean webhookEnabled;

        public LambdaCreateItemLambdaRequest() {
            this.builder = ImmutableCreateItemRequest.builder();
        }

        public ImmutableCreateItemRequest build() {return this.builder.build();}

        public String getUser() {
            return user;
        }

        public void setUser(String user) {
            this.builder.user(user);
            this.user = user;
        }

        public String getPublicToken() {
            return publicToken;
        }

        public void setPublicToken(String publicToken) {
            this.builder.publicToken(publicToken);
            this.publicToken = publicToken;
        }

        public String getInstitutionId() {
            return institutionId;
        }

        public void setInstitutionId(String institutionId) {
            this.builder.institutionId(institutionId);
            this.institutionId = institutionId;
        }

        public List<String> getAvailableProducts() {
            return availableProducts;
        }

        public void setAvailableProducts(List<String> availableProducts) {
            this.builder.availableProducts(availableProducts);
            this.availableProducts = availableProducts;
        }

        public List<String> getAccounts() {
            return accounts;
        }

        public void setAccounts(List<String> accounts) {
            this.builder.accounts(accounts);
            this.accounts = accounts;
        }

        public String getDateCreated() {
            return dateCreated;
        }

        public void setDateCreated(String dateCreated) {
            this.builder.dateCreated(dateCreated);
            this.dateCreated = dateCreated;
        }

        public String getMetadata() {
            return metadata;
        }

        public void setMetadata(String metadata) {
            this.builder.metadata(metadata);
            this.metadata = metadata;
        }

        public boolean isWebhookEnabled() {
            return webhookEnabled;
        }

        public void setWebhookEnabled(boolean webhookEnabled) {
            this.builder.webhookEnabled(webhookEnabled);
            this.webhookEnabled = webhookEnabled;
        }
    }
}
