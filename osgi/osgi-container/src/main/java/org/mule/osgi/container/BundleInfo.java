/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.osgi.container;

public class BundleInfo
{

    //TODO(pablo.kraan): OSGi -remove this class duplication
    private final String location;
    private final int startLevel;

    public BundleInfo(String location, int startLevel)
    {
        this.location = location;
        this.startLevel = startLevel;
    }

    public String getLocation()
    {
        return location;
    }

    public int getStartLevel()
    {
        return startLevel;
    }

    @Override
    public String toString()
    {
        return "BundleInfo{ " + location + ", startLevel= " + startLevel + " }";
    }
}
