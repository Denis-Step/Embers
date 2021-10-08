package plaid;

import com.plaid.client.PlaidClient;
import com.plaid.client.request.LinkTokenCreateRequest;
import com.plaid.client.response.LinkTokenCreateResponse;
import lambda.requests.link.CreateLinkTokenRequest;
import org.junit.jupiter.api.BeforeAll;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import plaid.clients.LinkGrabber;
import retrofit2.Call;
import retrofit2.Response;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class LinkTest {

    private String LINK_TOKEN = "access-development-e0744ae4-f524-4b97-b710-5949fdd58d3b";
    private List<String> PRODUCTS = Arrays.asList("transactions");
    private String USER = "JOE";
    private static PlaidClient plaidClient;

    @BeforeAll
    static void initAll() {
        plaidClient = mock(PlaidClient.class);
    }

    @Test
    void testLinkGrabber() throws IOException {
        LinkTokenCreateRequest request = new LinkTokenCreateRequest(new LinkTokenCreateRequest.User(USER),
                "PlaidJava",
                PRODUCTS,
                LinkGrabber.COUNTRY_CODES,
                "en"
        );
        Call<LinkTokenCreateResponse> mockedCall = mock(Call.class);
        Response<LinkTokenCreateResponse> resp = mock(Response.class);
        when(plaidClient.service().linkTokenCreate(request)).thenReturn(mockedCall);
        when(mockedCall.execute()).thenReturn(resp);
        when(resp.isSuccessful()).thenReturn(true);
        when(resp.body().getLinkToken()).thenReturn(LINK_TOKEN);


        LinkGrabber linkGrabber = new LinkGrabber(this.plaidClient);
        CreateLinkTokenRequest createLinkTokenRequest = sample_CreateLinkTokenRequest();
        String accessToken = linkGrabber.getLinkToken(
                createLinkTokenRequest.getUser(),
                createLinkTokenRequest.getProducts());
        assertEquals(accessToken, LINK_TOKEN);
    }

    private CreateLinkTokenRequest sample_CreateLinkTokenRequest() {
        CreateLinkTokenRequest request = new CreateLinkTokenRequest();
        request.setUser(USER);
        request.setProducts(PRODUCTS);
        return request;
    }

}
