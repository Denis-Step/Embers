package lambda.requests;

public class CreateItemRequest {

    public String user;
    public String itemId;
    public String accessToken;

    public CreateItemRequest(){};

    public CreateItemRequest(String user, String itemId, String accessToken) {
        this.user = user;
        this.itemId = itemId;
        this.accessToken = accessToken;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }


    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

}
