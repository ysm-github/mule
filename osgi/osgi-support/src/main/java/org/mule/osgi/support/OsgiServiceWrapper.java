/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.osgi.support;

import java.util.Collection;

import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;

public abstract class OsgiServiceWrapper implements ServiceListener
{

    protected final BundleContext bundleContext;

    public OsgiServiceWrapper(BundleContext bundleContext)
    {
        this.bundleContext = bundleContext;
    }

    @Override
    public void serviceChanged(ServiceEvent event)
    {
        ServiceReference serviceReference = event.getServiceReference();
        switch (event.getType())
        {
            case ServiceEvent.REGISTERED:
            {
                registerService(serviceReference);
            }
            break;
            case ServiceEvent.UNREGISTERING:
            {
                unregisterService(serviceReference);
            }
            break;
        }

    }

    private void registerService(ServiceReference serviceReference)
    {
        doRegisterService(serviceReference);
    }

    protected abstract void doRegisterService(ServiceReference serviceReference);

    private void unregisterService(ServiceReference serviceReference)
    {
        doUnregisterService(serviceReference);

        bundleContext.ungetService(serviceReference);
    }

    protected static void registerListener(BundleContext bundleContext, ServiceListener listener, Class... trackedClasses)
    {
        try
        {
            bundleContext.addServiceListener(listener, createFilter(trackedClasses));

            for (Class trackedClass : trackedClasses)
            {
                final Collection<ServiceReference> serviceReferences = bundleContext.getServiceReferences(trackedClass, null);

                for (ServiceReference serviceReference : serviceReferences)
                {
                    listener.serviceChanged(new ServiceEvent(ServiceEvent.REGISTERED, serviceReference));
                }
            }
        }
        catch (InvalidSyntaxException e)
        {
            throw new IllegalStateException(e);
        }
    }

    private static String createFilter(Class... classes)
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

    protected abstract void doUnregisterService(ServiceReference serviceReference);
}
