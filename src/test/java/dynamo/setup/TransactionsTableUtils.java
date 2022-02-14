package dynamo.setup;

import com.amazonaws.services.dynamodbv2.document.Table;
import dagger.DaggerAwsComponent;
import dynamo.TransactionDAO;
import external.plaid.entities.ImmutableTransaction;
import external.plaid.entities.Transaction;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class TransactionsTableUtils {
    public static final String TRANSACTION_TABLE_NAME = "Transactions";
    public static final String HASH_KEY_USER = "user";
    public static final String RANGE_KEY = "dateTransactionId";
    public static final String INSTITUTION_GSI_ATTRIBUTE = "institutionName";
    public static final String AMOUNT_LSI_ATTRIBUTE = "amount";
    public static final String DESCRIPTION_LSI_ATTRIBUTE = "description";
    public static final String ORIGINAL_DESCRIPTION_ATTRIBUTE = "originalDescription";
    public static final String MERCHANT_NAME_LSI_ATTRIBUTE = "merchantName";
    public static final String ITEM_ID_ATTRIBUTE = "itemId";

    private static final String USER = "USER";
    private static final String INSTITUTION = "INSTITUTION";

    private static final Double AMOUNT = 49.99;
    private static final String DESCRIPTION = "SAMPLE_TRANSACTION";
    private static final String ORIGINAL_DESCRIPTION = "SAMPLE_DESCRIPTION";
    private static final String MERCHANT_NAME = "SAMPLE_MERCHANT";
    private static final String DATE = "2020-01-01";
    private static final String ACCOUNT_ID = "1233456789";
    private static final String TRANSACTION_ID = "TX-123456789";

    private final DynamoDbClient dynamoDbClient;

    public TransactionsTableUtils(DynamoDbClient dynamoDbClient) {this.dynamoDbClient = dynamoDbClient;}

    /**
     * Clean up existing table and create new one.
     */
    public void setUpTransactionsTable() {

        try {
            deleteTransactionsTable();
        } catch (DynamoDbException e) {
            // no-op.
        }

        CreateTableRequest createTableRequest = CreateTableRequest.builder()
                .tableName(TRANSACTION_TABLE_NAME)
                .attributeDefinitions(getAttributeDefinitions())
                .localSecondaryIndexes(getLocalSecondaryIndices())
                .keySchema(getKeySchemaElements())
                .provisionedThroughput( ProvisionedThroughput.builder()
                        .readCapacityUnits(5L)
                        .writeCapacityUnits(5L)
                        .build())
                .build();

        dynamoDbClient.createTable(createTableRequest);
    }

    public void deleteTransactionsTable() {
        DeleteTableRequest deleteTableRequest = DeleteTableRequest.builder()
                .tableName(TRANSACTION_TABLE_NAME)
                .build();
        dynamoDbClient.deleteTable(deleteTableRequest);
    }

    private List<KeySchemaElement> getKeySchemaElements() {
        List<KeySchemaElement> keySchemaElements = new ArrayList<>();
        KeySchemaElement partitionKey = KeySchemaElement.builder()
                .keyType(KeyType.HASH)
                .attributeName(HASH_KEY_USER)
                .build();
        keySchemaElements.add(partitionKey);

        KeySchemaElement rangeKey = KeySchemaElement.builder()
                .keyType(KeyType.RANGE)
                .attributeName(RANGE_KEY)
                .build();
        keySchemaElements.add(rangeKey);

        return keySchemaElements;
    }

    private List<LocalSecondaryIndex> getLocalSecondaryIndices() {
        List<KeySchemaElement> keySchemaElements = new ArrayList<>();
        KeySchemaElement partitionKey = KeySchemaElement.builder()
                .keyType(KeyType.HASH)
                .attributeName(HASH_KEY_USER)
                .build();
        keySchemaElements.add(partitionKey);

        KeySchemaElement rangeKey = KeySchemaElement.builder()
                .keyType(KeyType.RANGE)
                .attributeName(AMOUNT_LSI_ATTRIBUTE)
                .build();
        keySchemaElements.add(rangeKey);

        List<LocalSecondaryIndex> localSecondaryIndices = new ArrayList<>();
        LocalSecondaryIndex amountLsi = LocalSecondaryIndex.builder()
                .indexName(AMOUNT_LSI_ATTRIBUTE + "Index")
                .keySchema(keySchemaElements)
                .projection( Projection.builder()
                        .projectionType("ALL")
                        .build() )
                .build();
        localSecondaryIndices.add(amountLsi);
        return localSecondaryIndices;
    }

    private List<AttributeDefinition> getAttributeDefinitions() {
        List<AttributeDefinition> attributeDefinitions = new ArrayList<>();

        AttributeDefinition userAttribute = AttributeDefinition.builder()
                .attributeName(HASH_KEY_USER)
                .attributeType(ScalarAttributeType.S)
                .build();
        attributeDefinitions.add(userAttribute);

        AttributeDefinition rangeKeyAttribute = AttributeDefinition.builder()
                .attributeName(RANGE_KEY)
                .attributeType(ScalarAttributeType.S)
                .build();
        attributeDefinitions.add(rangeKeyAttribute);

        AttributeDefinition amountAttribute = AttributeDefinition.builder()
                .attributeName(AMOUNT_LSI_ATTRIBUTE)
                .attributeType(ScalarAttributeType.N)
                .build();
        attributeDefinitions.add(amountAttribute);

        return attributeDefinitions;
    }

    /**
     * @return sample Transaction
     */
    public Transaction createTransaction() {
        Transaction transaction = ImmutableTransaction.builder()
                .transactionId(TRANSACTION_ID)
                .institutionName(INSTITUTION)
                .amount(AMOUNT)
                .date(DATE)
                .accountId(ACCOUNT_ID)
                .description(DESCRIPTION)
                .merchantName(MERCHANT_NAME)
                .accountId(ACCOUNT_ID)
                .user(USER)
                .originalDescription(ORIGINAL_DESCRIPTION)
                .build();

        return transaction;
    }

    /**
     * Creates sample transactions differing only by transactionId.
     * @param i number of transactions
     * @return List of Transactions
     */
    public List<Transaction> createTransactions(int i) {
        List<Transaction> transactions = new ArrayList<>();

        for (int j = 0; j <= i; j++) {
            Transaction baseTransaction = createTransaction();
            Transaction transaction = ImmutableTransaction.builder()
                    .from(baseTransaction)
                    .transactionId(baseTransaction.getTransactionId() + String.valueOf(j))
                    .build();
            transactions.add(transaction);
        }

        return transactions;
    }

    /**
     * @param transactionDAO DAO used to interact with DDB.
     * @param transaction transaction to persist.
     */
    public void saveTransaction(TransactionDAO transactionDAO,
                                       Transaction transaction) {
        transactionDAO.save(transaction);
    }

    /**
     * @param transactionDAO DAO used to interact with DDB.
     * @param transactions {@link Collection} of transactions to persist.
     */
    public void saveTransaction(TransactionDAO transactionDAO,
                                        Collection<Transaction> transactions) {
        transactions.stream()
                .forEach(tx -> saveTransaction(transactionDAO, tx));
    }

}
