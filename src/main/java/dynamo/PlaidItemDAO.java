package dynamo;

import com.amazonaws.services.dynamodbv2.datamodeling.*;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import dagger.DaggerAwsComponent;
import external.plaid.entities.ImmutablePlaidItem;
import external.plaid.entities.PlaidItem;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;



@DynamoDBTable(tableName="PlaidItems")
public class PlaidItemDAO {
    public static final String TABLE_NAME = "PlaidItems";
    // Primary Key:
    private String user;
    private String institutionIdAccessToken;
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
        this.setUser(plaidItem.getUser()); // Set partition key.
        this.setInstitutionIdAccessToken(plaidItem.getInstitutionId() +
                "#" + plaidItem.getAccessToken()); // Set sort key.
        this.setID(plaidItem.getId());
        this.setAvailableProducts(plaidItem.getAvailableProducts());
        this.setAccounts(plaidItem.getAccounts());
        this.setDateCreated(plaidItem.getDateCreated());
        this.setMetaData(plaidItem.getMetadata());
        this.setWebHook(plaidItem.getWebhook());
        if (plaidItem.getReceiverNumber().isPresent()) {
            this.setReceiverNumber(plaidItem.getReceiverNumber().get());
        };
    }

    public PlaidItem createItem() {
        PlaidItemDAO itemInfo = this;
        String institutionID = institutionIdAccessToken.split("#")[0];
        String accessToken = institutionIdAccessToken.split("#")[1];

        ImmutablePlaidItem.Builder builder = ImmutablePlaidItem.builder()
                .ID(itemInfo.getID())
                .accessToken(accessToken)
                .user(itemInfo.getUser())
                .dateCreated(itemInfo.getDateCreated())
                .availableProducts(itemInfo.getAvailableProducts())
                .accounts(itemInfo.getAccounts())
                .institutionId(institutionID)
                .metaData(itemInfo.getMetaData())
                .webhook(itemInfo.getWebHook());

        if (this.receiverNumber != null) {
            return builder.build().withReceiverNumber(this.receiverNumber);
        } else {
            return builder.build();
        }
    }

    @DynamoDBHashKey(attributeName = "user")
    public String getUser() { return user; }
    public void setUser(String user) { this.user = user; }

    @DynamoDBRangeKey(attributeName = "institutionIdAccessToken")
    public String getInstitutionIdAccessToken() { return institutionIdAccessToken; }
    public void setInstitutionIdAccessToken(String institutionIdAccessToken) { this.institutionIdAccessToken = institutionIdAccessToken; }

    @DynamoDBAttribute(attributeName = "ID")
    public String getID() { return ID; }
    public void setID(String ID) { this.ID = ID; }

    @DynamoDBAttribute(attributeName = "availableProducts")
    public List<String> getAvailableProducts() { return availableProducts; }
    public void setAvailableProducts(List<String> availableProducts) { this.availableProducts = availableProducts; }

    @DynamoDBAttribute(attributeName = "dateCreated")
    public String getDateCreated() { return dateCreated; }
    public void setDateCreated(String dateCreated) { this.dateCreated = dateCreated; }

    @DynamoDBAttribute(attributeName = "metaData")
    public String getMetaData() { return metaData; }
    public void setMetaData(String metaData) { this.metaData = metaData; }

    @DynamoDBAttribute(attributeName = "accounts")
    public List<String> getAccounts() { return accounts; }
    public void setAccounts(List<String> accounts) { this.accounts = accounts; }

    @DynamoDBAttribute(attributeName = "receiverNumber")
    public String getReceiverNumber() { return receiverNumber; }
    public void setReceiverNumber(String receiverNumber) { this.receiverNumber = receiverNumber; }

    @DynamoDBAttribute(attributeName = "webHook")
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

    public void delete(String user, String institutionIdAccessToken) {
        Map<String, AttributeValue> eav = new HashMap<>();
        eav.put(":name", new AttributeValue().withS(user));
        eav.put(":institutionId", new AttributeValue().withS(institutionIdAccessToken));

        DynamoDBDeleteExpression dynamoDBDeleteExpression = new DynamoDBDeleteExpression()
                .withConditionExpression("#U = :name AND #Ins = :institutionId")
                .addExpressionAttributeNamesEntry("#U", "user")
                .addExpressionAttributeNamesEntry("#Ins", "institutionIdAccessToken")
                .withExpressionAttributeValues(eav);

        dynamoDBMapper.delete(PlaidItemDAO.class, dynamoDBDeleteExpression);
    }

    public void delete(PlaidItem plaidItem) {
        dynamoDBMapper.delete(new PlaidItemDAO(plaidItem));
    }

    private void save() {
        this.dynamoDBMapper.save(this);
    }

    private DynamoDBQueryExpression<PlaidItemDAO> createQueryRequest(String user, String institutionId) {
        Map<String, AttributeValue> eav = new HashMap<>();
        eav.put(":name", new AttributeValue().withS(user));
        eav.put(":institution",new AttributeValue().withS(institutionId));

        return new DynamoDBQueryExpression<PlaidItemDAO>()
                .withKeyConditionExpression("#U = :name AND begins_with ( institutionIdAccessToken, :institution )")
                .addExpressionAttributeNamesEntry("#U", "user")
                .withExpressionAttributeValues(eav);

    }

    private DynamoDBQueryExpression<PlaidItemDAO> createQueryRequest(String user) {
        Map<String, AttributeValue> eav = new HashMap<>();
        eav.put(":name", new AttributeValue().withS(user));

        return new DynamoDBQueryExpression<PlaidItemDAO>()
                .withKeyConditionExpression("#U = :name")
                .addExpressionAttributeNamesEntry("#U", "user")
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
