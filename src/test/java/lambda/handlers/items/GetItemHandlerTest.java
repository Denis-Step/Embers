package lambda.handlers.items;

import com.amazonaws.services.lambda.runtime.Context;
import dynamo.setup.PlaidItemsTableUtils;
import external.plaid.entities.PlaidItem;
import lambda.handlers.items.GetItemHandler;
import lambda.processors.items.ItemProcessor;
import lambda.requests.items.GetItemRequest;
import lambda.requests.items.ImmutableGetItemRequest;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class GetItemHandlerTest {
    private final ItemProcessor processor;
    private final GetItemHandler getItemHandler;

    public GetItemHandlerTest() {
        this.processor = mock(ItemProcessor.class);
        this.getItemHandler = new GetItemHandler(processor);
    }

    @Test
    public void handlerCallsProcessor() {
        PlaidItem item = PlaidItemsTableUtils.createItem();
        List<PlaidItem> expectedPlaidItems = PlaidItemsTableUtils.createItems();
        when(processor.getItems(item.getUser(), item.getInstitutionId())).thenReturn(expectedPlaidItems);

        List<PlaidItem> plaidItems = getItemHandler.handleRequest(
                sampleGetItemRequest(item.getUser(), item.getInstitutionId()), mock(Context.class));
        assertEquals(expectedPlaidItems, plaidItems);
    }

    private GetItemRequest sampleGetItemRequest(String user, String institutionIdAccessToken) {
        return ImmutableGetItemRequest.builder()
                .user(user)
                .institutionIdAccessToken(institutionIdAccessToken)
                .build();
    }
}
