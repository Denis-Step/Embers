package external.plaid.clients;

import com.plaid.client.PlaidClient;
import com.plaid.client.request.LinkTokenCreateRequest;
import com.plaid.client.response.LinkTokenCreateResponse;
import retrofit2.Call;
import retrofit2.Response;

import javax.inject.Inject;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

public class LinkGrabber {

    public final PlaidClient plaidClient;
    private static final Logger LOGGER = Logger.getLogger(LinkGrabber.class.getName());

    // Serve only US requests.
    public static final List<String> COUNTRY_CODES = Arrays.asList("US");

    @Inject
    public LinkGrabber(PlaidClient plaidClient) {
        this.plaidClient = plaidClient;
    }

    public String getLinkToken(String user, List<String> products) {
        LinkTokenCreateRequest linkTokenCreateRequest = createLinkTokenCreateRequest(user, products);
        return callLinkTokenRequest(linkTokenCreateRequest);
    }

    public String getLinkToken(String user, List<String> products, String webhookUrl) {
        LOGGER.info(user + products + webhookUrl);
        LinkTokenCreateRequest linkTokenCreateRequest = createLinkTokenCreateRequest(user, products);
        linkTokenCreateRequest = linkTokenCreateRequest.withWebhook(webhookUrl);
        LOGGER.info(linkTokenCreateRequest.toString());

        return callLinkTokenRequest(linkTokenCreateRequest);
    }

    /**
     * Visible for testing.
     * @param linkTokenCreateRequest
     * @return
     */
    public String callLinkTokenRequest(LinkTokenCreateRequest linkTokenCreateRequest) {
        Call<LinkTokenCreateResponse> linkCall = plaidClient.service().linkTokenCreate(linkTokenCreateRequest);
        try {
            Response<LinkTokenCreateResponse> resp = linkCall.execute();
            return resp.body().getLinkToken();
        } catch (IOException ioException){
            throw new RuntimeException(ioException.getStackTrace().toString());
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
