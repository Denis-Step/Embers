package lambda.processors;

import com.plaid.client.PlaidClient;
import dagger.DaggerPlaidComponent;
import dynamo.PlaidItemDAO;
import dynamo.PlaidTransactionDAO;
import plaid.clients.TransactionsGrabber;
import plaid.entities.Transaction;

import java.io.IOException;
import java.time.Instant;
import java.util.Date;
import java.util.List;

public class LoadTransactionsProcessor {
    private final PlaidTransactionDAO transactionDAO;
    private final PlaidClient plaidClient;

    public LoadTransactionsProcessor() {
        this.transactionDAO = new PlaidTransactionDAO();
        this.plaidClient = DaggerPlaidComponent.create().buildPLaidClient();
    }

    public List<Transaction> pullFromPlaid(String user, Date startDate, Date endDate) throws IOException {
        TransactionsGrabber txGrab = new TransactionsGrabber(plaidClient, getAccessToken(user));
        return txGrab.requestTransactions(startDate, endDate);
    }

    public List<Transaction> pullFromPlaid(String user, Date startDate) throws IOException {
        return pullFromPlaid(user, startDate, Date.from(Instant.now()));
    }

    private String getAccessToken(String user) {
        return new PlaidItemDAO().query(user, "Discover").get(0).toString();
    }
}
