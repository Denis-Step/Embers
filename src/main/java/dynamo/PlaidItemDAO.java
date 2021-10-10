package dynamo;

import com.amazonaws.services.dynamodbv2.datamodeling.*;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import dagger.DaggerAwsComponent;
import plaid.entities.ImmutablePlaidItem;
import plaid.entities.PlaidItem;

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
    private String receiverNumber;
    private boolean webHook;

    private static final DynamoDBMapper dynamoDBMapper = DaggerAwsComponent.create().buildDynamo();

    public PlaidItemDAO() { }

    public PlaidItemDAO(PlaidItem plaidItem) {
        this.setUser(plaidItem.user()); // Set partition key.
        this.setInstitutionId(plaidItem.institutionId()); // Set sort key.
        this.setID(plaidItem.ID());
        this.setAccessToken(plaidItem.accessToken());
        this.setAvailableProducts(plaidItem.availableProducts());
        this.setAccounts(plaidItem.accounts());
        this.setDateCreated(plaidItem.dateCreated());
        this.setMetaData(plaidItem.metaData());
        this.setWebHook(plaidItem.webhook());
        if (plaidItem.receiverNumber().isPresent()) {
            this.setReceiverNumber(plaidItem.receiverNumber().get());
        };
    }

    public PlaidItem createItem() {
        PlaidItemDAO itemInfo = this;

        return ImmutablePlaidItem.builder()
                .ID(itemInfo.getID())
                .accessToken(itemInfo.getAccessToken())
                .user(itemInfo.getUser())
                .dateCreated(itemInfo.getDateCreated())
                .availableProducts(itemInfo.getAvailableProducts())
                .accounts(itemInfo.getAccounts())
                .institutionId(itemInfo.getInstitutionId())
                .metaData(itemInfo.getMetaData())
                .webhook(itemInfo.getWebHook())
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

    @DynamoDBAttribute(attributeName = "ReceiverNumber")
    public String getReceiverNumber() { return receiverNumber; }
    public void setReceiverNumber(String receiverNumber) { this.receiverNumber = receiverNumber; }

    @DynamoDBAttribute(attributeName = "Webhook")
    public Boolean getWebHook() { return webHook; }
    public void setWebHook(Boolean webHook) { this.webHook = webHook; }

    public PlaidItem getItem(String user, String institution) throws ItemException {
        List<PlaidItem> plaidItems = this.query(user, institution);

        if (plaidItems.size() > 1) {
            throw new MultipleItemsFoundException("Found " +
                    plaidItems.size() +
                    " items:" +
                    plaidItems.toString());
        }
        if (plaidItems.size() < 1) {
            throw new ItemNotFoundException("Item Not Found for User:" +
                    user +
                    "And institution:" +
                    institution);
        } else {
            return plaidItems.get(0);
        }
    }

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

    // Exceptions
    public static class ItemException extends Exception {
        public ItemException(String errorMessage) {super(errorMessage);}
    }

    public static class ItemNotFoundException extends ItemException {
        public ItemNotFoundException(String errorMessage) {
            super(errorMessage);
        }
    }

    public static class MultipleItemsFoundException extends ItemException {
        public MultipleItemsFoundException(String errorMessage) {super(errorMessage);}
    }
}
