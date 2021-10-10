package dagger;

import lambda.processors.messages.MessageProcessor;

@Component(modules = {TwilioModule.class})
public interface TwilioComponent {

    MessageProcessor buildMessageProcessor();
}
