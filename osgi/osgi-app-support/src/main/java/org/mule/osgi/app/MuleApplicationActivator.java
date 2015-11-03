/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.osgi.app;

import org.mule.api.MuleContext;
import org.mule.api.config.ConfigurationBuilder;
import org.mule.api.config.MuleConfiguration;
import org.mule.config.PropertiesMuleConfigurationFactory;
import org.mule.config.spring.SpringXmlConfigurationBuilder;
import org.mule.context.DefaultMuleContextBuilder;
import org.mule.context.DefaultMuleContextFactory;
import org.mule.module.extension.internal.manager.DefaultExtensionManager;
import org.mule.osgi.app.internal.ExtensionsManagerConfigurationBuilder;
import org.mule.osgi.app.internal.OsgiBootstrapPropertiesServiceDiscoverer;

import java.util.ArrayList;
import java.util.List;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.wiring.BundleWiring;

public class MuleApplicationActivator implements BundleActivator
{

    private MuleContext muleContext;

    @Override
    public void start(BundleContext bundleContext) throws Exception
    {

        System.out.println("Starting application:" + bundleContext.getBundle().getSymbolicName());

        String configResource = "mule-config.xml";

        SpringXmlConfigurationBuilder cfgBuilder = new SpringXmlConfigurationBuilder(configResource, bundleContext);

        final DefaultExtensionManager extensionManager = new DefaultExtensionManager();

        //TODO(pablo.kraan): add the rest of the original configuration builders
        List<ConfigurationBuilder> configBuilders = new ArrayList<ConfigurationBuilder>(1);
        configBuilders.add(new ExtensionsManagerConfigurationBuilder(extensionManager, bundleContext));

        // need to add the annotations config builder before Spring so we can use Mule
        // annotations in Spring
        //addAnnotationsConfigBuilder(configBuilders);
        //addStartupPropertiesConfigBuilder(configBuilders);
        configBuilders.add(cfgBuilder);

        MuleConfiguration configuration = createMuleConfiguration(configResource);

        DefaultMuleContextBuilder contextBuilder = new DefaultMuleContextBuilder();
        contextBuilder.setMuleConfiguration(configuration);
        contextBuilder.setExecutionClassLoader(getBundleClassLoader(bundleContext));
        contextBuilder.setBootstrapPropertiesServiceDiscoverer(OsgiBootstrapPropertiesServiceDiscoverer.create(bundleContext));

        DefaultMuleContextFactory contextFactory = new DefaultMuleContextFactory();

        muleContext = contextFactory.createMuleContext(configBuilders, contextBuilder);

        muleContext.start();

        System.out.println("Application started: " + bundleContext.getBundle().getSymbolicName());
    }

    //TODO(pablo.kraan): OSGi - move this method to some utility class
    private ClassLoader getBundleClassLoader(BundleContext bundleContext)
    {
        BundleWiring bundleWiring = bundleContext.getBundle().adapt(BundleWiring.class);

        return bundleWiring.getClassLoader();
    }

    protected MuleConfiguration createMuleConfiguration(String appConfigurationResource)
    {
        String appPropertiesFile;

        if (appConfigurationResource == null)
        {
            appPropertiesFile = PropertiesMuleConfigurationFactory.getMuleAppConfiguration(appConfigurationResource);
        }
        else
        {
            appPropertiesFile = appConfigurationResource;
        }

        return new PropertiesMuleConfigurationFactory(appPropertiesFile).createConfiguration();
    }

    @Override
    public void stop(BundleContext bundleContext) throws Exception
    {
        String bundleName = bundleContext.getBundle().getSymbolicName();
        System.out.println("Stopping application " + bundleName);

        if (muleContext != null)
        {
            muleContext.stop();
        }
        System.out.println(bundleName + " stopped");
    }
}
