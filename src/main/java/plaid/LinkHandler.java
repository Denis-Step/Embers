package plaid;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import dagger.DaggerPlaidComponent;
import lambda.PlaidLinkTokenCreateRequest;

import java.io.IOException;
import java.util.List;

public class LinkHandler implements RequestHandler<PlaidLinkTokenCreateRequest, String> {
    LinkGrabber linkGrabber = DaggerPlaidComponent.create().buildPlaidGrabber();

    public String handleRequest(PlaidLinkTokenCreateRequest event, Context context) {
        String user = event.getUser();
        List<String> products = event.getProducts();
        LambdaLogger logger = context.getLogger();
        String logTemplate = String.format("Requesting link token for user %s for %s products .", user, products.toString());
        logger.log(logTemplate);

        try {
            return linkGrabber.getLinkToken(user, products);
        } catch (IOException e) {
            return String.format("Exception: %s", e.toString());
        }
    }

}
