/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
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
