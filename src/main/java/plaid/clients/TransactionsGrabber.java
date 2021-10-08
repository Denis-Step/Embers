package plaid.clients;

import com.plaid.client.PlaidClient;
import com.plaid.client.request.TransactionsGetRequest;
import com.plaid.client.response.TransactionsGetResponse;
import dagger.DaggerPlaidComponent;
import plaid.entities.Transaction;
import retrofit2.Call;
import retrofit2.Response;

import java.io.IOException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Gets Transactions. Abstracts away interacting with Plaid.
 * Still requires accessToken to make it re-usable.
 */
public class TransactionsGrabber {
    private final PlaidClient plaidClient;
    private final String user;
    private final String institutionName;
    private final String accessToken;

    public TransactionsGrabber(String user, String institutionName, String accessToken){
        this.plaidClient = DaggerPlaidComponent.create().buildPLaidClient();
        this.user = user;
        this.institutionName = institutionName;
        this.accessToken = accessToken;
    }

    public List<Transaction> requestTransactions(Date startDate, Date endDate) throws IOException {

        TransactionsGetRequest transactionsGetRequest = new TransactionsGetRequest(accessToken, startDate, endDate);
        List<TransactionsGetResponse.Transaction> plaidTxs = callGetTransactionsRequest(transactionsGetRequest);

        return plaidTxs.stream()
                .map(tx -> buildFromPlaid(user, institutionName, tx))
                .collect(Collectors.toList());

    }

    private List<TransactionsGetResponse.Transaction> callGetTransactionsRequest(TransactionsGetRequest transactionsGetRequest)
            throws IOException {

        Call<TransactionsGetResponse> txCall = plaidClient.service().transactionsGet(transactionsGetRequest);
        Response<TransactionsGetResponse> resp = txCall.execute();

        if (resp.isSuccessful()) {
            return resp.body().getTransactions();
        } else { throw new RuntimeException(resp.toString()); }
    }

    private Transaction buildFromPlaid (String user, String institution, TransactionsGetResponse.Transaction plaidTransaction) {
        return Transaction.getBuilder()
                .setAmount(plaidTransaction.getAmount())
                .setDescription(plaidTransaction.getName())
                .setOriginalDescription(plaidTransaction.getOriginalDescription())
                .setMerchantName(plaidTransaction.getMerchantName())
                .setDate(plaidTransaction.getDate())
                .setAccountId(plaidTransaction.getAccountId())
                .setTransactionId(plaidTransaction.getTransactionId())
                .setUser(user)
                .setInstitutionName(institution)
                .build();
    }

}

