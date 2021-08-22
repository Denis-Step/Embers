package lambda.requests;

import java.util.List;

// LambdaRequest
public class CreateLinkTokenRequest {

    private String user;
    private List<String> products;

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
