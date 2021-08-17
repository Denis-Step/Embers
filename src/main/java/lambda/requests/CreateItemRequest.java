package lambda.requests;

public class CreateItemRequest {

    public String user;
    public String plaidItem;

    public CreateItemRequest() {}

    CreateItemRequest(String user, String plaidItem) {
        this.user = user;
        this.plaidItem = plaidItem;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPlaidItem() {
        return plaidItem;
    }

    public void setPlaidItem(String plaidItem) {
        this.plaidItem = plaidItem;
    }
}
