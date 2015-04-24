/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.context.notification;

import org.mule.api.MuleEvent;

import java.util.HashMap;
import java.util.Map;

public class ProcessorDebugLine
{

    private Map<Object,Object> flowVariables = new HashMap<>();
    private Map<Object,Object> sessionVariables = new HashMap<>();
    private Map<Object,Object> inboundProperties = new HashMap<>();
    private Map<Object,Object> outboundProperties = new HashMap<>();

    private String messageId;

    private String xml;

    private ProcessorDebugLine previousProcessorDebugLine;
    private Object payload;
    private boolean xmlContentIsMessageProcessor;

    public String getMessageId()
    {
        return messageId;
    }

    public void setXml(String xml)
    {
        this.xml = xml;
    }

    public String getXml()
    {
        return xml;
    }

    public void setEvent(MuleEvent muleEvent)
    {
        messageId = muleEvent.getMessage().getUniqueId();
        this.flowVariables = MuleEventDifferencesHelper.getMuleEventFlowVariables(muleEvent);
        this.sessionVariables = MuleEventDifferencesHelper.getMuleEventSessionVariables(muleEvent);
        this.inboundProperties = MuleEventDifferencesHelper.getMuleMessagingInboundProperties(muleEvent.getMessage());
        this.outboundProperties = MuleEventDifferencesHelper.getMuleMessagingOutboundProperties(muleEvent.getMessage());
        this.payload = muleEvent.getMessage().getPayload();
    }

    public void setPreviousProcessorDebugLine(ProcessorDebugLine previousProcessorDebugLine)
    {
        this.previousProcessorDebugLine = previousProcessorDebugLine;
    }

    public String getDifferences()
    {
        if (previousProcessorDebugLine != null)
        {
            return MuleEventDifferencesHelper.getDifferences(previousProcessorDebugLine, this);
        }
        return null;
    }

    public Map<Object, Object> getFlowVariables()
    {
        return flowVariables;
    }

    public Map<Object, Object> getSessionVariables()
    {
        return sessionVariables;
    }

    public Map<Object, Object> getInboundProperties()
    {
        return inboundProperties;
    }

    public Map<Object, Object> getOutboundProperties()
    {
        return outboundProperties;
    }

    public ProcessorDebugLine getPreviousProcessorDebugLine()
    {
        return previousProcessorDebugLine;
    }

    public Object getPayload()
    {
        return payload;
    }

    @Override
    public String toString()
    {
        StringBuilder debugLine = new StringBuilder();
        debugLine.append(xml);
        String differences = getDifferences();
        if (differences != null)
        {
            while (debugLine.length() < 79)
            {
                debugLine.append(" ");
            }
            debugLine.append(differences);
        }
        return debugLine.toString();
    }

    public void xmlContentIsMessageProcessor()
    {
        this.xmlContentIsMessageProcessor = true;
    }
}
