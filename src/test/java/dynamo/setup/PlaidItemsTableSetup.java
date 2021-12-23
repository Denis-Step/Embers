package dynamo.setup;

import dagger.DaggerAwsComponent;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;

import java.util.ArrayList;
import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class PlaidItemsTableSetup {
    public static final String PLAID_ITEMS_TABLE_NAME = "PlaidItems";
    public static final String HASH_KEY_USER = "user";
    public static final String RANGE_KEY = "institutionIdAccessToken";

    private static final DynamoDbClient dynamoDbClient = DaggerAwsComponent.create().buildDynamoDbClient();

    /**
     * Clean up existing table and create new one.
     */
    public static void setPlaidItemsTableName() {

        try {
            deletePlaidItemsTable();
        } catch (DynamoDbException e) {
            // no-op.
        }

        CreateTableRequest createTableRequest = CreateTableRequest.builder()
                .tableName(PLAID_ITEMS_TABLE_NAME)
                .attributeDefinitions(getAttributeDefinitions())
                .keySchema(getKeySchemaElements())
                .provisionedThroughput( ProvisionedThroughput.builder()
                        .readCapacityUnits(5L)
                        .writeCapacityUnits(5L)
                        .build())
                .build();

        dynamoDbClient.createTable(createTableRequest);
    }

    public static void deletePlaidItemsTable() {
        DeleteTableRequest deleteTableRequest = DeleteTableRequest.builder()
                .tableName(PLAID_ITEMS_TABLE_NAME)
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


        return attributeDefinitions;
    }
}