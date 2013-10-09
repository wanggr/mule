/*
 * (c) 2003-2014 MuleSoft, Inc. This software is protected under international copyright
 * law. All use of this software is subject to MuleSoft's Master Subscription Agreement
 * (or other master license agreement) separately entered into in writing between you and
 * MuleSoft. If such an agreement is not in place, you may not use the software.
 */
package org.mule.transport.vm.functional.transactions;

import org.hamcrest.core.Is;
import org.hamcrest.core.IsInstanceOf;
import org.hamcrest.core.IsNull;
import org.junit.Before;
import org.junit.Test;
import org.mule.api.MuleEventContext;
import org.mule.api.MuleMessage;
import org.mule.api.lifecycle.Callable;
import org.mule.api.transformer.TransformerException;
import org.mule.config.i18n.CoreMessages;
import org.mule.message.DefaultExceptionPayload;
import org.mule.message.ExceptionMessage;
import org.mule.module.client.MuleClient;
import org.mule.tck.junit4.FunctionalTestCase;
import org.mule.transformer.AbstractTransformer;
import org.mule.transport.NullPayload;
import org.mule.util.concurrent.Latch;

import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

public class VmExceptionStrategyRequestResponseTestCase extends FunctionalTestCase
{

    public static final int TIMEOUT = 3000;
    public static final int TINY_TIMEOUT = 300;
    public static final String ORIGINAL_MESSAGE = "some message";
    private static Latch outboundComponentLatch;
    private static Latch deadLetterQueueLatch;
    private static boolean outboundComponentReached;

    @Override
    protected String getConfigResources()
    {
        return "vm/vm-exception-strategy-config-request-response.xml";
    }

    @Before
    public void setUp() throws Exception
    {
        outboundComponentLatch = new Latch();
        deadLetterQueueLatch = new Latch();
        outboundComponentReached = false;
    }

    @Test
    public void testDeadLetterQueueWithInboundEndpointException() throws Exception
    {
        MuleClient muleClient = new MuleClient(muleContext);
        MuleMessage response = muleClient.send("vm://in1", ORIGINAL_MESSAGE, null);
        if (!deadLetterQueueLatch.await(TIMEOUT, TimeUnit.MILLISECONDS)) {
            fail("dead letter queue must be reached");
        }
        assertThat(outboundComponentReached, Is.is(false));
        assertThat(response, IsNull.<Object>notNullValue());
        assertThat(response.getPayload(),IsInstanceOf.instanceOf(NullPayload.class));
        assertThat(response.getExceptionPayload(), IsNull.<Object>notNullValue());
        assertThat(response.getExceptionPayload(), IsInstanceOf.instanceOf(DefaultExceptionPayload.class));
        assertThat(muleClient.request("vm://out1", TINY_TIMEOUT), IsNull.<Object>nullValue());
    }

    @Test
    public void testDeadLetterQueueWithInboundEndpointResponseException() throws Exception
    {
        MuleClient muleClient = new MuleClient(muleContext);
        MuleMessage response = muleClient.send("vm://in2", ORIGINAL_MESSAGE, null);
        //TODO PLG - ES - fix this, dlq is failing because transaction was already commited by next flow despite is called using one-way with vm
        /*if (!deadLetterQueueLatch.await(TIMEOUT, MILLISECONDS)) {
            fail("dead letter queue must be reached");
        }*/
        assertThat(response, IsNull.<Object>notNullValue());
        assertThat(response.getPayload(),IsInstanceOf.instanceOf(NullPayload.class));
        assertThat(response.getExceptionPayload(), IsNull.<Object>notNullValue());
        assertThat(response.getExceptionPayload(), IsInstanceOf.instanceOf(DefaultExceptionPayload.class));
        assertThat(muleClient.request("vm://out2", TINY_TIMEOUT), IsNull.<Object>nullValue());
        if (!outboundComponentLatch.await(TINY_TIMEOUT, TimeUnit.MILLISECONDS))
        {
            fail("outbound component not reached");
        }
    }

