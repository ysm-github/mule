/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.http.itest.listener;


import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import org.mule.api.MessagingException;
import org.mule.api.MuleEvent;
import org.mule.module.http.itest.AbstractHttpOsgiFunctionalTestCase;
import org.mule.tck.junit4.rule.DynamicPort;

import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;

@Ignore("Cannot find testConnector's constructor")
public class HttpListenerCustomTlsConfigMultipleKeysTestCase extends AbstractHttpOsgiFunctionalTestCase
{

    @Rule
    public DynamicPort port = new DynamicPort("port");

    @Override
    protected String getConfigFile()
    {
        return "http-listener-custom-tls-multiple-keys-config.xml";
    }

    @Test
    public void acceptsConnectionWithValidCertificate() throws Exception
    {
        MuleEvent event = runFlow("testFlowClientWithCertificate", TEST_MESSAGE);
        assertThat(getPayloadAsString(event.getMessage()), equalTo(TEST_MESSAGE));
    }

    @Test(expected = MessagingException.class)
    public void rejectsConnectionWithInvalidCertificate() throws Exception
    {
        runFlow("testFlowClientWithoutCertificate", TEST_MESSAGE);
    }


}
