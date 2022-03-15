package lambda.handlers.items;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import dagger.DaggerProcessorComponent;
import lambda.processors.items.ItemProcessor;
import lambda.requests.items.GetItemRequest;
import external.plaid.entities.PlaidItem;
import lambda.requests.items.ImmutableGetItemRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.List;

public class GetItemHandler
        implements RequestHandler<GetItemHandler.LambdaGetItemRequest, List<PlaidItem>> {
    private final ItemProcessor processor;
    private static final Logger LOGGER = LoggerFactory.getLogger(GetItemHandler.class);

    public GetItemHandler() {this.processor = DaggerProcessorComponent.create().buildItemProcessor();};

    @Inject
    public GetItemHandler(ItemProcessor processor) {
        this.processor = processor;
    }

    @Override
    public List<PlaidItem> handleRequest(LambdaGetItemRequest request, Context context) {
        return handleRequest(request.build(), context);
    }

    public List<PlaidItem> handleRequest(GetItemRequest event, Context context){
        LOGGER.info("Getting Item : {}", event.toString());
        List<PlaidItem> items = this.processor.getItems(event.getUser(), event.getInstitutionIdAccessToken());
        LOGGER.info("Retrieved item: {}",items.toString());
        return items;
    }

    public static class LambdaGetItemRequest {
        private final ImmutableGetItemRequest.Builder builder;

        private String user;
        private String institutionIdAccessToken;

        public LambdaGetItemRequest() {
            this.builder = ImmutableGetItemRequest.builder();
        }

        public ImmutableGetItemRequest build() {return this.builder.build();}

        public String getUser() {
            return user;
        }

        public void setUser(String user) {
            this.builder.user(user);
            this.user = user;
        }

        public String getInstitutionIdAccessToken() {
            return institutionIdAccessToken;
        }

        public void setInstitutionIdAccessToken(String institutionIdAccessToken) {
            this.builder.institutionIdAccessToken(institutionIdAccessToken);
            this.institutionIdAccessToken = institutionIdAccessToken;
        }
    }
}
