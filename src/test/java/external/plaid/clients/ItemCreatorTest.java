package external.plaid.clients;

import com.plaid.client.PlaidApiService;
import com.plaid.client.PlaidClient;
import com.plaid.client.request.ItemPublicTokenExchangeRequest;
import com.plaid.client.response.ItemPublicTokenExchangeResponse;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import external.plaid.responses.PublicTokenExchangeResponse;
import retrofit2.Call;
import retrofit2.Response;

import java.io.IOException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import static org.junit.Assert.*;

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

    @Test
    public void requestItemMakesCallToMockService() throws IOException {
        itemCreator.requestItem(PUBLIC_TOKEN);
        verify(mockService.itemPublicTokenExchange(new ItemPublicTokenExchangeRequest(PUBLIC_TOKEN))).execute();
    }

    @Test
    public void callItemReturnCorrectResponse() {
        PublicTokenExchangeResponse response = this.itemCreator.requestItem(PUBLIC_TOKEN);

        assert (response.getID().equals(ITEM_ID));
        assert (response.getAccessToken().equals(ACCESS_TOKEN));
    }

    private void setup_Mocks() throws IOException {
        // Set up mocks.
        Call<ItemPublicTokenExchangeResponse> mockCall = mock(Call.class);
        Response<ItemPublicTokenExchangeResponse> mockResponse = mock(Response.class);
        ItemPublicTokenExchangeResponse mockResponsebody = mock(ItemPublicTokenExchangeResponse.class);

        // Need any() because exact request cannot be mocked or injected. Verify interaction in another test.
        when(mockService.itemPublicTokenExchange(any())).thenReturn(mockCall);
        when(mockCall.execute()).thenReturn(mockResponse);
        when(mockCall.toString()).thenReturn("MOCK_CALL");
        when(mockResponse.isSuccessful()).thenReturn(true);
        when(mockResponse.body()).thenReturn(mockResponsebody);
        when(mockResponsebody.getItemId()).thenReturn(ITEM_ID);
        when(mockResponsebody.getAccessToken()).thenReturn(ACCESS_TOKEN);
    }

}
