/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.osgi;

import static org.junit.Assert.fail;
import static org.ops4j.pax.exam.CoreOptions.frameworkStartLevel;
import static org.ops4j.pax.exam.CoreOptions.junitBundles;
import static org.ops4j.pax.exam.CoreOptions.mavenBundle;
import static org.ops4j.pax.exam.CoreOptions.options;
import static org.ops4j.pax.exam.CoreOptions.systemPackage;

import javax.inject.Inject;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.ProbeBuilder;
import org.ops4j.pax.exam.TestProbeBuilder;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerSuite;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;

/**
 * Defines a base class for VM transport tests that run inside an OSGi container
 */
@RunWith(PaxExam.class)
@ExamReactorStrategy(PerSuite.class)
public class MuleOsgiTestCase
{

    @Inject
    public BundleContext bundleContext;

    @ProbeBuilder
    public TestProbeBuilder build(TestProbeBuilder builder)
    {
        builder.setHeader(Constants.BUNDLE_NAME, this.getClass().getSimpleName() + System.identityHashCode(this));

        return builder;
    }

    @Configuration
    public Option[] config()
    {
        System.out.println("Configuring test...");
        return options(
                systemPackage("sun.misc"),
                //systemProperty("log4j.configurationFile")
                //        .value("file:///Users/pablokraan/devel/workspaces/muleFull-4.x2/mule/osgi/integration-tests/src/test/resources/log4j2.xml"),
                //.value("file:" + PathUtils.getBaseDir() + "/src/test/resources/log4j.xml"),

                mavenBundle().groupId("com.lmax").artifactId("disruptor").version("3.3.0").startLevel(2),
                mavenBundle().groupId("org.apache.logging.log4j").artifactId("log4j-api").version("2.2").startLevel(2),
                mavenBundle().groupId("org.apache.logging.log4j").artifactId("log4j-core").version("2.2").startLevel(2),
                mavenBundle().groupId("org.apache.logging.log4j").artifactId("log4j-1.2-api").version("2.2").startLevel(2),
                mavenBundle().groupId("org.apache.logging.log4j").artifactId("log4j-slf4j-impl").version("2.2").startLevel(2),
                mavenBundle().groupId("org.slf4j").artifactId("jcl-over-slf4j").version("1.7.7").startLevel(2),
                mavenBundle().groupId("org.slf4j").artifactId("slf4j-api").version("1.7.7").startLevel(2),
                mavenBundle().groupId("commons-logging").artifactId("commons-logging").version("1.2").startLevel(2),
                mavenBundle().groupId("org.apache.logging.log4j").artifactId("log4j-jcl").version("2.2").startLevel(2),
                mavenBundle().groupId("org.apache.logging.log4j").artifactId("log4j-jul").version("2.2").startLevel(2),

                mavenBundle().groupId("org.ops4j.pax.url").artifactId("pax-url-aether").version("2.4.1").startLevel(3),
                mavenBundle().groupId("org.ops4j.pax.url").artifactId("pax-url-wrap").version("2.4.1").classifier("uber").startLevel(3),

                mavenBundle().groupId("org.mule.osgi").artifactId("mule-osgi-feature-deployer").version("4.0-SNAPSHOT").startLevel(4),
                frameworkStartLevel(100),
                junitBundles()
        );
    }

    @Test
    public void startsContainer() throws Exception
    {
        StringBuilder builder = new StringBuilder("Bundle status:\n");
        boolean failure = false;
        for (Bundle bundle : bundleContext.getBundles())
        {
            final boolean isFragment = isFragment(bundle);
            if (isFragment && bundle.getState() != Bundle.RESOLVED || !isFragment && bundle.getState() != Bundle.ACTIVE)
            {
                failure = true;
            }
            builder.append(bundle.getBundleId() + " ");
            builder.append(isFragment ? "Fragment" : "Bundle");
            builder.append(" - " + getBundleState(bundle.getState()) + " - " + bundle.getBundleId() + " - " + bundle.getSymbolicName() + " - " + bundle.getVersion() + "\n");
        }

        System.out.println(builder.toString());

        if (failure)
        {
            fail("There is at least a non active bundle");
        }
    }

    private static boolean isFragment(Bundle bundle)
    {
        return bundle.getHeaders().get(Constants.FRAGMENT_HOST) != null;
    }

    private static String getBundleState(int state)
    {
        switch (state)
        {
            case Bundle.INSTALLED:
                return "INSTALLED";
            case Bundle.RESOLVED:
                return "RESOLVED";
            case Bundle.ACTIVE:
                return "ACTIVE";
            case Bundle.UNINSTALLED:
                return "UNINSTALLED";
            case Bundle.STARTING:
                return "STARTING";
            case Bundle.STOPPING:
                return "STOPPING";
            default:
                throw new IllegalStateException("Unknown bundle state: " + state);
        }
    }
}
