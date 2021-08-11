package plaid;

import com.plaid.client.PlaidClient;

import javax.inject.Inject;

public class PlaidGrabber {

    public final PlaidClient plaidClient;

    @Inject
    public PlaidGrabber(PlaidClient plaidClient) {
        this.plaidClient = plaidClient;
    }

}
