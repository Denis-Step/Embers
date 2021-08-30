package lambda.processors;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import dagger.DaggerPlaidComponent;
import lambda.requests.CreateLinkTokenRequest;
import plaid.clients.LinkGrabber;

import java.io.IOException;
import java.util.List;

public class CreateLinkTokenProcessor {

    private final LinkGrabber linkGrabber;

    public CreateLinkTokenProcessor() {
        this.linkGrabber = DaggerPlaidComponent.create().buildPlaidGrabber();
    }

    public String createLinkToken(CreateLinkTokenRequest event) throws IOException {
        return linkGrabber.getLinkToken(event);
    }

}
