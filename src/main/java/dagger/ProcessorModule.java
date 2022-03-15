package dagger;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.plaid.client.PlaidClient;
import dynamo.PlaidItemDAO;
import external.plaid.clients.ItemCreator;
import external.plaid.clients.LinkGrabber;
import lambda.processors.items.CreateLinkTokenProcessor;

import javax.inject.Named;
import java.net.URI;
import java.net.URISyntaxException;

@Module
public interface ProcessorModule {

    @Provides
    static LinkGrabber provideLinkGrabber(PlaidClient plaidClient) {return new LinkGrabber(plaidClient);}

    @Provides
    static CreateLinkTokenProcessor provideCreateLinkTokenProcessor(LinkGrabber linkGrabber,
                                                                    @Named("WEBHOOK_URL") URI webhookUrl) {
        return new CreateLinkTokenProcessor(linkGrabber, webhookUrl);
    }

    @Provides
    static ItemCreator provideItemGrabber(PlaidClient plaidClient) {return new ItemCreator(plaidClient);}

    @Provides
    static PlaidItemDAO providePlaidItemDao() {return new PlaidItemDAO();}

    @Provides
    @Named("WEBHOOK_URL")
    static URI provideWebhookUrl() {
        try {
            return new URI("https://mv6o8yjeo1.execute-api.us-east-2.amazonaws.com/Beta/plaidhook");
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    @Provides
    @Named("DEFAULT_MAPPER")
    static ObjectMapper provideObjectMapper() {
        return new ObjectMapper();
    }

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
    static PollTransactionProcessor provideTransactionProcessor(ItemProcessor itemProcessor) {
        return new PollTransactionProcessor(new TransactionDAO(), itemProcessor);
    } */
}
