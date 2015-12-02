/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.module.http.itest;

import org.mule.osgi.tck.AbstractOsgiFunctionalTestCase;

import java.util.ArrayList;
import java.util.List;

public class AbstractHttpOsgiFunctionalTestCase extends AbstractOsgiFunctionalTestCase
{

    @Override
    protected List<Class> getTestFeatures()
    {
        final ArrayList<Class> classes = new ArrayList<>();
        classes.add(HttpTestFeature.class);
        return classes;
    }
}
