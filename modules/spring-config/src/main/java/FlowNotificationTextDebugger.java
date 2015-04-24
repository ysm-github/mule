import org.mule.api.context.notification.PipelineMessageNotificationListener;
import org.mule.context.notification.PipelineMessageNotification;

public class FlowNotificationTextDebugger implements PipelineMessageNotificationListener<PipelineMessageNotification>
{

    private final NotificationTextDebugger notificationTextDebugger;

    public FlowNotificationTextDebugger(NotificationTextDebugger notificationTextDebugger)
    {
        this.notificationTextDebugger  = notificationTextDebugger;
    }


    @Override
    public void onNotification(PipelineMessageNotification notification)
    {
        if (notification.getAction() == PipelineMessageNotification.PROCESS_COMPLETE)
        {
            notificationTextDebugger.onPipelineNotificationComplete(notification);
        }
        else if (notification.getAction() == PipelineMessageNotification.PROCESS_START)
        {
            notificationTextDebugger.onPipelineNotificationStart(notification);
        }
    }


}
