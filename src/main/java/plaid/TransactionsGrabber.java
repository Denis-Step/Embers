package plaid;

import com.plaid.client.PlaidClient;

import javax.inject.Inject;

public class TransactionsGrabber {
    PlaidClient plaidClient;

    @Inject
    public TransactionsGrabber(PlaidClient plaidClient){
        this.plaidClient = plaidClient;
    }
}
