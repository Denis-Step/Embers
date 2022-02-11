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


    /**
     * Use without a link token.
     * @param linkGrabber Plaid wrapper client.
     */
    public CreateLinkTokenProcessor(LinkGrabber linkGrabber) {
        this.linkGrabber = linkGrabber;
        this.webhookUrl = Optional.empty();
    }

    /**
     * Get new Link Token from Plaid.
     * @param request request for new link token.
     * @return Stringified link token.
     * @throws IOException
     */
    public String createLinkToken(CreateLinkTokenRequest request) throws IOException {
        LOGGER.info(request.toString());
        if (request.getWebhookEnabled()) {

            if (!webhookUrl.isPresent()) {
                throw new RuntimeException("Cannot create linkToken webhook without webhook URI configured in env.");
            }

            return this.linkGrabber.getLinkToken(request.getUser(), request.getProducts(),
                    this.webhookUrl.get().toString());
        } else {
            return this.linkGrabber.getLinkToken(request.getUser(), request.getProducts());
        }
    }

}
