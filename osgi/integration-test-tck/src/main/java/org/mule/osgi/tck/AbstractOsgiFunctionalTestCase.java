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
import static org.ops4j.pax.exam.CoreOptions.streamBundle;
import static org.ops4j.pax.exam.CoreOptions.systemPackage;
import org.mule.tck.junit4.FunctionalTestCase;
import org.mule.util.FileUtils;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.inject.Inject;

import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.CoreOptions;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.ProbeBuilder;
import org.ops4j.pax.exam.TestProbeBuilder;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.options.DefaultCompositeOption;
import org.ops4j.pax.exam.options.UrlProvisionOption;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerSuite;
import org.ops4j.pax.tinybundles.core.TinyBundle;
import org.ops4j.pax.tinybundles.core.TinyBundles;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;

/**
 * Defines a base class for VM transport tests that run inside an OSGi container
 */
@RunWith(PaxExam.class)
@ExamReactorStrategy(PerSuite.class)
public abstract class AbstractOsgiFunctionalTestCase extends FunctionalTestCase
{

    public static final String STARTUP_BUNDLES_FILE = "startupBundles.properties";

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

                //TODO(pablo.kraan): OSGi - need to use this dependency instead of the original from mockito (maybe we can update mockito) or use the new version (1.4)
                mavenBundle().groupId("org.objenesis").artifactId("objenesis").version("1.4"),
                mavenBundle().groupId("org.mockito").artifactId("mockito-core").version("1.9.0"),
                mavenBundle("org.mule.tests", "mule-tests-unit", "4.0-SNAPSHOT").startLevel(90),
                mavenBundle("org.mule.osgi", "mule-osgi-felix-itest", "4.0-SNAPSHOT").startLevel(90),

                streamBundle(createTestFeature()).startLevel(70),

                junitBundles(),

                frameworkStartLevel(100)
        );
    }

    private InputStream createTestFeature()
    {
        List<Class> features = getTestFeatures();

        final TinyBundle bundleBuilder = TinyBundles.bundle()
                .set(Constants.BUNDLE_SYMBOLICNAME, "testFeatures");
        bundleBuilder.set( Constants.DYNAMICIMPORT_PACKAGE, "*" );

        StringBuilder featureClassNames = new StringBuilder();
        for (Class feature : features)
        {
            bundleBuilder.add(feature);

            featureClassNames.append(feature.getName()).append("\n");
        }

        bundleBuilder.add("META-INF/features.properties", new ByteArrayInputStream(featureClassNames.toString().getBytes()));

        return bundleBuilder.build();
    }

    protected List<Class> getTestFeatures()
    {
        return new ArrayList<>();
    }

    private Option getStartupBundles()
    {
        final Properties properties = new Properties();
        //TODO(pablo.kraan): OSGi - this file must be read from the MULE folder
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(STARTUP_BUNDLES_FILE))
        {
            if (inputStream != null)
            {
                properties.load(inputStream);
            }
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
