package lambda.handlers.items;

import com.amazonaws.services.lambda.runtime.Context;
import lambda.handlers.items.CreateLinkTokenHandler;
import lambda.processors.items.CreateLinkTokenProcessor;
import lambda.requests.items.CreateLinkTokenRequest;
import lambda.requests.items.ImmutableCreateLinkTokenRequest;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class CreateLinkTokenHandlerTest {
    private CreateLinkTokenHandler handler;

    private final String USER = "USER";
    private final List<String> PRODUCTS = new ArrayList<>();
    private final String LINK_TOKEN = "LINK_TOKEN";

    @Test
    public void handlerCallsProcessor() throws IOException {
        CreateLinkTokenProcessor processor = mock(CreateLinkTokenProcessor.class);
        handler = new CreateLinkTokenHandler(processor);
        when(processor.createLinkToken(sampleLinkTokenRequest(false))).thenReturn(LINK_TOKEN);

        String linkToken = handler.handleRequest(sampleLinkTokenRequest(false),
                mock(Context.class));
        assertEquals(LINK_TOKEN, linkToken);
    }

    private CreateLinkTokenRequest sampleLinkTokenRequest(boolean hasWebhook) {
        return ImmutableCreateLinkTokenRequest.builder()
                .user(USER)
                .products(PRODUCTS)
                .webhookEnabled(hasWebhook)
                .build();
    }
}
