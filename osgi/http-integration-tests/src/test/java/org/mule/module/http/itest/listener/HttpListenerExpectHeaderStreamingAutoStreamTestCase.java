/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.http.itest.listener;

import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.junit.PaxExamParameterized;

@RunWith(PaxExamParameterized.class)
@Ignore("PaxExamParameterized needs static configuration")
public class HttpListenerExpectHeaderStreamingAutoStreamTestCase extends HttpListenerExpectHeaderStreamingAlwaysTestCase
{

    @Override
    protected String getConfigFile()
    {
        return "http-listener-expect-header-streaming-auto-stream-config.xml";
    }

    public HttpListenerExpectHeaderStreamingAutoStreamTestCase(String persistentConnections)
    {
        super(persistentConnections);
    }

}

