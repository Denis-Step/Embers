package plaid.clients;

import com.plaid.client.PlaidClient;
import com.plaid.client.request.LinkTokenCreateRequest;
import com.plaid.client.response.LinkTokenCreateResponse;
import lambda.requests.CreateLinkTokenRequest;
import retrofit2.Call;
import retrofit2.Response;

import javax.inject.Inject;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class LinkGrabber {

    public final PlaidClient plaidClient;

    // Serve only US requests.
    public static final List<String> COUNTRY_CODES = Arrays.asList("US");

    @Inject
    public LinkGrabber(PlaidClient plaidClient) {
        this.plaidClient = plaidClient;
    }

    public String getLinkToken(CreateLinkTokenRequest request) throws IOException {
        LinkTokenCreateRequest linkTokenCreateRequest = createLinkTokenCreateRequest(request.getUser(), request.getProducts());
        Call<LinkTokenCreateResponse> call = plaidClient.service().linkTokenCreate(linkTokenCreateRequest);
        Response<LinkTokenCreateResponse> resp = call.execute();

        if (resp.isSuccessful()) {
            return resp.body().getLinkToken();
        } else {
            throw new RuntimeException(resp.toString());
        }

    }

    // Only serve English requests. Differ only on users and products supported for the item.
    private LinkTokenCreateRequest createLinkTokenCreateRequest(String user, List<String> products) {
        return new LinkTokenCreateRequest(new LinkTokenCreateRequest.User(user),
                "PlaidJava",
                products,
                COUNTRY_CODES,
                "en"
        );
    }


}
