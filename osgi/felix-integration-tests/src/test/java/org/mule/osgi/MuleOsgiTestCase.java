/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.osgi;

import static org.junit.Assert.fail;
import static org.ops4j.pax.exam.CoreOptions.options;

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
        return options(
                //TODO(pablo.kraan): OSGi - add all the mule dependencies
        );
    }

    @Test
    public void startsContainer() throws Exception
    {
        StringBuilder builder = new StringBuilder("There is at least a non active bundle:\n");
        boolean failure = false;
        for (Bundle bundle : bundleContext.getBundles())
        {
            final boolean isFragment = isFragment(bundle);
            if (isFragment && bundle.getState() != Bundle.RESOLVED || !isFragment && bundle.getState() != Bundle.ACTIVE)
            {
                failure = true;
            }
            builder.append(isFragment ? "Fragment" : "Bundle");
            builder.append(" - " + getBundleState(bundle.getState()) + " - " + bundle.getBundleId() + " - " + bundle.getSymbolicName() + " - " + bundle.getVersion() + "\n");
        }

        if (failure)
        {
            fail(builder.toString());
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
