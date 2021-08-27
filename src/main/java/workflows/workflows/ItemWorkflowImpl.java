package workflows.workflows;

import plaid.entities.Transaction;
import workflows.activities.ItemActivities;
import workflows.activities.ItemActivitiesImpl;

import java.time.Instant;
import java.util.Date;
import java.util.List;

public class ItemWorkflowImpl implements ItemWorkflow {
    private ItemActivities activities = new ItemActivitiesImpl();
    private final String USER = "Derek";
    private final Date START_DATE = Date.from(Instant.parse("2020-08-01T18:35:24.00Z"));

    // Entry-point.
    @Override
    public void showTransactions() {
        String accessToken = activities.getAccessToken(USER);
        List<Transaction> transactions = activities.getTransactions(accessToken, START_DATE);
        activities.listTransactions(transactions);

    }
}
