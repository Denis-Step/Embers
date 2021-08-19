package dynamo;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBRangeKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

import java.util.List;

@DynamoDBTable(tableName="Items")
public class ItemsDAO {
    private String user;
    private String itemAccessToken;
    private String date;

    public ItemsDAO() {
    }

    @DynamoDBHashKey(attributeName = "User")
    public String getUser() {
        return this.user;
    }

    @DynamoDBRangeKey(attributeName = "ItemAccessToken")
    public String getItemAccessToken() {
        return itemAccessToken;
    }

    @DynamoDBAttribute(attributeName = "Date")
    public String getDate() {
        return date;
    }


    public void setDate(String date) {
        this.date = date;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public void setItemAccessToken(String itemAccessToken) {
        this.itemAccessToken = itemAccessToken;
    }
}
