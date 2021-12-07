package dagger;

import external.twilio.TwilioMessageSender;
import messages.MessageClient;
import messages.TwilioMessageClient;

@Module
public interface TwilioModule {

    @Provides
    static TwilioMessageSender provideTwilioMessageSender() {return new TwilioMessageSender(); }

    @Binds MessageClient provideMessageClient(TwilioMessageClient twilioMessageClient );
}
