package lambda.auth;

/**
 * Client to query for user credentials.
 */
public interface AuthzClient {

    /**
     * @param userToken JWT
     * @return the name of the user for this JWT.
     */
    String getUser(String userToken);
}
