package lambda.processors.items;

import dynamo.NewPlaidItemDAO;
import dynamo.NewPlaidItemDAO.MultipleItemsFoundException;
import dynamo.PlaidItemDAO;
import external.plaid.clients.ItemCreator;
import external.plaid.entities.PlaidItem;
import external.plaid.responses.PublicTokenExchangeResponse;
import lambda.requests.items.CreateItemRequest;
import lambda.requests.items.GetItemRequest;
import lambda.requests.items.ImmutableCreateItemRequest;
import lambda.requests.items.ImmutableGetItemRequest;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ItemProcessorTest {
    private ItemCreator itemCreator;
    private NewPlaidItemDAO plaidItemDAO;
    private ItemProcessor itemProcessor;

    private final String USER = "USER";
    private final String PUBLIC_TOKEN = "PUBLIC_TOKEN";
    private final String INST_ID = "INST_ID";
    private final List<String> AVAILABLE_PRODUCTS = new ArrayList<>();
    private final List<String> ACCOUNTS = new ArrayList<>();
    private final String DATE_CREATED = "2022-01-01";
    private final String METADATA = "METADATA";
    private final boolean webhook = false;

    private final String RESP_ID = "RESP_ID";
    private final String ACCESS_TOKEN = "ACESS_TOKEN";

    public ItemProcessorTest() {
        this.itemCreator = mock(ItemCreator.class);
        this.plaidItemDAO = mock(NewPlaidItemDAO.class);
        this.itemProcessor = new ItemProcessor(itemCreator, plaidItemDAO);

        AVAILABLE_PRODUCTS.add("PRODUCT");
        ACCOUNTS.add("ACCOUNT");
    }

    @Test
    public void createItemFromSampleRequest() throws IOException {
        PublicTokenExchangeResponse mockResponse = mock(PublicTokenExchangeResponse.class);
        when(mockResponse.getID()).thenReturn(RESP_ID);
        when(mockResponse.getAccessToken()).thenReturn(ACCESS_TOKEN);
        when(itemCreator.requestItem(PUBLIC_TOKEN)).thenReturn(mockResponse);

        CreateItemRequest request = sampleCreateItemRequest();
        PlaidItem item = itemProcessor.createPlaidItem(request);
        verify(plaidItemDAO).save(item);
        assertEquals(request.getUser(), item.getUser());
    }

    @Test
    public void callsDaoToGetAllItemsForUser() {
        PlaidItem mockItem1 = mock(PlaidItem.class);
        PlaidItem mockItem2 = mock(PlaidItem.class);

        List<PlaidItem> mockItemsList = new ArrayList<>();
        mockItemsList.add(mockItem1);
        mockItemsList.add(mockItem2);
        when(plaidItemDAO.query(USER, INST_ID)).thenReturn(mockItemsList);

        List<PlaidItem> returnedItemsList = itemProcessor.getItems(USER, INST_ID);
        verify(plaidItemDAO).query(USER, INST_ID);
        assertEquals(mockItemsList, returnedItemsList);
    }

    @Test
    public void getItemForSingleItem() throws MultipleItemsFoundException {
        GetItemRequest request = sampleGetItemRequest();
        PlaidItem mockItem = mock(PlaidItem.class);
        when(plaidItemDAO.get(request.getUser(), request.getInstitutionIdAccessToken())).thenReturn(
                Optional.of(mockItem));

        Optional<PlaidItem> plaidItemOptional = itemProcessor.getItem(request.getUser(),
                request.getInstitutionIdAccessToken());
        verify(plaidItemDAO).get(request.getUser(), request.getInstitutionIdAccessToken());
        assertEquals(mockItem, plaidItemOptional.get());
    }

    @Test
    public void getItemOptionalEmptyOnMultipleItemsReturned() throws MultipleItemsFoundException {
        GetItemRequest request = sampleGetItemRequest();
        PlaidItem mockItem = mock(PlaidItem.class);
        when(plaidItemDAO.get(request.getUser(), request.getInstitutionIdAccessToken()))
                .thenThrow(MultipleItemsFoundException.class);

        Optional<PlaidItem> plaidItemOptional = itemProcessor.getItem(request.getUser(),
                request.getInstitutionIdAccessToken());
        assertTrue(!plaidItemOptional.isPresent());
    }

    private GetItemRequest sampleGetItemRequest() {
        return ImmutableGetItemRequest.builder()
                .user(USER)
                .institutionIdAccessToken(INST_ID)
                .build();
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
