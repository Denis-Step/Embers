package testcontainers;

import com.amazonaws.services.dynamodbv2.xspec.L;
import dagger.DaggerAwsComponent;
import dynamo.setup.PlaidItemsTableSetup;
import external.plaid.entities.ImmutablePlaidItem;
import external.plaid.entities.PlaidItem;
import org.junit.ClassRule;
import org.junit.jupiter.api.*;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;

import java.util.ArrayList;
import java.util.List;

//@Testcontainers
@RunWith(MockitoJUnitRunner.class)
public class TestSetup {

    //@Container
    private static final GenericContainer localDynamoDbContainer = LocalDynamoDbContainer.getInstance();

    @Test
    public void test_sample() {

    }

}