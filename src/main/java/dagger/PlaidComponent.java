package dagger;

import com.plaid.client.PlaidClient;
import plaid.ItemRequester;
import plaid.LinkGrabber;
import dagger.PlaidClientModule;
import dagger.PlaidCredentialsModule;
import plaid.TransactionsGrabber;

import javax.inject.Singleton;

@Singleton
@Component(modules = {PlaidClientModule.class, PlaidCredentialsModule.class})
public interface PlaidComponent {

    PlaidClient buildPLaidClient();

    LinkGrabber buildPlaidGrabber();
    ItemRequester buildItemRequestor();

    TransactionsGrabber buildTransactionsGrabber();
}
