/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.osgi.app.internal;

import org.mule.extension.api.ExtensionManager;
import org.mule.extension.api.introspection.ExtensionModel;
import org.mule.osgi.support.OsgiServiceWrapper;

import org.osgi.framework.BundleContext;
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
        final OsgiExtensionManager listener = new OsgiExtensionManager(bundleContext, extensionManager);

        registerListener(bundleContext, listener,  ExtensionModel.class);

        return listener;
    }
}
