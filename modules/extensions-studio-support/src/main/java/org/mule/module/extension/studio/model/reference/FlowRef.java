/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.extension.studio.model.reference;

import org.mule.module.extension.studio.model.IEditorElementVisitor;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class FlowRef extends AbstractRef
{

    private Boolean supportFlow;

    @Override
    public String toString()
    {
        return "FlowRef [getName()=" + getName() + ", getCaption()=" + getCaption() + "]";
    }

    @Override
    public void accept(IEditorElementVisitor visitor)
    {
        visitor.visit(this);
    }

    @XmlAttribute
    public Boolean getSupportFlow()
    {
        return supportFlow;
    }

    public void setSupportFlow(Boolean supportFlow)
    {
        this.supportFlow = supportFlow;
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((supportFlow == null) ? 0 : supportFlow.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
        {
            return true;
        }
        if (!super.equals(obj))
        {
            return false;
        }
        if (getClass() != obj.getClass())
        {
            return false;
        }
        FlowRef other = (FlowRef) obj;
        if (supportFlow == null)
        {
            if (other.supportFlow != null)
            {
                return false;
            }
        }
        else if (!supportFlow.equals(other.supportFlow))
        {
            return false;
        }
        return true;
    }
}
