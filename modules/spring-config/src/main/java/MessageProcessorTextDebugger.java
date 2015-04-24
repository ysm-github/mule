import org.mule.api.context.notification.MessageProcessorNotificationListener;
import org.mule.context.notification.MessageProcessorNotification;

public class MessageProcessorTextDebugger implements MessageProcessorNotificationListener<MessageProcessorNotification>
{

    private final NotificationTextDebugger notificationTextDebugger;

    public MessageProcessorTextDebugger(NotificationTextDebugger notificationTextDebugger)
    {
        this.notificationTextDebugger = notificationTextDebugger;
    }


    @Override
    public void onNotification(MessageProcessorNotification notification)
    {
        notificationTextDebugger.onMessageProcessorNotification(notification);
    }

}
