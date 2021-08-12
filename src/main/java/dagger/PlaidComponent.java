package dagger;

import com.plaid.client.PlaidClient;
import plaid.LinkGrabber;

import javax.inject.Singleton;

@Singleton
@Component(modules = {PlaidClientModule.class, CredentialsModule.class})
public interface PlaidComponent {

    PlaidClient buildPLaidClient();
    LinkGrabber buildPlaidGrabber();
}
