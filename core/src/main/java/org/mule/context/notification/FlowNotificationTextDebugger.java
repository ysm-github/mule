package org.mule.context.notification;

import org.mule.api.context.notification.PipelineMessageNotificationListener;
import org.mule.context.notification.PipelineMessageNotification;

/**
 * Listener for PipelineMessageNotification that delegates notifications to NotificationTextDebugger
 */
public class FlowNotificationTextDebugger implements PipelineMessageNotificationListener<PipelineMessageNotification>
{

    private final MessageProcessingStackManager messageProcessingStackManager;

    public FlowNotificationTextDebugger(MessageProcessingStackManager messageProcessingStackManager)
    {
        this.messageProcessingStackManager = messageProcessingStackManager;
    }


    @Override
    public void onNotification(PipelineMessageNotification notification)
    {
        if (notification.getAction() == PipelineMessageNotification.PROCESS_COMPLETE)
        {
            messageProcessingStackManager.onPipelineNotificationComplete(notification);
        }
        else if (notification.getAction() == PipelineMessageNotification.PROCESS_START)
        {
            messageProcessingStackManager.onPipelineNotificationStart(notification);
        }
    }


}
