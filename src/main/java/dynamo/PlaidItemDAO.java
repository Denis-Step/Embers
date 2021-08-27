package dynamo;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.*;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.QueryRequest;
import com.amazonaws.services.dynamodbv2.model.QueryResult;
import dagger.DaggerAwsComponent;
import java.util.List;
import java.util.stream.Collectors;


@DynamoDBTable(tableName="PlaidItems")
public class PlaidItemDAO {
    public static final String TABLE_NAME = "PlaidItems";
    // Primary Key:
    private String user;
    private String institutionId;
    private String accessToken;
    private String ID;
    private List<String> availableProducts;
    private List<String> accounts;
    private String dateCreated;
    private String metaData; // Remaining metadata. Rarely used.

    private static final AmazonDynamoDB dynamoDB = DaggerAwsComponent.create().buildDynamoClient();


    @DynamoDBHashKey(attributeName = "User")
    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    @DynamoDBRangeKey(attributeName = "InstitutionID")
    public String getInstitutionId() {
        return institutionId;
    }

    public void setInstitutionId(String institutionId) {
        this.institutionId = institutionId;
    }

    @DynamoDBAttribute(attributeName = "AccessToken")
    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    @DynamoDBAttribute(attributeName = "ID")
    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    @DynamoDBAttribute(attributeName = "AvailableProducts")
    public List<String> getAvailableProducts() {
        return availableProducts;
    }

    public void setAvailableProducts(List<String> availableProducts) {
        this.availableProducts = availableProducts;
    }

    @DynamoDBAttribute(attributeName = "DateCreated")
    public String getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(String dateCreated) {
        this.dateCreated = dateCreated;
    }

    @DynamoDBAttribute(attributeName = "Metadata")
    public String getMetaData() {
        return metaData;
    }

    public void setMetaData(String metaData) {
        this.metaData = metaData;
    }

    @DynamoDBAttribute(attributeName = "Accounts")
    public List<String> getAccounts() {
        return accounts;
    }

    public void setAccounts(List<String> accounts) {
        this.accounts = accounts;
    }

    public static class ItemNotFoundException extends Exception {
        public ItemNotFoundException(String errorMessage) {
            super(errorMessage);
        }
    }

    public static List<String> queryAccessTokens(String user, String institution) {
        QueryRequest queryRequest = createQueryRequest(user, institution);
        QueryResult result = dynamoDB.query(queryRequest);

        return result.getItems().stream()
                .map( item -> item.get("AccessToken").getS())
                .collect(Collectors.toList());
    }

    public static List<String> queryAccessTokens(String user) {
        QueryRequest queryRequest = createQueryRequest(user);
        QueryResult result = dynamoDB.query(queryRequest);

        return result.getItems().stream()
                .map( item -> item.get("AccessToken").getS())
                .collect(Collectors.toList());

    }

    private static QueryRequest createQueryRequest(String user, String institution) {
        QueryRequest queryRequest = new QueryRequest(TABLE_NAME);

        // Query Expression
        queryRequest.setKeyConditionExpression(String.format("#U = :name AND begins_with ( InstitutionID, :sortkeyval )"));

        // Add partition key. User is a reserved word, so must be aliased to #U.
        queryRequest.addExpressionAttributeNamesEntry("#U", "User");
        queryRequest.addExpressionAttributeValuesEntry(":name", new AttributeValue(user));

        // Add sort key.
        queryRequest.addExpressionAttributeValuesEntry(":sortkeyval", new AttributeValue(institution));

        return queryRequest;
    }

    // No sort key, no institution name provided needs diff query.
    private static QueryRequest createQueryRequest(String user) {
        QueryRequest queryRequest = new QueryRequest(TABLE_NAME);

        // Query Expression
        queryRequest.setKeyConditionExpression(String.format("#U = :name"));

        // Add partition key. User is a reserved word, so must be aliased to #U.
        queryRequest.addExpressionAttributeNamesEntry("#U", "User");
        queryRequest.addExpressionAttributeValuesEntry(":name", new AttributeValue(user));

        return queryRequest;
    }
}
