/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
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
    private Map<String, Map<Integer, Integer>> maxDepthSeenMap = new HashMap<>();

    private Map<String, MessageProcessStack> messageProcessStackMap = new HashMap();

    public MessageProcessingStackManager()
    {
        System.out.println("Creating MessageProcessingStackManager");
        messageProcessorTextDebugger = new MessageProcessorTextDebugger(this);
        pipelineProcessorDebugger = new FlowNotificationTextDebugger(this);

    }

    public MessageProcessStack getMessageProcessStack(MuleEvent muleEvent)
    {
        return messageProcessStackMap.get(muleEvent.getMessage().getUniqueId());
    }

    public void onMessageProcessorNotification(MessageProcessorNotification notification)
    {
        if (notification.getAction() == MessageProcessorNotification.MESSAGE_PROCESSOR_PRE_INVOKE)
        {
            return;
        }
        System.out.println("MessageProcessingStackManager -- Notification post invoke");
        MuleEvent muleEvent = notification.getSource();
        String uniqueId = muleEvent.getMessage().getUniqueId();

        if (!maxDepthSeenMap.containsKey(uniqueId))
        {
            maxDepthSeenMap.put(uniqueId, new HashMap<Integer, Integer>());
        }

        Map<Integer,Integer> maxDepthSeenPerMessage = maxDepthSeenMap.get(uniqueId);

        String processorPath = notification.getProcessorPath();
        System.out.println("MessageProcessingStackManager -- processor path: " + processorPath);
        String[] parts = processorPath.split("/");
        FlowXmlDescriptor flowXmlDescriptor = muleContext.getRegistry().get(parts[1] + "Descriptor");
        MessageProcessorDescriptor messageProcessorDescriptor = flowXmlDescriptor.getMessageProcessorDescriptor();

        MessageProcessStack messageProcessStack = messageProcessStackMap.get(uniqueId);

        System.out.println("MessageProcessingStackManager -- found previous processor path: " + messageProcessStack);


        try
        {
            ProcessorDebugLine lastProcessorDebugLine = messageProcessStack.getLastEventDebugLine();

            for (int i = 3; i < parts.length; i++)
            {
                String indexPart = parts[i];
                try
                {
                    Integer partIndex = Integer.valueOf(indexPart);
                    int maxDepthSeenIndex = i - 3;
                    Integer maxDepthSeen = maxDepthSeenPerMessage.get(maxDepthSeenIndex);
                    MessageProcessStack newMessageProcessorStack = null;
                    if (maxDepthSeen != null)
                    {
                        if (maxDepthSeen >= partIndex)
                        {
                            messageProcessorDescriptor = messageProcessorDescriptor.getChild(partIndex);
                            messageProcessStack = messageProcessStack.getChildMessageProcessorStack(partIndex);
                            continue;
                        }
                        else
                        {
                            newMessageProcessorStack = new MessageProcessStack();
                            newMessageProcessorStack.setProcessingPath(buildProcessingPath(processorPath, i));
                            messageProcessStack.addChildMessageProcessorStack(newMessageProcessorStack);
                            maxDepthSeenPerMessage.put(maxDepthSeenIndex, partIndex);
                        }
                    }
                    else
                    {
                        newMessageProcessorStack = new MessageProcessStack();
                        newMessageProcessorStack.setProcessingPath(buildProcessingPath(processorPath, i));
                        messageProcessStack.addChildMessageProcessorStack(newMessageProcessorStack);
                        maxDepthSeenPerMessage.put(maxDepthSeenIndex, partIndex);
                    }

                    final ProcessorDebugLine processorDebugLine = new ProcessorDebugLine();
                    processorDebugLine.setEvent(muleEvent);
                    newMessageProcessorStack.setProcessorDebugLine(processorDebugLine);
                    processorDebugLine.setPreviousProcessorDebugLine(lastProcessorDebugLine);
                    messageProcessorDescriptor = messageProcessorDescriptor.getChild(partIndex);
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
        catch (Exception e)
        {
            System.out.println("MessageProcessingStackManager -- excepiton process processorPath - " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        System.out.println("MessageProcessingStackManager -- successfully process processorPath");
    }

    private String buildProcessingPath(String processorPath, int currentEndIndex)
    {
        String[] parts = processorPath.split("/");
        StringBuilder processingPath = new StringBuilder();
        for (int i = 1; i < currentEndIndex + 1; i++)
        {
            processingPath.append("/" + parts[i]);
        }
        return processingPath.toString();
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
        //TODO - read but catch is not working
        //messageProcessStackMap.remove(muleEvent.getMessage().getUniqueId());
        System.out.println("MessageProcessingStackManager -- Pipeline complete - sending stack");
        System.out.println("MessageProcessingStackManager -- onPipelineNotificationComplete - children size: " + messageProcessStack.getChildrenMessageProcessorStack().size());
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
        messageProcessStack.setFlow(flowName);
        messageProcessStack.setProcessorDebugLine(processorDebugLine);
        messageProcessStackMap.put(muleEvent.getMessage().getUniqueId(), messageProcessStack);
    }
}
