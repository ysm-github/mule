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
