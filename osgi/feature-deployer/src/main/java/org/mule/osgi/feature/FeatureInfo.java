/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.osgi.feature;

import java.util.List;

public class FeatureInfo

{

    private final String name;
    private final List<Dependency> dependencies;

    public FeatureInfo(String name, List<Dependency> dependencies)
    {
        this.name = name;
        this.dependencies = dependencies;
    }

    public String getName()
    {
        return name;
    }

    public List<Dependency> getDependencies()
    {
        return dependencies;
    }
}
