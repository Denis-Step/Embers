package dagger;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import dynamo.NewTransactionDAO;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.eventbridge.EventBridgeClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;

import javax.inject.Named;
import javax.inject.Singleton;
import java.net.URI;
import java.net.URISyntaxException;

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
    static AWSCredentialsProvider provideAWSCredentials() {
        return new DefaultAWSCredentialsProviderChain();
    }

    @Provides
    @Singleton
    static DynamoDBMapper provideDynamoDbMapper(AmazonDynamoDB amazonDynamoDB) {
        return new DynamoDBMapper(amazonDynamoDB);
    }

    @Provides
    @Singleton
    static AmazonDynamoDB provideAmazonDynamoDb(AWSCredentialsProvider awsCredentialsProvider) {
        return AmazonDynamoDBClientBuilder
                .standard()
                .withRegion("us-east-2")
                .withCredentials(awsCredentialsProvider)
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
     * Comment out endpoint override when pushing out to prod.
     */
    @Provides
    @Singleton
    static DynamoDbClient provideDdbClient() {
        try {
            return DynamoDbClient.builder()
                    .credentialsProvider(DefaultCredentialsProvider.create())
                    .endpointOverride(new URI("http://localhost:8000"))
                    .build();
        } catch (URISyntaxException e) {
            throw new RuntimeException("Cannot build DynamoDbClient");
        }
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
    static TableSchema<NewTransactionDAO> provideTransactionTableSchema() {
        return TableSchema.fromBean(NewTransactionDAO.class);
    }

    @Provides
    @Singleton
    @Named("TRANSACTION_TABLE")
    static DynamoDbTable<NewTransactionDAO> provideNewTransactionDdbTable(
            DynamoDbEnhancedClient dynamoDbEnhancedClient,
            @Named("TRANSACTION_TABLE_NAME") String transactionTableName,
            @Named("TRANSACTION_TABLE_SCHEMA") TableSchema<NewTransactionDAO> tableSchema) {

        return dynamoDbEnhancedClient
                .table(transactionTableName, tableSchema);
    }

    @Provides
    static NewTransactionDAO provideNewTransactionDao(DynamoDbEnhancedClient client,
            @Named("TRANSACTION_TABLE") DynamoDbTable<NewTransactionDAO> table) {
        return new NewTransactionDAO(client, table);
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
