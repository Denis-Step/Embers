package dagger;

import com.plaid.client.PlaidClient;
import dynamo.PlaidItemDAO;
import dynamo.TransactionDAO;
import plaid.clients.ItemCreator;
import plaid.clients.LinkGrabber;

@Module
public interface ProcessorModule {

    @Provides
    static LinkGrabber provideLinkGrabber(PlaidClient plaidClient) {return new LinkGrabber(plaidClient);}

    @Provides
    static ItemCreator provideItemGrabber(PlaidClient plaidClient) {return new ItemCreator(plaidClient);}

    @Provides
    static PlaidItemDAO providePlaidItemDao() {return new PlaidItemDAO();}

    @Provides
    static TransactionDAO providePlaidTransactionDao() {return new TransactionDAO();}

    /*@Provides
    @Singleton
    static CreateLinkTokenProcessor provideCreateLinkTokenProcessor(LinkGrabber linkGrabber) {
        return new CreateLinkTokenProcessor(linkGrabber);
    }

    @Provides
    @Singleton
    static ItemProcessor provideItemProcessor(ItemCreator itemGrabber) {
        return new ItemProcessor(itemGrabber, new PlaidItemDAO());
    }

    @Provides
    @Singleton
    static TransactionProcessor provideTransactionProcessor(ItemProcessor itemProcessor) {
        return new TransactionProcessor(new TransactionDAO(), itemProcessor);
    } */
}
