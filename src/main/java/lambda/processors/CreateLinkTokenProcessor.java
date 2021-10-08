package lambda.processors;

import lambda.requests.link.CreateLinkTokenRequest;
import plaid.clients.LinkGrabber;

import javax.inject.Inject;
import java.io.IOException;
import java.util.logging.Logger;

public class CreateLinkTokenProcessor {

    private final LinkGrabber linkGrabber;
    private final String webhookUrl;
    private static final Logger LOGGER = Logger.getLogger(CreateLinkTokenProcessor.class.getName());

    @Inject
    public CreateLinkTokenProcessor(LinkGrabber linkGrabber) {
        this.linkGrabber = linkGrabber;
        this.webhookUrl = "https://mv6o8yjeo1.execute-api.us-east-2.amazonaws.com/Beta/plaidhook";
    }

    public String createLinkToken(CreateLinkTokenRequest event) throws IOException {
        LOGGER.info(event.toString());
        if (event.webhook) {
            return this.linkGrabber.getLinkToken(event.getUser(), event.getProducts(), this.webhookUrl);
        } else {
            return this.linkGrabber.getLinkToken(event.getUser(), event.getProducts());
        }
    }

}
