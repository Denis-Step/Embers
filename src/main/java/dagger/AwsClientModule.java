package dagger;

import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.auth.EnvironmentVariableCredentialsProvider;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;

import javax.inject.Singleton;

@Module
public interface AwsClientModule {

    @Provides
    @Singleton
    static ProfileCredentialsProvider provideProfileCredentialsProvider() {
        return new ProfileCredentialsProvider();
    }

    @Provides
    @Singleton
    static DynamoDBMapper provideDynamoDbMapper(AmazonDynamoDB amazonDynamoDB) {
        return new DynamoDBMapper(amazonDynamoDB);
    }

    @Provides
    @Singleton
    static AmazonDynamoDB provideAmazonDynamoDb(ProfileCredentialsProvider profileCredentialsProvider) {
        return AmazonDynamoDBClientBuilder
                .standard()
                .withRegion("us-east-2")
                .withCredentials(profileCredentialsProvider)
                .build();
    }

}
