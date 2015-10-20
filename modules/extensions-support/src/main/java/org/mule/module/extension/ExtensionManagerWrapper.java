/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.module.extension;

import org.mule.extension.api.ExtensionManager;
import org.mule.extension.api.introspection.ExtensionModel;
import org.mule.osgi.OsgiServiceWrapper;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

public class ExtensionManagerWrapper extends OsgiServiceWrapper
{

    private final ExtensionManager extensionManager;

    public ExtensionManagerWrapper(BundleContext bundleContext, ExtensionManager extensionManager)
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
        //TODO(pablo.kraan): OSGi - implement unregistering extensions
    }
}
