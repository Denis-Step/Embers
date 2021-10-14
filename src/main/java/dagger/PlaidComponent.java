package dagger;

import com.plaid.client.PlaidClient;
import dynamo.PlaidItemDAO;
import lambda.processors.items.CreateLinkTokenProcessor;
import lambda.processors.items.ItemProcessor;
import lambda.processors.transactions.LoadTransactionsProcessor;
import lambda.processors.transactions.NewTransactionProcessor;
import lambda.processors.transactions.ReceiveTransactionsProcessor;
import external.plaid.clients.ItemCreator;
import external.plaid.clients.LinkGrabber;

import javax.inject.Singleton;

@Singleton
@Component(modules = {
        AwsClientModule.class,
        PlaidClientModule.class,
        PlaidCredentialsModule.class,
        ProcessorModule.class})
public interface PlaidComponent {

    PlaidClient buildPLaidClient();

    PlaidItemDAO buildPlaidItemDao();

    ItemCreator buildItemGrabber();
    LinkGrabber buildPlaidGrabber();

    CreateLinkTokenProcessor buildLinkTokenProcessor();
    ItemProcessor buildItemProcessor();
    LoadTransactionsProcessor buildLoadTransactionsProcessor();
    NewTransactionProcessor buildNewTransactionProcessor();
    ReceiveTransactionsProcessor buildReceiveTransactionsProcessor();

}
