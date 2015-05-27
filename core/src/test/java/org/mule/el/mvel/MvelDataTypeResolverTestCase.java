/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.el.mvel;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.mule.mvel2.MVEL.compileExpression;
import static org.mule.tck.junit4.matcher.DataTypeMatcher.like;
import static org.mule.transformer.types.MimeTypes.JSON;
import org.mule.api.MuleEvent;
import org.mule.api.transformer.DataType;
import org.mule.api.transport.PropertyScope;
import org.mule.mvel2.MVEL;
import org.mule.mvel2.ParserConfiguration;
import org.mule.mvel2.ParserContext;
import org.mule.mvel2.compiler.CompiledExpression;
import org.mule.mvel2.integration.impl.CachedMapVariableResolverFactory;
import org.mule.tck.junit4.AbstractMuleContextTestCase;
import org.mule.transformer.types.DataTypeFactory;
import org.mule.transformer.types.MimeTypes;

import java.nio.charset.StandardCharsets;
import java.util.Collections;

import org.junit.Test;

public class MvelDataTypeResolverTestCase extends AbstractMuleContextTestCase
{

    public static final String EXPRESSION_VALUE = "bar";
    public static final String CUSTOM_ENCODING = StandardCharsets.UTF_16.name();
    private MvelDataTypeResolver dataTypeResolver;

    @Test
    public void returnsDefaultDataTypeForNonNullValue() throws Exception
    {
        final String expression = "someExpression";

        MVELExpressionLanguage expressionLanguage = (MVELExpressionLanguage) muleContext.getExpressionLanguage();
        final CompiledExpression compiledExpression = (CompiledExpression) compileExpression(expression, new ParserContext(expressionLanguage.getParserConfiguration()));

        MuleEvent testEvent = getTestEvent(TEST_MESSAGE);

        dataTypeResolver = new MvelDataTypeResolver();

        assertThat(dataTypeResolver.resolve(EXPRESSION_VALUE, testEvent.getMessage(), compiledExpression), like(String.class, MimeTypes.ANY, null));
    }

    @Test
    public void returnsDefaultDataTypeForNullValue() throws Exception
    {
        final String expression = "someExpression";

        MVELExpressionLanguage expressionLanguage = (MVELExpressionLanguage) muleContext.getExpressionLanguage();
        final CompiledExpression compiledExpression = (CompiledExpression) compileExpression(expression, new ParserContext(expressionLanguage.getParserConfiguration()));

        MuleEvent testEvent = getTestEvent(TEST_MESSAGE);

        dataTypeResolver = new MvelDataTypeResolver();

        assertThat(dataTypeResolver.resolve(null, testEvent.getMessage(), compiledExpression), like(Object.class, MimeTypes.ANY, null));
    }

    @Test
    public void returnsPayloadDataType() throws Exception
    {
        final String expression = "payload";
        final DataType expectedDataType = DataTypeFactory.create(String.class, JSON);
        expectedDataType.setEncoding(CUSTOM_ENCODING);

        MVELExpressionLanguage expressionLanguage = (MVELExpressionLanguage) muleContext.getExpressionLanguage();
        final CompiledExpression compiledExpression = (CompiledExpression) compileExpression(expression, new ParserContext(expressionLanguage.getParserConfiguration()));

        MuleEvent testEvent = getTestEvent(TEST_MESSAGE);
        testEvent.getMessage().setPayload(TEST_MESSAGE, expectedDataType);

        dataTypeResolver = new MvelDataTypeResolver();

        assertThat(dataTypeResolver.resolve(TEST_MESSAGE, testEvent.getMessage(), compiledExpression), like(String.class, JSON, CUSTOM_ENCODING));
    }

    @Test
    public void returnsInlineFlowVarDataType() throws Exception
    {
        final String expression = "foo";
        final DataType expectedDataType = DataTypeFactory.create(String.class, JSON);
        expectedDataType.setEncoding(CUSTOM_ENCODING);

        MVELExpressionLanguage expressionLanguage = (MVELExpressionLanguage) muleContext.getExpressionLanguage();
        final CompiledExpression compiledExpression = (CompiledExpression) compileExpression(expression, new ParserContext(expressionLanguage.getParserConfiguration()));

        MuleEvent testEvent = getTestEvent(TEST_MESSAGE);
        testEvent.getMessage().setProperty("foo", EXPRESSION_VALUE, PropertyScope.INVOCATION, expectedDataType);

        dataTypeResolver = new MvelDataTypeResolver();

        assertThat(dataTypeResolver.resolve(EXPRESSION_VALUE, testEvent.getMessage(), compiledExpression), like(String.class, JSON, CUSTOM_ENCODING));
    }

