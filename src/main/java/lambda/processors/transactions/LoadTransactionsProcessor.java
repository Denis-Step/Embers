package lambda.processors.transactions;

import dynamo.PlaidItemDAO;
import dynamo.TransactionDAO;
import lambda.processors.items.ItemProcessor;
import lambda.requests.transactions.GetTransactionsRequest;
import plaid.clients.TransactionsGrabber;
import plaid.entities.PlaidItem;
import plaid.entities.Transaction;

import javax.inject.Inject;
import java.io.IOException;
import java.util.Date;
import java.util.List;

// Params: Link --> User, InstitutionId,
// Transactions --> StartDate?, EndDate?, User, InstitutionId, AccountName
public class LoadTransactionsProcessor {
    private final TransactionDAO transactionDAO;
    private final ItemProcessor itemProcessor;

    @Inject
    public LoadTransactionsProcessor(TransactionDAO transactionDAO, ItemProcessor itemProcessor) {
        this.transactionDAO = transactionDAO;
        this.itemProcessor = itemProcessor;
    }

    // First get item, then pull Tx from Plaid for that item.
    public List<Transaction> pullNewTransactions(String user, String institution, Date startDate, Date endDate)
            throws PlaidItemDAO.ItemException, IOException {
        String accessToken = getItem(user, institution).accessToken();

        TransactionsGrabber txGrabber = new TransactionsGrabber(user, institution, accessToken);
        return txGrabber.requestTransactions(startDate, endDate);
    }

    public List<Transaction> getTransactions(GetTransactionsRequest request) {
        if (request.accountId != null) {
           return this.transactionDAO.query(request.user, request.institutionName, request.accountId);
        } else {
            return this.transactionDAO.query(request.user, request.institutionName);
        }
    }

    private PlaidItem getItem(String user, String institution) throws PlaidItemDAO.ItemException {
        return itemProcessor.getItem(user, institution);
    }

}
