import org.mule.api.MuleContext;
import org.mule.api.MuleEvent;
import org.mule.api.context.MuleContextAware;
import org.mule.api.lifecycle.Initialisable;
import org.mule.api.lifecycle.InitialisationException;
import org.mule.config.spring.EventDebugLine;
import org.mule.config.spring.MuleEventDifferencesHelper;
import org.mule.construct.FlowXmlDescriptor;
import org.mule.construct.MessageProcessorDescriptor;
import org.mule.context.notification.MessageProcessorNotification;
import org.mule.context.notification.PipelineMessageNotification;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Stack;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NotificationTextDebugger implements MuleContextAware, Initialisable
{


    private final FlowNotificationTextDebugger pipelineProcessorDebugger;
    private Logger log = LoggerFactory.getLogger(NotificationTextDebugger.class);
    public MessageProcessorTextDebugger messageProcessorTextDebugger;
    private MuleContext muleContext;
    private Map<String, LinkedList<EventDebugLine>> stacks = new HashMap<String, LinkedList<EventDebugLine>>();
    private Map<Integer, Integer> maxDepthSeen = new HashMap<>();
    private String previousElement;
    private int previousDeepth;
    private Stack scopes = new Stack();

    public NotificationTextDebugger()
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

        for (int i = 3; i < parts.length; i++)
        {
            String indexPart = parts[i];
            try
            {
                Integer childIndex = Integer.valueOf(indexPart);
                int maxDepthSeenIndex = i - 3;
                Integer value = maxDepthSeen.get(maxDepthSeenIndex);
                if (value != null)
                {
                    if (value >= childIndex)
                    {
                        messageProcessorDescriptor = messageProcessorDescriptor.getChild(childIndex);
                        continue;
                    }
                    else
                    {
                        maxDepthSeen.put(maxDepthSeenIndex, childIndex);
                    }
                }
                else
                {
                    maxDepthSeen.put(maxDepthSeenIndex, childIndex);
                }
                int identationSpaces = 0;
                for (int j = 0; j < i; j++)
                {
                    identationSpaces++;
                }
                final EventDebugLine eventDebugLine = new EventDebugLine();
                eventDebugLine.setEvent(muleEvent);

                LinkedList<EventDebugLine> eventDebugLinesForMessageId = stacks.get(uniqueId);
                if (eventDebugLinesForMessageId == null)
                {
                    eventDebugLinesForMessageId = new LinkedList<>();
                    stacks.put(uniqueId, new LinkedList<EventDebugLine>());
                }

                final EventDebugLine previousEventDebugLine;
                if (!eventDebugLinesForMessageId.isEmpty())
                {
                    previousEventDebugLine = eventDebugLinesForMessageId.getLast();
                    eventDebugLine.setPreviousEventDebugLine(previousEventDebugLine);
                }

                stacks.get(uniqueId).add(eventDebugLine);
                eventDebugLine.setIdentationSpces(identationSpaces);
                //TODO review why for sub-flow, or perhaps any flow-ref, the first index is 1
                //messageProcessorDescriptor = messageProcessorDescriptor.getChild(flowXmlDescriptor.isSubflow() ? childIndex - 1 : childIndex);
                //messageProcessorDescriptor = messageProcessorDescriptor.getChild(flowXmlDescriptor.isSubflow() && innerElement ? childIndex - 1 : childIndex);
                messageProcessorDescriptor = messageProcessorDescriptor.getChild(childIndex);
                eventDebugLine.setXml(messageProcessorDescriptor.getRepresentation());
                if (i == parts.length - 1)
                {
                    eventDebugLine.xmlContentIsMessageProcessor();
                }
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
        LinkedList<EventDebugLine> eventDebugLines = stacks.get(muleEvent.getMessage().getUniqueId());
        for (EventDebugLine eventDebugLine : eventDebugLines)
        {
            System.out.println(eventDebugLine);
        }
    }

    public void onPipelineNotificationStart(PipelineMessageNotification notification)
    {
        String flowName = notification.getResourceIdentifier();
        MuleEvent muleEvent = (MuleEvent) notification.getSource();
        stacks.put(muleEvent.getMessage().getUniqueId(), new LinkedList<EventDebugLine>());
        EventDebugLine eventDebugLine = new EventDebugLine();
        eventDebugLine.setEvent(muleEvent);
        eventDebugLine.setXml("\n<flow name=\"" + flowName + "\" />");
        stacks.get(muleEvent.getMessage().getUniqueId()).add(eventDebugLine);
    }
}
