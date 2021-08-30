package dynamo;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.*;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.QueryRequest;
import com.amazonaws.services.dynamodbv2.model.QueryResult;
import dagger.DaggerAwsComponent;
import plaid.entities.PlaidItem;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    private DynamoDBMapper dynamoDBMapper;

    public PlaidItemDAO() {
        dynamoDBMapper = DaggerAwsComponent.create().buildDynamo();
    }

    public PlaidItemDAO(PlaidItem plaidItem) {
        this();
        this.setUser(plaidItem.getUser()); // Set partition key.
        this.setInstitutionId(plaidItem.getInstitutionId()); // Set sort key.
        this.setID(plaidItem.getID());
        this.setAccessToken(plaidItem.getAccessToken());
        this.setAvailableProducts(plaidItem.getAvailableProducts());
        this.setAccounts(plaidItem.getAccounts());
        this.setDateCreated(plaidItem.getDateCreated());
        this.setMetaData(plaidItem.getMetaData());
    }

    public PlaidItem createItem() {
        PlaidItemDAO itemInfo = this;

        return PlaidItem.getBuilder()
                .setID(itemInfo.getID())
                .setAccessToken(itemInfo.getAccessToken())
                .setUser(itemInfo.getUser())
                .setDateCreated(itemInfo.getDateCreated())
                .setAvailableProducts(itemInfo.getAvailableProducts())
                .setAccounts(itemInfo.getAccounts())
                .setInstitutionId(itemInfo.getInstitutionId())
                .setMetaData(itemInfo.getMetaData())
                .build();
    }

    @DynamoDBHashKey(attributeName = "User")
    public String getUser() { return user; }
    public void setUser(String user) { this.user = user; }

    @DynamoDBRangeKey(attributeName = "InstitutionID")
    public String getInstitutionId() { return institutionId; }
    public void setInstitutionId(String institutionId) { this.institutionId = institutionId; }

    @DynamoDBAttribute(attributeName = "AccessToken")
    public String getAccessToken() { return accessToken; }
    public void setAccessToken(String accessToken) { this.accessToken = accessToken; }

    @DynamoDBAttribute(attributeName = "ID")
    public String getID() { return ID; }
    public void setID(String ID) { this.ID = ID; }

    @DynamoDBAttribute(attributeName = "AvailableProducts")
    public List<String> getAvailableProducts() { return availableProducts; }
    public void setAvailableProducts(List<String> availableProducts) { this.availableProducts = availableProducts; }

    @DynamoDBAttribute(attributeName = "DateCreated")
    public String getDateCreated() { return dateCreated; }
    public void setDateCreated(String dateCreated) { this.dateCreated = dateCreated; }

    @DynamoDBAttribute(attributeName = "Metadata")
    public String getMetaData() { return metaData; }
    public void setMetaData(String metaData) { this.metaData = metaData; }

    @DynamoDBAttribute(attributeName = "Accounts")
    public List<String> getAccounts() { return accounts; }
    public void setAccounts(List<String> accounts) { this.accounts = accounts; }

    public List<PlaidItem> query(String user, String institutionId) {
        DynamoDBQueryExpression<PlaidItemDAO> queryExpression = createQueryRequest(user, institutionId);
        List<PlaidItemDAO> plaidItemDAOList = dynamoDBMapper.query(PlaidItemDAO.class, queryExpression);

        return plaidItemDAOList.stream()
                .map(PlaidItemDAO::createItem)
                .collect(Collectors.toList());
    }

    public List<PlaidItem> query(String user) {
        DynamoDBQueryExpression<PlaidItemDAO> queryExpression = createQueryRequest(user);
        List<PlaidItemDAO> plaidItemDAOList = dynamoDBMapper.query(PlaidItemDAO.class, queryExpression);

        return plaidItemDAOList.stream()
                .map(PlaidItemDAO::createItem)
                .collect(Collectors.toList());
    }

    public void save(PlaidItem plaidItem){
        PlaidItemDAO dao = new PlaidItemDAO(plaidItem);
        dao.save();
    }

    private void save() {
        this.dynamoDBMapper.save(this);
    }

    private DynamoDBQueryExpression<PlaidItemDAO> createQueryRequest(String user, String institutionId) {
        Map<String, AttributeValue> eav = new HashMap<>();
        eav.put(":name", new AttributeValue().withS(user));
        eav.put(":institution",new AttributeValue().withS(institutionId));

        return new DynamoDBQueryExpression<PlaidItemDAO>()
                .withKeyConditionExpression("#U = :name AND begins_with ( InstitutionID, :institution )")
                .addExpressionAttributeNamesEntry("#U", "User")
                .withExpressionAttributeValues(eav);
    }

    private DynamoDBQueryExpression<PlaidItemDAO> createQueryRequest(String user) {
        Map<String, AttributeValue> eav = new HashMap<>();
        eav.put(":name", new AttributeValue().withS(user));

        return new DynamoDBQueryExpression<PlaidItemDAO>()
                .withKeyConditionExpression("#U = :name")
                .addExpressionAttributeNamesEntry("#U", "User")
                .withExpressionAttributeValues(eav);
    }
}
