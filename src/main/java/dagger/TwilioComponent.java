package dagger;

import lambda.processors.MessageProcessor;

@Component(modules = {TwilioModule.class})
public interface TwilioComponent {

    MessageProcessor buildMessageProcessor();
}
