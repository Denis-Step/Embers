package twilio;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

import javax.inject.Inject;

public class MessageClient {
    // Find your Account Sid and Token at twilio.com/user/account
    private final String ACCOUNT_SID = "AC68520309e3f9449e42697f9d37436a98";
    private final String AUTH_TOKEN = "1b779c06887f5838dd9efe08103d8454";

    private static final String DEFAULT_SENDER = "+12014313173";

    private String senderNumber;

    public MessageClient() {
        Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
        this.senderNumber = DEFAULT_SENDER;
    }

    public MessageClient(String senderNumber) {
        this();
        this.senderNumber = senderNumber;
    }

    public Message sendMessage(String receiverNumber, String messageText) {

        // To number first, then From number

        return Message.creator(new PhoneNumber(receiverNumber),
                new PhoneNumber(senderNumber),
                messageText).create();
    }

    public String getSenderNumber() {
        return senderNumber;
    }

    public void setSenderNumber(String senderNumber) {
        this.senderNumber = senderNumber;
    }
}