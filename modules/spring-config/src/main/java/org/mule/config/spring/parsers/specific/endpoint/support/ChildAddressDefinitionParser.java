/*
 * (c) 2003-2014 MuleSoft, Inc. This software is protected under international copyright
 * law. All use of this software is subject to MuleSoft's Master Subscription Agreement
 * (or other master license agreement) separately entered into in writing between you and
 * MuleSoft. If such an agreement is not in place, you may not use the software.
 */
package org.mule.config.spring.parsers.specific.endpoint.support;

import org.mule.config.spring.parsers.generic.ChildDefinitionParser;
import org.mule.config.spring.parsers.processors.AddAttribute;
import org.mule.endpoint.URIBuilder;

/**
 * Generate an Endpoint URI from simple address components.
 */
public class ChildAddressDefinitionParser extends ChildDefinitionParser
{

    public static final boolean META = true;
    public static final boolean PROTOCOL = false;

    public ChildAddressDefinitionParser(String protocol)
    {
        this(protocol, PROTOCOL);
    }

    public ChildAddressDefinitionParser(String metaOrProtocol, boolean isMeta)
    {
        super(EndpointUtils.URI_BUILDER_ATTRIBUTE, URIBuilder.class);
        registerPreProcessor(
                new AddAttribute(
                        isMeta ? URIBuilder.META : URIBuilder.PROTOCOL,
                        metaOrProtocol));
    }

}
