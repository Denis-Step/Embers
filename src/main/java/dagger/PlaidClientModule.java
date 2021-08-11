package dagger;

import com.plaid.client.PlaidClient;
import plaid.PlaidGrabber;

import javax.inject.Named;
import javax.inject.Singleton;

@Module
public interface PlaidClientModule {

    @Provides
    @Singleton
    static PlaidClient providePlaidClient(@Named("CLIENT_ID") String clientId,
                                          @Named("SANDBOX_SECRET") String clientSecret){
        return PlaidClient.newBuilder()
                .clientIdAndSecret(clientId, clientSecret)
                .sandboxBaseUrl()
                .build();
    }

}
