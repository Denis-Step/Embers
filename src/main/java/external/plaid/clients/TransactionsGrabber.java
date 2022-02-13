package external.plaid.clients;

import com.plaid.client.PlaidClient;
import com.plaid.client.request.TransactionsGetRequest;
import com.plaid.client.response.TransactionsGetResponse;
import external.plaid.entities.ImmutableTransaction;
import external.plaid.entities.Transaction;
import retrofit2.Call;
import retrofit2.Response;

import javax.inject.Inject;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Gets Transactions. Abstracts away interacting with Plaid.
 * Still requires accessToken to make it re-usable.
 */
public class TransactionsGrabber {
    private final PlaidClient plaidClient;

    /**
     * @param plaidClient {@link PlaidClient}
     */
    @Inject
    public TransactionsGrabber(PlaidClient plaidClient){
        this.plaidClient = plaidClient;
    }

    /**
     * @param user user to query.
     * @param institutionName institution to query.
     * @param accessToken plaid Item access token.
     * @param startDate inclusive.
     * @param endDate inclusive.
     * @return {@link Transaction}s.
     */
    public List<Transaction> requestTransactions(String user,
                                                 String institutionName,
                                                 String accessToken,
                                                 Date startDate,
                                                 Date endDate){
        TransactionsGetRequest transactionsGetRequest = new TransactionsGetRequest(accessToken, startDate, endDate);
        List<TransactionsGetResponse.Transaction> plaidTxs = callGetTransactionsRequest(transactionsGetRequest);

        return plaidTxs.stream()
                .map(tx -> buildFromPlaid(user, institutionName, tx))
                .collect(Collectors.toList());

    }

    /**
     * Leave public to allow different implementations.
     * @param transactionsGetRequest Plaid client's request type.
     * @return plaidclient's transactions.
     */
    public List<TransactionsGetResponse.Transaction> callGetTransactionsRequest
            (TransactionsGetRequest transactionsGetRequest) {

        try {

            Call<TransactionsGetResponse> txCall = plaidClient.service().transactionsGet(transactionsGetRequest);
            Response<TransactionsGetResponse> resp = txCall.execute();
            if (resp.isSuccessful()) {
                return resp.body().getTransactions();
            } else {
                throw new RuntimeException("Couldn't fetch transactions for " + transactionsGetRequest.clientId);
            }

        } catch (IOException ioException) {
            throw new RuntimeException(ioException.getCause());
        }
    }

    private Transaction buildFromPlaid (String user, String institution, TransactionsGetResponse.Transaction plaidTransaction) {
        return ImmutableTransaction.builder()
                .amount(plaidTransaction.getAmount())
                .description(plaidTransaction.getName())
                .originalDescription(plaidTransaction.getOriginalDescription())
                .merchantName(plaidTransaction.getMerchantName())
                .date(plaidTransaction.getDate())
                .accountId(plaidTransaction.getAccountId())
                .transactionId(plaidTransaction.getTransactionId())
                .user(user)
                .institutionName(institution)
                .build();
    }

}

