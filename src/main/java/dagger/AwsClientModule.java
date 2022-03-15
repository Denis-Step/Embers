package dagger;

import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import dynamo.DynamoTableSchemas;
import dynamo.TransactionDAO;
import events.impl.SmsEbClient;
import events.impl.TransactionsEbClient;
import external.plaid.entities.PlaidItem;
import external.plaid.entities.Transaction;
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

    static final String TRANSACTIONS_EVENT_BUS_NAME = "TransactionsBus";
    static final String SMS_EVENT_BUS_NAME = "SmsBus";

    @Provides
    @Singleton
    @Named("TRANSACTION_TABLE_NAME")
    static String provideTransactionTableName() {
        return "Transactions";
    }

    @Provides
    @Singleton
    @Named("PLAID_ITEM_TABLE_NAME")
    static String providePlaidItemTableName() {return "PlaidItems";}

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
    @Named("OLD_TRANSACTION_TABLE_SCHEMA")
    static TableSchema<TransactionDAO> provideOldTransactionTableSchema() {
        return TableSchema.fromBean(TransactionDAO.class);
    }

    @Provides
    @Singleton
    @Named("PLAID_ITEM_TABLE_SCHEMA")
    static TableSchema<PlaidItem> providePlaidItemSchema() {
        return DynamoTableSchemas.PLAID_ITEM_SCHEMA;
    }

    @Provides
    @Singleton
    @Named("TRANSACTION_TABLE_SCHEMA")
    static TableSchema<Transaction> provideTransactionTableSchema() {
        return DynamoTableSchemas.TRANSACTION_SCHEMA;
    }

    @Provides
    @Singleton
    @Named("OLD_TRANSACTION_TABLE")
    static DynamoDbTable<TransactionDAO> provideNewTransactionDdbTable(
            DynamoDbEnhancedClient dynamoDbEnhancedClient,
            @Named("TRANSACTION_TABLE_NAME") String transactionTableName,
            @Named("OLD_TRANSACTION_TABLE_SCHEMA") TableSchema<TransactionDAO> tableSchema) {

        return dynamoDbEnhancedClient
                .table(transactionTableName, tableSchema);
    }

    @Provides
    @Singleton
    static DynamoDbTable<PlaidItem> providePlaidItemTable(DynamoDbEnhancedClient dynamoDbEnhancedClient,
                                                          @Named("PLAID_ITEM_TABLE_NAME") String itemTableName,
                                                          @Named("PLAID_ITEM_TABLE_SCHEMA") TableSchema<PlaidItem> tableSchema) {

        return dynamoDbEnhancedClient.table(itemTableName, tableSchema);
    }

    @Provides
    @Singleton
    static DynamoDbTable<Transaction> provideTransactionTable(DynamoDbEnhancedClient dynamoDbEnhancedClient,
                                                              @Named("TRANSACTION_TABLE_NAME") String txTableName,
                                                              @Named("TRANSACTION_TABLE_SCHEMA") TableSchema<Transaction> tableSchema) {
        return dynamoDbEnhancedClient.table(txTableName, tableSchema);
    }

    @Provides
    static TransactionDAO provideTransactionDao(DynamoDbEnhancedClient client,
                                                   @Named("OLD_TRANSACTION_TABLE") DynamoDbTable<TransactionDAO> table) {
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


    @Provides
    static TransactionsEbClient provideTransactionsEbClient(EventBridgeClient eventBridgeClient,
                                                            ObjectMapper objectMapper) {
        return new TransactionsEbClient(eventBridgeClient, TRANSACTIONS_EVENT_BUS_NAME, objectMapper);
    }

    @Provides
    static SmsEbClient provideSmsEbClient(EventBridgeClient eventBridgeClient,
                                          @Named("DEFAULT_MAPPER") ObjectMapper objectMapper) {
        return new SmsEbClient(eventBridgeClient, SMS_EVENT_BUS_NAME, objectMapper);
    }

}
