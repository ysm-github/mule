package org.mule.config.spring;

import org.mule.api.MuleEvent;

import java.util.HashMap;
import java.util.Map;

public class EventDebugLine
{

    private Map<Object,Object> flowVariables = new HashMap<>();
    private Map<Object,Object> sessionVariables = new HashMap<>();
    private Map<Object,Object> inboundProperties = new HashMap<>();
    private Map<Object,Object> outboundProperties = new HashMap<>();

    private String messageId;

    private String xml;

    private EventDebugLine previousEventDebugLine;
    private Object payload;
    private int identationSpces;
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

    public void setPreviousEventDebugLine(EventDebugLine previousEventDebugLine)
    {
        this.previousEventDebugLine = previousEventDebugLine;
    }

    public String getDifferences()
    {
        if (previousEventDebugLine != null)
        {
            return MuleEventDifferencesHelper.getDifferences(previousEventDebugLine, this);
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

    public EventDebugLine getPreviousEventDebugLine()
    {
        return previousEventDebugLine;
    }

    public Object getPayload()
    {
        return payload;
    }

    public void setIdentationSpces(int identationSpces)
    {
        this.identationSpces = identationSpces;
    }

    @Override
    public String toString()
    {
        StringBuilder debugLine = new StringBuilder();
        for (int i = 0; i < identationSpces; i++)
        {
            debugLine.append(" ");
        }
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
