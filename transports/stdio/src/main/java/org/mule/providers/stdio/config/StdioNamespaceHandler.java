/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.providers.stdio.config;

import org.mule.config.spring.handlers.AbstractMuleNamespaceHandler;
import org.mule.config.spring.parsers.generic.MuleOrphanDefinitionParser;
import org.mule.impl.endpoint.URIBuilder;
import org.mule.providers.stdio.PromptStdioConnector;
import org.mule.providers.stdio.StdioConnector;

/**
 * Reigsters a Bean Definition Parser for handling <code><tcp:connector></code> elements.
 *
 */
public class StdioNamespaceHandler extends AbstractMuleNamespaceHandler
{

    public static final String SYSTEM_ATTRIBUTE = "system";
    public static final String SYSTEM_MAP =
            "IN=" + StdioConnector.STREAM_SYSTEM_IN +
            ",OUT=" + StdioConnector.STREAM_SYSTEM_OUT +
            ",ERR=" + StdioConnector.STREAM_SYSTEM_ERR;
    public static final String[] SYSTEM_ATTRIBUTE_ARRAY = new String[]{SYSTEM_ATTRIBUTE};

    public void init()
    {
        registerStandardTransportEndpoints(StdioConnector.STDIO, SYSTEM_ATTRIBUTE_ARRAY).addMapping(SYSTEM_ATTRIBUTE, SYSTEM_MAP).addAlias(SYSTEM_ATTRIBUTE, URIBuilder.PATH);
        registerBeanDefinitionParser("connector", new MuleOrphanDefinitionParser(PromptStdioConnector.class, true));
    }

}
