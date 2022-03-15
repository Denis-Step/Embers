package dynamo.setup;

import external.plaid.entities.ImmutablePlaidItem;
import external.plaid.entities.PlaidItem;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;

import java.util.ArrayList;
import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class PlaidItemsTableUtils {
    public static final String PLAID_ITEMS_TABLE_NAME = "PlaidItems";
    public static final String HASH_KEY_USER = "user";
    public static final String RANGE_KEY = "institutionIdAccessToken";

    public static String USER = "USER";
    public static String INSTITUTION = "INSTITUTION";
    public static String ACCESS_TOKEN = "ACCESSTOKEN1234";
    public static String ID = "0000";
    public static List<String> AVAILABLE_PRODUCTS;
    public static List<String> ACCOUNTS;
    public static String DATE_CREATED = "2020-12-01";
    public static String METADATA = "METADATA";
    public static String RECEIVER_NUMBER = "1-212-555-1234";
    public static boolean WEBHOOK = false;

    private final DynamoDbClient dynamoDbClient;

    public PlaidItemsTableUtils(DynamoDbClient dynamoDbClient) {
        this.dynamoDbClient = dynamoDbClient;
    }

    /**
     * Clean up existing table and create new one.
     */
    public void setupPlaidItemsTable() {

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

    public void deletePlaidItemsTable() {
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


        return attributeDefinitions;
    }

    public static PlaidItem createItem() {
        AVAILABLE_PRODUCTS = new ArrayList<>();
        AVAILABLE_PRODUCTS.add("transactions");
        ACCOUNTS = new ArrayList<>();
        ACCOUNTS.add("ACCOUNT");

        return ImmutablePlaidItem.builder()
                .metadata(METADATA)
                .accessToken(ACCESS_TOKEN)
                .accounts(ACCOUNTS)
                .dateCreated(DATE_CREATED)
                .id(ID)
                .institutionId(INSTITUTION)
                .user(USER)
                .receiverNumber(RECEIVER_NUMBER)
                .webhook(WEBHOOK)
                .build();
    }

    public static List<PlaidItem> createItems() {
        List<PlaidItem> items = new ArrayList<>();
        PlaidItem item = createItem();

        for (int i = 0; i < 25; i++) {
            ImmutablePlaidItem newItem = ImmutablePlaidItem.copyOf(item)
                    .withAccessToken(item.getAccessToken() + String.valueOf(i))
                    .withId( item.getId() + String.valueOf(i) );
            items.add(newItem);
        }
        return items;
    }
}