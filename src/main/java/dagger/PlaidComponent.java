package dagger;

import com.plaid.client.PlaidClient;
import lambda.processors.CreateLinkTokenProcessor;
import lambda.processors.ItemProcessor;
import lambda.processors.TransactionProcessor;
import plaid.clients.ItemGrabber;
import plaid.clients.LinkGrabber;

import javax.inject.Singleton;

@Singleton
@Component(modules = {
        PlaidClientModule.class,
        PlaidCredentialsModule.class,
        ProcessorModule.class})
public interface PlaidComponent {

    PlaidClient buildPLaidClient();

    LinkGrabber buildPlaidGrabber();
    ItemGrabber buildItemGrabber();

    CreateLinkTokenProcessor buildLinkTokenProcessor();
    ItemProcessor buildItemProcessor();
    TransactionProcessor buildTransactionProcessor();

}
