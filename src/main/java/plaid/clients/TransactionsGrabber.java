package plaid.clients;

import com.plaid.client.PlaidClient;
import com.plaid.client.request.TransactionsGetRequest;
import com.plaid.client.response.TransactionsGetResponse;
import dagger.DaggerPlaidComponent;
import lambda.requests.GetTransactionsRequest;
import plaid.entities.Transaction;
import retrofit2.Call;
import retrofit2.Response;

import java.io.IOException;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Gets Transactions. Abstracts away interacting with Plaid.
 * Still requires accessToken to make it re-usable.
 */
public class TransactionsGrabber {
    private final PlaidClient plaidClient;
    private final String accessToken;

    public TransactionsGrabber(String accessToken){
        this.plaidClient = DaggerPlaidComponent.create().buildPLaidClient();
        this.accessToken = accessToken;
    }

    public List<Transaction> requestTransactions(GetTransactionsRequest request) throws IOException {
        Date startDate = Date.from(Instant.parse(request.getStartDate()));
        Date endDate = Date.from(Instant.parse(request.getEndDate()));
        TransactionsGetRequest transactionsGetRequest = getTransactionsRequest(accessToken, startDate, endDate);

        Call<TransactionsGetResponse> call =  plaidClient.service().transactionsGet(transactionsGetRequest);
        Response<TransactionsGetResponse> resp = call.execute();

        if (resp.isSuccessful()) {

            return resp.body().getTransactions().stream()
                    .map(tx -> buildFromPlaid(request.getUser(), request.getInstitutionName(), tx))
                    .collect(Collectors.toList());

        } else {
            throw new RuntimeException(resp.toString());
        }
    }

   /*
    public List<Transaction> requestTransactions(Date startDate, Date endDate) throws IOException {
        TransactionsGetRequest transactionsGetRequest = getTransactionsRequest(accessToken, startDate, endDate);

        Call<TransactionsGetResponse> call =  plaidClient.service().transactionsGet(transactionsGetRequest);
        Response<TransactionsGetResponse> resp = call.execute();

        if (resp.isSuccessful()) {

            return resp.body().getTransactions().stream()
                    .map(tx -> buildFromPlaid(tx))
                    .collect(Collectors.toList());

        } else {
            throw new RuntimeException(resp.toString());
        }
    }

    // Expected to mostly be used through start Date only.
    // Automatically makes end date now.
    public List<Transaction> requestTransactions(Date startDate) throws IOException {
        return requestTransactions(startDate, Date.from(Instant.now()));
    }

    private Transaction buildFromPlaid (TransactionsGetResponse.Transaction plaidTransaction) {
        Transaction.Builder builder = new Transaction.Builder()
                .setAmount(plaidTransaction.getAmount())
                .setDescription(plaidTransaction.getName())
                .setOriginalDescription(plaidTransaction.getOriginalDescription())
                .setMerchantName(plaidTransaction.getMerchantName())
                .setDate(plaidTransaction.getDate())
                .setAccountId(plaidTransaction.getAccountId())
                .setTransactionId(plaidTransaction.getTransactionId());

        return builder.build();
    }

    */

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

    private TransactionsGetRequest getTransactionsRequest(String accessToken, Date startDate, Date endDate) {
        return new TransactionsGetRequest(accessToken, startDate, endDate);
    }

}

