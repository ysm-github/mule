/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.construct;

import java.util.LinkedList;
import java.util.List;

public class MessageProcessorDescriptor
{

    private String xmlRepresentation;
    private List<MessageProcessorDescriptor> childMessageProcessors = new LinkedList<>();

    public MessageProcessorDescriptor(String xmlRepresentation)
    {
        this.xmlRepresentation = xmlRepresentation;
    }

    public MessageProcessorDescriptor(String xmlRepresentation, List<MessageProcessorDescriptor> childMessageProcessors)
    {
        this.xmlRepresentation = xmlRepresentation;
        this.childMessageProcessors = childMessageProcessors;
    }

    public String getRepresentation()
    {
        return this.xmlRepresentation;
    }

    boolean hasChilds()
    {
        return !childMessageProcessors.isEmpty();
    }

    public MessageProcessorDescriptor getChild(int index)
    {
        try
        {
            return childMessageProcessors.get(index);
        }
        catch (IndexOutOfBoundsException e)
        {
            //TODO add logging
            //Fixes the cases like Transactional where first element index in path is 1 instead of 0
            return childMessageProcessors.get(index - 1);
        }
    }

    public void addChild(MessageProcessorDescriptor messageProcessorDescriptor)
    {
        this.childMessageProcessors.add(messageProcessorDescriptor);
    }

}
