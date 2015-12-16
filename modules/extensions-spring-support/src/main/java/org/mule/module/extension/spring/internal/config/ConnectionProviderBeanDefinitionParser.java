/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.extension.spring.internal.config;

import static org.mule.module.extension.spring.internal.xml.schema.model.SchemaConstants.MULE_NAMESPACE;
import static org.mule.module.extension.spring.internal.config.XmlExtensionParserUtils.parseConnectionProviderName;
import static org.mule.module.extension.spring.internal.config.XmlExtensionParserUtils.toElementDescriptorBeanDefinition;
import org.mule.extension.api.introspection.ConnectionProviderModel;

import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.xml.DomUtils;
import org.w3c.dom.Element;

/**
 * Implementation of {@link BaseExtensionBeanDefinitionParser} capable of parsing instances
 * which are compliant with the model defined in a {@link #providerModel}. The outcome of
 * this parser will be a {@link ConnectionProviderModel}.
 * <p>
 * It supports simple attributes, pojos, lists/sets of simple attributes, list/sets of beans,
 * and maps of simple attributes.
 *
 * @since 4.0
 */
final class ConnectionProviderBeanDefinitionParser extends BaseExtensionBeanDefinitionParser
{

    private final ConnectionProviderModel providerModel;

    public ConnectionProviderBeanDefinitionParser(ConnectionProviderModel providerModel)
    {
        super(ConnectionProviderFactoryBean.class);
        this.providerModel = providerModel;
    }

    @Override
    protected void doParse(BeanDefinitionBuilder builder, Element element, ParserContext parserContext)
    {
        if (element.getParentNode().getNamespaceURI().equals(MULE_NAMESPACE))
        {
            parseConnectionProviderName(element, builder);
        }

        builder.addConstructorArgValue(providerModel);
        builder.addConstructorArgValue(toElementDescriptorBeanDefinition(element));

        for (Element poolingProfileElement : DomUtils.getChildElements(element))
        {
            if (poolingProfileElement.getSchemaTypeInfo().isDerivedFrom(MULE_NAMESPACE, MULE_ABSTRACT_POOLING_PROFILE_TYPE.getLocalPart(), DERIVATION_EXTENSION))
            {
                builder.addPropertyValue("poolingProfile", getPoolingProfileParser().parse(poolingProfileElement, parserContext));
                break;
            }
        }
    }

    //TODO: MULE-9047 should have a better way of parsing this
    private BeanDefinitionParser getPoolingProfileParser()
    {
        OrphanDefinitionParser poolingProfileParser = new OrphanDefinitionParser(PoolingProfile.class, true);
        poolingProfileParser.addMapping("initialisationPolicy", PoolingProfile.POOL_INITIALISATION_POLICIES);
        poolingProfileParser.addMapping("exhaustedAction", PoolingProfile.POOL_EXHAUSTED_ACTIONS);
        return poolingProfileParser;
    }
}
