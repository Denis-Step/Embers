package messages;

import external.plaid.entities.ImmutableTransaction;
import external.plaid.entities.Transaction;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class TransactionSmsMessageConverterTest {
    private static final String USER = "USER";
    private static final String INSTITUTION = "INSTITUTION";

    private static final Double AMOUNT = 49.99;
    private static final String DESCRIPTION = "SAMPLE_TRANSACTION";
    private static final String ORIGINAL_DESCRIPTION = "SAMPLE_DESCRIPTION";
    private static final String MERCHANT_NAME = "SAMPLE_MERCHANT";
    private static final String DATE = "2020-01-01";
    private static final String ACCOUNT_ID = "1233456789";
    private static final String TRANSACTION_ID = "TX-123456789";

    private final TransactionSmsMessageConverter transactionSmsMessageConverter;

    public TransactionSmsMessageConverterTest() {
        this.transactionSmsMessageConverter = new TransactionSmsMessageConverter();
    }

    @Test
    public void test_convertTransactionToSms() {
        Transaction transaction = sampleTransaction();
        String newTxSms = this.transactionSmsMessageConverter.createNewTransactionMessage(transaction);
        assert (newTxSms.equals("New Transaction: " +
                transaction.getDescription() + " " +
                "for " +
                transaction.getAmount() + " " +
                "at " +
                transaction.getMerchantName() +
                " on " +
                transaction.getInstitutionName()));

    }

    private Transaction sampleTransaction() {
        return ImmutableTransaction.builder()
                .transactionId(TRANSACTION_ID)
                .institutionName(INSTITUTION)
                .amount(AMOUNT)
                .date(DATE)
                .accountId(ACCOUNT_ID)
                .description(DESCRIPTION)
                .merchantName(MERCHANT_NAME)
                .accountId(ACCOUNT_ID)
                .user(USER)
                .originalDescription(ORIGINAL_DESCRIPTION)
                .build();
    }
}
