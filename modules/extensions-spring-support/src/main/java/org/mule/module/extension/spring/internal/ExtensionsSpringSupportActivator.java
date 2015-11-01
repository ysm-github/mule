/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.module.extension.spring.internal;

import org.mule.extension.api.introspection.declaration.spi.ModelEnricher;
import org.mule.module.extension.spring.internal.xml.XmlModelEnricher;

import java.util.Dictionary;
import java.util.Hashtable;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

public class ExtensionsSpringSupportActivator implements BundleActivator
{

    private ServiceRegistration<ModelEnricher> registeredService;

    @Override
    public void start(BundleContext bundleContext) throws Exception
    {
        Dictionary<String, String> serviceProperties = new Hashtable<>();
        registeredService = bundleContext.registerService(ModelEnricher.class, new XmlModelEnricher(), serviceProperties);
    }

    @Override
    public void stop(BundleContext bundleContext) throws Exception
    {
        if (registeredService != null)
        {
            registeredService.unregister();
        }
    }
}