    @Test
    public void testDeadLetterQueueWithComponentException() throws Exception
    {
        MuleClient muleClient = new MuleClient(muleContext);
        MuleMessage response = muleClient.send("vm://in3", ORIGINAL_MESSAGE, null);
        if (!deadLetterQueueLatch.await(TIMEOUT, TimeUnit.MILLISECONDS)) {
            fail("dead letter queue must be reached");
        }
        assertThat(outboundComponentReached, Is.is(false));
        assertThat(response, IsNull.<Object>notNullValue());
        assertThat(response.getPayload(),IsInstanceOf.instanceOf(NullPayload.class));
        assertThat(response.getExceptionPayload(), IsNull.<Object>notNullValue());
        assertThat(response.getExceptionPayload(), IsInstanceOf.instanceOf(DefaultExceptionPayload.class));
        assertThat(muleClient.request("vm://out3", TINY_TIMEOUT), IsNull.<Object>nullValue());
    }

    @Test
    public void testDeadLetterQueueWithOutboundEndpointException() throws Exception
    {
        MuleClient muleClient = new MuleClient(muleContext);
        MuleMessage response = muleClient.send("vm://in4", ORIGINAL_MESSAGE, null);
        if (!deadLetterQueueLatch.await(TIMEOUT, TimeUnit.MILLISECONDS)) {
            fail("dead letter queue must be reached");
        }
        assertThat(outboundComponentReached, Is.is(false));
        assertThat(response, IsNull.<Object>notNullValue());
        assertThat(response.getPayload(),IsInstanceOf.instanceOf(NullPayload.class));
        assertThat(response.getExceptionPayload(), IsNull.<Object>notNullValue());
        assertThat(response.getExceptionPayload(), IsInstanceOf.instanceOf(DefaultExceptionPayload.class));
        assertThat(muleClient.request("vm://out4", TINY_TIMEOUT), IsNull.<Object>nullValue());
    }

    @Test
    public void testDeadLetterQueueWithOutboundEndpointResponseException() throws Exception
    {
        MuleClient muleClient = new MuleClient(muleContext);
        MuleMessage response = muleClient.send("vm://in5", ORIGINAL_MESSAGE, null);
        //TODO PLG - ES - fix this issue, the response must have an exception since there was a failire in the flow. It seems that response chain was not executed
        /*assertThat(response, IsNull.<Object>notNullValue());
        assertThat(response.getPayload(),IsInstanceOf.instanceOf(NullPayload.class));
        assertThat(response.getExceptionPayload(), IsNull.<Object>notNullValue());
        assertThat(response.getExceptionPayload(), IsInstanceOf.instanceOf(DefaultExceptionPayload.class));*/
        assertThat(muleClient.request("vm://out5", TINY_TIMEOUT), IsNull.<Object>nullValue());
        if (!outboundComponentLatch.await(TINY_TIMEOUT, TimeUnit.MILLISECONDS))
        {
            fail("outbound component not reached");
        }
        //TODO PLG - ES - fix this issue. dead letter queue component is not reached
        /*if (!deadLetterQueueLatch.await(TIMEOUT, MILLISECONDS)) {
            fail("dead letter queue must be reached");
        }*/
    }

    public static class FailingTransformer extends AbstractTransformer
    {

        @Override
        protected Object doTransform(Object src, String enc) throws TransformerException
        {
            throw new TransformerException(CoreMessages.failedToBuildMessage(), this);
        }
    }

    public static class DeadLetterQueueComponent implements Callable
    {

        public Object onCall(MuleEventContext eventContext) throws Exception
        {
            deadLetterQueueLatch.release();
            MuleMessage message = eventContext.getMessage();
            assertThat(message, IsNull.<Object>notNullValue());
            assertThat(message.getExceptionPayload(), IsNull.<Object>nullValue());
            assertThat(message.getPayload(), IsInstanceOf.instanceOf(ExceptionMessage.class));
            return eventContext.getMessage();
        }
    }

    public static class OutboundComponent implements Callable
    {

        public Object onCall(MuleEventContext eventContext) throws Exception
        {
            outboundComponentLatch.release();
            return eventContext.getMessage();
        }
    }
}
