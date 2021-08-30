package dynamo;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.*;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import dagger.DaggerAwsComponent;
import plaid.entities.Transaction;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@DynamoDBTable(tableName="Transactions")
public class PlaidTransactionDAO {
    public static final String TABLE_NAME = "Transactions";
    private static final AmazonDynamoDB dynamoDB = DaggerAwsComponent.create().buildDynamoClient();

    private String user;
    private String institutionNameAccountIdTransactionId;
    private Double amount;

    // This maps to Plaid "name"
    private String description;
    private String originalDescription;
    private String merchantName;
    private String date;

    private String transactionId;

    private final DynamoDBMapper dynamoDBMapper;

    public PlaidTransactionDAO() { dynamoDBMapper = DaggerAwsComponent.create().buildDynamo(); }

    public PlaidTransactionDAO(Transaction transaction) {
        this();
        this.setUser(transaction.getUser()); // Partition key
        this.setInstitutionNameAccountIdTransactionId(transaction.getInstitutionName() +
                "-" +
                transaction.getAccountId() +
                "-" +
                transaction.getTransactionId()); // Sort key
        this.setAmount(transaction.getAmount());
        this.setDescription(transaction.getDescription());
        this.setOriginalDescription(transaction.getOriginalDescription().orElse("No Original Description"));
        this.setMerchantName(transaction.getMerchantName());
        this.setDate(transaction.getDate());
    }

    public Transaction createTransaction() {
        PlaidTransactionDAO txInfo = this;
        String institutionName = this.institutionNameAccountIdTransactionId.split("-")[0];
        String accountId = this.institutionNameAccountIdTransactionId.split("-")[1];
        String transactionId = this.institutionNameAccountIdTransactionId.split("-")[2];

        return Transaction.getBuilder()
                .setUser(this.user)
                .setInstitutionName(institutionName)
                .setAccountId(accountId)
                .setAmount(this.amount)
                .setDescription(this.description)
                .setOriginalDescription(this.originalDescription)
                .setMerchantName(this.merchantName)
                .setDate(this.date)
                .setTransactionId(transactionId)
                .build();
    }

    @DynamoDBHashKey(attributeName = "User")
    public String getUser() { return user; }
    public void setUser(String user) { this.user = user; }

    @DynamoDBRangeKey(attributeName = "InstitutionNameAccountId")
    public String getInstitutionNameAccountIdTransactionId() { return institutionNameAccountIdTransactionId; }
    public void setInstitutionNameAccountIdTransactionId(String institutionNameAccountIdTransactionId) { this.institutionNameAccountIdTransactionId = institutionNameAccountIdTransactionId; }

    /*@DynamoDBAttribute(attributeName = "TransactionId")
    public String getTransactionId() { return transactionId; }
    public void setTransactionId(String transactionId) { this.transactionId = transactionId; } */

    @DynamoDBAttribute(attributeName = "Amount")
    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }

    @DynamoDBAttribute(attributeName = "Description")
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    @DynamoDBAttribute(attributeName = "OriginalDescription")
    public String getOriginalDescription() { return originalDescription; }
    public void setOriginalDescription(String originalDescription) { this.originalDescription = originalDescription; }

    @DynamoDBAttribute(attributeName = "MerchantName")
    public String getMerchantName() { return merchantName; }
    public void setMerchantName(String merchantName) { this.merchantName = merchantName; }

    @DynamoDBAttribute(attributeName = "Date")
    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public List<Transaction> query(String user) {
        DynamoDBQueryExpression<PlaidTransactionDAO> queryExpression = createQueryRequest(user);
        List<PlaidTransactionDAO> plaidTransactionDAOList = this.dynamoDBMapper.query(PlaidTransactionDAO.class, queryExpression);

        return plaidTransactionDAOList.stream()
                .map(PlaidTransactionDAO::createTransaction)
                .collect(Collectors.toList());
    }

    public List<Transaction> query(String user, String institutionName) {
        DynamoDBQueryExpression<PlaidTransactionDAO> queryExpression = createQueryRequest(user, institutionName);
        List<PlaidTransactionDAO> plaidTransactionDAOList = this.dynamoDBMapper.query(PlaidTransactionDAO.class, queryExpression);

        return plaidTransactionDAOList.stream()
                .map(PlaidTransactionDAO::createTransaction)
                .collect(Collectors.toList());
    }

    public List<Transaction> query(String user, String institutionName, String accountId) {
        String sortKey = institutionName + "-" + accountId;
        DynamoDBQueryExpression<PlaidTransactionDAO> queryExpression = createQueryRequest(user, sortKey);
        List<PlaidTransactionDAO> plaidTransactionDAOList = this.dynamoDBMapper.query(PlaidTransactionDAO.class, queryExpression);

        return plaidTransactionDAOList.stream()
                .map(PlaidTransactionDAO::createTransaction)
                .collect(Collectors.toList());
    }

    public void save(Transaction transaction) {
        PlaidTransactionDAO dao = new PlaidTransactionDAO(transaction);
        dao.save();
    }

    public void save(List<Transaction> transactionList) {
        for (Transaction transaction: transactionList) {
            PlaidTransactionDAO dao = new PlaidTransactionDAO(transaction);
            dao.save();
        }
    }

    private void save() { this.dynamoDBMapper.save(this); }

    private DynamoDBQueryExpression<PlaidTransactionDAO> createQueryRequest(String user, String sortKey) {
        Map<String, AttributeValue> eav = new HashMap<>();
        eav.put(":name", new AttributeValue().withS(user));
        eav.put(":institutionAccount",new AttributeValue().withS(sortKey));

        return new DynamoDBQueryExpression<PlaidTransactionDAO>()
                .withKeyConditionExpression("#U = :name AND begins_with ( InstitutionNameAccountId, :institutionAccount )")
                .addExpressionAttributeNamesEntry("#U", "User")
                .withExpressionAttributeValues(eav);
    }

    private DynamoDBQueryExpression<PlaidTransactionDAO> createQueryRequest(String user) {
        Map<String, AttributeValue> eav = new HashMap<>();
        eav.put(":name", new AttributeValue().withS(user));

        return new DynamoDBQueryExpression<PlaidTransactionDAO>()
                .withKeyConditionExpression("#U = :name")
                .addExpressionAttributeNamesEntry("#U", "User")
                .withExpressionAttributeValues(eav);
    }

    @Override
    public String toString() {
        return "PlaidTransactionDAO{" +
                "user='" + user + '\'' +
                ", institutionNameAccountId='" + institutionNameAccountIdTransactionId + '\'' +
                ", amount=" + amount +
                ", description='" + description + '\'' +
                ", originalDescription='" + originalDescription + '\'' +
                ", merchantName='" + merchantName + '\'' +
                ", date='" + date + '\'' +
                ", transactionId='" + transactionId + '\'' +
                ", dynamoDBMapper=" + dynamoDBMapper +
                '}';
    }
}
