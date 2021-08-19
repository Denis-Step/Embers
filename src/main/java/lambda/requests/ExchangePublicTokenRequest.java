package lambda.requests;

// LambdaRequest
public class ExchangePublicTokenRequest {
    private String user;
    public String publicToken;

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPublicToken() {
        return publicToken;
    }

    public void setPublicToken(String publicToken) {
        this.publicToken = publicToken;
    }
}
