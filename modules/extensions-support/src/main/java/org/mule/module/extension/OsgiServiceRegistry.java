/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.extension;

import org.mule.api.registry.ServiceRegistry;
import org.mule.extension.api.introspection.declaration.spi.ModelEnricher;
import org.mule.osgi.support.OsgiServiceWrapper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceReference;

public class OsgiServiceRegistry extends OsgiServiceWrapper implements ServiceRegistry
{

    private List<Object> providers = new ArrayList<>();

    public OsgiServiceRegistry(BundleContext bundleContext)
    {
        super(bundleContext);
    }

    @Override
    protected void doRegisterService(ServiceReference serviceReference)
    {
        final Object service = bundleContext.getService(serviceReference);

        providers.add(service);
    }

    @Override
    protected void doUnregisterService(ServiceReference serviceReference)
    {
        final Object service = bundleContext.getService(serviceReference);
        providers.remove(service);
        bundleContext.ungetService(serviceReference);
    }

    @Override
    public <T> Collection<T> lookupProviders(Class<T> providerClass, ClassLoader loader)
    {
        //TODO(pablo.kraan): OSGi - how can filter providers based on visibility
        return lookupProviders(providerClass);
    }

    @Override
    public <T> Collection<T> lookupProviders(Class<T> providerClass)
    {
        return providers.stream().filter(provider -> providerClass.isAssignableFrom(provider.getClass())).map(provider -> (T) provider).collect(Collectors.toList());
    }

    public static OsgiServiceRegistry create(BundleContext bundleContext)
    {
        final OsgiServiceRegistry osgiExtensionManager = new OsgiServiceRegistry(bundleContext);

        try
        {
            List<Class> classes = new LinkedList<>();
            //classes.add(Describer.class);
            classes.add(ModelEnricher.class);

            String filter = getClassFilter(classes);
            bundleContext.addServiceListener(osgiExtensionManager, filter);

            for (Class clazz : classes)
            {
                Collection<ServiceReference<Object>> serviceReferences = bundleContext.getServiceReferences(clazz, null);

                for (ServiceReference<Object> serviceReference : serviceReferences)
                {
                    osgiExtensionManager.serviceChanged(new ServiceEvent(ServiceEvent.REGISTERED, serviceReference));
                }
            }
        }
        catch (InvalidSyntaxException e)
        {
            throw new IllegalStateException(e);
        }

        return osgiExtensionManager;
    }

    private static String getClassFilter(List<Class> classes)
    {
        StringBuilder builder = new StringBuilder("(");

        for (Class clazz : classes)
        {
            if (builder.length() > 1)
            {
                builder.append("|");
            }
            builder.append("objectclass=");
            builder.append(clazz.getName());
        }

        builder.append(")");

        return builder.toString();
    }
}
