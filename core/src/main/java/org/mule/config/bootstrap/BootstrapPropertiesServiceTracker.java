/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.config.bootstrap;

import org.mule.api.lifecycle.Disposable;
import org.mule.api.lifecycle.Initialisable;
import org.mule.api.lifecycle.InitialisationException;

import java.io.IOException;
import java.net.URL;
import java.util.Properties;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.framework.wiring.BundleWiring;

public class BootstrapPropertiesServiceTracker implements Initialisable, Disposable
{

    private final BundleContext bundleContext;
    private ServiceRegistration<?> registeredService;

    public BootstrapPropertiesServiceTracker(BundleContext bundleContext)
    {
        this.bundleContext = bundleContext;
    }

    @Override
    public void dispose()
    {
        if (registeredService != null)
        {
            bundleContext.ungetService(registeredService.getReference());
        }
    }

    @Override
    public void initialise() throws InitialisationException
    {
        URL resource = bundleContext.getBundle().getResource(ClassPathRegistryBootstrapDiscoverer.BOOTSTRAP_PROPERTIES);

        if (resource != null)
        {
            BundleWiring bundleWiring = bundleContext.getBundle().adapt(BundleWiring.class);
            ClassLoader bundleClassLoader = bundleWiring.getClassLoader();

            try
            {
                Properties properties = new Properties();
                properties.load(resource.openStream());
                BootstrapPropertiesService service = new MuleBootstrapPropertiesService(bundleClassLoader, properties);
                registeredService = bundleContext.registerService(BootstrapPropertiesService.class.getName(), service, null);
            }
            catch (IOException e)
            {
                throw new InitialisationException(e, this);
            }
        }
    }
}
