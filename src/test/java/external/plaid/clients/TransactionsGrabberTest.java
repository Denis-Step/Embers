package external.plaid.clients;

import com.plaid.client.PlaidApiService;
import com.plaid.client.PlaidClient;
import com.plaid.client.response.TransactionsGetResponse;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import external.plaid.entities.Transaction;
import retrofit2.Call;
import retrofit2.Response;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class TransactionsGrabberTest {

    @Mock
    PlaidApiService mockService;

    @Mock
    PlaidClient plaidClient;

    private final TransactionsGrabber transactionsGrabber;

    private static final String USER = "USER";
    private static final String INSTITUTION = "INSTITUTION";
    private static final String ACCESS_TOKEN = "ACCESS_TOKEN";

    private static final Date START_DATE = Date.from(Instant.now());
    private static final Date END_DATE = Date.from(Instant.now());

    private static final Double AMOUNT = 49.99;
    private static final String DESCRIPTION = "SAMPLE_TRANSACTION";
    private static final String ORIGINAL_DESCRIPTION = "SAMPLE_DESCRIPTION";
    private static final String MERCHANT_NAME = "SAMPLE_MERCHANT";
    private static final String DATE = "2020-01-01";
    private static final String ACCOUNT_ID = "1233456789";
    private static final String TRANSACTION_ID = "TX-123456789";

    public TransactionsGrabberTest() throws IOException {
        this.plaidClient = mock(PlaidClient.class);
        this.transactionsGrabber = new TransactionsGrabber(this.plaidClient);
        this.mockService = mock(PlaidApiService.class);
        when(this.plaidClient.service()).thenReturn(this.mockService);
        setup_Mocks();
    }

    @Test
    public void test_requestTransactions() {
        List<Transaction> transactions = this.transactionsGrabber.requestTransactions(USER, INSTITUTION,
                ACCESS_TOKEN, START_DATE, END_DATE);
        Transaction sampleTx = transactions.get(0);
        assert (sampleTx.institutionName == INSTITUTION);
        assert (sampleTx.amount == AMOUNT);
        verify(this.mockService, times(1)).transactionsGet(any());
    }

    private void setup_Mocks() throws IOException {
        Response<TransactionsGetResponse> mockResponse = mock(Response.class);
        Call<TransactionsGetResponse> mockCall = mock(Call.class);
        TransactionsGetResponse mockResponseBody = mock(TransactionsGetResponse.class);

        when(this.mockService.transactionsGet(any())).thenReturn(mockCall);
        when(mockCall.execute()).thenReturn(mockResponse);
        when(mockResponse.isSuccessful()).thenReturn(true);
        when(mockResponse.body()).thenReturn(mockResponseBody);

        List<TransactionsGetResponse.Transaction> newTransactions = new ArrayList<>();
        newTransactions.add(mock_Transaction());
        when(mockResponseBody.getTransactions()).thenReturn(newTransactions);
    }

    private TransactionsGetResponse.Transaction mock_Transaction() {
        TransactionsGetResponse.Transaction mockTransaction = mock(TransactionsGetResponse.Transaction.class);
        when(mockTransaction.getAmount()).thenReturn(AMOUNT);
        when(mockTransaction.getName()).thenReturn(DESCRIPTION);
        when(mockTransaction.getOriginalDescription()).thenReturn(ORIGINAL_DESCRIPTION);
        when(mockTransaction.getMerchantName()).thenReturn(MERCHANT_NAME);
        when(mockTransaction.getDate()).thenReturn(DATE);
        when(mockTransaction.getAccountId()).thenReturn(ACCOUNT_ID);
        when(mockTransaction.getTransactionId()).thenReturn(TRANSACTION_ID);

        return mockTransaction;
    }

}
