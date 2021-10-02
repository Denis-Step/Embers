package dagger;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.eventbridge.AmazonEventBridge;
import com.amazonaws.services.eventbridge.AmazonEventBridgeClient;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
@Component(modules = {AwsClientModule.class})
public interface AwsComponent {

    DynamoDBMapper buildDynamo();
    AmazonDynamoDB buildDynamoClient();
    AWSCredentialsProvider buildCredentialsProvider();
    AmazonEventBridge buildAmazonEventBridge();
}
