package dagger;

import com.plaid.client.PlaidClient;
import plaid.PlaidGrabber;

import javax.inject.Singleton;

@Singleton
@Component(modules = {PlaidClientModule.class, CredentialsModule.class})
public interface PlaidComponent {

    PlaidClient buildPLaidClient();
    PlaidGrabber buildPlaidGrabber();
}
