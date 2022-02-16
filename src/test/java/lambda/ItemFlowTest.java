package lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.plaid.client.PlaidApiService;
import com.plaid.client.PlaidClient;
import com.plaid.client.request.ItemPublicTokenExchangeRequest;
import com.plaid.client.response.ItemPublicTokenExchangeResponse;
import com.plaid.client.response.LinkTokenCreateResponse;
import dynamo.DynamoTableSchemas;
import dynamo.NewPlaidItemDAO;
import dynamo.setup.PlaidItemsTableUtils;
import dynamo.setup.client.LocalDynamoDbClient;
import external.plaid.clients.ItemCreator;
import external.plaid.clients.LinkGrabber;
import external.plaid.entities.PlaidItem;
import lambda.handlers.items.CreateItemHandler;
import lambda.handlers.items.CreateLinkTokenHandler;
import lambda.handlers.items.GetItemHandler;
import lambda.processors.items.CreateLinkTokenProcessor;
import lambda.processors.items.ItemProcessor;
import lambda.requests.items.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import retrofit2.Call;
import retrofit2.Response;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * This test will fetch a link token, use it to create an item, and query to make sure it is stored.
 * Imitates a user by running the full PlaidItem Link flow, mocking ONLY the network calls to Plaid.
 * This is essentially a local integration test and a sanity test.
 */
