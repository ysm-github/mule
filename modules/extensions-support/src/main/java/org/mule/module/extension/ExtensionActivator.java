/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.module.extension;

import org.mule.extension.api.introspection.ExtensionModel;
import org.mule.module.extension.internal.introspection.DefaultExtensionFactory;
import org.mule.module.extension.internal.manager.DefaultExtensionDiscoverer;
import org.mule.registry.SpiServiceRegistry;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.framework.wiring.BundleWiring;

public class ExtensionActivator implements BundleActivator
{

    private final List<ServiceRegistration> registeredExtensionModels = new ArrayList<>();

    @Override
    public void start(BundleContext bundleContext) throws Exception
    {
        BundleWiring bundleWiring = bundleContext.getBundle().adapt(BundleWiring.class);
        ClassLoader bundleClassLoader = bundleWiring.getClassLoader();
        final SpiServiceRegistry serviceRegistry = new SpiServiceRegistry();
        final DefaultExtensionDiscoverer bundleExtensionDiscoverer = new DefaultExtensionDiscoverer(new DefaultExtensionFactory(serviceRegistry, bundleClassLoader), serviceRegistry);
        final List<ExtensionModel> discoveredExtensionModels = bundleExtensionDiscoverer.discover(bundleClassLoader);

        for (ExtensionModel discoveredExtensionModel : discoveredExtensionModels)
        {
            Dictionary<String, String> serviceProperties = new Hashtable<>();
            serviceProperties.put("EXTENSION", discoveredExtensionModel.getName());
            serviceProperties.put("VERSION", discoveredExtensionModel.getVersion());
            registeredExtensionModels.add(bundleContext.registerService(ExtensionModel.class.getName(), discoveredExtensionModel, serviceProperties));
        }
    }

    @Override
    public void stop(BundleContext bundleContext) throws Exception
    {

    }
}
