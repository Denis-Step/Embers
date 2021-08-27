package workflows.activities;

import plaid.entities.Transaction;

import java.util.Date;
import java.util.List;

public interface ItemActivities {

    // Will fetch an access Token.
    String getAccessToken(String user);

    // Will get Tx with accessToken
    List<Transaction> getTransactions(String accessToken, Date startDate);

    // Will print and log transactions.
    void listTransactions(List<Transaction> transactions);
}
