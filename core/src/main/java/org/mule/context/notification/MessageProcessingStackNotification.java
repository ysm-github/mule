package org.mule.context.notification;

import org.mule.api.context.notification.BlockingServerEvent;
import org.mule.api.context.notification.ServerNotification;

/**
 *
 */
public class MessageProcessingStackNotification extends ServerNotification implements BlockingServerEvent
{

    private static final long serialVersionUID = 1L;

    public MessageProcessingStackNotification(Object message)
    {
        super(message, ServerNotification.NULL_ACTION);
    }
}
