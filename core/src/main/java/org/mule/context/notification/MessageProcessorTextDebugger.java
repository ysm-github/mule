package org.mule.context.notification;

import org.mule.api.context.notification.MessageProcessorNotificationListener;
import org.mule.context.notification.MessageProcessorNotification;

/**
 * Listener for MessageProcessorNotification that delegates notifications to NotificationTextDebugger
 */
public class MessageProcessorTextDebugger implements MessageProcessorNotificationListener<MessageProcessorNotification>
{

    private final MessageProcessingStackManager messageProcessingStackManager;

    public MessageProcessorTextDebugger(MessageProcessingStackManager messageProcessingStackManager)
    {
        this.messageProcessingStackManager = messageProcessingStackManager;
    }


    @Override
    public void onNotification(MessageProcessorNotification notification)
    {
        messageProcessingStackManager.onMessageProcessorNotification(notification);
    }

}
