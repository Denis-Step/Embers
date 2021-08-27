package workflows.activities;

import com.plaid.client.PlaidClient;
import dagger.DaggerPlaidComponent;
import dynamo.PlaidItemDAO;
import plaid.clients.TransactionsGrabber;
import plaid.entities.Transaction;

import java.io.IOException;
import java.time.Instant;
import java.util.Date;
import java.util.List;

public class ItemActivitiesImpl implements ItemActivities {
    private final PlaidClient plaidClient = DaggerPlaidComponent.create().buildPLaidClient();

    @Override
    public String getAccessToken(String user) {
        return PlaidItemDAO.queryAccessTokens(user).get(0);
    }

    @Override
    public List<Transaction> getTransactions(String accessToken, Date startDate)  {
        TransactionsGrabber transactionsGrabber = new TransactionsGrabber(plaidClient, accessToken);
        try {
            List<Transaction> transactions = transactionsGrabber.requestTransactions(startDate);
            return transactions;
        } catch (IOException ioException) {
            throw new RuntimeException(ioException.getCause());
        }
    }

    @Override
    public void listTransactions(List<Transaction> transactions) {
        System.out.println(transactions.toString());
    }
}
