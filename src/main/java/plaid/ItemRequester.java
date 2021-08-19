package plaid;

import com.plaid.client.PlaidClient;
import com.plaid.client.request.ItemPublicTokenExchangeRequest;
import com.plaid.client.response.ItemPublicTokenExchangeResponse;
import retrofit2.Response;

import javax.inject.Inject;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class ItemRequester {

    public final PlaidClient plaidClient;

    @Inject
    public ItemRequester(PlaidClient plaidClient) {
        this.plaidClient = plaidClient;
    }

    public PlaidItem requestItem(String publicToken) throws IOException {
        ItemPublicTokenExchangeRequest request = createRequest(publicToken);
        Response<ItemPublicTokenExchangeResponse> resp = plaidClient.service().itemPublicTokenExchange(request).execute();

        if (resp.isSuccessful()) {
            return new PlaidItem(resp.body().getAccessToken(),resp.body().getItemId());
        }
        else {
            throw new RuntimeException(resp.toString());
        }
    }

    private ItemPublicTokenExchangeRequest createRequest(String publicToken){
        return new ItemPublicTokenExchangeRequest(publicToken);
    }

}
