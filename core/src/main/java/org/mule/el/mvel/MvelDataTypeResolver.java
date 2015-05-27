/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.el.mvel;

import org.mule.api.MuleMessage;
import org.mule.api.transformer.DataType;
import org.mule.api.transport.PropertyScope;
import org.mule.mvel2.ast.ASTNode;
import org.mule.mvel2.compiler.Accessor;
import org.mule.mvel2.compiler.AccessorNode;
import org.mule.mvel2.compiler.CompiledExpression;
import org.mule.mvel2.compiler.ExecutableLiteral;
import org.mule.mvel2.optimizers.impl.refl.nodes.MapAccessorNest;
import org.mule.mvel2.optimizers.impl.refl.nodes.VariableAccessor;
import org.mule.mvel2.util.ASTIterator;
import org.mule.mvel2.util.ASTLinkedList;
import org.mule.transformer.types.DataTypeFactory;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MvelDataTypeResolver
{

    private interface DataTypeResolver
    {
        DataType resolve(MuleMessage message, CompiledExpression compiledExpression);
    }

    private static class PayloadDataTypeResolver implements DataTypeResolver
    {

        @Override
        public DataType resolve(MuleMessage message, CompiledExpression compiledExpression)
        {
            ASTIterator iterator = new ASTLinkedList(compiledExpression.getFirstNode());

            if (!iterator.hasMoreNodes())
            {
                return null;
            }
            else
            {
                ASTNode node = iterator.nextNode();

                if (node.isIdentifier() && ("payload".equals(node.getName()) || "message.payload".equals(node.getName())))
                {
                    return message.getDataType();
                }
                else
                {
                    return null;
                }
            }
        }
    }

    private static class PropertyDataTypeResolver implements DataTypeResolver
    {

        @Override
        public DataType resolve(MuleMessage message, CompiledExpression compiledExpression)
        {
            ASTIterator iterator = new ASTLinkedList(compiledExpression.getFirstNode());

            if (!iterator.hasMoreNodes())
            {
                return null;
            }
            else
            {
                ASTNode node = iterator.nextNode();

                if (node.isIdentifier() && message.getPropertyNames(PropertyScope.INVOCATION).contains(node.getName()))
                {
                    return message.getPropertyDataType(node.getName(), PropertyScope.INVOCATION);
                }
                else if (node.isIdentifier() && message.getPropertyNames(PropertyScope.SESSION).contains(node.getName()))
                {
                    return message.getPropertyDataType(node.getName(), PropertyScope.SESSION);
                }
                else
                {
                    return null;
                }
            }
        }
    }

    public static class FlowVarDataTypeResolver implements DataTypeResolver
    {

        @Override
        public DataType resolve(MuleMessage message, CompiledExpression compiledExpression)
        {
            return getPropertyDataType(message, compiledExpression, PropertyScope.INVOCATION, "flowVars");
        }
    }

    public static class SessionVarDataTypeResolver implements DataTypeResolver
    {

        @Override
        public DataType resolve(MuleMessage message, CompiledExpression compiledExpression)
        {
            return getPropertyDataType(message, compiledExpression, PropertyScope.SESSION, "sessionVars");
        }
    }


    private final List<DataTypeResolver> resolvers;

    public MvelDataTypeResolver()
    {
        resolvers = new ArrayList<>();
        resolvers.add(new PayloadDataTypeResolver());
        resolvers.add(new PropertyDataTypeResolver());
        resolvers.add(new FlowVarDataTypeResolver());
        resolvers.add(new SessionVarDataTypeResolver());
    }

    public DataType resolve(Object value, MuleMessage message, Serializable serializedExpression)
    {
        DataType result = null;
        if (serializedExpression instanceof CompiledExpression)
        {
            CompiledExpression compiledExpression = (CompiledExpression) serializedExpression;

            //TODO(pablo.kraan): DFL - add an interface and use a loop instead of this if/then/else
            for (DataTypeResolver resolver : resolvers)
            {
                result = resolver.resolve(message, compiledExpression);

                if (result != null)
                {
                    break;
                }
            }
        }

        if (result == null)
        {
            result =  DataTypeFactory.create(value == null ? Object.class : value.getClass(), null);
        }

        return result;
    }

    private static DataType getPropertyDataType(MuleMessage message, CompiledExpression compiledExpression, PropertyScope scope, String varsName)
    {
        ASTIterator iterator = new ASTLinkedList(compiledExpression.getFirstNode());

        if (!iterator.hasMoreNodes())
        {
            return null;
        }
        else
        {
            ASTNode node = iterator.nextNode();
            final Accessor accessor = node.getAccessor();
            //TODO(pablo.kraan): DFL - add checks before casting
            if (accessor instanceof VariableAccessor && ((VariableAccessor) accessor).getProperty().equals(varsName))
            {
                VariableAccessor variableAccessor = (VariableAccessor) accessor;
                final AccessorNode nextNode = variableAccessor.getNextNode();
                final String propertyName = (String) ((ExecutableLiteral) ((MapAccessorNest) nextNode).getProperty()).getLiteral();
                return message.getPropertyDataType(propertyName, scope);
            }
            else
            {
                return null;
            }
        }
    }
}
