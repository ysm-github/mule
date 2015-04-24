package org.mule.context.notification;

import org.mule.api.MuleContext;
import org.mule.api.MuleEvent;
import org.mule.api.context.MuleContextAware;
import org.mule.api.lifecycle.Initialisable;
import org.mule.api.lifecycle.InitialisationException;
import org.mule.construct.FlowXmlDescriptor;
import org.mule.construct.MessageProcessorDescriptor;

import java.util.HashMap;
import java.util.Map;

/**
 * Manager for handling message processing stacks.
 */
public class MessageProcessingStackManager implements MuleContextAware, Initialisable
{

    private final FlowNotificationTextDebugger pipelineProcessorDebugger;
    public MessageProcessorTextDebugger messageProcessorTextDebugger;
    private MuleContext muleContext;
    private Map<Integer, Integer> maxDepthSeen = new HashMap<>();

    private Map<String, MessageProcessStack> messageProcessStackMap = new HashMap();

    public MessageProcessingStackManager()
    {
        messageProcessorTextDebugger = new MessageProcessorTextDebugger(this);
        pipelineProcessorDebugger = new FlowNotificationTextDebugger(this);

    }

    public void onMessageProcessorNotification(MessageProcessorNotification notification)
    {
        if (notification.getAction() == MessageProcessorNotification.MESSAGE_PROCESSOR_PRE_INVOKE)
        {
            return;
        }
        MuleEvent muleEvent = notification.getSource();
        String uniqueId = muleEvent.getMessage().getUniqueId();

        String processorPath = notification.getProcessorPath();
        String[] parts = processorPath.split("/");
        FlowXmlDescriptor flowXmlDescriptor = muleContext.getRegistry().get(parts[1] + "Descriptor");
        MessageProcessorDescriptor messageProcessorDescriptor = flowXmlDescriptor.getMessageProcessorDescriptor();

        MessageProcessStack messageProcessStack = messageProcessStackMap.get(uniqueId);
        ProcessorDebugLine lastProcessorDebugLine = messageProcessStack.getLastEventDebugLine();

        for (int i = 3; i < parts.length; i++)
        {
            String indexPart = parts[i];
            try
            {
                Integer childIndex = Integer.valueOf(indexPart);
                int maxDepthSeenIndex = i - 3;
                Integer value = maxDepthSeen.get(maxDepthSeenIndex);
                MessageProcessStack newMessageProcessorStack = null;
                if (value != null)
                {
                    if (value >= childIndex)
                    {
                        messageProcessorDescriptor = messageProcessorDescriptor.getChild(childIndex);
                        messageProcessStack = messageProcessStack.getChildMessageProcessorStack(childIndex);
                        continue;
                    }
                    else
                    {
                        newMessageProcessorStack = new MessageProcessStack();
                        messageProcessStack.addChildMessageProcessorStack(newMessageProcessorStack);
                        maxDepthSeen.put(maxDepthSeenIndex, childIndex);
                    }
                }
                else
                {
                    newMessageProcessorStack = new MessageProcessStack();
                    messageProcessStack.addChildMessageProcessorStack(newMessageProcessorStack);
                    maxDepthSeen.put(maxDepthSeenIndex, childIndex);
                }

                final ProcessorDebugLine processorDebugLine = new ProcessorDebugLine();
                processorDebugLine.setEvent(muleEvent);
                newMessageProcessorStack.setProcessorDebugLine(processorDebugLine);
                processorDebugLine.setPreviousProcessorDebugLine(lastProcessorDebugLine);
                messageProcessorDescriptor = messageProcessorDescriptor.getChild(childIndex);
                processorDebugLine.setXml(messageProcessorDescriptor.getRepresentation());
                if (i == parts.length - 1)
                {
                    processorDebugLine.xmlContentIsMessageProcessor();
                }
                messageProcessStack = newMessageProcessorStack;
            }
            catch (NumberFormatException e)
            {
                String anotherFlowName = indexPart;
                flowXmlDescriptor = muleContext.getRegistry().get(anotherFlowName + "Descriptor");
                i++; //skip 'processors' entry
                messageProcessorDescriptor = flowXmlDescriptor.getMessageProcessorDescriptor();
            }
        }
    }


    @Override
    public void setMuleContext(MuleContext context)
    {
        this.muleContext = context;
    }


    @Override
    public void initialise() throws InitialisationException
    {
        muleContext.getNotificationManager().addListener(messageProcessorTextDebugger);
        muleContext.getNotificationManager().addListener(pipelineProcessorDebugger);
    }

    public void onPipelineNotificationComplete(PipelineMessageNotification notification)
    {
        MuleEvent muleEvent = (MuleEvent) notification.getSource();
        MessageProcessStack messageProcessStack = messageProcessStackMap.get(muleEvent.getMessage().getUniqueId());
        messageProcessStack.print(0);
        messageProcessStack.finishProcessing();
        messageProcessStackMap.remove(muleEvent.getMessage().getUniqueId());
        muleContext.fireNotification(new MessageProcessingStackNotification(messageProcessStack));
    }

    public void onPipelineNotificationStart(PipelineMessageNotification notification)
    {
        String flowName = notification.getResourceIdentifier();
        MuleEvent muleEvent = (MuleEvent) notification.getSource();
        ProcessorDebugLine processorDebugLine = new ProcessorDebugLine();
        processorDebugLine.setEvent(muleEvent);
        processorDebugLine.setXml("\n<flow name=\"" + flowName + "\" />");

        MessageProcessStack messageProcessStack = new MessageProcessStack();
        messageProcessStack.setProcessorDebugLine(processorDebugLine);
        messageProcessStackMap.put(muleEvent.getMessage().getUniqueId(), messageProcessStack);
    }
}