@RunWith(MockitoJUnitRunner.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ItemFlowTest {
    private static final PlaidItemsTableUtils plaidItemsTableUtils = new PlaidItemsTableUtils(LocalDynamoDbClient.getDynamoClient());

    @Mock
    private final PlaidApiService plaidApiService;
    @Mock
    private final PlaidClient plaidClient;

    private final LinkGrabber linkGrabber;
    private final ItemCreator itemCreator;

    private final NewPlaidItemDAO plaidItemDAO;

    private final CreateLinkTokenProcessor createLinkTokenProcessor;
    private final ItemProcessor itemProcessor;

    private final CreateLinkTokenHandler createLinkTokenHandler;
    private final CreateItemHandler createItemHandler;
    private final GetItemHandler getItemHandler;

    private final String LINK_TOKEN = "Link-1234";
    private final String[] PRODUCTS_ARRAY = {"transactions"};
    private final List<String> PRODUCTS = Arrays.asList(PRODUCTS_ARRAY);
    private final String[] COUNTRIES_ARRAY = {"US"};
    private final List<String> COUNTRY_CODES = Arrays.asList(COUNTRIES_ARRAY);
    private final String USER = "USER";

    private final String INST_ID = "INST_ID";
    private final String[] ACCOUNTS_ARRAY = {"CHASE1001"};
    private final List<String> ACCOUNTS = Arrays.asList(ACCOUNTS_ARRAY);
    private final String DATE_CREATED = "2022-01-01";
    private final String METADATA = "METADATA";
    private final boolean usingWebhook = false;

    private final String RESP_ID = "RESP_ID";
    private final String ACCESS_TOKEN = "ACCESS_TOKEN";
    private static final String ITEM_ID = "Item-1234";

    public ItemFlowTest() {
        this.plaidClient = mock(PlaidClient.class);
        this.plaidApiService = mock(PlaidApiService.class);
        when(plaidClient.service()).thenReturn(plaidApiService);

        this.plaidItemDAO = getPlaidItemDAO();
        this.linkGrabber = new LinkGrabber(plaidClient);
        this.itemCreator = new ItemCreator(plaidClient);

        this.createLinkTokenProcessor = new CreateLinkTokenProcessor(linkGrabber);
        this.itemProcessor = new ItemProcessor(itemCreator, plaidItemDAO);

        this.createLinkTokenHandler = new CreateLinkTokenHandler(createLinkTokenProcessor);
        this.createItemHandler = new CreateItemHandler(itemProcessor);
        this.getItemHandler = new GetItemHandler(itemProcessor);
    }

    @BeforeAll
    public void setUpTable() {
        plaidItemsTableUtils.setupPlaidItemsTable();
    }

    @Test
    public void itemFlowSuccessful() throws IOException {
        // Get link token first
        setup_LinkTokenMocks();
        String linkToken = createLinkTokenHandler.handleRequest(sampleLinkTokenRequest(usingWebhook), mock(Context.class));
        assertEquals(LINK_TOKEN, linkToken);

        // Use link token to create a new item. Verify interactions & make sure item is in stored/loaded in DDB properly.
        setup_CreateItemMocks(linkToken);
        PlaidItem createdPlaidItem = createItemHandler.handleRequest(sampleCreateItemRequest(linkToken), mock(Context.class));
        verify(plaidApiService.itemPublicTokenExchange(new ItemPublicTokenExchangeRequest(linkToken))).execute();
        List<PlaidItem> queriedPlaidItems = plaidItemDAO.query(createdPlaidItem.getUser());
        assertEquals(1, queriedPlaidItems.size());
        assertEquals(createdPlaidItem, queriedPlaidItems.get(0));

        // Search for the item through the GetItemHandler.
        List<PlaidItem> lambdaPlaidItemsList = getItemHandler.handleRequest(
                sampleGetItemRequest(createdPlaidItem.getUser(), createdPlaidItem.getInstitutionId()),
                mock(Context.class));
        assertEquals(1, lambdaPlaidItemsList.size());
        assertEquals(createdPlaidItem, lambdaPlaidItemsList.get(0));

        // Cleanup and verify.
        plaidItemDAO.delete(createdPlaidItem);
        List<PlaidItem> emptyPlaidItemsList = plaidItemDAO.query(createdPlaidItem.getUser());
        assertEquals(0, emptyPlaidItemsList.size());
    }

    private static NewPlaidItemDAO getPlaidItemDAO() {
        DynamoDbEnhancedClient enhancedDynamoClient = LocalDynamoDbClient.getEnhancedDynamoClient();
        DynamoDbTable<PlaidItem> itemsTable = enhancedDynamoClient.table("PlaidItems",
                DynamoTableSchemas.PLAID_ITEM_SCHEMA);

        return new NewPlaidItemDAO(itemsTable);
    }

    private void setup_LinkTokenMocks() throws IOException {
        Call<LinkTokenCreateResponse> mockCall = mock(Call.class);
        Response<LinkTokenCreateResponse> mockResponse = mock(Response.class);
        LinkTokenCreateResponse mockResponseBody = mock(LinkTokenCreateResponse.class);

        when(plaidApiService.linkTokenCreate(any())).thenReturn(mockCall);
        when(mockCall.execute()).thenReturn(mockResponse);
        when(mockResponse.isSuccessful()).thenReturn(true);
        when(mockResponse.body()).thenReturn(mockResponseBody);
        when(mockResponseBody.getLinkToken()).thenReturn(LINK_TOKEN);
    }

    public void setup_CreateItemMocks(String publicToken) throws IOException {
        Call<ItemPublicTokenExchangeResponse> mockCall = mock(Call.class);
        Response<ItemPublicTokenExchangeResponse> mockResponse = mock(Response.class);
        ItemPublicTokenExchangeResponse mockResponsebody = mock(ItemPublicTokenExchangeResponse.class);

        // Need any() because exact request cannot be mocked or injected. Verify interaction in another test.
        when(plaidApiService.itemPublicTokenExchange(any())).thenReturn(mockCall);
        when(mockCall.execute()).thenReturn(mockResponse);
        when(mockCall.toString()).thenReturn("MOCK_CALL");
        when(mockResponse.isSuccessful()).thenReturn(true);
        when(mockResponse.body()).thenReturn(mockResponsebody);
        when(mockResponsebody.getItemId()).thenReturn(ITEM_ID);
        when(mockResponsebody.getAccessToken()).thenReturn(ACCESS_TOKEN);
    }

    private CreateLinkTokenRequest sampleLinkTokenRequest(boolean hasWebhook) {
        return ImmutableCreateLinkTokenRequest.builder()
                .user(USER)
                .products(PRODUCTS)
                .webhookEnabled(hasWebhook)
                .build();
    }

    private CreateItemRequest sampleCreateItemRequest(String publicToken) {
        return ImmutableCreateItemRequest.builder()
                .user(USER)
                .accounts(ACCOUNTS)
                .dateCreated(DATE_CREATED)
                .institutionId(INST_ID)
                .metadata(METADATA)
                .publicToken(publicToken)
                .webhook(usingWebhook)
                .build();
    }

    private GetItemRequest sampleGetItemRequest(String user, String institutionIdAccessToken) {
        return ImmutableGetItemRequest.builder()
                .user(user)
                .institutionIdAccessToken(institutionIdAccessToken)
                .build();
    }

}
