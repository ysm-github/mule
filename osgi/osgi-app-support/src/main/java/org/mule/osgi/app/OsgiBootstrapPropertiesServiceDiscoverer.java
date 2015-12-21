/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.osgi.app;

import org.mule.config.bootstrap.BootstrapService;
import org.mule.config.bootstrap.BootstrapServiceDiscoverer;
import org.mule.osgi.support.OsgiServiceWrapper;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

public class OsgiBootstrapPropertiesServiceDiscoverer extends OsgiServiceWrapper implements BootstrapServiceDiscoverer
{

    private List<BootstrapService> services = new LinkedList<>();

    public OsgiBootstrapPropertiesServiceDiscoverer(BundleContext bundleContext)
    {
        super(bundleContext);
    }

    @Override
    protected void doRegisterService(ServiceReference serviceReference)
    {
        BootstrapService bootstrapPropertiesService = (BootstrapService) bundleContext.getService(serviceReference);
        services.add(bootstrapPropertiesService);
    }

    @Override
    protected void doUnregisterService(ServiceReference serviceReference)
    {
        BootstrapService bootstrapPropertiesService = (BootstrapService) bundleContext.getService(serviceReference);
        services.remove(bootstrapPropertiesService);
    }

    @Override
    public List<BootstrapService> discover()
    {
        return Collections.unmodifiableList(services);
    }

    public static OsgiBootstrapPropertiesServiceDiscoverer create(BundleContext bundleContext)
    {
        final OsgiBootstrapPropertiesServiceDiscoverer listener = new OsgiBootstrapPropertiesServiceDiscoverer(bundleContext);

        registerListener(bundleContext, listener, BootstrapService.class);

        return listener;
    }
}
