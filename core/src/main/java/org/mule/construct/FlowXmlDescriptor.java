package org.mule.construct;

import java.util.List;

/**
 *
 */
public class FlowXmlDescriptor
{

    private boolean isSubflow;

    public FlowXmlDescriptor(List<MessageProcessorDescriptor> content)
    {
        this.content = content;
    }

    private List<MessageProcessorDescriptor> content;

    public List<MessageProcessorDescriptor> getContent()
    {
        return content;
    }

    public MessageProcessorDescriptor getMessageProcessorDescriptor()
    {
        return new MessageProcessorDescriptor("", content);
    }

    public void setIsSubflow(boolean isSubflow)
    {
        this.isSubflow = isSubflow;
    }

    public boolean isSubflow()
    {
        return isSubflow;
    }
}
