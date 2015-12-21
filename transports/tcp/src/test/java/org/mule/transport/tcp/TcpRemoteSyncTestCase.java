/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.transport.tcp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import org.mule.DefaultMuleMessage;
import org.mule.api.MuleMessage;
import org.mule.api.client.MuleClient;
import org.mule.api.config.MuleProperties;
import org.mule.api.endpoint.InboundEndpoint;
import org.mule.functional.junit4.FunctionalTestCase;
import org.mule.tck.junit4.rule.DynamicPort;

import java.util.HashMap;
import java.util.Map;

import org.junit.Rule;
import org.junit.Test;

public class TcpRemoteSyncTestCase extends FunctionalTestCase
{
    public static final String message = "mule";

    @Rule
    public DynamicPort dynamicPort1 = new DynamicPort("port1");

    @Rule
    public DynamicPort dynamicPort2 = new DynamicPort("port2");

    @Rule
    public DynamicPort dynamicPort3 = new DynamicPort("port3");

    @Override
    protected String getConfigFile()
    {
        return "tcp-remotesync-flow.xml";
    }

    @Test
    public void testTcpTcpRemoteSync() throws Exception
    {
        MuleClient client = muleContext.getClient();
        Map<String, Object> props = new HashMap<String, Object>();

        // must notify the client to wait for a response from the server
        props.put(MuleProperties.MULE_REMOTE_SYNC_PROPERTY, Boolean.TRUE);
        MuleMessage reply = client.send(((InboundEndpoint) muleContext.getRegistry().lookupObject("echoInTcp")).getAddress(),
            new DefaultMuleMessage(message, muleContext), props);

        assertNotNull(reply);
        assertNotNull(reply.getPayload());
        assertEquals("Received: " + message, getPayloadAsString(reply));
    }

    @Test
    public void testTcpVmRemoteSync() throws Exception
    {
        MuleClient client = muleContext.getClient();
        Map<String, Object> props = new HashMap<String, Object>();

        //must notify the client to wait for a response from the server
        props.put(MuleProperties.MULE_REMOTE_SYNC_PROPERTY, Boolean.TRUE);

        MuleMessage reply = client.send(((InboundEndpoint) muleContext.getRegistry().lookupObject("echo2InTcp")).getAddress(),
            new DefaultMuleMessage(message, muleContext), props);

        assertNotNull(reply);
        assertNotNull(reply.getPayload());
        assertEquals("Received: " + message, getPayloadAsString(reply));
    }
}
