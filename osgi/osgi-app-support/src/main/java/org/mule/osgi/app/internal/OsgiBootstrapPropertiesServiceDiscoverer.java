/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.osgi.app.internal;

import org.mule.config.bootstrap.BootstrapPropertiesService;
import org.mule.config.bootstrap.BootstrapPropertiesServiceDiscoverer;
import org.mule.osgi.support.OsgiServiceWrapper;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceReference;

public class OsgiBootstrapPropertiesServiceDiscoverer extends OsgiServiceWrapper implements BootstrapPropertiesServiceDiscoverer
{

    private List<BootstrapPropertiesService> services = new LinkedList<>();

    public OsgiBootstrapPropertiesServiceDiscoverer(BundleContext bundleContext)
    {
        super(bundleContext);
    }

    @Override
    protected void doRegisterService(ServiceReference serviceReference)
    {
        BootstrapPropertiesService bootstrapPropertiesService = (BootstrapPropertiesService) bundleContext.getService(serviceReference);
        services.add(bootstrapPropertiesService);
    }

    @Override
    protected void doUnregisterService(ServiceReference serviceReference)
    {
        BootstrapPropertiesService bootstrapPropertiesService = (BootstrapPropertiesService) bundleContext.getService(serviceReference);
        services.remove(bootstrapPropertiesService);
        bundleContext.ungetService(serviceReference);
    }

    @Override
    public List<BootstrapPropertiesService> discover()
    {
        return Collections.unmodifiableList(services);
    }

    public static OsgiBootstrapPropertiesServiceDiscoverer create(BundleContext bundleContext)
    {
        final OsgiBootstrapPropertiesServiceDiscoverer propertiesServiceDiscoverer= new OsgiBootstrapPropertiesServiceDiscoverer(bundleContext);

        try
        {
            String filter = "(objectclass=" + BootstrapPropertiesService.class.getName() + ")";
            bundleContext.addServiceListener(propertiesServiceDiscoverer, filter);

            Collection<ServiceReference<BootstrapPropertiesService>> serviceReferences = bundleContext.getServiceReferences(BootstrapPropertiesService.class, null);

            for (ServiceReference<BootstrapPropertiesService> serviceReference : serviceReferences)
            {
                propertiesServiceDiscoverer.serviceChanged(new ServiceEvent(ServiceEvent.REGISTERED, serviceReference));
            }
        }
        catch (InvalidSyntaxException e)
        {
            throw new IllegalStateException(e);
        }

        return propertiesServiceDiscoverer;
    }
}
