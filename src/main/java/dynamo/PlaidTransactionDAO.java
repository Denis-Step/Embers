package dynamo;

import com.amazonaws.services.dynamodbv2.datamodeling.*;

@DynamoDBTable(tableName="Transactions")
public class PlaidTransactionDAO {
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
}
