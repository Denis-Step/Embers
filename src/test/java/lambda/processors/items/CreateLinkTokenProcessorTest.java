package lambda.processors.items;

import external.plaid.clients.LinkGrabber;
import lambda.requests.items.CreateLinkTokenRequest;
import lambda.requests.items.ImmutableCreateLinkTokenRequest;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CreateLinkTokenProcessorTest {
    private final String USER = "USER";
    private final List<String> PRODUCTS = new ArrayList<>();
    private final String LINK_TOKEN = "LINK_TOKEN";
    private final String WEBHOOK_LINK_TOKEN = "WEBHOOK_LINK_TOKEN";

    private LinkGrabber linkGrabber;
    private String webhookUrlString = "https://wwww.google.com";
    private URI webhookUrl;

    private final CreateLinkTokenProcessor linkTokenProcessor;

    public CreateLinkTokenProcessorTest() throws URISyntaxException {
        webhookUrl = new URI(webhookUrlString);
        this.linkGrabber = mock(LinkGrabber.class);
        this.linkTokenProcessor = new CreateLinkTokenProcessor(linkGrabber, webhookUrl);
    }

    @Test
    public void createLinkTokenWithNoWebhook() {
        when(linkGrabber.getLinkToken(USER, PRODUCTS)).thenReturn(LINK_TOKEN);

        CreateLinkTokenRequest request = sampleLinkTokenRequest(false);

        try {
            String linkToken = this.linkTokenProcessor.createLinkToken(request);
            assertEquals(LINK_TOKEN, linkToken);
        } catch (IOException e) {
            assert false;
        }
    }

    @Test
    public void createLinkTokenWithWebhook() {
        when(linkGrabber.getLinkToken(USER, PRODUCTS, webhookUrlString)).thenReturn(WEBHOOK_LINK_TOKEN);

        CreateLinkTokenRequest request = sampleLinkTokenRequest(true);

        try {
            String linkToken = this.linkTokenProcessor.createLinkToken(request);
            assertEquals(WEBHOOK_LINK_TOKEN, linkToken);
        } catch (IOException e) {
            assert false;
        }
    }

    private CreateLinkTokenRequest sampleLinkTokenRequest(boolean hasWebhook) {
        return ImmutableCreateLinkTokenRequest.builder()
                .user(USER)
                .products(PRODUCTS)
                .webhookEnabled(hasWebhook)
                .build();
    }
}
