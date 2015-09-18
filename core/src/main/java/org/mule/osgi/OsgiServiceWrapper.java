/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.osgi;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;

/**
 *
 */
public abstract class OsgiServiceWrapper implements ServiceListener
{

    //TODO(pablo.kraan): OSGi - move these classes to core and refactor to remove duplication
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
    }

    protected abstract void doUnregisterService(ServiceReference serviceReference);
}
