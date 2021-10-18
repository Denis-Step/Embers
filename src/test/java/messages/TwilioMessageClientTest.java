package messages;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import com.plaid.client.PlaidApiService;
import com.plaid.client.PlaidClient;
import com.plaid.client.response.ItemPublicTokenExchangeResponse;
import lambda.requests.items.CreateItemRequest;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import external.plaid.responses.PublicTokenExchangeResponse;
import retrofit2.Call;
import retrofit2.Response;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class TwilioMessageClientTest {

    public TwilioMessageClientTest() { }

    @Test
    public void test_Fake() {
        assert (0==0);
    }
}
