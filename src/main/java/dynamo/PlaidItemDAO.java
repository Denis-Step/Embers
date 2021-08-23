package dynamo;

import com.amazonaws.services.dynamodbv2.datamodeling.*;
import com.amazonaws.services.dynamodbv2.xspec.S;

import java.util.List;
import java.util.Map;

@DynamoDBTable(tableName="PlaidItems")
public class PlaidItemDAO {
    // Primary Key:
    private String user;
    private String institutionId;
    private String accessToken;
    private String ID;
    private List<String> availableProducts;
    private List<String> accounts;
    private String dateCreated;
    private String metaData; // Remaining metadata. Rarely used.


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
}
