package lambda.processors.transactions;

import dynamo.PlaidItemDAO;
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
    private final TransactionsGrabber transactionsGrabber;

    @Inject
    public LoadTransactionsProcessor(PlaidItemDAO plaidItemDAO, TransactionsGrabber transactionsGrabber) {
        this.plaidItemDAO = plaidItemDAO;
        this.transactionsGrabber = transactionsGrabber;
    }

    /**
     * First get item for User, then pull Tx from Plaid for that item.
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

        return transactionsGrabber.requestTransactions(user, institution, accessToken, startDate, endDate);
    }

    private PlaidItem getItem(String user, String institution) throws PlaidItemDAO.ItemException {
        return plaidItemDAO.getItem(user, institution);
    }

}
