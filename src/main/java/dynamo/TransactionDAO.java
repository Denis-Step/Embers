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
public class TransactionDAO {
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

    private static final DynamoDBMapper dynamoDBMapper = DaggerAwsComponent.create().buildDynamo();;

    public TransactionDAO() {}

    public TransactionDAO(Transaction transaction) {
        this.setUser(transaction.getUser()); // Partition key
        this.setInstitutionNameAccountIdTransactionId(transaction.getInstitutionName() +
                "-" +
                transaction.getAccountId() +
                "-" +
                transaction.getTransactionId()); // Sort key
        this.setAmount(transaction.getAmount());
        this.setDescription(transaction.getDescription());
        this.setOriginalDescription(transaction.getOriginalDescription());
        this.setMerchantName(transaction.getMerchantName());
        this.setDate(transaction.getDate());
    }

    public Transaction createTransaction() {
        TransactionDAO txInfo = this;
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
        DynamoDBQueryExpression<TransactionDAO> queryExpression = createQueryRequest(user);
        List<TransactionDAO> transactionDAOList = this.dynamoDBMapper.query(TransactionDAO.class, queryExpression);

        return transactionDAOList.stream()
                .map(TransactionDAO::createTransaction)
                .collect(Collectors.toList());
    }

    public List<Transaction> query(String user, String institutionName) {
        DynamoDBQueryExpression<TransactionDAO> queryExpression = createQueryRequest(user, institutionName);
        List<TransactionDAO> transactionDAOList = this.dynamoDBMapper.query(TransactionDAO.class, queryExpression);

        return transactionDAOList.stream()
                .map(TransactionDAO::createTransaction)
                .collect(Collectors.toList());
    }

    public List<Transaction> query(String user, String institutionName, String accountId) {
        String sortKey = institutionName + "-" + accountId;
        DynamoDBQueryExpression<TransactionDAO> queryExpression = createQueryRequest(user, sortKey);
        List<TransactionDAO> transactionDAOList = this.dynamoDBMapper.query(TransactionDAO.class, queryExpression);

        return transactionDAOList.stream()
                .map(TransactionDAO::createTransaction)
                .collect(Collectors.toList());
    }

    public List<Transaction> query(String user, String institutionName, String accountId, String transactionId) {
        String sortKey = institutionName + "-" + accountId + "-" + transactionId;
        DynamoDBQueryExpression<TransactionDAO> queryExpression = createQueryRequest(user, sortKey);
        List<TransactionDAO> transactionDAOList = this.dynamoDBMapper.query(TransactionDAO.class, queryExpression);

        return transactionDAOList.stream()
                .map(TransactionDAO::createTransaction)
                .collect(Collectors.toList());
    }

    public void save(Transaction transaction) {
        TransactionDAO dao = new TransactionDAO(transaction);
        dao.save();
    }

    public void save(List<Transaction> transactionList) {
        for (Transaction transaction: transactionList) {
            TransactionDAO dao = new TransactionDAO(transaction);
            dao.save();
        }
    }

    private void save() { this.dynamoDBMapper.save(this); }

    private DynamoDBQueryExpression<TransactionDAO> createQueryRequest(String user, String sortKey) {
        Map<String, AttributeValue> eav = new HashMap<>();
        eav.put(":name", new AttributeValue().withS(user));
        eav.put(":institutionAccount",new AttributeValue().withS(sortKey));

        return new DynamoDBQueryExpression<TransactionDAO>()
                .withKeyConditionExpression("#U = :name AND begins_with ( InstitutionNameAccountId, :institutionAccount )")
                .addExpressionAttributeNamesEntry("#U", "User")
                .withExpressionAttributeValues(eav);
    }

    private DynamoDBQueryExpression<TransactionDAO> createQueryRequest(String user) {
        Map<String, AttributeValue> eav = new HashMap<>();
        eav.put(":name", new AttributeValue().withS(user));

        return new DynamoDBQueryExpression<TransactionDAO>()
                .withKeyConditionExpression("#U = :name")
                .addExpressionAttributeNamesEntry("#U", "User")
                .withExpressionAttributeValues(eav);
    }

    @Override
    public String toString() {
        return "TransactionDAO{" +
                "user='" + user + '\'' +
                ", institutionNameAccountIdTransactionId='" + institutionNameAccountIdTransactionId + '\'' +
                ", amount=" + amount +
                ", description='" + description + '\'' +
                ", originalDescription='" + originalDescription + '\'' +
                ", merchantName='" + merchantName + '\'' +
                ", date='" + date + '\'' +
                ", dynamoDBMapper=" + dynamoDBMapper +
                '}';
    }
}