package lambda.requests.items;

public class GetItemRequest {

    private String user;
    private String institution; // @Nullable

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getInstitution() {
        return institution;
    }

    public void setInstitution(String institution) {
        this.institution = institution;
    }

    @Override
    public String toString() {
        return "GetItemRequest{" +
                "user='" + user + '\'' +
                ", institution='" + institution + '\'' +
                '}';
    }
}
