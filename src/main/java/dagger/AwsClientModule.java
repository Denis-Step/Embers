package dagger;

import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import dynamo.TransactionDAO;
import external.plaid.entities.PlaidItem;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.eventbridge.EventBridgeClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;

import javax.inject.Named;
import javax.inject.Singleton;

@Module
public interface AwsClientModule {

    @Provides
    @Singleton
    @Named("TRANSACTION_TABLE_NAME")
    static String provideTransactionTableName() {
        return "Transactions";
    }

    @Provides
    @Singleton
    static AwsCredentialsProvider provideAWSCredentials() {
        return DefaultCredentialsProvider.create();
    }

    @Provides
    @Singleton
    static DynamoDBMapper provideDynamoDbMapper(AmazonDynamoDB amazonDynamoDB) {
        return new DynamoDBMapper(amazonDynamoDB);
    }

    @Provides
    @Singleton
    static AmazonDynamoDB provideAmazonDynamoDb() {
        return AmazonDynamoDBClientBuilder
                .standard()
                .withRegion("us-east-2")
                .withCredentials(new DefaultAWSCredentialsProviderChain())
                .build();
    }

    @Provides
    @Singleton
    static EventBridgeClient provideAmazonEventBridge() {
        return EventBridgeClient.builder()
                .credentialsProvider( DefaultCredentialsProvider.create() )
                .build();

    }

    /**
     * @return v2 Ddb Client.
     */
    @Provides
    @Singleton
    static DynamoDbClient provideDdbClient() {
            return DynamoDbClient.builder()
                    .credentialsProvider(DefaultCredentialsProvider.create())
                    .build();
    }

    @Provides
    static DynamoDbEnhancedClient provideEnhancedDdbClient(DynamoDbClient dynamoDbClient) {
        return DynamoDbEnhancedClient.builder()
                .dynamoDbClient(dynamoDbClient)
                .build();
    }

    @Provides
    @Singleton
    @Named("TRANSACTION_TABLE_SCHEMA")
    static TableSchema<TransactionDAO> provideTransactionTableSchema() {
        return TableSchema.fromBean(TransactionDAO.class);
    }

    @Provides
    @Singleton
    @Named("TRANSACTION_TABLE")
    static DynamoDbTable<TransactionDAO> provideNewTransactionDdbTable(
            DynamoDbEnhancedClient dynamoDbEnhancedClient,
            @Named("TRANSACTION_TABLE_NAME") String transactionTableName,
            @Named("TRANSACTION_TABLE_SCHEMA") TableSchema<TransactionDAO> tableSchema) {

        return dynamoDbEnhancedClient
                .table(transactionTableName, tableSchema);
    }

    @Provides
    static TransactionDAO provideNewTransactionDao(DynamoDbEnhancedClient client,
                                                   @Named("TRANSACTION_TABLE") DynamoDbTable<TransactionDAO> table) {
        return new TransactionDAO(client, table);
    }

    @Provides
    @Singleton
    static SecretsManagerClient provideSecretsManager(AwsCredentialsProvider awsCredentialsProvider) {
        return SecretsManagerClient.builder()
                .credentialsProvider(awsCredentialsProvider)
                .build();

    }

    @Provides
    @Named("WEBHOOK_CALLBACK")
    static String provideApiEndpoint() {
        return "https://mv6o8yjeo1.execute-api.us-east-2.amazonaws.com/Beta/";
    }

    @Provides
    static ObjectMapper provideObjectMapper() {
        return new ObjectMapper();
    }
}
