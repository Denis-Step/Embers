package dagger;

import com.plaid.client.PlaidClient;
import plaid.clients.ItemRequester;
import plaid.clients.LinkGrabber;

import javax.inject.Singleton;

@Singleton
@Component(modules = {PlaidClientModule.class, PlaidCredentialsModule.class})
public interface PlaidComponent {

    PlaidClient buildPLaidClient();

    LinkGrabber buildPlaidGrabber();
    ItemRequester buildItemRequestor();

}
