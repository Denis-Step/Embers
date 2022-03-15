package dagger;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.plaid.client.PlaidClient;
import messages.TransactionSmsMessageConverter;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;

import javax.inject.Named;
import javax.inject.Singleton;
import java.io.IOException;

@Module
public interface PlaidClientModule {
    String PLAID_SECRET_ARN = "arn:aws:secretsmanager:us-east-2:397250182609:secret:PlaidSecrets-kVhh0D";
    String PLAID_CLIENT_ID_KEY = "PLAID_CLIENT_ID";
    String PLAID_DEVELOPMENT_SECRET_KEY = "PLAID_DEVELOPMENT_SECRET";
    String PLAID_SANDBOX_SECRET_KEY = "PLAID_SANDBOX_SECRET";

    @Provides
    @Singleton
    @Named("PLAID_SECRETS_JSON")
    static JsonNode providePlaidSecretsJson(SecretsManagerClient secretsManagerClient,
                                            ObjectMapper objectMapper) {
        String secretsString = secretsManagerClient.getSecretValue(
                GetSecretValueRequest.builder()
                        .secretId(PLAID_SECRET_ARN)
                        .build()
        ).secretString();

        try {
            return objectMapper.readTree(secretsString);
        } catch (IOException e) {
            throw new RuntimeException("Cannot read plaid secrets");
        }
    }

    @Provides
    @Singleton
    static PlaidClient providePlaidClient(@Named("PLAID_SECRETS_JSON") JsonNode plaidSecretsJson){

            String clientId = plaidSecretsJson.get(PLAID_CLIENT_ID_KEY).textValue();
            String developmentSecret = plaidSecretsJson.get(PLAID_DEVELOPMENT_SECRET_KEY).textValue();

            return PlaidClient.newBuilder()
                    .clientIdAndSecret(clientId, developmentSecret)
                    .developmentBaseUrl()
                    .build();
    }

    @Provides
    static TransactionSmsMessageConverter provideTransactionSmsMessageConverter() {
        return new TransactionSmsMessageConverter();
    }


}
