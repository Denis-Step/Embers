package plaid.clients;

import com.plaid.client.PlaidClient;
import com.plaid.client.request.ItemPublicTokenExchangeRequest;
import com.plaid.client.response.ItemPublicTokenExchangeResponse;
import lambda.requests.CreateItemRequest;
import plaid.entities.ImmutableLinkedItem;
import plaid.entities.ImmutablePlaidItem;
import plaid.entities.LinkedItem;
import plaid.entities.PlaidItem;
import plaid.responses.PublicTokenExchangeResponse;
import retrofit2.Response;

import javax.inject.Inject;
import java.io.IOException;

public class ItemGrabber {

    public final PlaidClient plaidClient;

    @Inject
    public ItemGrabber(PlaidClient plaidClient) {
        this.plaidClient = plaidClient;
    }

    public PlaidItem createItem(CreateItemRequest createItemRequest) throws IOException {
        PublicTokenExchangeResponse itemInfo = requestItem(createItemRequest.getPublicToken());

        return ImmutablePlaidItem.builder().
                ID(itemInfo.getID())
                .accessToken(itemInfo.getAccessToken())
                .user(createItemRequest.getUser())
                .dateCreated(createItemRequest.getDateCreated())
                .availableProducts(createItemRequest.getAvailableProducts())
                .accounts(createItemRequest.getAccounts())
                .institutionId(createItemRequest.getInstitutionId())
                .metaData(createItemRequest.getMetaData())
                .build();
    }

    // Leave public to allow different implementations of PlaidItem by exposing bare requestItem functionality.
    public PublicTokenExchangeResponse requestItem(String publicToken) throws IOException {
        ItemPublicTokenExchangeRequest request = createItemPublicTokenExchangeRequest(publicToken);
        Response<ItemPublicTokenExchangeResponse> resp = plaidClient.service().itemPublicTokenExchange(request).execute();

        if (resp.isSuccessful()) {
            return new PublicTokenExchangeResponse(
                    resp.body().getItemId(),
                    resp.body().getAccessToken(),
                    false
            );
        }
        else {
            throw new RuntimeException(resp.toString());
        }
    }

    // Leave this to be similar to LinkGrabber.
    private ItemPublicTokenExchangeRequest createItemPublicTokenExchangeRequest(String publicToken) {
        return new ItemPublicTokenExchangeRequest(publicToken);
    }

}
