package dynamo.setup;

import dynamo.TransactionDAO;
import external.plaid.entities.ImmutableTransaction;
import external.plaid.entities.Transaction;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * DynamoDb SDK v2 createTable() doesn't create LSI's from schemas properly, so this class is necessary
 * To create all indices.
 */
@RunWith(MockitoJUnitRunner.class)
public class TransactionsTableUtils {
    public static final String TRANSACTION_TABLE_NAME = "Transactions";
    public static final String HASH_KEY_USER = "user";
    public static final String RANGE_KEY = "dateTransactionId";
    public static final String INSTITUTION_LSI_ATTRIBUTE = "institutionName";
    public static final String AMOUNT_LSI_ATTRIBUTE = "amount";
    public static final String ACCOUNT_GSI_ATTRIBUTE = "accountId";
    public static final String DESCRIPTION_LSI_ATTRIBUTE = "description";
    public static final String TRANSACTION_ID_GSI_ATTRIBUTE = "transactionId";

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
                .globalSecondaryIndexes(getGlobalSecondaryIndices())
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

    private List<GlobalSecondaryIndex> getGlobalSecondaryIndices() {
        List<GlobalSecondaryIndex> globalSecondaryIndices = new ArrayList<>();

        KeySchemaElement accountPartitionKey = KeySchemaElement.builder()
                .keyType(KeyType.HASH)
                .attributeName(ACCOUNT_GSI_ATTRIBUTE)
                .build();

        KeySchemaElement transactionIdPartitionKey = KeySchemaElement.builder()
                .keyType(KeyType.HASH)
                .attributeName(TRANSACTION_ID_GSI_ATTRIBUTE)
                .build();

        KeySchemaElement dateRangeKey = KeySchemaElement.builder()
                .keyType(KeyType.RANGE)
                .attributeName(RANGE_KEY)
                .build();

        GlobalSecondaryIndex accountGsi = GlobalSecondaryIndex.builder()
                .indexName(ACCOUNT_GSI_ATTRIBUTE + "Index")
                .keySchema(accountPartitionKey, dateRangeKey)
                .projection(Projection.builder()
                        .projectionType(ProjectionType.ALL)
                        .build())
                .provisionedThroughput(ProvisionedThroughput.builder()
                        .readCapacityUnits(5L)
                        .writeCapacityUnits(5L)
                        .build())
                .build();

        GlobalSecondaryIndex transactionIdGsi = GlobalSecondaryIndex.builder()
                .indexName(TRANSACTION_ID_GSI_ATTRIBUTE + "Index")
                .keySchema(transactionIdPartitionKey)
                .projection(Projection.builder()
                        .projectionType(ProjectionType.ALL)
                        .build())
                .provisionedThroughput(ProvisionedThroughput.builder()
                        .readCapacityUnits(5L)
                        .writeCapacityUnits(5L)
                        .build())
                .build();

        globalSecondaryIndices.add(accountGsi);
        globalSecondaryIndices.add(transactionIdGsi);
        return globalSecondaryIndices;
    }

    private List<LocalSecondaryIndex> getLocalSecondaryIndices() {
        List<LocalSecondaryIndex> localSecondaryIndices = new ArrayList<>();

        KeySchemaElement userPartitionKey = KeySchemaElement.builder()
                .keyType(KeyType.HASH)
                .attributeName(HASH_KEY_USER)
                .build();

        KeySchemaElement amountRangeKey = KeySchemaElement.builder()
                .keyType(KeyType.RANGE)
                .attributeName(AMOUNT_LSI_ATTRIBUTE)
                .build();

        KeySchemaElement instRangeKey = KeySchemaElement.builder()
                .keyType(KeyType.RANGE)
                .attributeName(INSTITUTION_LSI_ATTRIBUTE)
                .build();

        KeySchemaElement descriptionRangeKey = KeySchemaElement.builder()
                .keyType(KeyType.RANGE)
                .attributeName(DESCRIPTION_LSI_ATTRIBUTE)
                .build();

        LocalSecondaryIndex amountLsi = LocalSecondaryIndex.builder()
                .indexName(AMOUNT_LSI_ATTRIBUTE + "Index")
                .keySchema(userPartitionKey, amountRangeKey)
                .projection( Projection.builder()
                        .projectionType("ALL")
                        .build() )
                .build();

        LocalSecondaryIndex descriptionLsi = LocalSecondaryIndex.builder()
                .indexName(DESCRIPTION_LSI_ATTRIBUTE + "Index")
                .keySchema(userPartitionKey, descriptionRangeKey)
                .projection( Projection.builder()
                        .projectionType("ALL")
                        .build() )
                .build();

        LocalSecondaryIndex institutionLsi = LocalSecondaryIndex.builder()
                .indexName(INSTITUTION_LSI_ATTRIBUTE + "Index")
                .keySchema(userPartitionKey, instRangeKey)
                .projection( Projection.builder()
                        .projectionType("ALL")
                        .build() )
                .build();

        localSecondaryIndices.add(amountLsi);
        localSecondaryIndices.add(descriptionLsi);
        localSecondaryIndices.add(institutionLsi);
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

        AttributeDefinition institutionAttribute = AttributeDefinition.builder()
                .attributeName(INSTITUTION_LSI_ATTRIBUTE)
                .attributeType(ScalarAttributeType.S)
                .build();
        attributeDefinitions.add(institutionAttribute);

        AttributeDefinition accountAttribute = AttributeDefinition.builder()
                .attributeName(ACCOUNT_GSI_ATTRIBUTE)
                .attributeType(ScalarAttributeType.S)
                .build();
        attributeDefinitions.add(accountAttribute);

        AttributeDefinition descriptionAttribute = AttributeDefinition.builder()
                .attributeName(DESCRIPTION_LSI_ATTRIBUTE)
                .attributeType(ScalarAttributeType.S)
                .build();
        attributeDefinitions.add(descriptionAttribute);

        AttributeDefinition transactionIdAttribute = AttributeDefinition.builder()
                .attributeName(TRANSACTION_ID_GSI_ATTRIBUTE)
                .attributeType(ScalarAttributeType.S)
                .build();
        attributeDefinitions.add(transactionIdAttribute);

        return attributeDefinitions;
    }

    /**
     * @return sample Transaction
     */
    public static Transaction createTransaction() {
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
    public static List<Transaction> createTransactions(int i) {
        List<Transaction> transactions = new ArrayList<>();

        for (int j = 0; j < i; j++) {
            Transaction baseTransaction = createTransaction();
            Transaction transaction = ImmutableTransaction.builder()
                    .from(baseTransaction)
                    .transactionId(baseTransaction.getTransactionId() + String.valueOf(j))
                    .build();
            transactions.add(transaction);
        }

        return transactions;
    }


}
