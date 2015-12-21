/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.test.usecases.sync;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import org.mule.api.MuleMessage;
import org.mule.api.client.MuleClient;
import org.mule.functional.junit4.FunctionalTestCase;
import org.mule.tck.junit4.rule.DynamicPort;
import org.mule.transformer.compression.GZipUncompressTransformer;
import org.mule.transformer.simple.ByteArrayToSerializable;
import org.mule.transformer.types.DataTypeFactory;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.Rule;
import org.junit.Test;

public class HttpTransformTestCase extends FunctionalTestCase
{

    @Rule
    public DynamicPort httpPort1 = new DynamicPort("port1");

    @Rule
    public DynamicPort httpPort2 = new DynamicPort("port2");

    @Override
    protected String getConfigFile()
    {
        return "org/mule/test/usecases/sync/http-transform-flow.xml";
    }

    @Test
    public void testTransform() throws Exception
    {
        MuleClient client = muleContext.getClient();
        MuleMessage message = client.send(String.format("http://localhost:%d/RemoteService", httpPort1.getNumber()), getTestMuleMessage("payload"));
        assertNotNull(message);
        GZipUncompressTransformer gu = new GZipUncompressTransformer();
        gu.setMuleContext(muleContext);
        gu.setReturnDataType(DataTypeFactory.STRING);
        assertNotNull(message.getPayload());
        String result = (String)gu.transform(getPayloadAsBytes(message));
        assertEquals("<string>payload</string>", result);
    }

    @Test
    public void testBinary() throws Exception
    {
        MuleClient client = muleContext.getClient();
        ArrayList<Integer> payload = new ArrayList<Integer>();
        payload.add(42);
        MuleMessage message = client.send(String.format("http://localhost:%d/RemoteService", httpPort2.getNumber()),
                                          muleContext.getObjectSerializer().serialize(payload),
                                          null);
        assertNotNull(message);
        ByteArrayToSerializable bas = new ByteArrayToSerializable();
        bas.setMuleContext(muleContext);
        assertNotNull(message.getPayload());
        Object result = bas.transform(message.getPayload());
        assertEquals(payload, result);
    }

    @Test
    public void testBinaryWithBridge() throws Exception
    {
        MuleClient client = muleContext.getClient();
        Object payload = Arrays.asList(42);
        MuleMessage message = client.send("vm://LocalService", getTestMuleMessage(payload));
        assertNotNull(message);
        ByteArrayToSerializable bas = new ByteArrayToSerializable();
        bas.setMuleContext(muleContext);
        assertNotNull(message.getPayload());
        Object result = bas.transform(message.getPayload());
        assertEquals(payload, result);
    }
}
