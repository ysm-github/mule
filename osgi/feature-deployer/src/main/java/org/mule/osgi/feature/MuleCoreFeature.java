/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.osgi.feature;

import java.util.ArrayList;
import java.util.List;

public class MuleCoreFeature extends FeatureInfo
{

    public MuleCoreFeature()
    {
        super("mule-core", createDependencies());
    }

    private static List<Dependency> createDependencies()
    {
        final List<Dependency> dependencies = new ArrayList<>();

        //TODO(pablo.kraan): OSGi - should use a default start level if no one is configured
        //mule-logging

        //mule-common
        dependencies.add(new BundleInfo("wrap:mvn:org.apache.servicemix.bundles/org.apache.servicemix.bundles.xmlbeans/2.4.0_5/jar", 30));
        dependencies.add(new BundleInfo("mvn:com.fasterxml.jackson.core/jackson-core/2.4.3/jar", 30));
        dependencies.add(new BundleInfo("mvn:com.fasterxml.jackson.core/jackson-annotations/2.4.3/jar", 30));
        dependencies.add(new BundleInfo("mvn:com.fasterxml.jackson.core/jackson-databind/2.4.3/jar", 30));
        dependencies.add(new BundleInfo("wrap:mvn:org.antlr/antlr-runtime/3.5/jar", 30));
        dependencies.add(new BundleInfo("wrap:mvn:org.antlr/stringtemplate/3.2.1/jar", 30));
        dependencies.add(new BundleInfo("wrap:mvn:commons-io/commons-io/2.4/jar", 30));
        dependencies.add(new BundleInfo("wrap:mvn:org.json/json/20140107/jar", 30));
        dependencies.add(new BundleInfo("wrap:mvn:org.mule.common/mule-common/4.0-SNAPSHOT/jar", 30));


        dependencies.add(new BundleInfo("mvn:org.apache.servicemix.bundles/org.apache.servicemix.bundles.javax-inject/1_2/jar", 30));
        dependencies.add(new BundleInfo("mvn:org.apache.geronimo.specs/geronimo-jta_1.1_spec/1.1.1/jar", 30));
        dependencies.add(new BundleInfo("mvn:org.apache.geronimo.specs/geronimo-jta_1.1_spec/1.1.1/jar", 30));
        dependencies.add(new BundleInfo("mvn:org.apache.geronimo.specs/geronimo-j2ee-connector_1.5_spec/2.0.0/jar", 30));
        dependencies.add(new BundleInfo("wrap:mvn:com.github.stephenc.eaio-uuid/uuid/3.4.0-osgi/jar", 30));
        dependencies.add(new BundleInfo("mvn:com.google.guava/guava/18.0/jar", 30));
        dependencies.add(new BundleInfo("wrap:mvn:commons-collections/commons-collections/3.2.1/jar", 30));
        dependencies.add(new BundleInfo("wrap:mvn:commons-beanutils/commons-beanutils/1.8.0/jar", 30));
        dependencies.add(new BundleInfo("wrap:mvn:commons-cli/commons-cli/1.2/jar", 30));
        dependencies.add(new BundleInfo("wrap:mvn:commons-lang/commons-lang/2.4/jar", 30));
        dependencies.add(new BundleInfo("wrap:mvn:commons-pool/commons-pool/1.6/jar", 30));
        dependencies.add(new BundleInfo("wrap:mvn:org.jgrapht/jgrapht-jdk1.5/0.7.3/jar", 30));
        dependencies.add(new BundleInfo("wrap:mvn:org.mule/mule-api/1.0.0-SNAPSHOT/jar", 30));
        dependencies.add(new BundleInfo("wrap:mvn:org.mule.extensions/mule-extensions-api/1.0.0-SNAPSHOT/jar", 30));
        dependencies.add(new BundleInfo("wrap:mvn:org.reflections/reflections/0.9.9/jar", 30));
        dependencies.add(new BundleInfo("wrap:mvn:org.mule.mvel/mule-mvel2/2.1.9-MULE-007/jar", 30));
        dependencies.add(new BundleInfo("wrap:mvn:commons-beanutils/commons-beanutils/1.9.2/jar", 30));
        //dependencies.add(new BundleInfo("mvn:org.apache.geronimo.specs/geronimo-jms_1.1_spec/1.1.1/jar", 30));
        //dependencies.add(new BundleInfo("mvn:org.apache.geronimo.specs/geronimo-j2ee-connector_1.5_spec/2.0.0/jar", 30));
        dependencies.add(new BundleInfo("mvn:org.mule/mule-core/4.0-SNAPSHOT/jar", 30));

        // Spring Support
        //TODO(pablo.kraan): OSGi - maybe this dependency should go inside the container
        dependencies.add(new BundleInfo("mvn:org.apache.felix/org.apache.felix.fileinstall/3.4.2", 11));

        dependencies.add(new BundleInfo("mvn:org.apache.servicemix.bundles/org.apache.servicemix.bundles.aopalliance/1.0_6", 30));
        dependencies.add(new BundleInfo("mvn:org.apache.servicemix.bundles/org.apache.servicemix.bundles.spring-core/4.1.2.RELEASE_1", 30));
        dependencies.add(new BundleInfo("mvn:org.apache.servicemix.bundles/org.apache.servicemix.bundles.spring-expression/4.1.2.RELEASE_1", 30));
        dependencies.add(new BundleInfo("mvn:org.apache.servicemix.bundles/org.apache.servicemix.bundles.spring-beans/4.1.2.RELEASE_1", 30));
        dependencies.add(new BundleInfo("mvn:org.apache.servicemix.bundles/org.apache.servicemix.bundles.spring-aop/4.1.2.RELEASE_1", 30));
        dependencies.add(new BundleInfo("mvn:org.apache.servicemix.bundles/org.apache.servicemix.bundles.spring-context/4.1.2.RELEASE_1", 30));
        dependencies.add(new BundleInfo("mvn:org.apache.servicemix.bundles/org.apache.servicemix.bundles.spring-context-support/4.1.2.RELEASE_1", 30));

        // Spring Config
        //<feature>mule-core</feature>
        //<feature>spring</feature>
        dependencies.add(new BundleInfo("mvn:org.eclipse.gemini.blueprint/gemini-blueprint-io/2.0.0.BUILD-SNAPSHOT/jar", 30));
        dependencies.add(new BundleInfo("mvn:org.eclipse.gemini.blueprint/gemini-blueprint-core/2.0.0.BUILD-SNAPSHOT/jar", 30));
        dependencies.add(new BundleInfo("mvn:org.eclipse.gemini.blueprint/gemini-blueprint-extender/2.0.0.BUILD-SNAPSHOT/jar", 30));
        dependencies.add(new BundleInfo("wrap:mvn:dom4j/dom4j/1.6.1/jar", 30));
        dependencies.add(new BundleInfo("mvn:org.apache.servicemix.bundles/org.apache.servicemix.bundles.cglib/2.2_2/jar", 30));
        dependencies.add(new BundleInfo("mvn:org.mule.modules/mule-module-spring-config/4.0-SNAPSHOT/jar", 30));

        //TODO(pablo.kraan): OSGi - need to find a better place for this dependency
        dependencies.add(new BundleInfo("mvn:org.mule.osgi/mule-osgi-support/4.0-SNAPSHOT/jar", 30));

        // Extensions API
        //<feature>mule-core</feature>
        //<feature>mule-spring-config</feature>
        dependencies.add(new BundleInfo("wrap:mvn:org.mule.extensions/mule-extensions-annotations/1.0.0-SNAPSHOT/jar", 30));
        dependencies.add(new BundleInfo("mvn:org.mule.modules/mule-module-extensions-support/4.0-SNAPSHOT/jar", 30));
        dependencies.add(new BundleInfo("mvn:org.mule.modules/mule-module-extensions-spring-support/4.0-SNAPSHOT/jar", 30));

        // Extension Validation
        //<feature>mule-extension-api</feature>
        dependencies.add(new BundleInfo("wrap:mvn:commons-digester/commons-digester/1.8/jar", 30));
        dependencies.add(new BundleInfo("mvn:commons-validator/commons-validator/1.4.0/jar", 30));
        dependencies.add(new BundleInfo("mvn:org.mule.modules/mule-module-validation/4.0-SNAPSHOT/jar", 30));

        // HTTP Connector
        //<feature>mule-core</feature>
        //<feature>mule-spring-config</feature>
        dependencies.add(new BundleInfo("wrap:mvn:org.mule.extensions/mule-extensions-annotations/1.0.0-SNAPSHOT/jar", 30));
        dependencies.add(new BundleInfo("mvn:org.mule.modules/mule-module-extensions-support/4.0-SNAPSHOT/jar", 30));
        dependencies.add(new BundleInfo("mvn:org.mule.modules/mule-module-extensions-spring-support/4.0-SNAPSHOT/jar", 30));

        // Extension Validation
        //<feature>mule-extension-api</feature>
        dependencies.add(new BundleInfo("wrap:mvn:commons-digester/commons-digester/1.8/jar", 30));
        dependencies.add(new BundleInfo("mvn:commons-validator/commons-validator/1.4.0/jar", 30));
        dependencies.add(new BundleInfo("mvn:org.mule.modules/mule-module-validation/4.0-SNAPSHOT/jar", 30));

        // HTTP Connector
        //<feature>mule-core</feature>
        //<feature>mule-spring-config</feature>

        // Grizzly
        dependencies.add(new BundleInfo("wrap:mvn:javax.servlet/javax.servlet-api/3.1.0/jar", 30));
        dependencies.add(new BundleInfo("mvn:org.glassfish.grizzly/grizzly-framework/2.3.21/jar", 30));
        dependencies.add(new BundleInfo("mvn:org.glassfish.grizzly/grizzly-http/2.3.21/jar", 30));
        dependencies.add(new BundleInfo("mvn:org.glassfish.grizzly/connection-pool/2.3.21/jar", 30));
        dependencies.add(new BundleInfo("mvn:org.glassfish.grizzly/grizzly-http-server/2.3.21/jar", 30));
        dependencies.add(new BundleInfo("mvn:org.glassfish.grizzly/grizzly-http-servlet/2.3.21/jar", 30));
        dependencies.add(new BundleInfo("mvn:org.glassfish.grizzly/grizzly-websockets/2.3.21/jar", 30));

        // Transport dependencies
        dependencies.add(new BundleInfo("wrap:mvn:org.samba.jcifs/jcifs/1.3.3/jar", 30));
        dependencies.add(new BundleInfo("wrap:mvn:commons-codec/commons-codec/1.9/jar", 30));
        dependencies.add(new BundleInfo("wrap:mvn:commons-httpclient/commons-httpclient/3.1/jar", 30));
        dependencies.add(new BundleInfo("wrap:mvn:org.apache.tomcat/coyote/6.0.44/jar", 30));
        dependencies.add(new BundleInfo("wrap:mvn:joda-time/joda-time/2.5/jar", 30));
        dependencies.add(new BundleInfo("wrap:mvn:org.mule.transports/mule-transport-ssl/4.0-SNAPSHOT/jar", 30));
        dependencies.add(new BundleInfo("wrap:mvn:org.mule.transports/mule-transport-tcp/4.0-SNAPSHOT/jar", 30));
        dependencies.add(new BundleInfo("wrap:mvn:com.ning/async-http-client/1.9.31/jar", 30));
        dependencies.add(new BundleInfo("wrap:mvn:javax.mail/mail/1.4.3/jar", 30));
        dependencies.add(new BundleInfo("mvn:org.mule.modules/mule-module-http/4.0-SNAPSHOT/jar", 30));
        dependencies.add(new BundleInfo("mvn:org.mule.transports/mule-transport-http/4.0-SNAPSHOT/jar", 30));

        // App support
        //TODO(pablo.kraan): OSGi - this has to be part of the deployer feature
        dependencies.add(new BundleInfo("mvn:org.mule.osgi/mule-osgi-app-support/4.0-SNAPSHOT/jar", 30));

        // Sample Mule Application
        dependencies.add(new BundleInfo("mvn:org.mule.osgi/mule-osgi-sample-app/4.0-SNAPSHOT/jar", 30));

        return dependencies;
    }
}
