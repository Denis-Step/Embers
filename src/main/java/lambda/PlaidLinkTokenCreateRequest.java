package lambda;

import java.util.List;


public class PlaidLinkTokenCreateRequest {

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
