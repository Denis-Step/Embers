package lambda.processors.transactions;

import dynamo.PlaidItemDAO;
import events.impl.SmsEbClient;
import external.plaid.entities.PlaidItem;
import external.plaid.entities.Transaction;
import messages.SmsMessage;
import messages.TransactionSmsMessageConverter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Optional;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class NewTransactionProcessorTest {

    @Mock
    private final PlaidItem plaidItem;

    @Mock
    private final PlaidItemDAO plaidItemDAO;

    @Mock
    private final SmsEbClient smsEbClient;

    private final NewTransactionProcessor newTransactionProcessor;

    private static final String USER = "USER";
    private static final String INSTITUTION = "INSTITUTION";

    private static final Double AMOUNT = 49.99;
    private static final String DESCRIPTION = "SAMPLE_TRANSACTION";
    private static final String ORIGINAL_DESCRIPTION = "SAMPLE_DESCRIPTION";
    private static final String MERCHANT_NAME = "SAMPLE_MERCHANT";
    private static final String DATE = "2020-01-01";
    private static final String ACCOUNT_ID = "1233456789";
    private static final String TRANSACTION_ID = "TX-123456789";

    public NewTransactionProcessorTest() throws PlaidItemDAO.ItemException {
        plaidItemDAO = Mockito.mock(PlaidItemDAO.class);
        plaidItem = mock(PlaidItem.class);
        smsEbClient = Mockito.mock(SmsEbClient.class);
        TransactionSmsMessageConverter converter = new TransactionSmsMessageConverter();

        newTransactionProcessor = new NewTransactionProcessor(plaidItemDAO, smsEbClient, converter);
    }

    @Test(expected = PlaidItemDAO.ItemException.class)
    public void test_throwsException() throws PlaidItemDAO.ItemException {
        when(plaidItemDAO.getItem(any(), any())).thenReturn(plaidItem);
        Optional receiverNumberOptional = Optional.empty();
        when(plaidItem.receiverNumber()).thenReturn(receiverNumberOptional);
        newTransactionProcessor.process(createTransaction());
    }

    @Test
    public void test_sendsSmsMessage() throws PlaidItemDAO.ItemException {
        when(plaidItemDAO.getItem(any(), any())).thenReturn(plaidItem);
        Optional receiverNumberOptional = Optional.of("+191755555555");
        when(plaidItem.receiverNumber()).thenReturn(receiverNumberOptional);

        verify(smsEbClient).createNewSmsEvent((SmsMessage) any());
    }

    private Transaction createTransaction() {
        Transaction transaction = new Transaction();
        transaction.setTransactionId(TRANSACTION_ID);
        transaction.setInstitutionName(INSTITUTION);
        transaction.setAmount(AMOUNT);
        transaction.setDate(DATE);
        transaction.setAccountId(ACCOUNT_ID);
        transaction.setDescription(DESCRIPTION);
        transaction.setMerchantName(MERCHANT_NAME);
        transaction.setAccountId(ACCOUNT_ID);
        transaction.setUser(USER);
        transaction.setOriginalDescription(ORIGINAL_DESCRIPTION);
        return transaction;
    }
}
