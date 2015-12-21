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

import javax.inject.Inject;

import org.junit.runner.RunWith;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerSuite;
import org.osgi.framework.BundleContext;

@RunWith(PaxExam.class)
@ExamReactorStrategy(PerSuite.class)
public class AbstractHttpOsgiFunctionalTestCase extends AbstractOsgiFunctionalTestCase
{

    @Inject
    public BundleContext bundleContext;

    @Override
    protected BundleContext getBundleContext()
    {
        return bundleContext;
    }

    @Override
    protected List<Class> getTestFeatures()
    {
        final ArrayList<Class> classes = new ArrayList<>();
        classes.add(HttpTestFeature.class);
        return classes;
    }
}
