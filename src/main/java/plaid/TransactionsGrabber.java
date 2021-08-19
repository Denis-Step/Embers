package plaid;

import com.plaid.client.PlaidClient;
import com.plaid.client.request.LinkTokenCreateRequest;
import com.plaid.client.request.TransactionsGetRequest;
import com.plaid.client.response.LinkTokenCreateResponse;
import retrofit2.Call;
import retrofit2.Response;

import javax.inject.Inject;
import java.time.Instant;
import java.util.Date;

// @TODO: Tx builder
public class TransactionsGrabber {
    PlaidClient plaidClient;

    @Inject
    public TransactionsGrabber(PlaidClient plaidClient){
        this.plaidClient = plaidClient;
    }

    private Object requestTransactions(String accessToken, Date startDate, Date endDate) {
        TransactionsGetRequest transactionsGetRequest = getLinkTokenCreateRequest(user, products);

        Call<LinkTokenCreateResponse> call =  plaidClient.service().linkTokenCreate(linkTokenCreateRequest);
        Response<LinkTokenCreateResponse> resp = call.execute();

        if (resp.isSuccessful()) {
            return resp.body().getLinkToken();
        } else {
            throw new RuntimeException(resp.toString());
        }
    }

    private TransactionsGetRequest getTransactionsRequest(String accessToken, Date startDate, Date endDate) {
        return new TransactionsGetRequest(accessToken, startDate, endDate);
    }

    // Default getTransactions is to get starting from a certain date.
    private TransactionsGetRequest getTransactionsRequest(String accessToken, Date startDate){
        return getTransactionsRequest(accessToken, startDate, Date.from(Instant.now()));
    }
}

