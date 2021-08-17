package lambda.requests;

import java.util.List;


public class CreateLinkTokenRequest {

    public String user;
    public List<String> products;

    CreateLinkTokenRequest() {}

    CreateLinkTokenRequest(String user, List<String> products) {
        this.user = user;
        this.products = products;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public List<String> getProducts() {
        return products;
    }

    public void setProducts(List<String> products) {
        this.products = products;
    }
}
