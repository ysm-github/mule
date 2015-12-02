/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.http.itest.requester;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import org.mule.module.http.internal.request.DefaultHttpRequesterConfig;
import org.mule.module.http.itest.AbstractHttpOsgiFunctionalTestCase;

import org.junit.Test;

public class HttpRequestApiConfigurationTestCase extends AbstractHttpOsgiFunctionalTestCase
{

    @Override
    protected String getConfigFile()
    {
        return "http-request-api-config.xml";
    }

    @Test
    public void parseApiConfigurationFromConfig()
    {
        DefaultHttpRequesterConfig config = muleContext.getRegistry().get("ramlConfig");

        assertNotNull(config.getApiConfiguration());
        assertThat(config.getApiConfiguration().getLocation(), equalTo("TestFile.raml"));
    }
}
