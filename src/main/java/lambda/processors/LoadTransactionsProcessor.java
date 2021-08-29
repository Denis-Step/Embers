package lambda.processors;

import com.plaid.client.PlaidClient;
import dagger.DaggerPlaidComponent;
import dynamo.PlaidItemDAO;
import dynamo.PlaidTransactionDAO;
import plaid.clients.TransactionsGrabber;
import plaid.entities.PlaidItem;
import plaid.entities.Transaction;

import java.io.IOException;
import java.time.Instant;
import java.util.Date;
import java.util.List;

public class LoadTransactionsProcessor {
    private final PlaidTransactionDAO transactionDAO;
    private final PlaidClient plaidClient;
    private final PlaidItemDAO plaidItemDAO;

    public LoadTransactionsProcessor() {
        this.transactionDAO = new PlaidTransactionDAO();
        this.plaidItemDAO = new PlaidItemDAO();
        this.plaidClient = DaggerPlaidComponent.create().buildPLaidClient();
    }

    public List<Transaction> pullFromPlaid(String user, Date startDate, Date endDate) throws IOException, MultipleItemsFoundException {
        TransactionsGrabber txGrab = new TransactionsGrabber(plaidClient, getItem(user).getAccessToken() );
        return txGrab.requestTransactions(startDate, endDate);
    }

    public List<Transaction> pullFromPlaid(String user, Date startDate) throws IOException, MultipleItemsFoundException {
        return pullFromPlaid(user, startDate, Date.from(Instant.now()));
    }

    private PlaidItem getItem(String user) throws MultipleItemsFoundException {
        List<PlaidItem> plaidItems = new PlaidItemDAO().query(user);

        // Make sure only access token returned.
        if (plaidItems.size() > 1) {
            throw new MultipleItemsFoundException("Found: " + plaidItems);
        } else {
            return plaidItems.get(0);
        }
    }

    public static class MultipleItemsFoundException extends Exception {
        public MultipleItemsFoundException(String errorMessage) {
            super(errorMessage);
        }
    }
}
