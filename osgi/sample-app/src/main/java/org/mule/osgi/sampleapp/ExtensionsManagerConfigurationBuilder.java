/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.osgi.sampleapp;

import static org.mule.api.lifecycle.LifecycleUtils.initialiseIfNeeded;
import org.mule.DefaultMuleContext;
import org.mule.api.MuleContext;
import org.mule.config.builders.AbstractConfigurationBuilder;
import org.mule.extension.api.ExtensionManager;
import org.mule.module.extension.internal.manager.DefaultExtensionManager;

import org.osgi.framework.BundleContext;

/**
 * Implementation of {@link org.mule.api.config.ConfigurationBuilder}
 * that register a {@link ExtensionManager} if
 * it's present in the classpath
 *
 * @since 3.7.0
 */
public class ExtensionsManagerConfigurationBuilder extends AbstractConfigurationBuilder
{

    private static final String EXTENSIONS_MANAGER_CLASS_NAME = "org.mule.module.extension.internal.manager.DefaultExtensionManager";
    private final DefaultExtensionManager extensionManager;

    public ExtensionsManagerConfigurationBuilder(DefaultExtensionManager extensionManager)
    {

        this.extensionManager = extensionManager;
    }

    @Override
    protected void doConfigure(MuleContext muleContext, BundleContext bundleContext) throws Exception
    {
        ((DefaultMuleContext) muleContext).setExtensionManager(extensionManager);
        initialiseIfNeeded(extensionManager, muleContext);
        //extensionManager.discoverExtensions(Thread.currentThread().getContextClassLoader());
    }
}
