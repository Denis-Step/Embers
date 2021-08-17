package lambda.handlers;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import dagger.DaggerPlaidComponent;
import lambda.requests.CreateLinkTokenRequest;
import plaid.LinkGrabber;

import java.util.List;

public class CreateLinkTokenHandler implements RequestHandler<CreateLinkTokenRequest, String> {
    LinkGrabber linkGrabber = DaggerPlaidComponent.create().buildPlaidGrabber();

    @Override
    public String handleRequest(CreateLinkTokenRequest event, Context context) {
        String user = event.getUser();
        List<String> products = event.getProducts();
        LambdaLogger logger = context.getLogger();
        logger.log(user);
        logger.log(event.toString());
        String logTemplate = String.format("Requesting link token for user %s for %s products .", user, products.toString());
        logger.log(logTemplate);

        try {
            String linkToken = linkGrabber.getLinkToken(user, products);
            logger.log( String.format("Retrieved linkToken %s for user %s for %s products", linkToken, user, products));
            return linkToken;
        } catch (Exception e) {
            // Rethrow Exception to prevent Lambda from succeeding.
            logger.log("Exception" + e.toString() + System.currentTimeMillis());
            throw new RuntimeException(String.format("Exception: %s", e.toString()));
        }
    }

}
