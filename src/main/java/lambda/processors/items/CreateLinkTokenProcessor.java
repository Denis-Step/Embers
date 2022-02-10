package lambda.processors.items;

import lambda.requests.items.CreateLinkTokenRequest;
import external.plaid.clients.LinkGrabber;

import java.io.IOException;
import java.net.URI;
import java.util.Optional;
import java.util.logging.Logger;

public class CreateLinkTokenProcessor {

    private final LinkGrabber linkGrabber;
    private final Optional<URI> webhookUrl;
    private static final Logger LOGGER = Logger.getLogger(CreateLinkTokenProcessor.class.getName());

    public CreateLinkTokenProcessor(LinkGrabber linkGrabber, URI webhookUrl) {
        this.linkGrabber = linkGrabber;
        this.webhookUrl = Optional.of(webhookUrl);
    }

    public CreateLinkTokenProcessor(LinkGrabber linkGrabber) {
        this.linkGrabber = linkGrabber;
        this.webhookUrl = Optional.empty();
    }

    public String createLinkToken(CreateLinkTokenRequest event) throws IOException {
        LOGGER.info(event.toString());
        if (event.getWebhookEnabled()) {

            if (!webhookUrl.isPresent()) {
                throw new RuntimeException("Cannot create linkToken webhook without webhook URI configured in env.");
            }

            return this.linkGrabber.getLinkToken(event.getUser(), event.getProducts(),
                    this.webhookUrl.get().toString());
        } else {
            return this.linkGrabber.getLinkToken(event.getUser(), event.getProducts());
        }
    }

}
