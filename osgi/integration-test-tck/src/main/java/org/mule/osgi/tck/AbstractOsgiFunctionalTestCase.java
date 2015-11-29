/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.osgi.tck;

import static org.junit.Assert.fail;
import static org.ops4j.pax.exam.CoreOptions.bundle;
import static org.ops4j.pax.exam.CoreOptions.frameworkStartLevel;
import static org.ops4j.pax.exam.CoreOptions.junitBundles;
import static org.ops4j.pax.exam.CoreOptions.mavenBundle;
import static org.ops4j.pax.exam.CoreOptions.systemPackage;
import static org.ops4j.pax.exam.CoreOptions.systemProperty;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.inject.Inject;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.CoreOptions;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.ProbeBuilder;
import org.ops4j.pax.exam.TestProbeBuilder;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.options.DefaultCompositeOption;
import org.ops4j.pax.exam.options.MavenArtifactProvisionOption;
import org.ops4j.pax.exam.options.UrlProvisionOption;
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
public abstract class AbstractOsgiFunctionalTestCase
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

        return CoreOptions.options(
                systemPackage("sun.misc"),
                systemPackage("org.ops4j.pax.exam.junit"),
                systemPackage("org.ops4j.pax.exam.spi.reactors"),

                getStartupBundles(),

                mavenBundle("org.mule.osgi", "mule-osgi-felix-itest", "4.0-SNAPSHOT"),

                frameworkStartLevel(100),

                junitBundles(),
                systemProperty("pax.exam.logging").value("none")
        );
    }

    public static final String STARTUP_BUNDLES_FILE = "startupBundles.properties";

    private Option getStartupBundles()
    {
        final Properties properties = new Properties();
        //TODO(pablo.kraan): OSGi - this file must be read from the MULE folder
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(STARTUP_BUNDLES_FILE))
        {
            properties.load(inputStream);
        }
        catch (IOException e)
        {
            throw new IllegalStateException(String.format("Unable to open %s file", STARTUP_BUNDLES_FILE), e);
        }

        final List<Option> options = new ArrayList<>();
        for (Object bundleUrl : properties.keySet())
        {
            try
            {
                final int startLevel = Integer.parseInt(properties.getProperty((String) bundleUrl));
                final UrlProvisionOption option = bundle((String) bundleUrl).startLevel(startLevel);

                options.add(option);
            }
            catch (Exception e)
            {
                throw new IllegalStateException("Invalid bundle information format", e);
            }
        }

        return new DefaultCompositeOption(options.toArray(new Option[0]));
    }

    protected void assertStartedBundles()
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
