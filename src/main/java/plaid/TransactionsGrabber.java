package plaid;

import com.plaid.client.PlaidClient;
import com.plaid.client.request.TransactionsGetRequest;
import com.plaid.client.response.TransactionsGetResponse;
import retrofit2.Call;
import retrofit2.Response;

import javax.inject.Inject;
import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Gets Transactions. Abstracts away interacting with Plaid.
 * Still requires accessToken to make it re-usable for
 */
// @TODO: Tx builder
public class TransactionsGrabber {
    PlaidClient plaidClient;

    @Inject
    public TransactionsGrabber(PlaidClient plaidClient){
        this.plaidClient = plaidClient;
    }

    /**
     * @param accessToken
     * @param startDate
     * @param endDate
     * @return
     * @throws IOException
     */
    public List<Transaction> requestTransactions(String accessToken, Date startDate, Date endDate) throws IOException {
        TransactionsGetRequest transactionsGetRequest = getTransactionsRequest(accessToken, startDate, endDate);

        Call<TransactionsGetResponse> call =  plaidClient.service().transactionsGet(transactionsGetRequest);
        Response<TransactionsGetResponse> resp = call.execute();

        if (resp.isSuccessful()) {
            List<Transaction> processedTransactions = new ArrayList<>();

            for (TransactionsGetResponse.Transaction plaidTransaction: resp.body().getTransactions()) {
                Transaction newTransaction = new Transaction();
                newTransaction.accountId = plaidTransaction.getAccountId();
                newTransaction.transactionId = plaidTransaction.getTransactionId();
                newTransaction.amount = plaidTransaction.getAmount();
                newTransaction.date = plaidTransaction.getDate();
                newTransaction.description = plaidTransaction.getName();
                newTransaction.originalDescription = plaidTransaction.getOriginalDescription();
                newTransaction.merchantName = plaidTransaction.getMerchantName();
                processedTransactions.add(newTransaction);
            }

            return processedTransactions;

        } else {
            throw new RuntimeException(resp.toString());
        }
    }

    // Expected to mostly be used through start Date only.
    public List<Transaction> requestTransactions(String accessToken, Date startDate) throws IOException {
        return requestTransactions(accessToken, startDate, Date.from(Instant.now()));
    }

    private TransactionsGetRequest getTransactionsRequest(String accessToken, Date startDate, Date endDate) {
        return new TransactionsGetRequest(accessToken, startDate, endDate);
    }
}

