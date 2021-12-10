package external.plaid.clients;

import com.plaid.client.PlaidClient;
import com.plaid.client.request.ItemPublicTokenExchangeRequest;
import com.plaid.client.response.ItemPublicTokenExchangeResponse;
import external.plaid.responses.PublicTokenExchangeResponse;
import lambda.processors.transactions.NewTransactionProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import retrofit2.Call;
import retrofit2.Response;

import javax.inject.Inject;
import java.io.IOException;

public class ItemCreator {

    private final PlaidClient plaidClient;
    private static final Logger LOGGER = LoggerFactory.getLogger(ItemCreator.class);

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
     * @return response.
     */
    public PublicTokenExchangeResponse callItemPublicTokenExchangeRequest(ItemPublicTokenExchangeRequest request) {
        Call<ItemPublicTokenExchangeResponse> publicTokenExchangeResponseCall = plaidClient.service()
                .itemPublicTokenExchange(request);

        LOGGER.info(publicTokenExchangeResponseCall.toString());

        try {
            Response<ItemPublicTokenExchangeResponse> resp = publicTokenExchangeResponseCall.execute();
            LOGGER.info("Response result: {}", resp);

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
