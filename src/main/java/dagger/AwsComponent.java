package dagger;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import dynamo.NewPlaidItemDAO;
import dynamo.NewTransactionDAO;
import dynamo.TransactionDAO;
import events.impl.TransactionsEbClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

import javax.inject.Named;
import javax.inject.Singleton;

@Singleton
@Component(modules = {AwsClientModule.class})
public interface AwsComponent {

    DynamoDBMapper buildDynamo();
    AmazonDynamoDB buildDynamoClient();
    DynamoDbClient buildDynamoDbClient();
    DynamoDbEnhancedClient buildDynamoEnhancedClient();

    @Named("OLD_TRANSACTION_TABLE") DynamoDbTable<TransactionDAO> buildNewTransactionsTable();
    NewPlaidItemDAO buildPlaidItemDAO();
    NewTransactionDAO buildTransactionDAO();

    TransactionsEbClient buildTransactionsEbClient();
}
