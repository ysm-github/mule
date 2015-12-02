/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.osgi.feature.deployer;

import org.mule.osgi.feature.model.Dependency;
import org.mule.osgi.feature.model.FeatureInfo;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.BundleException;
import org.osgi.framework.BundleListener;
import org.osgi.framework.Constants;
import org.osgi.framework.startlevel.BundleStartLevel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FeatureBundleListener implements BundleListener
{

    private static final transient Logger LOGGER = LoggerFactory.getLogger(FeatureBundleListener.class);
    private final BundleContext bundleContext;

    public FeatureBundleListener(BundleContext bundleContext)
    {
        this.bundleContext = bundleContext;
    }

    public void initialise()
    {
        bundleContext.addBundleListener(this);

        for (Bundle bundle : bundleContext.getBundles())
        {
            if (bundle.getState() == Bundle.RESOLVED || bundle.getState() == Bundle.STARTING
                || bundle.getState() == Bundle.ACTIVE)
            {
                bundleChanged(new BundleEvent(BundleEvent.RESOLVED, bundle));
            }
        }
    }

    public void dispose()
    {
        bundleContext.removeBundleListener(this);
    }

    @Override
    public void bundleChanged(BundleEvent event)
    {
        Bundle bundle = event.getBundle();
        if (event.getType() == BundleEvent.RESOLVED)
        {
            LOGGER.info("Processing bundle: " + bundle.getSymbolicName());

            Enumeration featuresUrlEnumeration = bundle.findEntries("/META-INF/", "features.properties", false);
            while (featuresUrlEnumeration != null && featuresUrlEnumeration.hasMoreElements())
            {
                final Properties properties = new Properties();
                try
                {
                    URL url = (URL) featuresUrlEnumeration.nextElement();
                    properties.load(url.openStream());
                }
                catch (IOException e)
                {
                    LOGGER.warn("Unable to read features from bundle " + bundle.getSymbolicName(), e);
                }

                for (Object featureClassName : properties.keySet())
                {
                    try
                    {
                        final Class<?> featureClass = bundle.loadClass((String) featureClassName);

                        final FeatureInfo feature = (FeatureInfo) featureClass.newInstance();
                        installFeature(bundleContext, feature);
                    }
                    catch (Exception e)
                    {
                        LOGGER.warn("Unable to load feature from bundle " + bundle.getSymbolicName(), e);
                    }
                }
            }
        }
    }

    private static List<Bundle> installFeature(BundleContext context, FeatureInfo featureInfo)
    {
        LOGGER.info("Installing feature: " + featureInfo.getName());

        //TODO(pablo.kraan): OSGi - installedBundles must be uninstalled when there is an error installing the feature
        List<Bundle> installedBundles = new ArrayList<>();

        for (Dependency dependency : featureInfo.getDependencies())
        {
            try
            {
                final Bundle bundle = context.installBundle(dependency.getLocation());

                if (bundle != null)
                {
                    installedBundles.add(bundle);

                    if (!isFragment(bundle))
                    {
                        bundle.adapt(BundleStartLevel.class).setStartLevel(dependency.getStartLevel());
                        bundle.start();
                    }
                }
            }
            catch (BundleException e)
            {
                LOGGER.error(String.format("Feature '%s' install error on bundle: '%s'", featureInfo.getName(), dependency.getLocation()), e);
            }
        }

        return installedBundles;
    }

    private static boolean isFragment(Bundle bundle)
    {
        return bundle.getHeaders().get(Constants.FRAGMENT_HOST) != null;
    }
}
