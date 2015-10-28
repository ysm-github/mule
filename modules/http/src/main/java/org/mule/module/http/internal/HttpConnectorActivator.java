/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.module.http.internal;

import org.mule.config.bootstrap.BootstrapPropertiesServiceTracker;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class HttpConnectorActivator implements BundleActivator
{

    BootstrapPropertiesServiceTracker bootstrapPropertiesServiceTracker;

    @Override
    public void start(BundleContext bundleContext) throws Exception
    {
        bootstrapPropertiesServiceTracker = new BootstrapPropertiesServiceTracker(bundleContext);
        bootstrapPropertiesServiceTracker.initialise();
    }

    @Override
    public void stop(BundleContext bundleContext) throws Exception
    {
        if (bootstrapPropertiesServiceTracker != null)
        {
            bootstrapPropertiesServiceTracker.dispose();
        }
    }
}
