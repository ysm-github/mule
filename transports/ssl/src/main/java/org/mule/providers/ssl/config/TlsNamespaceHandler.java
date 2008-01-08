/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.providers.ssl.config;

import org.mule.config.spring.handlers.AbstractMuleNamespaceHandler;
import org.mule.config.spring.parsers.generic.MuleOrphanDefinitionParser;
import org.mule.config.spring.parsers.generic.ParentDefinitionParser;
import org.mule.impl.endpoint.URIBuilder;
import org.mule.providers.ssl.TlsConnector;

/**
 * Reigsters a Bean Definition Parser for handling <code><tls:connector></code> elements.
 */
public class TlsNamespaceHandler extends AbstractMuleNamespaceHandler
{
    
    public void init()
    {
        registerStandardTransportEndpoints(TlsConnector.TLS, URIBuilder.SOCKET_ATTRIBUTES);
        registerBeanDefinitionParser("connector", new MuleOrphanDefinitionParser(TlsConnector.class, true));
        registerBeanDefinitionParser("tls-key-store", new ParentDefinitionParser());
        registerBeanDefinitionParser("tls-client", new ParentDefinitionParser());
        registerBeanDefinitionParser("tls-server", new ParentDefinitionParser());
        registerBeanDefinitionParser("tls-protocol-handler", new ParentDefinitionParser());
    }

}