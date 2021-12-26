package dynamo.setup;

import com.amazonaws.services.dynamodbv2.document.Table;
import dagger.DaggerAwsComponent;
import dynamo.TransactionDAO;
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
public class TransactionsTableSetup {
    public static final String TRANSACTION_TABLE_NAME = "Transactions";
    public static final String HASH_KEY_USER = "user";
    public static final String RANGE_KEY = "dateAmountTransactionId";
    public static final String INSTITUTION_GSI_ATTRIBUTE = "institutionName";
    public static final String ACCOUNT_LSI_ATTRIBUTE = "account";
    public static final String DESCRIPTION_LSI_ATTRIBUTE = "description";
    public static final String ORIGINAL_DESCRIPTION_ATTRIBUTE = "originalDescription";
    public static final String MERCHANT_NAME_LSI_ATTRIBUTE = "merchantName";
    public static final String ITEM_ID_ATTRIBUTE = "itemId";

    private static final DynamoDbClient dynamoDbClient = DaggerAwsComponent.create().buildDynamoDbClient();

    private static final String USER = "USER";
    private static final String INSTITUTION = "INSTITUTION";

    private static final Double AMOUNT = 49.99;
    private static final String DESCRIPTION = "SAMPLE_TRANSACTION";
    private static final String ORIGINAL_DESCRIPTION = "SAMPLE_DESCRIPTION";
    private static final String MERCHANT_NAME = "SAMPLE_MERCHANT";
    private static final String DATE = "2020-01-01";
    private static final String ACCOUNT_ID = "1233456789";
    private static final String TRANSACTION_ID = "TX-123456789";



    /**
     * Clean up existing table and create new one.
     */
    public static void setUpTransactionsTable() {

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

    public static void deleteTransactionsTable() {
        DeleteTableRequest deleteTableRequest = DeleteTableRequest.builder()
                .tableName(TRANSACTION_TABLE_NAME)
                .build();
        dynamoDbClient.deleteTable(deleteTableRequest);
    }

    private static List<KeySchemaElement> getKeySchemaElements() {
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

    private static List<LocalSecondaryIndex> getLocalSecondaryIndices() {
        List<KeySchemaElement> keySchemaElements = new ArrayList<>();
        KeySchemaElement partitionKey = KeySchemaElement.builder()
                .keyType(KeyType.HASH)
                .attributeName(HASH_KEY_USER)
                .build();
        keySchemaElements.add(partitionKey);

        KeySchemaElement rangeKey = KeySchemaElement.builder()
                .keyType(KeyType.RANGE)
                .attributeName(INSTITUTION_GSI_ATTRIBUTE)
                .build();
        keySchemaElements.add(rangeKey);

        List<LocalSecondaryIndex> localSecondaryIndices = new ArrayList<>();
        LocalSecondaryIndex institutionLsi = LocalSecondaryIndex.builder()
                .indexName(INSTITUTION_GSI_ATTRIBUTE + "Index")
                .keySchema(keySchemaElements)
                .projection( Projection.builder()
                        .projectionType("ALL")
                        .build() )
                .build();
        localSecondaryIndices.add(institutionLsi);
        return localSecondaryIndices;
    }

    private static List<AttributeDefinition> getAttributeDefinitions() {
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

        AttributeDefinition institutionAttribute = AttributeDefinition.builder()
                .attributeName(INSTITUTION_GSI_ATTRIBUTE)
                .attributeType(ScalarAttributeType.S)
                .build();
        attributeDefinitions.add(institutionAttribute);

        return attributeDefinitions;
    }

    /**
     * @return Set up by creating 25 transactions with diff ID's.
     * @implSpec Requires table to be instantiated already.
     */
    public static List<Transaction> createNewTransactions() {
        List<Transaction> transactions = new ArrayList<>();
        for (int i = 0; i < 25; i++) {

            Transaction transaction = new Transaction();
            transaction.setTransactionId(TRANSACTION_ID + String.valueOf(i));
            transaction.setInstitutionName(INSTITUTION);
            transaction.setAmount(AMOUNT);
            transaction.setDate(DATE);
            transaction.setAccountId(ACCOUNT_ID);
            transaction.setDescription(DESCRIPTION);
            transaction.setMerchantName(MERCHANT_NAME);
            transaction.setAccountId(ACCOUNT_ID);
            transaction.setUser(USER);
            transaction.setOriginalDescription(ORIGINAL_DESCRIPTION);
        }
        return transactions;
    }

    /**
     * @return sample Transaction
     */
    public static Transaction createTransaction() {
        Transaction transaction = new Transaction();
        transaction.setTransactionId(TRANSACTION_ID);
        transaction.setInstitutionName(INSTITUTION);
        transaction.setAmount(AMOUNT);
        transaction.setDate(DATE);
        transaction.setAccountId(ACCOUNT_ID);
        transaction.setDescription(DESCRIPTION);
        transaction.setMerchantName(MERCHANT_NAME);
        transaction.setAccountId(ACCOUNT_ID);
        transaction.setUser(USER);
        transaction.setOriginalDescription(ORIGINAL_DESCRIPTION);

        return transaction;
    }

    /**
     * @param transactionDAO DAO used to interact with DDB.
     * @param transaction transaction to persist.
     */
    public static void saveTransaction(TransactionDAO transactionDAO,
                                       Transaction transaction) {
        transactionDAO.save(transaction);
    }

    /**
     * @param transactionDAO DAO used to interact with DDB.
     * @param transactions {@link Collection} of transactions to persist.
     */
    public static void saveTransaction(TransactionDAO transactionDAO,
                                        Collection<Transaction> transactions) {
        transactions.stream()
                .forEach(tx -> saveTransaction(transactionDAO, tx));
    }

}
