package lambda.processors.transactions;


import dynamo.PlaidItemDAO;
import dynamo.TransactionDAO;
import external.plaid.clients.TransactionsGrabber;
import external.plaid.entities.PlaidItem;
import external.plaid.entities.Transaction;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest(TransactionsGrabber.class)
public class LoadTransactionsProcessorTest {

    private static final String USER = "USER";
    private static final String INSTITUTION = "INSTITUTION";
    private static final String ACCESS_TOKEN = "1234456STOKEN";

    private static final Double AMOUNT = 49.99;
    private static final String DESCRIPTION = "SAMPLE_TRANSACTION";
    private static final String ORIGINAL_DESCRIPTION = "SAMPLE_DESCRIPTION";
    private static final String MERCHANT_NAME = "SAMPLE_MERCHANT";
    private static final String DATE = "2020-01-01";
    private static final String ACCOUNT_ID = "1233456789";
    private static final String TRANSACTION_ID = "TX-123456789";

    private final Date START_DATE;
    private final Date END_DATE;

    @Mock
    private final PlaidItemDAO plaidItemDAO;

    @Mock
    private final TransactionsGrabber transactionsGrabber;

    private final LoadTransactionsProcessor loadTransactionsProcessor;

    public LoadTransactionsProcessorTest() throws Exception {
        plaidItemDAO = mock(PlaidItemDAO.class);
        PlaidItem mockPlaidItem = mock(PlaidItem.class);
        when(plaidItemDAO.getItem(any(), any())).thenReturn(mockPlaidItem);
        when(mockPlaidItem.accessToken()).thenReturn(ACCESS_TOKEN);

        END_DATE = Date.from(Instant.now());
        START_DATE = Date.from(Instant.now().minus(1, ChronoUnit.DAYS));

        transactionsGrabber = mock(TransactionsGrabber.class);
        loadTransactionsProcessor = new LoadTransactionsProcessor(plaidItemDAO, transactionsGrabber);


    }

    @Test
    public void test_PullNewTransactions() throws PlaidItemDAO.ItemException, Exception {
        List<Transaction> mockPlaidTransactions = mock(List.class);
        when(transactionsGrabber.requestTransactions(USER, INSTITUTION, ACCESS_TOKEN,
                START_DATE, END_DATE)).thenReturn(mockPlaidTransactions);

        List<Transaction> transactions = loadTransactionsProcessor.pullNewTransactions(USER, INSTITUTION,
                START_DATE, END_DATE);
        assert (mockPlaidTransactions.equals(transactions));

    }

    private List<Transaction> createAndSaveNewTransactions() {
        List<Transaction> transactions = new ArrayList<>();
        for (int i = 0; i < 25; i++) {

            Transaction transaction = new Transaction();
            transaction.setTransactionId(TRANSACTION_ID + String.valueOf(i));
            transaction.setInstitutionName(INSTITUTION);
            transaction.setAmount(AMOUNT);
            transaction.setDate(DATE);
            transaction.setAccountId(ACCOUNT_ID);
            transaction.setDescription(DESCRIPTION);
            transaction.setMerchantName(MERCHANT_NAME);
            transaction.setAccountId(ACCOUNT_ID);
            transaction.setUser(USER);
            transaction.setOriginalDescription(ORIGINAL_DESCRIPTION);
        }
        return transactions;
    }

}
