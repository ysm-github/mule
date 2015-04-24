package org.mule.api.context.notification;

import org.mule.context.notification.MessageProcessingStackNotification;

public interface MessageProcessingStackReadyNotificationListener <T extends MessageProcessingStackNotification> extends ServerNotificationListener<MessageProcessingStackNotification>
{

}
