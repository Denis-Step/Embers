package dagger;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import dynamo.NewTransactionDAO;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.services.eventbridge.EventBridgeClient;

import javax.inject.Named;
import javax.inject.Singleton;

@Singleton
@Component(modules = {AwsClientModule.class})
public interface AwsComponent {

    DynamoDBMapper buildDynamo();
    AmazonDynamoDB buildDynamoClient();
    DynamoDbEnhancedClient buildDynamoEnhancedClient();
    AWSCredentialsProvider buildCredentialsProvider();
    EventBridgeClient buildEventBridgeClient();

    NewTransactionDAO buildNewTransactionDao();
    @Named("TRANSACTION_TABLE") DynamoDbTable<NewTransactionDAO> buildNewTransactionsTable();
}
