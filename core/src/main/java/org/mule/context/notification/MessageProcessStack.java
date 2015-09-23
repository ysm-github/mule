/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.context.notification;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Represents a message processor execution including all the child message processors
 * in case the message processor is an scope
 */
public class MessageProcessStack implements Serializable
{

    private Calendar initialProcessingTime = Calendar.getInstance();
    private ProcessorDebugLine elementDebugLine;
    private List<MessageProcessStack> childrenMessageProcessorStack = new ArrayList<>();
    private Calendar finishProcessingTime;
    private String flow;
    private String processingPath;

    public void print(int ident)
    {
        printIndentationSpaces(ident);
        System.out.println(elementDebugLine);
        for (MessageProcessStack messageProcessStack : childrenMessageProcessorStack)
        {
            messageProcessStack.print(ident + 1);
        }
        if (!childrenMessageProcessorStack.isEmpty())
        {
            printIndentationSpaces(ident);
            System.out.println(elementDebugLine);
        }
    }

    private void printIndentationSpaces(int ident)
    {
        for (int i = 0; i < ident; i++)
        {
            System.out.print(" ");
        }
    }

    public ProcessorDebugLine getLastEventDebugLine()
    {
        if (childrenMessageProcessorStack.isEmpty())
        {
            return elementDebugLine;
        }
        return childrenMessageProcessorStack.get(childrenMessageProcessorStack.size() - 1).getLastEventDebugLine();
    }

    public void setProcessorDebugLine(ProcessorDebugLine elementDebugLine)
    {
        this.elementDebugLine = elementDebugLine;
    }

    public void addChildMessageProcessorStack(MessageProcessStack messageProcessStack)
    {
        this.childrenMessageProcessorStack.add(messageProcessStack);
    }

    public MessageProcessStack getChildMessageProcessorStack(Integer childIndex)
    {
        return childrenMessageProcessorStack.get(childIndex);
    }

    public List<MessageProcessStack> getChildrenMessageProcessorStack()
    {
        return childrenMessageProcessorStack;
    }

    public void finishProcessing()
    {
        this.finishProcessingTime = Calendar.getInstance();
    }

    public Calendar getInitialProcessingTime()
    {
        return initialProcessingTime;
    }

    public Calendar getFinishProcessingTime()
    {
        return finishProcessingTime;
    }

    public ProcessorDebugLine getElementDebugLine()
    {
        return elementDebugLine;
    }

    public String getFlow()
    {
        return flow;
    }

    public void setFlow(String flow)
    {
        this.flow = flow;
    }

    public String getProcessingPath()
    {
        return processingPath;
    }

    public void setProcessingPath(String processingPath)
    {
        this.processingPath = processingPath;
    }
}
