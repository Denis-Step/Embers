package dynamo;

import dagger.DaggerAwsComponent;
import external.plaid.entities.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;

import javax.inject.Inject;

@DynamoDbBean
public class NewTransactionDAO {
    private final DynamoDbEnhancedClient client;
    private final DynamoDbTable<NewTransactionDAO> table;

    private String user; // PK
    private String dateAmountTransactionId; // SK

    private String institutionName;
    private String account;
    private String description;
    private String originalDescription;
    private String merchantName;
    private String itemId;

    private static final Logger LOGGER = LoggerFactory.getLogger(NewTransactionDAO.class);

    /**
     * Needs to be a bean, so it needs DI directly inside here.
     */
    public NewTransactionDAO() {
        this.client = DaggerAwsComponent.create().buildDynamoEnhancedClient();
        this.table = DaggerAwsComponent.create().buildNewTransactionsTable();
    }

    @Inject
    public NewTransactionDAO(DynamoDbEnhancedClient dynamoDbEnhancedClient,
                             DynamoDbTable<NewTransactionDAO> transactionDynamoDbTable) {
        this.client = dynamoDbEnhancedClient;
        this.table = transactionDynamoDbTable;
    }

    public static NewTransactionDAO fromTransaction(Transaction transaction) {
        NewTransactionDAO newTransactionDAO = DaggerAwsComponent.create().buildNewTransactionDao();
        newTransactionDAO.setUser(transaction.getUser()); // Partition key
        LOGGER.info(transaction.getUser());
        newTransactionDAO.setDateAmountTransactionId(transaction.getDate() +
                "#" +
                transaction.getAmount() +
                "#" +
                transaction.getTransactionId()); // Sort key
        newTransactionDAO.setInstitutionName(transaction.getInstitutionName());
        newTransactionDAO.setAccount(transaction.getAccountId());
        newTransactionDAO.setDescription(transaction.getDescription());
        newTransactionDAO.setOriginalDescription(transaction.getOriginalDescription());
        newTransactionDAO.setMerchantName(transaction.getMerchantName());
        newTransactionDAO.setItemId(transaction.getDate());

        return newTransactionDAO;
    }

    public static Transaction load(Transaction transaction) {
        NewTransactionDAO newTransactionDAO = NewTransactionDAO.fromTransaction(transaction);
        return newTransactionDAO.load();
    }

    @DynamoDbPartitionKey
    public String getUser() { return user; }
    public void setUser(String user) { this.user = user; }

    @DynamoDbSortKey
    public String getDateAmountTransactionId() { return dateAmountTransactionId; }
    public void setDateAmountTransactionId(String dateAmountTransactionId) {
        this.dateAmountTransactionId = dateAmountTransactionId;
    }

    public String getInstitutionName() { return institutionName; }
    public void setInstitutionName(String institutionName) { this.institutionName = institutionName; }

    public String getAccount() { return account; }
    public void setAccount(String account) { this.account = account; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getOriginalDescription() { return originalDescription; }
    public void setOriginalDescription(String originalDescription) {
        this.originalDescription = originalDescription;
    }

    public String getItemId() { return itemId; }
    public void setItemId(String itemId) { this.itemId = itemId; }

    public String getMerchantName() { return merchantName; }
    public void setMerchantName(String merchantName) { this.merchantName = merchantName; }

    public void save() {
        this.table.putItem(this);
    }

    private Transaction asTransaction() {
        LOGGER.info(this.dateAmountTransactionId);
        String date = this.dateAmountTransactionId.split("#")[0];
        double amount = Double.valueOf(this.dateAmountTransactionId.split("#")[1]);
        String transactionId = this.dateAmountTransactionId.split("#")[2];

        return Transaction.getBuilder()
                .setUser(this.user)
                .setInstitutionName(this.institutionName)
                .setAccountId(this.account)
                .setAmount(amount)
                .setDescription(this.description)
                .setOriginalDescription(this.originalDescription)
                .setMerchantName(this.merchantName)
                .setDate(date)
                .setTransactionId(transactionId)
                .build();
    }

    private Transaction load() {
       return table.getItem(this).asTransaction();
    }
}
