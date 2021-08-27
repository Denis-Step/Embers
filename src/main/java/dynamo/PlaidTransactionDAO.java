package dynamo;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.*;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.QueryRequest;
import com.amazonaws.services.dynamodbv2.model.QueryResult;
import dagger.DaggerAwsComponent;
import plaid.entities.Transaction;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@DynamoDBTable(tableName="Transactions")
public class PlaidTransactionDAO {
    public static final String TABLE_NAME = "Transactions";
    private static final AmazonDynamoDB dynamoDB = DaggerAwsComponent.create().buildDynamoClient();

    private String user;
    private String institutionNameAccountId;
    private Double amount;

    // This maps to Plaid "name"
    private String description;
    private String originalDescription;
    private String merchantName;
    private String date;
    private String transactionId;

    @DynamoDBHashKey(attributeName = "User")
    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    @DynamoDBAttribute(attributeName = "TransactionId")
    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    @DynamoDBAttribute(attributeName = "Amount")
    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    @DynamoDBAttribute(attributeName = "InstitutionNameAccountId")
    public String getInstitutionNameAccountId() {
        return institutionNameAccountId;
    }

    public void setInstitutionNameAccountId(String institutionNameAccountId) {
        this.institutionNameAccountId = institutionNameAccountId;
    }

    @DynamoDBAttribute(attributeName = "Description")
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @DynamoDBAttribute(attributeName = "OriginalDescription")
    public String getOriginalDescription() {
        return originalDescription;
    }

    public void setOriginalDescription(String originalDescription) {
        this.originalDescription = originalDescription;
    }

    @DynamoDBAttribute(attributeName = "MerchantName")
    public String getMerchantName() {
        return merchantName;
    }

    public void setMerchantName(String merchantName) {
        this.merchantName = merchantName;
    }

    @DynamoDBAttribute(attributeName = "Date")
    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public static Transaction deserialize(Map<String, AttributeValue> txMap) {
        return Transaction.getBuilder()
                .setAmount(txMap.get("amount").getB().getDouble())
                .setDescription(txMap.get("description").getS())
                .setOriginalDescription(txMap.get("originalDescription").getS())
                .setMerchantName(txMap.get("merchantName").getS())
                .setDate(txMap.get("date").getS())
                .setAccountId(txMap.get("accountId").getS())
                .setTransactionId(txMap.get("transactionId").getS())
                .build();
    }

    public static List<Transaction> queryTransactions(String user) {
        QueryRequest queryRequest = createQueryRequest(user);
        QueryResult result = dynamoDB.query(queryRequest);

        return result.getItems().stream()
                .map(txMap -> deserialize(txMap))
                .collect(Collectors.toList());
    }

    private static QueryRequest createQueryRequest(String user, String institutionName, String accountId) {
        String sortKey = institutionName + "-" + accountId;
        // Query Expression
        QueryRequest queryRequest = new QueryRequest(TABLE_NAME);
        queryRequest.setKeyConditionExpression(String.format("#U = :name AND begins_with ( InstitutionNameAccountId, :sortkeyval )"));

        // Add partition key. User is a reserved word, so must be aliased to #U.
        queryRequest.addExpressionAttributeNamesEntry("#U", "User");
        queryRequest.addExpressionAttributeValuesEntry(":name", new AttributeValue(user));

        // Add sort key.
        queryRequest.addExpressionAttributeValuesEntry(":sortkeyval", new AttributeValue(sortKey));

        return queryRequest;
    }

    private static QueryRequest createQueryRequest(String user, String institutionName) {
        return createQueryRequest(user, institutionName, "");
    }


    // No sort key, no institution name provided needs diff query.
    private static QueryRequest createQueryRequest(String user) {
        // Query Expression
        QueryRequest queryRequest = new QueryRequest(TABLE_NAME);
        queryRequest.setKeyConditionExpression(String.format("#U = :name"));

        // Add partition key. User is a reserved word, so must be aliased to #U.
        queryRequest.addExpressionAttributeNamesEntry("#U", "User");
        queryRequest.addExpressionAttributeValuesEntry(":name", new AttributeValue(user));

        return queryRequest;
    }
}
