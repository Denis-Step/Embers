package lambda.handlers.items;

import dynamo.setup.PlaidItemsTableUtils;
import external.plaid.entities.PlaidItem;
import lambda.handlers.items.CreateItemHandler;
import lambda.processors.items.ItemProcessor;
import lambda.requests.items.CreateItemRequest;
import lambda.requests.items.ImmutableCreateItemRequest;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class CreateItemHandlerTest {
    private CreateItemHandler createItemHandler;

    private final String USER = "USER";
    private final String PUBLIC_TOKEN = "PUBLIC_TOKEN";
    private final String INST_ID = "INST_ID";
    private final List<String> AVAILABLE_PRODUCTS = new ArrayList<>();
    private final List<String> ACCOUNTS = new ArrayList<>();
    private final String DATE_CREATED = "2022-01-01";
    private final String METADATA = "METADATA";
    private final boolean webhook = false;

    @Test
    public void handlerCallsProcessor() throws IOException {
        ItemProcessor processor = mock(ItemProcessor.class);
        PlaidItem expectedItem = PlaidItemsTableUtils.createItem();
        when(processor.createPlaidItem(sampleCreateItemRequest())).thenReturn(expectedItem);
        createItemHandler = new CreateItemHandler(mock(ItemProcessor.class));
        PlaidItem item = processor.createPlaidItem(sampleCreateItemRequest());
        assertEquals(expectedItem, item);

    }

    private CreateItemRequest sampleCreateItemRequest() {
        return ImmutableCreateItemRequest.builder()
                .user(USER)
                .accounts(ACCOUNTS)
                .dateCreated(DATE_CREATED)
                .institutionId(INST_ID)
                .metadata(METADATA)
                .publicToken(PUBLIC_TOKEN)
                .webhook(webhook)
                .build();
    }
}
