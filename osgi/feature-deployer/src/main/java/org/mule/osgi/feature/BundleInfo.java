/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.osgi.feature;

public class BundleInfo implements Dependency
{

    private final String location;
    private final int startLevel;

    public BundleInfo(String location, int startLevel)
    {
        this.location = location;
        this.startLevel = startLevel;
    }

    @Override
    public String getLocation()
    {
        return location;
    }

    @Override
    public int getStartLevel()
    {
        return startLevel;
    }
}
