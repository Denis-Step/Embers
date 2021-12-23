package dynamo;

import dagger.DaggerAwsComponent;
import external.plaid.entities.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.core.pagination.sync.SdkIterable;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSecondarySortKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;
import software.amazon.awssdk.enhanced.dynamodb.model.Page;
import software.amazon.awssdk.enhanced.dynamodb.model.PageIterable;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@DynamoDbBean
public class TransactionDAO {
    private final DynamoDbEnhancedClient client;
    private final DynamoDbTable<TransactionDAO> table;

    private String user; // PK
    private String dateAmountTransactionId; // SK

    private String institutionName;
    private String account;
    private String description;
    private String originalDescription;
    private String merchantName;
    private String itemId;

    private static final Logger LOGGER = LoggerFactory.getLogger(TransactionDAO.class);
    private static final String INSTITUTION_LSI = "institutionNameIndex";

    /**
     * Needs to be a bean, so it needs DI directly inside here.
     */
    public TransactionDAO() {
        this.client = DaggerAwsComponent.create().buildDynamoEnhancedClient();
        this.table = DaggerAwsComponent.create().buildNewTransactionsTable();
    }

    @Inject
    public TransactionDAO(DynamoDbEnhancedClient dynamoDbEnhancedClient,
                          DynamoDbTable<TransactionDAO> transactionDynamoDbTable) {
        this.client = dynamoDbEnhancedClient;
        this.table = transactionDynamoDbTable;
    }

    public void delete(Transaction transaction) {
        TransactionDAO transactionDAO = new TransactionDAO(client, table)
                .withTransaction(transaction);
        transactionDAO.table.deleteItem( Key.builder()
                .partitionValue(transactionDAO.getUser())
                .sortValue(transactionDAO.dateAmountTransactionId)
                .build()
        );
    }

    public void delete(String user, String dateAmountTransactionId) {
        this.table.deleteItem( Key.builder()
                .partitionValue(user)
                .sortValue(dateAmountTransactionId)
                .build()
        );
    }

    public List<Transaction> query(String user) {
        QueryConditional queryConditional = QueryConditional.keyEqualTo(
                Key.builder()
                .partitionValue(user)
                .build()
        );

        QueryEnhancedRequest queryRequest = QueryEnhancedRequest.builder()
                .queryConditional(queryConditional)
                .build();

        PageIterable<TransactionDAO> pages = this.table.query(queryRequest);

        return pages.items().stream()
                .map(TransactionDAO::asTransaction)
                .collect(Collectors.toList());

    }

    public List<Transaction> queryByInstitution(String user, String institutionName) {
        QueryConditional queryConditional = QueryConditional.keyEqualTo(
                Key.builder()
                        .partitionValue(user)
                        .sortValue(institutionName)
                        .build()
        );

        QueryEnhancedRequest queryRequest = QueryEnhancedRequest.builder()
                .queryConditional(queryConditional)
                .build();

        this.table.index(INSTITUTION_LSI).query(queryRequest);

        SdkIterable<Page<TransactionDAO>> pages = this.table.index(INSTITUTION_LSI).query(queryRequest);

        return pages.stream()
                .flatMap(page -> page.items().stream()
                        .map(TransactionDAO::asTransaction))
                .collect(Collectors.toList());
    }

    public Optional<Transaction> query(Transaction transaction) {
        String sortKey = transaction.getDate() +
                "#" +
                transaction.getAmount() +
                "#" +
                transaction.getTransactionId();

        QueryConditional queryConditional = QueryConditional.keyEqualTo(Key.builder()
                        .partitionValue(transaction.getUser())
                        .sortValue(sortKey)
                        .build());

        QueryEnhancedRequest queryRequest = QueryEnhancedRequest.builder()
                .queryConditional(queryConditional)
                .build();

        PageIterable<TransactionDAO> pages = this.table.query(queryRequest);

        List<Transaction> transactions = pages.items().stream()
                .map(TransactionDAO::asTransaction)
                .collect(Collectors.toList());

        if (!transactions.isEmpty()) {
            return Optional.of(transactions.get(0));
        } else {
            return Optional.empty();
        }
    }

    /**
     * @param user
     * @param sortKey usually date.
     * @return
     */
    public List<Transaction> query(String user, String sortKey) {

        QueryConditional queryConditional = QueryConditional
                .sortGreaterThanOrEqualTo(Key.builder()
                        .partitionValue(user)
                        .sortValue(sortKey)
                        .build()
                );

        QueryEnhancedRequest queryRequest = QueryEnhancedRequest.builder()
                .queryConditional(queryConditional)
                .build();

        PageIterable<TransactionDAO> pages = this.table.query(queryRequest);

        return pages.items().stream()
                .map(TransactionDAO::asTransaction)
                .collect(Collectors.toList());

    }

    public List<Transaction> query(String user, String date, Double amount) {
        String sortKey = date + "#" + amount.toString();
        return query(user, sortKey);
    }

    public List<Transaction> query(String user, String date, Double amount, String transactionId) {
        String sortKey = date + "#" + amount.toString() + "#" + transactionId;
        return query(user, sortKey);
    }

    public void save(Transaction transaction) {
        TransactionDAO transactionDAO = new TransactionDAO(client, table).withTransaction(transaction);
        transactionDAO.save();
    }

    public Transaction load(Transaction transaction) {
        TransactionDAO transactionDAO = new TransactionDAO(client, table).withTransaction(transaction);
        return transactionDAO.load();
    }

    @DynamoDbPartitionKey
    public String getUser() { return user; }
    public void setUser(String user) { this.user = user; }

    @DynamoDbSortKey
    public String getDateAmountTransactionId() { return dateAmountTransactionId; }
    public void setDateAmountTransactionId(String dateAmountTransactionId) {
        this.dateAmountTransactionId = dateAmountTransactionId;
    }

    @DynamoDbSecondarySortKey(indexNames = {"institutionNameIndex"})
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

    private void save() {
        this.table.putItem(this);
    }

    private TransactionDAO withTransaction(Transaction transaction) {
        TransactionDAO transactionDAO = new TransactionDAO(client, table);
        transactionDAO.setUser(transaction.getUser()); // Partition key
        transactionDAO.setDateAmountTransactionId(transaction.getDate() +
                "#" +
                transaction.getAmount() +
                "#" +
                transaction.getTransactionId()); // Sort key
        transactionDAO.setInstitutionName(transaction.getInstitutionName());
        transactionDAO.setAccount(transaction.getAccountId());
        transactionDAO.setDescription(transaction.getDescription());
        transactionDAO.setOriginalDescription(transaction.getOriginalDescription());
        transactionDAO.setMerchantName(transaction.getMerchantName());
        transactionDAO.setItemId(transaction.getDate());

        return transactionDAO;
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

    // Exceptions
    public static class TransactionException extends Exception {
        public TransactionException(String errorMessage) {super(errorMessage);}
    }

    public static class TransactionNotFoundException extends TransactionDAO.TransactionException {
        public TransactionNotFoundException(String errorMessage) {
            super(errorMessage);
        }
    }

}
