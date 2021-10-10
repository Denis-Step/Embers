package dagger;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import software.amazon.awssdk.services.eventbridge.EventBridgeClient;

import javax.inject.Singleton;

@Singleton
@Component(modules = {AwsClientModule.class})
public interface AwsComponent {

    DynamoDBMapper buildDynamo();
    AmazonDynamoDB buildDynamoClient();
    AWSCredentialsProvider buildCredentialsProvider();
    EventBridgeClient buildEventBridgeClient();
}
