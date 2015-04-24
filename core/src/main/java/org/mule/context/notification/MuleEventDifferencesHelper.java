/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.context.notification;

import org.mule.api.MuleEvent;
import org.mule.api.MuleMessage;

import com.google.common.collect.MapDifference;
import com.google.common.collect.Maps;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class MuleEventDifferencesHelper
{

    public static Map<Object, Object> getMuleEventFlowVariables(MuleEvent muleEvent)
    {
        HashMap<Object, Object> flowVariables = new HashMap<>();
        Set<String> flowVariableNames = muleEvent.getFlowVariableNames();
        for (String flowVariableName : flowVariableNames)
        {
            flowVariables.put(flowVariableName, muleEvent.getFlowVariable(flowVariableName));
        }
        return flowVariables;
    }

    public static String showFlowVariablesDifferences(MuleEvent previousEvent, MuleEvent currentEvent)
    {
        Map<Object, Object> currentEventFlowVariables = getMuleEventFlowVariables(currentEvent);
        Map<Object, Object> previousEventFlowVariables = getMuleEventFlowVariables(previousEvent);
        return logMapDifferences("flow variables",currentEventFlowVariables, previousEventFlowVariables);
    }

    public static Map<Object, Object> getMuleEventSessionVariables(MuleEvent muleEvent)
    {
        HashMap<Object, Object> sessionVariables = new HashMap<>();
        Set<String> sessionVariableNames = muleEvent.getSessionVariableNames();
        for (String sessionVariableName : sessionVariableNames)
        {
            sessionVariables.put(sessionVariableName, muleEvent.getFlowVariable(sessionVariableName));
        }
        return sessionVariables;
    }

    public static String showSessionVariablesDifferences(MuleEvent previousEvent, MuleEvent currentEvent)
    {
        Map<Object, Object> currentEventFlowVariables = getMuleEventSessionVariables(currentEvent);
        Map<Object, Object> previousEventFlowVariables = getMuleEventSessionVariables(previousEvent);
        return logMapDifferences("session variables", currentEventFlowVariables, previousEventFlowVariables);
    }

    public static Map<Object, Object> getMuleMessagingOutboundProperties(MuleMessage muleMessage)
    {
        HashMap<Object, Object> outboundProperties = new HashMap<>();
        Set<String> outboundPropertyNames = muleMessage.getOutboundPropertyNames();
        for (String outboundPropertyName : outboundPropertyNames)
        {
            outboundProperties.put(outboundPropertyName, muleMessage.getOutboundProperty(outboundPropertyName));
        }
        return outboundProperties;
    }

    public static String showOutboundPropertiesDifferences(MuleMessage previousMessage, MuleMessage currentMessage)
    {
        Map<Object, Object> currentEventFlowVariables = getMuleMessagingOutboundProperties(currentMessage);
        Map<Object, Object> previousEventFlowVariables = getMuleMessagingOutboundProperties(previousMessage);
        return logMapDifferences("outbound properties", currentEventFlowVariables, previousEventFlowVariables);
    }

    public static Map<Object, Object> getMuleMessagingInboundProperties(MuleMessage muleMessage)
    {
        HashMap<Object, Object> inboundProperties = new HashMap<>();
        Set<String> inboundPropertyNames = muleMessage.getInboundPropertyNames();
        for (String outboundPropertyName : inboundPropertyNames)
        {
            inboundProperties.put(outboundPropertyName, muleMessage.getInboundProperty(outboundPropertyName));
        }
        return inboundProperties;
    }

    public static String showInboundPropertiesDifferences(MuleMessage previousMessage, MuleMessage currentMessage)
    {
        Map<Object, Object> currentEventFlowVariables = getMuleMessagingInboundProperties(currentMessage);
        Map<Object, Object> previousEventFlowVariables = getMuleMessagingInboundProperties(previousMessage);
        return logMapDifferences("inbound properties", currentEventFlowVariables, previousEventFlowVariables);
    }

    private static String logMapDifferences(String mapName, Map<Object, Object> currentEventFlowVariables, Map<Object, Object> previousEventFlowVariables)
    {
        StringBuilder differences = new StringBuilder();
        MapDifference<Object, Object> mapDifferences = Maps.difference(previousEventFlowVariables, currentEventFlowVariables);
        if (!mapDifferences.areEqual())
        {
            if (!mapDifferences.entriesOnlyOnLeft().isEmpty())
            {
                differences.append("removed(");
                for (Object key : mapDifferences.entriesOnlyOnLeft().keySet())
                {
                    differences.append(key);
                }
                differences.append(") ");
            }
            if (!mapDifferences.entriesOnlyOnRight().isEmpty())
            {
                differences.append("added(");
                for (Object key : mapDifferences.entriesOnlyOnRight().keySet())
                {
                    differences.append(key);
                }
                differences.append(") ");
            }
            if (!mapDifferences.entriesDiffering().isEmpty())
            {
                differences.append("differ(");
                for (Object key : mapDifferences.entriesDiffering().keySet())
                {
                    differences.append(key);
                }
                differences.append(") ");
            }
            return mapName + " changes " + differences;
        }
        return null;
    }

    public static String getDifferences(MuleEvent previousEvent, MuleMessage previousMessage, MuleEvent muleEvent, MuleMessage message)
    {
        StringBuilder stringBuilder = new StringBuilder();
        ArrayList<String> differences = new ArrayList<>();
        differences.add(showInboundPropertiesDifferences(previousMessage, message));
        differences.add(showOutboundPropertiesDifferences(previousMessage, message));
        differences.add(showFlowVariablesDifferences(previousEvent, muleEvent));
        differences.add(showSessionVariablesDifferences(previousEvent, muleEvent));
        for (String difference : differences)
        {
            if (difference != null)
            {
                stringBuilder.append(" : " + difference);
            }
        }
        if (stringBuilder.length() > 0)
        {
            return stringBuilder.toString();
        }
        return null;
    }

    public static String getDifferences(ProcessorDebugLine previousProcessorDebugLine, ProcessorDebugLine processorDebugLine)
    {
        StringBuilder stringBuilder = new StringBuilder();
        ArrayList<String> differences = new ArrayList<>();
        differences.add(logMapDifferences("inbound properties", previousProcessorDebugLine.getInboundProperties(), processorDebugLine.getInboundProperties()));
        differences.add(logMapDifferences("outbound properties", previousProcessorDebugLine.getOutboundProperties(), processorDebugLine.getOutboundProperties()));
        differences.add(logMapDifferences("flow variables", previousProcessorDebugLine.getFlowVariables(), processorDebugLine.getFlowVariables()));
        differences.add(logMapDifferences("session variables", previousProcessorDebugLine.getSessionVariables(), processorDebugLine.getSessionVariables()));
        differences.add(logPayloadDifferences(previousProcessorDebugLine.getPayload(), processorDebugLine.getPayload()));
        for (String difference : differences)
        {
            if (difference != null)
            {
                stringBuilder.append(" : " + difference);
            }
        }
        if (stringBuilder.length() > 0)
        {
            return stringBuilder.toString();
        }
        return null;
    }

    private static String logPayloadDifferences(Object previousPayload, Object currentPayload)
    {
        StringBuilder payloadChanges = new StringBuilder();
        if (previousPayload != currentPayload)
        {
            payloadChanges.append("payload changed(");
            if (previousPayload.getClass() != currentPayload.getClass())
            {
                payloadChanges.append(String.format("type(%s -> %s)", previousPayload.getClass(), currentPayload.getClass()));
            }
            payloadChanges.append(")");
        }
        if (payloadChanges.length() > 0)
        {
            return payloadChanges.toString();
        }
        return null;
    }
}
