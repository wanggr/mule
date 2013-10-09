/*
 * (c) 2003-2014 MuleSoft, Inc. This software is protected under international copyright
 * law. All use of this software is subject to MuleSoft's Master Subscription Agreement
 * (or other master license agreement) separately entered into in writing between you and
 * MuleSoft. If such an agreement is not in place, you may not use the software.
 */
package org.mule.test.integration.exceptions;

import org.mule.api.MuleMessage;
import org.mule.module.client.MuleClient;
import org.mule.tck.AbstractServiceAndFlowTestCase;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.junit.runners.Parameterized.Parameters;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

public class ExceptionStrategyMessagePropertiesTestCase extends AbstractServiceAndFlowTestCase
{
    private final int numMessages = 100;

    @Parameters
    public static Collection<Object[]> parameters()
    {
        return Arrays.asList(new Object[][]{
            {ConfigVariant.SERVICE, "org/mule/test/integration/exceptions/exception-strategy-message-properties-service.xml"},
            {ConfigVariant.FLOW, "org/mule/test/integration/exceptions/exception-strategy-message-properties-flow.xml"}
        });
    }

    public ExceptionStrategyMessagePropertiesTestCase(ConfigVariant variant, String configResources)
    {
        super(variant, configResources);
    }

    @Test
    public void testException() throws Exception
    {
        Thread tester1 = new Tester();
        Thread tester2 = new Tester();
        tester1.start();
        tester2.start();

        MuleClient client = new MuleClient(muleContext);
        MuleMessage msg;
        for (int i = 0; i < numMessages; ++i)
        {
            msg = client.request("vm://error", 5000);
            assertNotNull(msg);
            assertEquals("bar", msg.getInboundProperty("foo"));
        }
    }

    class Tester extends Thread
    {
        @Override
        public void run()
        {
            try
            {
                MuleClient client = new MuleClient(muleContext);

                Map<String, Object> props = new HashMap<String, Object>();
                props.put("foo", "bar");
                for (int i = 0; i < numMessages; ++i)
                {
                    client.dispatch("vm://in", "test", props);
                }
            }
            catch (Exception e)
            {
                fail(e.getMessage());
            }
        }
    }
}
