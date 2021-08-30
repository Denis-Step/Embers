package dagger;

import com.plaid.client.PlaidClient;
import plaid.clients.ItemGrabber;
import plaid.clients.LinkGrabber;

import javax.inject.Singleton;

@Singleton
@Component(modules = {PlaidClientModule.class, PlaidCredentialsModule.class})
public interface PlaidComponent {

    PlaidClient buildPLaidClient();

    LinkGrabber buildPlaidGrabber();
    ItemGrabber buildItemGrabber();

}
