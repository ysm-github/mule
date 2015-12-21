/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.osgi.support;

import org.mule.api.lifecycle.InitialisationException;
import org.mule.config.bootstrap.BootstrapService;
import org.mule.config.bootstrap.ClassPathRegistryBootstrapDiscoverer;
import org.mule.config.bootstrap.PropertiesBootstrapService;

import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Properties;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.BundleListener;
import org.osgi.framework.wiring.BundleWiring;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BoostrapPropertiesBundleListener implements BundleListener
{

    private static final transient Logger LOGGER = LoggerFactory.getLogger(BoostrapPropertiesBundleListener.class);
    private final BundleContext bundleContext;

    public BoostrapPropertiesBundleListener(BundleContext bundleContext)
    {
        this.bundleContext = bundleContext;
    }

    public void initialise()
    {
        bundleContext.addBundleListener(this);

        for (Bundle bundle : bundleContext.getBundles())
        {
            if (bundle.getState() == Bundle.RESOLVED || bundle.getState() == Bundle.STARTING
                || bundle.getState() == Bundle.ACTIVE)
            {
                bundleChanged(new BundleEvent(BundleEvent.RESOLVED, bundle));
            }
        }
    }

    public void dispose()
    {
        bundleContext.removeBundleListener(this);
    }

    @Override
    public void bundleChanged(BundleEvent event)
    {
        Bundle bundle = event.getBundle();
        if (event.getType() == BundleEvent.RESOLVED)
        {
            LOGGER.info("Processing bundle: " + bundle.getSymbolicName());

            Enumeration bootstrapProperties = bundle.findEntries(ClassPathRegistryBootstrapDiscoverer.BOOTSTRAP_PROPERTIES_PATH, ClassPathRegistryBootstrapDiscoverer.BOOTSTRAP_PROPERTIES_FILE, false);
            while (bootstrapProperties != null && bootstrapProperties.hasMoreElements())
            {
                final Properties properties = new Properties();
                try
                {
                    URL url = (URL) bootstrapProperties.nextElement();
                    properties.load(url.openStream());
                }
                catch (IOException e)
                {
                    LOGGER.warn("Unable to read bootstrap properties from bundle " + bundle.getSymbolicName(), e);
                }


                BundleWiring bundleWiring = bundle.adapt(BundleWiring.class);
                ClassLoader bundleClassLoader = bundleWiring.getClassLoader();

                //TODO(pablo.kraan): OSGi - need to track services in order to remove them when the bundles are unregistered
                BootstrapService service = new PropertiesBootstrapService(bundleClassLoader, properties);
                bundleContext.registerService(BootstrapService.class.getName(), service, null);
            }
        }
    }
}