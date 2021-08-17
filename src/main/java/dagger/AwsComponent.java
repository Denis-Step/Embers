package dagger;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
@Component(modules = {AwsClientModule.class})
public interface AwsComponent {

    DynamoDBMapper buildDynamo();
}
