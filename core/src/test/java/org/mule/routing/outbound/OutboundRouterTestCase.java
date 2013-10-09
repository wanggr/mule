/*
 * (c) 2003-2014 MuleSoft, Inc. This software is protected under international copyright
 * law. All use of this software is subject to MuleSoft's Master Subscription Agreement
 * (or other master license agreement) separately entered into in writing between you and
 * MuleSoft. If such an agreement is not in place, you may not use the software.
 */
package org.mule.routing.outbound;

import org.mule.api.MessagingException;
import org.mule.api.MuleEvent;
import org.mule.api.MuleException;
import org.mule.api.MuleMessage;
import org.mule.api.endpoint.OutboundEndpoint;
import org.mule.api.processor.MessageProcessor;
import org.mule.tck.junit4.AbstractMuleContextTestCase;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class OutboundRouterTestCase extends AbstractMuleContextTestCase
{
    @Test
    public void testAddGoodEndpoint() throws Exception
    {
        AbstractOutboundRouter router = new DummyOutboundRouter();
        OutboundEndpoint endpoint = getTestOutboundEndpoint("test");
        router.addRoute(endpoint);
        assertNotNull(router.getRoutes());
        assertTrue(router.getRoutes().contains(endpoint));
    }

    @Test
    public void testSetGoodEndpoints() throws Exception
    {
        List<MessageProcessor> list= new ArrayList<MessageProcessor>();
        list.add(getTestOutboundEndpoint("test"));
        list.add(getTestOutboundEndpoint("test"));
        AbstractOutboundRouter router = new DummyOutboundRouter();
        assertNotNull(router.getRoutes());
        assertEquals(0, router.getRoutes().size());
        router.addRoute(getTestOutboundEndpoint("test"));
        assertEquals(1, router.getRoutes().size());
        router.setRoutes(list);
        assertNotNull(router.getRoutes());
        assertEquals(2, router.getRoutes().size());
    }

    @Test
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void testSetBadEndpoints() throws Exception
    {
        List list = new ArrayList();
        list.add(getTestInboundEndpoint("test"));
        list.add(getTestOutboundEndpoint("test"));
        AbstractOutboundRouter router = new DummyOutboundRouter();

        try
        {
            router.setRoutes(list);
            fail("Invalid endpoint: Expecting an exception");
        }
        catch(Exception e)
        {
            assertEquals(ClassCastException.class, e.getClass());
        }
    }

    @Test
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void testSetBad2Endpoints() throws Exception
    {
        List list= new ArrayList();
        list.add(getTestOutboundEndpoint("test"));
        list.add(getTestInboundEndpoint("test"));
        AbstractOutboundRouter router = new DummyOutboundRouter();

        try
        {
            router.setRoutes(list);
            fail("Invalid endpoint: Expecting an exception");
        }
        catch(Exception e)
        {
            assertEquals(ClassCastException.class, e.getClass());
        }
    }

    private static class DummyOutboundRouter extends AbstractOutboundRouter
    {
        public boolean isMatch(MuleMessage message) throws MuleException
        {
            return false;
        }

        @Override
        protected MuleEvent route(MuleEvent event) throws MessagingException
        {
            return null;
        }
    }
}
