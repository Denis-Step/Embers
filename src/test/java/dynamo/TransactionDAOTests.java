//package dynamo;
//
//import external.plaid.entities.Transaction;
//import org.junit.jupiter.api.AfterAll;
//import org.junit.jupiter.api.BeforeAll;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.TestInstance;
//import org.junit.runner.RunWith;
//import org.mockito.junit.MockitoJUnitRunner;
//import dynamo.setup.TransactionsTableSetup;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import java.util.ArrayList;
//import java.util.List;
//
//@RunWith(MockitoJUnitRunner.class)
//@TestInstance(TestInstance.Lifecycle.PER_CLASS)
//public class TransactionDAOTests {
//
//    private final TransactionDAO transactionDao;
//
//    private static final Logger LOGGER = LoggerFactory.getLogger(TransactionDAOTests.class);
//
//    TransactionDAOTests() {
//        this.transactionDao = new TransactionDAO();
//        TransactionsTableSetup.setUpTransactionsTable();
//    }
//
//    @AfterAll
//    public void deleteTransactionsTable() {
//        TransactionsTableSetup.deleteTransactionsTable();
//    }
//
//    @Test
//    public void test_LoadQueryTransaction() {
//        Transaction transaction = TransactionsTableSetup.createTransaction();
//        transactionDao.save(transaction);
//
//        Transaction loadedTransaction = transactionDao.load(transaction);
//        assert (loadedTransaction.equals(transaction));
//
//        List<Transaction> queriedTransactions = transactionDao.query(transaction.getUser());
//        assert (queriedTransactions.get(0).equals(transaction));
//
//        queriedTransactions = transactionDao.query(transaction.getUser(), transaction.getDate());
//        assert (queriedTransactions.get(0).equals(transaction));
//
//        queriedTransactions = transactionDao.query(transaction.getUser(), transaction.getDate(),
//                transaction.getAmount());
//        assert (queriedTransactions.get(0).equals(transaction));
//
//        queriedTransactions = transactionDao.query(transaction.getUser(), transaction.getDate(),
//                transaction.getAmount(), transaction.getTransactionId());
//        assert (queriedTransactions.get(0).equals(transaction));
//
//        transactionDao.delete(transaction);
//    }
//
//    @Test
//    public void test_deleteTransaction() {
//        Transaction transaction = TransactionsTableSetup.createTransaction();
//        transactionDao.save(transaction);
//        transactionDao.delete(transaction);
//
//        List<Transaction> queriedTransaction = transactionDao.query(transaction.getUser());
//        assert (queriedTransaction.size() == 0);
//
//        transactionDao.save(transaction);
//        transactionDao.delete(transaction.getUser(), transaction.getDate() +
//                "#" +
//                transaction.getAmount() +
//                "#" +
//                transaction.getTransactionId());
//        assert (queriedTransaction.size() == 0);
//    }
//
//    @Test
//    public void test_queryLsi() {
//        Transaction transaction = TransactionsTableSetup.createTransaction();
//        transactionDao.save(transaction);
//
//        List<Transaction> queriedTransaction = transactionDao.queryByInstitution(
//                transaction.getUser(),
//                transaction.getInstitutionName());
//        assert (queriedTransaction.get(0).equals(transaction));
//
//        transactionDao.delete(transaction);
//    }
//
//
//}
