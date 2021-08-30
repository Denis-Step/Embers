package lambda.processors;

import dynamo.PlaidItemDAO;
import dynamo.PlaidTransactionDAO;
import lambda.requests.GetItemRequest;
import lambda.requests.GetTransactionsRequest;
import plaid.clients.TransactionsGrabber;
import plaid.entities.PlaidItem;
import plaid.entities.Transaction;

import java.io.IOException;
import java.time.Instant;
import java.util.Date;
import java.util.List;

// Params: Link --> User, InstitutionId,
// Transactions --> StartDate, EndDate?, InstitutionId, AccountName
public class TransactionProcessor {
    private final PlaidTransactionDAO transactionDAO;
    private final ItemProcessor itemProcessor;

    public TransactionProcessor() {
        this.transactionDAO = new PlaidTransactionDAO();
        this.itemProcessor = new ItemProcessor();
    }

    // First get item, then pull Tx from Plaid for that item.
    // NO FIELDS NULLABLE.
    public List<Transaction> pullFromPlaid(GetTransactionsRequest transactionsRequest) throws IOException, ItemProcessor.ItemException {

        // Get access token and initialize txGrabber.
        String accessToken = getItem(transactionsRequest.getUser(), transactionsRequest.getInstitutionName()).getAccessToken();
        TransactionsGrabber txGrabber = new TransactionsGrabber(accessToken);

        List<Transaction> transactions = txGrabber.requestTransactions(transactionsRequest);
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
