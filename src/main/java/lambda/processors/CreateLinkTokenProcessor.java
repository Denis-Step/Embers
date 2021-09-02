package lambda.processors;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import dagger.DaggerPlaidComponent;
import lambda.requests.CreateLinkTokenRequest;
import plaid.clients.LinkGrabber;

import javax.inject.Inject;
import java.io.IOException;

public class CreateLinkTokenProcessor {

    private final LinkGrabber linkGrabber;

    @Inject
    public CreateLinkTokenProcessor(LinkGrabber linkGrabber) {this.linkGrabber = linkGrabber;}

    public String createLinkToken(CreateLinkTokenRequest event) throws IOException {
        return linkGrabber.getLinkToken(event);
    }

}
