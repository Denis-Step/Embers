import com.plaid.client.request.LinkTokenCreateRequest;
import com.plaid.client.response.LinkTokenCreateResponse;
import dagger.DaggerPlaidComponent;
import plaid.LinkGrabber;
import retrofit2.Call;
import retrofit2.Response;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class Main {

    public static void main(String[] args){
        LinkGrabber linkGrabber = DaggerPlaidComponent.create().buildPlaidGrabber();
        System.out.println(Main.class.getPackage());
        List<String> products = Arrays.asList("transactions");
        List<String> countryCodes = Arrays.asList("US");

        LinkTokenCreateRequest linkTokenCreateRequest = new LinkTokenCreateRequest(new LinkTokenCreateRequest.User("Denis"),
                "PlaidJava",
                products,
                countryCodes,
                "en"
                );
        Call<LinkTokenCreateResponse> response =  linkGrabber.plaidClient.service().linkTokenCreate(linkTokenCreateRequest);
        try {
            Response<LinkTokenCreateResponse> resp = response.execute();
            System.out.println(resp);
            System.out.println(resp.body().getLinkToken());
        } catch (IOException e) {
            System.out.println(e);
        }
    }
}
