package plaid.clients;

import com.plaid.client.PlaidApiService;
import com.plaid.client.PlaidClient;
import com.plaid.client.response.ItemPublicTokenExchangeResponse;
import lambda.requests.items.CreateItemRequest;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import plaid.responses.PublicTokenExchangeResponse;
import retrofit2.Call;
import retrofit2.Response;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ItemCreatorTest {

    @Mock
    private PlaidClient plaidClient;

    @Mock
    private PlaidApiService mockService;

    private ItemCreator itemCreator;

    private static final String PUBLIC_TOKEN = "Pub-1234-Token";
    private static final String ITEM_ID = "Item-1234";
    private static final String ACCESS_TOKEN = "Access-1234";

    public ItemCreatorTest() throws IOException {
        this.plaidClient = mock(PlaidClient.class);
        mockService = mock(PlaidApiService.class);
        when(plaidClient.service()).thenReturn(mockService);
        this.itemCreator = new ItemCreator(plaidClient);

        setup_Mocks();
    }


    public void setup_Mocks() throws IOException {
        // Set up mocks.
        Call<ItemPublicTokenExchangeResponse> mockCall = mock(Call.class);
        Response<ItemPublicTokenExchangeResponse> mockResponse = mock(Response.class);
        ItemPublicTokenExchangeResponse mockResponsebody = mock(ItemPublicTokenExchangeResponse.class);

        when(mockService.itemPublicTokenExchange(any())).thenReturn(mockCall);
        when(mockCall.execute()).thenReturn(mockResponse);
        when(mockResponse.isSuccessful()).thenReturn(true);
        when(mockResponse.body()).thenReturn(mockResponsebody);
        when(mockResponsebody.getItemId()).thenReturn(ITEM_ID);
        when(mockResponsebody.getAccessToken()).thenReturn(ACCESS_TOKEN);
    }

    @Test
    public void test_requestItem() throws IOException {
        PublicTokenExchangeResponse response = itemCreator.requestItem(PUBLIC_TOKEN);
        verify(mockService.itemPublicTokenExchange(any())).execute();
    }

    @Test
    public void test_callItemPublicTokenExchangeRequest() {
        //PublicTokenExchangeResponse response = new PublicTokenExchangeResponse(ITEM_ID, ACCESS_TOKEN, false);
        CreateItemRequest createItemRequest = getSampleCreateItemRequest();
        PublicTokenExchangeResponse response = this.itemCreator.requestItem(createItemRequest.getPublicToken());

        assert (response.getID() == ITEM_ID);
        assert (response.getAccessToken() == ACCESS_TOKEN);

    }

    private CreateItemRequest getSampleCreateItemRequest() {
        List<String> products = new ArrayList<>();
        products.add("transactions");
        List<String> accounts = new ArrayList<>();
        accounts.add("CHASE-1001");

        CreateItemRequest createItemRequest = new CreateItemRequest();
        createItemRequest.setUser("Den");
        createItemRequest.setPublicToken(PUBLIC_TOKEN);
        createItemRequest.setInstitutionId("INST-1234");
        createItemRequest.setAvailableProducts(products);
        createItemRequest.setAccounts(accounts);
        createItemRequest.setDateCreated("2020-01-01");
        createItemRequest.setMetaData("METADATA");

        return createItemRequest;
    }
}
