/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.osgi.sampleapp;

import org.mule.extension.api.ExtensionManager;
import org.mule.extension.api.introspection.ExtensionModel;
import org.mule.osgi.OsgiServiceWrapper;

import java.util.Collection;

import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceReference;

public class OsgiExtensionManager extends OsgiServiceWrapper
{

    private final ExtensionManager extensionManager;

    private OsgiExtensionManager(BundleContext bundleContext, ExtensionManager extensionManager)
    {
        super(bundleContext);
        this.extensionManager = extensionManager;
    }

    @Override
    protected void doRegisterService(ServiceReference serviceReference)
    {
        final ExtensionModel extensionModel = (ExtensionModel) bundleContext.getService(serviceReference);
        extensionManager.registerExtension(extensionModel);
    }

    @Override
    protected void doUnregisterService(ServiceReference serviceReference)
    {

    }

    public static OsgiExtensionManager create(BundleContext bundleContext, ExtensionManager extensionManager)
    {
        final OsgiExtensionManager osgiExtensionManager = new OsgiExtensionManager(bundleContext, extensionManager);

        try
        {
            String filter = "(objectclass=" + ExtensionModel.class.getName() + ")";
            bundleContext.addServiceListener(osgiExtensionManager, filter);

            Collection<ServiceReference<ExtensionModel>> serviceReferences = bundleContext.getServiceReferences(ExtensionModel.class, null);

            for (ServiceReference<ExtensionModel> serviceReference : serviceReferences)
            {
                osgiExtensionManager.serviceChanged(new ServiceEvent(ServiceEvent.REGISTERED, serviceReference));
            }
        }
        catch (InvalidSyntaxException e)
        {
            throw new IllegalStateException(e);
        }

        return osgiExtensionManager;
    }
}
