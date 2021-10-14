package dagger;

import com.plaid.client.PlaidClient;
import messages.TransactionSmsMessageConverter;

import javax.inject.Named;
import javax.inject.Singleton;

@Module
public interface PlaidClientModule {

    @Provides
    @Singleton
    static PlaidClient providePlaidClient(@Named("CLIENT_ID") String clientId,
                                          @Named("DEVELOPMENT_SECRET") String clientSecret){
        return PlaidClient.newBuilder()
                .clientIdAndSecret(clientId, clientSecret)
                .developmentBaseUrl()
                .build();
    }

    @Provides
    static TransactionSmsMessageConverter provideTransactionSmsMessageConverter() {
        return new TransactionSmsMessageConverter();
    }

}
