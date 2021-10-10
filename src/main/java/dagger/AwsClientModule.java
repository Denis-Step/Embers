package dagger;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.services.eventbridge.EventBridgeClient;

import javax.inject.Named;
import javax.inject.Singleton;

@Module
public interface AwsClientModule {

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
