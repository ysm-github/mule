/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.osgi.feature;

import java.util.ArrayList;
import java.util.List;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.Constants;
import org.osgi.framework.startlevel.BundleStartLevel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FeatureDeployerActivator implements BundleActivator
{

    private static final transient Logger LOGGER = LoggerFactory.getLogger(FeatureDeployerActivator.class);

    @Override
    public void start(BundleContext context) throws Exception
    {
        installFeature(context, new MuleCoreFeature());
    }

    @Override
    public void stop(BundleContext context) throws Exception
    {

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
                LOGGER.error(String.format("Feature '%s' install error on bundle: '$%", featureInfo.getName(), dependency.getLocation()), e);
            }
        }

        return installedBundles;
    }

    private static boolean isFragment(Bundle bundle)
    {
        return bundle.getHeaders().get(Constants.FRAGMENT_HOST) != null;
    }
}
