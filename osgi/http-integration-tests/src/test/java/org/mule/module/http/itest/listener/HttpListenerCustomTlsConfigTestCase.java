/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.http.itest.listener;


import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import org.mule.api.MuleEvent;
import org.mule.construct.Flow;
import org.mule.module.http.itest.AbstractHttpOsgiFunctionalTestCase;
import org.mule.tck.junit4.rule.DynamicPort;

import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;

@Ignore("Cannot find testConnector's constructor")
public class HttpListenerCustomTlsConfigTestCase extends AbstractHttpOsgiFunctionalTestCase
{

    @Rule
    public DynamicPort port1 = new DynamicPort("port1");
    @Rule
    public DynamicPort port2 = new DynamicPort("port2");
    @Rule
    public DynamicPort port3 = new DynamicPort("port3");

    @Override
    protected String getConfigFile()
    {
        return "http-listener-custom-tls-config.xml";
    }

    @Test
    public void customTlsGlobalContext() throws Exception
    {
        Flow flow = (Flow) getFlowConstruct("testFlowGlobalContextClient");
        final MuleEvent res = flow.process(getTestEvent("data"));
        assertThat(getPayloadAsString(res.getMessage()), is("ok"));
    }

    @Test
    public void customTlsNestedContext() throws Exception
    {
        Flow flow = (Flow) getFlowConstruct("testFlowNestedContextClient");
        final MuleEvent res = flow.process(getTestEvent("data"));
        assertThat(getPayloadAsString(res.getMessage()), is("all right"));
    }

}
