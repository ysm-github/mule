/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.config.bootstrap;

import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultBootstrapPropertiesServiceDiscoverer implements BootstrapPropertiesServiceDiscoverer
{

    protected final Logger logger = LoggerFactory.getLogger(getClass());
    private final ClassLoader classLoader;

    public DefaultBootstrapPropertiesServiceDiscoverer(ClassLoader classLoader)
    {
        this.classLoader = classLoader;
    }

    @Override
    public List<BootstrapPropertiesService> discover()
    {
        final ClassPathRegistryBootstrapDiscoverer bootstrapDiscoverer = new ClassPathRegistryBootstrapDiscoverer();

        List<BootstrapPropertiesService> propertiesServices = new LinkedList<>();
        try
        {
            final List<Properties> discoveredProperties = bootstrapDiscoverer.discover();

            propertiesServices.addAll(discoveredProperties.stream().map(discoveredProperty -> new MuleBootstrapPropertiesService(classLoader, discoveredProperty)).collect(Collectors.toList()));
        }
        catch (BootstrapException e)
        {
            logger.error("Unable to discover bootstrap properties", e);
        }
        return propertiesServices;
    }
}
