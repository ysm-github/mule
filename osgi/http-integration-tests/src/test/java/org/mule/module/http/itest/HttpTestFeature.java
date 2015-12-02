/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.module.http.itest;

import org.mule.osgi.feature.model.BundleInfo;
import org.mule.osgi.feature.model.Dependency;
import org.mule.osgi.feature.model.FeatureInfo;

import java.util.ArrayList;
import java.util.List;

public class HttpTestFeature extends FeatureInfo
{

    public HttpTestFeature()
    {
        super("http-test", createDependencies());
    }

    private static List<Dependency> createDependencies()
    {
        final List<Dependency> dependencies = new ArrayList<>();

        dependencies.add(new BundleInfo("wrap:mvn:commons-codec/commons-codec/1.9/jar", 70));
        dependencies.add(new BundleInfo("wrap:mvn:org.apache.httpcomponents/httpcore/4.3.2/jar", 70));
        dependencies.add(new BundleInfo("wrap:mvn:org.apache.httpcomponents/httpclient/4.3.5/jar", 70));
        dependencies.add(new BundleInfo("wrap:mvn:org.apache.httpcomponents/fluent-hc/4.3.5/jar", 70));

        return dependencies;
    }
}
