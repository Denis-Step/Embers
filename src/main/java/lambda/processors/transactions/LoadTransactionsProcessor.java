package lambda.processors.transactions;

import dynamo.PlaidItemDAO;
import dynamo.TransactionDAO;
import lambda.processors.items.ItemProcessor;
import lambda.requests.transactions.GetTransactionsRequest;
import external.plaid.clients.TransactionsGrabber;
import external.plaid.entities.PlaidItem;
import external.plaid.entities.Transaction;

import javax.inject.Inject;
import java.util.Date;
import java.util.List;

// @TODO: Add support for accountId and dates.
// Params: Link --> User, InstitutionId,
// Transactions --> StartDate?, EndDate?, User, InstitutionId, AccountName
public class LoadTransactionsProcessor {
    private final PlaidItemDAO plaidItemDAO;

    @Inject
    public LoadTransactionsProcessor(PlaidItemDAO plaidItemDAO) {
        this.plaidItemDAO = plaidItemDAO;
    }

    /**
     * First get item, then pull Tx from Plaid for that item.
     * @param user plaid Item user.
     * @param institution institutionName for transactions.
     * @param startDate inclusive.
     * @param endDate inclusive.
     * @return list of {@link Transaction}s from Plaid.
     * @throws PlaidItemDAO.ItemException
     */
    public List<Transaction> pullNewTransactions(String user, String institution, Date startDate, Date endDate)
            throws PlaidItemDAO.ItemException {
        String accessToken = getItem(user, institution).accessToken();

        TransactionsGrabber txGrabber = new TransactionsGrabber(user, institution, accessToken);
        return txGrabber.requestTransactions(startDate, endDate);
    }

    private PlaidItem getItem(String user, String institution) throws PlaidItemDAO.ItemException {
        return plaidItemDAO.getItem(user, institution);
    }

}
