package dagger;

import twilio.MessageClient;

@Module
public interface TwilioModule {

    @Provides
    static MessageClient provideMessageClient() {return new MessageClient(); }
}
