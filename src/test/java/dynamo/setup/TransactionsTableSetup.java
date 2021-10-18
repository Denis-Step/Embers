package dynamo.setup;

import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
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


    public static void createTable(DynamoDbClient ddbClient) {
        CreateTableRequest createTableRequest = CreateTableRequest.builder()
                .tableName(TRANSACTION_TABLE_NAME)
                .attributeDefinitions(getAttributeDefinitions())
                .globalSecondaryIndexes(getGlobalSecondaryIndices())
                .keySchema(getKeySchemaElements())
                .provisionedThroughput( ProvisionedThroughput.builder()
                        .readCapacityUnits(5L)
                        .writeCapacityUnits(5L)
                        .build())
                .build();

        ddbClient.createTable(createTableRequest);
    }

    public static List<KeySchemaElement> getKeySchemaElements() {
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

    public static List<GlobalSecondaryIndex> getGlobalSecondaryIndices() {
        List<GlobalSecondaryIndex> globalSecondaryIndices = new ArrayList<>();
        GlobalSecondaryIndex institutionGsi = GlobalSecondaryIndex.builder()
                .indexName(INSTITUTION_GSI_ATTRIBUTE + "Index")
                .keySchema(KeySchemaElement.builder()
                        .keyType(KeyType.HASH)
                        .attributeName(INSTITUTION_GSI_ATTRIBUTE)
                        .build())
                .provisionedThroughput( ProvisionedThroughput.builder()
                        .readCapacityUnits(5L)
                        .writeCapacityUnits(5L)
                        .build())
                .projection( Projection.builder()
                        .projectionType("ALL")
                        .build() )
                .build();
        globalSecondaryIndices.add(institutionGsi);
        return globalSecondaryIndices;
    }

    public static List<AttributeDefinition> getAttributeDefinitions() {
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

         /*AttributeDefinition accountAttribute = AttributeDefinition.builder()
                 .attributeName(ACCOUNT_LSI_ATTRIBUTE)
                 .attributeType(ScalarAttributeType.S)
                 .build();
         attributeDefinitions.add(accountAttribute);

         AttributeDefinition descriptionAttribute = AttributeDefinition.builder()
                 .attributeName(DESCRIPTION_LSI_ATTRIBUTE)
                 .attributeType(ScalarAttributeType.S)
                 .build();
         attributeDefinitions.add(descriptionAttribute);

         AttributeDefinition originalDescriptionAttribute = AttributeDefinition.builder()
                 .attributeName(ORIGINAL_DESCRIPTION_ATTRIBUTE)
                 .attributeType(ScalarAttributeType.S)
                 .build();
         attributeDefinitions.add(originalDescriptionAttribute);

         AttributeDefinition merchantNameAttribute = AttributeDefinition.builder()
                 .attributeName(MERCHANT_NAME_LSI_ATTRIBUTE)
                 .attributeType(ScalarAttributeType.S)
                 .build();
         attributeDefinitions.add(merchantNameAttribute);

         AttributeDefinition itemIdAttribute = AttributeDefinition.builder()
                 .attributeName(ITEM_ID_ATTRIBUTE)
                 .attributeType(ScalarAttributeType.S)
                 .build();
         attributeDefinitions.add(itemIdAttribute); */

        return attributeDefinitions;
    }

    /**
     * @return returns default DDB client for Local DDB.
     */
    public static DynamoDbClient getDefaultClient() {
        DynamoDbClient dynamoDbClient = DynamoDbClient.builder()
                .credentialsProvider(DefaultCredentialsProvider.create())
                .build();

        try {
            return DynamoDbClient.builder()
                    .credentialsProvider(DefaultCredentialsProvider.create())
                    .endpointOverride(new URI("http://localhost:8000"))
                    .build();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e.getCause());
        }
    }
}
