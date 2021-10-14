package external.plaid.clients;

import com.plaid.client.PlaidApiService;
import com.plaid.client.PlaidClient;
import com.plaid.client.request.LinkTokenCreateRequest;
import com.plaid.client.response.LinkTokenCreateResponse;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import retrofit2.Call;
import retrofit2.Response;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class LinkGrabberTest {

    @Mock
    private final PlaidClient plaidClient;

    @Mock
    private final PlaidApiService mockService;

    @Mock
    private final LinkGrabber linkGrabber;

    private final String USER = "SAMPLE";
    private final String[] PRODUCTS_ARRAY = {"transactions"};
    private final List<String> PRODUCTS = Arrays.asList(PRODUCTS_ARRAY);
    private final String[] COUNTRIES_ARRAY = {"US"};
    private final List<String> COUNTRY_CODES = Arrays.asList(COUNTRIES_ARRAY);
    private final String WEBHOOK_URL = "http://www.google.com";
    private final String LINK_TOKEN = "Link-1234";

    public LinkGrabberTest() throws IOException {
        this.plaidClient = mock(PlaidClient.class);
        mockService = mock(PlaidApiService.class);
        when(plaidClient.service()).thenReturn(mockService);
        this.linkGrabber = new LinkGrabber(plaidClient);

        setup_Mocks();
    }

    @Test
    public void test_getLinkToken() {
        String linkTokenA = this.linkGrabber.getLinkToken(USER, PRODUCTS);
        String linkTokenB = this.linkGrabber.getLinkToken(USER, PRODUCTS, WEBHOOK_URL);
        assert (linkTokenA == LINK_TOKEN);
    }

    @Test
    public void test_callLinkTokenRequest() {
        LinkTokenCreateRequest linkTokenCreateRequest = new LinkTokenCreateRequest(new LinkTokenCreateRequest.User(USER),
                "PlaidJava",
                PRODUCTS,
                COUNTRY_CODES,
                "en"
        );
        String linkToken =  this.linkGrabber.callLinkTokenRequest(linkTokenCreateRequest);
        assert (linkToken == LINK_TOKEN);
    }

    private void setup_Mocks() throws IOException {
        // Set up mocks.
        Call<LinkTokenCreateResponse> mockCall = mock(Call.class);
        Response<LinkTokenCreateResponse> mockResponse = mock(Response.class);
        LinkTokenCreateResponse mockResponseBody = mock(LinkTokenCreateResponse.class);

        when(mockService.linkTokenCreate(any())).thenReturn(mockCall);
        when(mockCall.execute()).thenReturn(mockResponse);
        when(mockResponse.isSuccessful()).thenReturn(true);
        when(mockResponse.body()).thenReturn(mockResponseBody);
        when(mockResponseBody.getLinkToken()).thenReturn(LINK_TOKEN);
    }

}
