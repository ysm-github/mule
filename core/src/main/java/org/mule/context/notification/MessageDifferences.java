/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.context.notification;

import java.util.HashMap;
import java.util.Map;

/**
 *
 */
public class MessageDifferences
{

    public Map<Object,Object> removedFlowVariables = new HashMap<>();
    public Map<Object,Object> updatedFlowVariables = new HashMap<>();
    public Map<Object,Object> newFlowVariables = new HashMap<>();

    public Map<Object,Object> removedSessionVariables = new HashMap<>();
    public Map<Object,Object> updatedSessionVariables = new HashMap<>();
    public Map<Object,Object> newSessionVariables = new HashMap<>();

    public Map<Object,Object> removedInboundProperties = new HashMap<>();
    private Map<Object,Object> updatedInboundProperties = new HashMap<>();
    private Map<Object,Object> newInboundProperties = new HashMap<>();

    private Map<Object,Object> removedOutboundProperties = new HashMap<>();
    private Map<Object,Object> updatedOutboundProperties = new HashMap<>();
    private Map<Object,Object> newOutboundProperties = new HashMap<>();

    private String newPayloadClass;
    private String newPayloadValue;


}
