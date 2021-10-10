package dagger;

import com.plaid.client.PlaidClient;
import dynamo.PlaidItemDAO;
import lambda.processors.items.CreateLinkTokenProcessor;
import lambda.processors.items.ItemProcessor;
import lambda.processors.transactions.LoadTransactionsProcessor;
import lambda.processors.transactions.ReceiveTransactionsProcessor;
import lambda.processors.transactions.CreateSummaryMessageProcessor;
import plaid.clients.ItemGrabber;
import plaid.clients.LinkGrabber;

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

    LinkGrabber buildPlaidGrabber();
    ItemGrabber buildItemGrabber();

    CreateLinkTokenProcessor buildLinkTokenProcessor();
    ItemProcessor buildItemProcessor();
    LoadTransactionsProcessor buildLoadTransactionsProcessor();
    ReceiveTransactionsProcessor buildReceiveTransactionsProcessor();
    CreateSummaryMessageProcessor buildSummarizeTransactionsProcessor();

}
