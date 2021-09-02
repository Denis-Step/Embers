package dagger;

import com.plaid.client.PlaidClient;
import dynamo.PlaidItemDAO;
import dynamo.PlaidTransactionDAO;
import lambda.processors.CreateLinkTokenProcessor;
import lambda.processors.ItemProcessor;
import lambda.processors.TransactionProcessor;
import plaid.clients.ItemGrabber;
import plaid.clients.LinkGrabber;

import javax.inject.Singleton;

@Module
public interface ProcessorModule {

    @Provides
    static LinkGrabber provideLinkGrabber(PlaidClient plaidClient) {return new LinkGrabber(plaidClient);}

    @Provides
    static ItemGrabber provideItemGrabber(PlaidClient plaidClient) {return new ItemGrabber(plaidClient);}

    @Provides
    static PlaidItemDAO providePlaidItemDao() {return new PlaidItemDAO();}

    @Provides
    static PlaidTransactionDAO providePlaidTransactionDao() {return new PlaidTransactionDAO();}

    /*@Provides
    @Singleton
    static CreateLinkTokenProcessor provideCreateLinkTokenProcessor(LinkGrabber linkGrabber) {
        return new CreateLinkTokenProcessor(linkGrabber);
    }

    @Provides
    @Singleton
    static ItemProcessor provideItemProcessor(ItemGrabber itemGrabber) {
        return new ItemProcessor(itemGrabber, new PlaidItemDAO());
    }

    @Provides
    @Singleton
    static TransactionProcessor provideTransactionProcessor(ItemProcessor itemProcessor) {
        return new TransactionProcessor(new PlaidTransactionDAO(), itemProcessor);
    } */
}