    @Test
    public void returnsInlineSessionPropertyDataType() throws Exception
    {
        final String expression = "foo";
        final DataType expectedDataType = DataTypeFactory.create(String.class, JSON);
        expectedDataType.setEncoding(CUSTOM_ENCODING);

        MVELExpressionLanguage expressionLanguage = (MVELExpressionLanguage) muleContext.getExpressionLanguage();
        final CompiledExpression compiledExpression = (CompiledExpression) compileExpression(expression, new ParserContext(expressionLanguage.getParserConfiguration()));

        MuleEvent testEvent = getTestEvent(TEST_MESSAGE);
        testEvent.getMessage().setProperty("foo", EXPRESSION_VALUE, PropertyScope.SESSION, expectedDataType);

        dataTypeResolver = new MvelDataTypeResolver();

        assertThat(dataTypeResolver.resolve(EXPRESSION_VALUE, testEvent.getMessage(), compiledExpression), like(String.class, JSON, CUSTOM_ENCODING));
    }

    @Test
    public void returnsFlowVarDataType() throws Exception
    {
        final String expression = "flowVars['foo']";
        final DataType expectedDataType = DataTypeFactory.create(String.class, JSON);
        expectedDataType.setEncoding(CUSTOM_ENCODING);

        MuleEvent testEvent = getTestEvent(TEST_MESSAGE);
        testEvent.getMessage().setProperty("foo", EXPRESSION_VALUE, PropertyScope.INVOCATION, expectedDataType);

        final ParserConfiguration parserConfiguration = MVELExpressionLanguage.createParserConfiguration(Collections.EMPTY_MAP);
        final MVELExpressionLanguageContext context = createMvelExpressionLanguageContext(testEvent, parserConfiguration);

        CompiledExpression compiledExpression = (CompiledExpression) compileExpression(expression, new ParserContext(parserConfiguration));
        // Expression must be executed, otherwise the variable accessor is not properly configured
        MVEL.executeExpression(compiledExpression, context);

        dataTypeResolver = new MvelDataTypeResolver();

        assertThat(dataTypeResolver.resolve(EXPRESSION_VALUE, testEvent.getMessage(), compiledExpression), like(String.class, JSON, CUSTOM_ENCODING));
    }

    @Test
    public void returnsSessionVarDataType() throws Exception
    {
        final String expression = "sessionVars['foo']";
        final DataType expectedDataType = DataTypeFactory.create(String.class, JSON);
        expectedDataType.setEncoding(CUSTOM_ENCODING);

        MuleEvent testEvent = getTestEvent(TEST_MESSAGE);
        testEvent.getMessage().setProperty("foo", EXPRESSION_VALUE, PropertyScope.SESSION, expectedDataType);

        final ParserConfiguration parserConfiguration = MVELExpressionLanguage.createParserConfiguration(Collections.EMPTY_MAP);
        final MVELExpressionLanguageContext context = createMvelExpressionLanguageContext(testEvent, parserConfiguration);

        CompiledExpression compiledExpression = (CompiledExpression) compileExpression(expression, new ParserContext(parserConfiguration));
        // Expression must be executed, otherwise the variable accessor is not properly configured
        MVEL.executeExpression(compiledExpression, context);

        dataTypeResolver = new MvelDataTypeResolver();

        assertThat(dataTypeResolver.resolve(EXPRESSION_VALUE, testEvent.getMessage(), compiledExpression), like(String.class, JSON, CUSTOM_ENCODING));
    }

    private MVELExpressionLanguageContext createMvelExpressionLanguageContext(MuleEvent testEvent, ParserConfiguration parserConfiguration)
    {
        final MVELExpressionLanguageContext context = new MVELExpressionLanguageContext(parserConfiguration, muleContext);
        final StaticVariableResolverFactory staticContext = new StaticVariableResolverFactory(parserConfiguration, muleContext);
        final GlobalVariableResolverFactory globalContext = new GlobalVariableResolverFactory(Collections.EMPTY_MAP, Collections.EMPTY_MAP, parserConfiguration, muleContext);

        context.setNextFactory(new CachedMapVariableResolverFactory(Collections.EMPTY_MAP,
                                                                    new DelegateVariableResolverFactory(staticContext, new MessageVariableResolverFactory(
                                                                            parserConfiguration, muleContext, testEvent.getMessage(), new DelegateVariableResolverFactory(
                                                                            globalContext, new VariableVariableResolverFactory(parserConfiguration, muleContext, testEvent))))));
        return context;
    }

}
