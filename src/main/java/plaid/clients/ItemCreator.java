package plaid.clients;

import com.plaid.client.PlaidClient;
import com.plaid.client.request.ItemPublicTokenExchangeRequest;
import com.plaid.client.response.ItemPublicTokenExchangeResponse;
import com.plaid.client.response.LinkTokenCreateResponse;
import lambda.requests.items.CreateItemRequest;
import plaid.entities.ImmutablePlaidItem;
import plaid.entities.PlaidItem;
import plaid.responses.PublicTokenExchangeResponse;
import retrofit2.Call;
import retrofit2.Response;

import javax.inject.Inject;
import java.io.IOException;

public class ItemCreator {

    private final PlaidClient plaidClient;

    @Inject
    public ItemCreator(PlaidClient plaidClient) {
        this.plaidClient = plaidClient;
    }

    public PublicTokenExchangeResponse requestItem(String publicToken)  {
        ItemPublicTokenExchangeRequest request = createItemPublicTokenExchangeRequest(publicToken);
        return callItemPublicTokenExchangeRequest(request);
    }

    /**
     * @VisibleForTesting Leave Public to allow alternative implementations.
     * Throw all exceptions at runtime as they are unrecoverable.
     * @param request Plaid-provided request type.
     * @return
     */
    public PublicTokenExchangeResponse callItemPublicTokenExchangeRequest(ItemPublicTokenExchangeRequest request) {
        Call<ItemPublicTokenExchangeResponse> publicTokenExchangeResponseCall = plaidClient.service()
                .itemPublicTokenExchange(request);

        try {
            Response<ItemPublicTokenExchangeResponse> resp = publicTokenExchangeResponseCall.execute();

            if (resp.isSuccessful()) {
                return new PublicTokenExchangeResponse(
                        resp.body().getItemId(),
                        resp.body().getAccessToken());
            } else {
                throw new RuntimeException("Can't create link token for " + request.clientId);
            }

        } catch (IOException ioException){
            throw new RuntimeException(ioException.getStackTrace().toString());
        }
    }

    // Leave this to be similar to LinkGrabber.
    private ItemPublicTokenExchangeRequest createItemPublicTokenExchangeRequest(String publicToken) {
        return new ItemPublicTokenExchangeRequest(publicToken);
    }

}
