/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.osgi.support;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class MuleSupportActivator implements BundleActivator
{

    private BoostrapPropertiesBundleListener boostrapPropertiesBundleListener;

    @Override
    public void start(BundleContext context) throws Exception
    {
        boostrapPropertiesBundleListener = new BoostrapPropertiesBundleListener(context);
        boostrapPropertiesBundleListener.initialise();
    }

    @Override
    public void stop(BundleContext context) throws Exception
    {
        if (boostrapPropertiesBundleListener != null)
        {
            boostrapPropertiesBundleListener.dispose();
        }
    }
}
