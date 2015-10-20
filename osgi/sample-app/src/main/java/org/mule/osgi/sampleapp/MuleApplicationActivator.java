/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.osgi.sampleapp;

import org.mule.api.MuleContext;
import org.mule.api.MuleEvent;
import org.mule.api.config.ConfigurationBuilder;
import org.mule.api.config.MuleConfiguration;
import org.mule.config.PropertiesMuleConfigurationFactory;
import org.mule.config.spring.SpringXmlConfigurationBuilder;
import org.mule.context.DefaultMuleContextBuilder;
import org.mule.context.DefaultMuleContextFactory;
import org.mule.extension.validation.api.ValidationResult;
import org.mule.extension.validation.api.Validator;
import org.mule.module.extension.internal.manager.DefaultExtensionManager;
import org.mule.module.http.api.listener.HttpListenerBuilder;

import java.util.ArrayList;
import java.util.List;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class MuleApplicationActivator implements BundleActivator
{
    //protected transient Log logger = LogFactory.getLog(MuleApplicationActivator.class);

    //TODO(pablo.kraan): OSGi - move this class to another package/module
    private MuleContext muleContext;
    private OsgiExtensionManager osgiExtensionManager;
    //private TransportDescriptorServiceWrapper transportDescriptorServiceWrapper;
    //private RegistryBootstrapServiceWrapper registryBootstrapServiceWrapper;

    @Override
    public void start(BundleContext bundleContext) throws Exception
    {

        System.out.println("Starting application:" + bundleContext.getBundle().getSymbolicName());

        //TODO(pablo.kraan): OSGi - setting property to see full exceptions
        System.setProperty("mule.verbose.exceptions", "true");

        try
        {
            Validator validator = new Validator()
            {
                @Override
                public ValidationResult validate(MuleEvent event)
                {
                    return null;
                }
            };
            String configResource = "mule-config.xml";

            //TODO(pablo.kraan): OSGi - need to bundleContext here
            SpringXmlConfigurationBuilder cfgBuilder = new SpringXmlConfigurationBuilder(configResource, bundleContext);

            final DefaultExtensionManager extensionManager = new DefaultExtensionManager();

            //TODO(pablo.kraan): add the rest of the original configuration builders
            List<ConfigurationBuilder> configBuilders = new ArrayList<ConfigurationBuilder>(1);
            configBuilders.add(new ExtensionsManagerConfigurationBuilder(extensionManager));

            // need to add the annotations config builder before Spring so we can use Mule
            // annotations in Spring
            //addAnnotationsConfigBuilder(configBuilders);
            //addStartupPropertiesConfigBuilder(configBuilders);
            configBuilders.add(cfgBuilder);

            //TODO(pablo.kraan): OSGi - need to register all the service wrappers to registering services (like TransportDescriptorServiceWrapper)
            MuleConfiguration configuration = createMuleConfiguration(configResource);

            DefaultMuleContextBuilder contextBuilder = new DefaultMuleContextBuilder();
            contextBuilder.setMuleConfiguration(configuration);

            DefaultMuleContextFactory contextFactory = new DefaultMuleContextFactory();
            contextFactory.setBundleContext(bundleContext);

            osgiExtensionManager = OsgiExtensionManager.create(bundleContext, extensionManager);

            muleContext = contextFactory.createMuleContext(configBuilders, contextBuilder);
            extensionManager.setMuleContext(muleContext);
            extensionManager.initialise();


            //TODO(pablo.kraan): OSGi - added to force a dependency against HTTP, remove and test that still works
            new HttpListenerBuilder(muleContext);

            muleContext.start();

            System.out.println("Application started: " + bundleContext.getBundle().getSymbolicName());
        }
        catch (Throwable e)
        {
            System.out.println("Error starting bundle: " + bundleContext.getBundle().getSymbolicName() + ". " + e.getMessage());
            e.printStackTrace();
        }
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

        if (osgiExtensionManager != null)
        {
            bundleContext.removeServiceListener(osgiExtensionManager);
        }

        if (muleContext != null)
        {
            muleContext.stop();
        }
        System.out.println(bundleName + " stopped");
    }
}
