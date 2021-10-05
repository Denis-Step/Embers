package lambda.processors;

import dynamo.PlaidTransactionDAO;
import lambda.requests.GetItemRequest;
import lambda.requests.GetTransactionsRequest;
import plaid.clients.TransactionsGrabber;
import plaid.entities.PlaidItem;
import plaid.entities.Transaction;

import javax.inject.Inject;
import java.io.IOException;
import java.sql.Date;
import java.time.Instant;
import java.util.List;

// Params: Link --> User, InstitutionId,
// Transactions --> StartDate, EndDate?, InstitutionId, AccountName
public class TransactionProcessor {
    private final PlaidTransactionDAO transactionDAO;
    private final ItemProcessor itemProcessor;

    @Inject
    public TransactionProcessor(PlaidTransactionDAO transactionDAO, ItemProcessor itemProcessor) {
        this.transactionDAO = transactionDAO;
        this.itemProcessor = itemProcessor;
    }

    // First get item, then pull Tx from Plaid for that item.
    // NO FIELDS NULLABLE.
    public List<Transaction> pullFromPlaid(GetTransactionsRequest transactionsRequest) throws IOException, ItemProcessor.ItemException {

        // Get access token and initialize txGrabber.
        String user = transactionsRequest.getUser();
        String institutionName = transactionsRequest.getInstitutionName();
        String accessToken = getItem(user, institutionName).accessToken();

        TransactionsGrabber txGrabber = new TransactionsGrabber(user, institutionName, accessToken);

        List<Transaction> transactions;
        if (transactionsRequest.startDate != null && transactionsRequest.endDate != null) {
            transactions = txGrabber.requestTransactions(
                    Date.from(Instant.parse(transactionsRequest.getStartDate())),
                    Date.from(Instant.parse(transactionsRequest.getEndDate())));
        }

        if (transactionsRequest.endDate == null) {
            transactions = txGrabber.requestTransactions(Date.from(Instant.parse(transactionsRequest.getStartDate())));
        }

        // @TODO: Add support for only end date and no start date.

        else {
            // Default 30 days.
            transactions = txGrabber.requestTransactions();
        }

        transactionDAO.save(transactions);
        return transactions;
    }

    private PlaidItem getItem(String user, String institution) throws ItemProcessor.ItemException {
        GetItemRequest request = createGetItemRequest(user, institution);
        return itemProcessor.getItem(request);
    }

    private GetItemRequest createGetItemRequest(String user, String institution) {
        GetItemRequest request = new GetItemRequest();
        request.setUser(user);
        request.setInstitution(institution);
        return request;
    }


}
