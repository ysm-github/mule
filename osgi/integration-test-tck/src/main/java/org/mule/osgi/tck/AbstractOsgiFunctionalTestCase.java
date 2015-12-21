/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.osgi.tck;

import static org.junit.Assert.fail;
import static org.ops4j.pax.exam.CoreOptions.bootDelegationPackage;
import static org.ops4j.pax.exam.CoreOptions.bundle;
import static org.ops4j.pax.exam.CoreOptions.frameworkStartLevel;
import static org.ops4j.pax.exam.CoreOptions.junitBundles;
import static org.ops4j.pax.exam.CoreOptions.mavenBundle;
import static org.ops4j.pax.exam.CoreOptions.streamBundle;
import static org.ops4j.pax.exam.CoreOptions.systemPackage;
import static org.ops4j.pax.exam.CoreOptions.systemProperty;
import org.mule.api.config.ConfigurationBuilder;
import org.mule.api.config.ConfigurationException;
import org.mule.config.spring.SpringXmlConfigurationBuilder;
import org.mule.context.DefaultMuleContextBuilder;
import org.mule.functional.junit4.FunctionalTestCase;
import org.mule.osgi.app.OsgiBootstrapPropertiesServiceDiscoverer;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.junit.Before;
import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.CoreOptions;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.ProbeBuilder;
import org.ops4j.pax.exam.TestProbeBuilder;
import org.ops4j.pax.exam.options.DefaultCompositeOption;
import org.ops4j.pax.exam.options.UrlProvisionOption;
import org.ops4j.pax.tinybundles.core.TinyBundle;
import org.ops4j.pax.tinybundles.core.TinyBundles;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;

/**
 * Defines a base class for VM transport tests that run inside an OSGi container
 */
public abstract class AbstractOsgiFunctionalTestCase extends FunctionalTestCase
{

    public static final String STARTUP_BUNDLES_FILE = "startupBundles.properties";

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

                ////TODO(pablo.kraan): OSGi - use this dependency instead of the original hamcrest library
                // Dependency added in order to provide non core Hamcrest matchers
                mavenBundle("org.apache.servicemix.bundles",  "org.apache.servicemix.bundles.hamcrest",  "1.3_1"),

                //TODO(pablo.kraan): OSGi - need to use this dependency instead of the original from mockito (maybe we can update mockito) or use the new version (1.4)
                mavenBundle("org.objenesis",  "objenesis",  "1.4"),
                mavenBundle("org.mockito",  "mockito-core",  "1.9.0"),

                getStartupBundles(),

                streamBundle(createTestFeature()).startLevel(70),

                junitBundles(),

                frameworkStartLevel(100)
        );
    }

    @Before
    public void setUp() throws Exception
    {
        System.out.println("MONCHO BEFORE FROM: " + AbstractOsgiFunctionalTestCase.class.getName() );
        super.setUp();
    }

    @Override
    protected DefaultMuleContextBuilder createMuleContextBuilder()
    {
        final DefaultMuleContextBuilder contextBuilder = super.createMuleContextBuilder();
        contextBuilder.setBootstrapServiceDiscoverer(OsgiBootstrapPropertiesServiceDiscoverer.create(getBundleContext()));

        return contextBuilder;
    }

    @Override
    protected SpringXmlConfigurationBuilder createConfigurationBuilder(String configResources) throws ConfigurationException
    {
        final SpringXmlConfigurationBuilder springXmlConfigurationBuilder = new SpringXmlConfigurationBuilder(configResources, getBundleContext());
        return springXmlConfigurationBuilder;
    }

    protected abstract BundleContext getBundleContext();

    @Override
    protected ConfigurationBuilder createConfigurationBuilder(String[] multipleConfigResources) throws ConfigurationException
    {
        return new SpringXmlConfigurationBuilder(multipleConfigResources, getBundleContext());
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
        for (Bundle bundle : getBundleContext().getBundles())
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
