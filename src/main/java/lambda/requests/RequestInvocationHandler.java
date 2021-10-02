package lambda.requests;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;


public class RequestInvocationHandler implements InvocationHandler {
    private ImmutableLinkRequest.Builder builder;
    private LinkRequest linkRequest;
    private int counter = 0;

    public RequestInvocationHandler() {
        super();
        System.out.println("Hello");
        this.builder = ImmutableLinkRequest.builder();
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        CreateLinkTokenRequest.class.getDeclaredMethods();
            if (method.toString().contains("setUser")) {
                System.out.println(args[0]);
                System.out.println(args[0]);
                builder.user((String) args[0]);
                counter++;
            }
            if (method.toString().contains("setProducts")) {
                builder.products((List<String>) args[0]);
                counter++;
            }

            if (method.toString().contains("getUser")) {
                return linkRequest.user();
            }
            if (method.toString().contains("getBuilt")) {
                return builder.build();
            }


            else {
                return this;
            }
        }

}
