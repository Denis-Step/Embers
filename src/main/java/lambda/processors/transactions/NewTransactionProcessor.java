package lambda.processors.transactions;

import dagger.DaggerPlaidComponent;
import dynamo.PlaidItemDAO;

import javax.inject.Inject;

public class NewTransactionProcessor {
    private final PlaidItemDAO plaidItemDAO;

    @Inject
    public NewTransactionProcessor(PlaidItemDAO plaidItemDAO) {
        this.plaidItemDAO = plaidItemDAO;
    }

    public NewTransactionProcessor() {
        this.plaidItemDAO =  DaggerPlaidComponent.create().buildPlaidItemDao();
    }
}
