/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.extension.studio.model.element;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import org.mule.module.extension.studio.model.IEditorElementVisitor;

@XmlRootElement(name = "element-controller-listOfMap")
public class ElementControllerListOfMap extends AbstractElementController
{

    private String listName;
    private String innerName;

    @Override
    public void accept(IEditorElementVisitor visitor)
    {
        visitor.visit(this);
    }

    @XmlAttribute
    public String getListName()
    {
        return listName;
    }

    public void setListName(String listName)
    {
        this.listName = listName;
    }

    @XmlAttribute
    public String getInnerName()
    {
        return innerName;
    }

    public void setInnerName(String innerName)
    {
        this.innerName = innerName;
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((innerName == null) ? 0 : innerName.hashCode());
        result = prime * result + ((listName == null) ? 0 : listName.hashCode());
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
        ElementControllerListOfMap other = (ElementControllerListOfMap) obj;
        if (innerName == null)
        {
            if (other.innerName != null)
            {
                return false;
            }
        }
        else if (!innerName.equals(other.innerName))
        {
            return false;
        }
        if (listName == null)
        {
            if (other.listName != null)
            {
                return false;
            }
        }
        else if (!listName.equals(other.listName))
        {
            return false;
        }
        return true;
    }
}
