/*
 * (c) 2003-2014 MuleSoft, Inc. This software is protected under international copyright
 * law. All use of this software is subject to MuleSoft's Master Subscription Agreement
 * (or other master license agreement) separately entered into in writing between you and
 * MuleSoft. If such an agreement is not in place, you may not use the software.
 */
package org.mule.module.jersey;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.junit.runners.Parameterized.Parameters;
import org.mule.api.MuleMessage;
import org.mule.module.client.MuleClient;
import org.mule.tck.AbstractServiceAndFlowTestCase;
import org.mule.transport.http.HttpConnector;
import org.mule.transport.http.HttpConstants;

/**
 * Tests that the jersey:resources component can handle multiple components
 * correctly.
 */
public class MultipleResourcesTestCase extends AbstractServiceAndFlowTestCase
{

    public MultipleResourcesTestCase(ConfigVariant variant, String configResources)
    {
        super(variant, configResources);
    }
    
    @Parameters
    public static Collection<Object[]> parameters()
    {
        return Arrays.asList(new Object[][]{
            {ConfigVariant.SERVICE, "multiple-resources-conf-service.xml"},
            {ConfigVariant.FLOW, "multiple-resources-conf-flow.xml"}
        });
    }

    @Test
    public void testParams() throws Exception
    {
        MuleClient client = new MuleClient(muleContext);

        Map<String, String> props = new HashMap<String, String>();
        props.put(HttpConnector.HTTP_METHOD_PROPERTY, HttpConstants.METHOD_GET);
        MuleMessage result = client.send("http://localhost:63081/helloworld/sayHelloWithUri/Dan", "", props);
        assertEquals((Integer)200, result.getInboundProperty(HttpConnector.HTTP_STATUS_PROPERTY, 0));
        assertEquals("Hello Dan", result.getPayloadAsString());

        result = client.send("http://localhost:63081/anotherworld/sayHelloWithUri/Dan", "", props);
        assertEquals((Integer)200, result.getInboundProperty(HttpConnector.HTTP_STATUS_PROPERTY, 0));
        assertEquals("Bonjour Dan", result.getPayloadAsString());
    }

}
