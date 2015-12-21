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

        dependencies.add(new BundleInfo("mvn:org.eclipse.jetty/jetty-util/9.0.7.v20131107/jar", 70));
        dependencies.add(new BundleInfo("mvn:org.eclipse.jetty/jetty-io/9.0.7.v20131107/jar", 70));
        dependencies.add(new BundleInfo("mvn:org.eclipse.jetty/jetty-http/9.0.7.v20131107/jar", 70));
        dependencies.add(new BundleInfo("mvn:org.eclipse.jetty/jetty-server/9.0.7.v20131107/jar", 70));

        dependencies.add(new BundleInfo("mvn:org.eclipse.jetty/jetty-security/9.0.7.v20131107/jar", 70));
        dependencies.add(new BundleInfo("mvn:org.eclipse.jetty/jetty-servlet/9.0.7.v20131107/jar", 70));


        dependencies.add(new BundleInfo("mvn:commons-dbutils/commons-dbutils/1.2/jar", 70));
        dependencies.add(new BundleInfo("mvn:commons-net/commons-net/2.2/jar", 70));
        dependencies.add(new BundleInfo("mvn:org.apache.mina/mina-core/2.0.4/jar", 70));
        dependencies.add(new BundleInfo("mvn:org.apache.ftpserver/ftplet-api/1.0.6/jar", 70));
        dependencies.add(new BundleInfo("mvn:org.apache.ftpserver/ftpserver-core/1.0.6/jar", 70));
        dependencies.add(new BundleInfo("mvn:org.apache.sshd/sshd-core/0.6.0/jar", 70));
        dependencies.add(new BundleInfo("mvn:org.bouncycastle/bcprov-jdk15on/1.50/jar", 70));


        dependencies.add(new BundleInfo("mvn:org.mule.tests/mule-tests-unit/4.0-SNAPSHOT/jar", 75));
        dependencies.add(new BundleInfo("mvn:org.mule.tests/mule-tests-functional/4.0-SNAPSHOT/jar", 73));


        dependencies.add(new BundleInfo("mvn:org.ops4j.base/ops4j-base-monitors/1.5.0/jar", 75));
        dependencies.add(new BundleInfo("mvn:org.ops4j.base/ops4j-base-lang/1.5.0/jar", 75));
        dependencies.add(new BundleInfo("mvn:org.ops4j.base/ops4j-base-io/1.5.0/jar", 75));
        dependencies.add(new BundleInfo("mvn:org.ops4j.base/ops4j-base-store/1.5.0/jar", 75));

        dependencies.add(new BundleInfo("mvn:biz.aQute.bnd/bndlib/2.4.0/jar", 75));
        dependencies.add(new BundleInfo("mvn:org.ops4j.pax.tinybundles/tinybundles/2.1.1/jar", 75));
        dependencies.add(new BundleInfo("mvn:org.mule.osgi/mule-osgi-itest-tck/4.0-SNAPSHOT/jar", 75));

        return dependencies;
    }
}
