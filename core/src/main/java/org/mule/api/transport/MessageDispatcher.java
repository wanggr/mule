/*
 * (c) 2003-2014 MuleSoft, Inc. This software is protected under international copyright
 * law. All use of this software is subject to MuleSoft's Master Subscription Agreement
 * (or other master license agreement) separately entered into in writing between you and
 * MuleSoft. If such an agreement is not in place, you may not use the software.
 */
package org.mule.api.transport;

import org.mule.api.MuleException;
import org.mule.api.MuleMessage;
import org.mule.api.endpoint.OutboundEndpoint;
import org.mule.api.lifecycle.Disposable;
import org.mule.api.lifecycle.LifecycleStateEnabled;
import org.mule.api.processor.MessageProcessor;

/**
 * Combine {@link MessageDispatching} with
 * various lifecycle methods for the actual instances doing message sending.
 */
public interface MessageDispatcher extends Connectable, MessageProcessor, LifecycleStateEnabled
{
    long RECEIVE_WAIT_INDEFINITELY = 0;
    long RECEIVE_NO_WAIT = -1;

    /**
     * This method can perform necessary state updates before any of the
     * {@link MessageDispatching} methods are invoked.
     * 
     * @see MessageDispatcherFactory#activate(OutboundEndpoint, MessageDispatcher)
     */
    void activate();

    /**
     * After sending a message, the dispatcher can use this method e.g. to
     * clean up its internal state (if it has any) or return pooled resources to
     * whereever it got them during {@link #activate()}.
     * 
     * @see MessageDispatcherFactory#passivate(OutboundEndpoint, MessageDispatcher)
     */
    void passivate();

    /**
     * Determines whether this dispatcher can be reused after message sending.
     * 
     * @return <code>true</code> if this dispatcher can be reused,
     *         <code>false</code> otherwise (for example when
     *         {@link Disposable#dispose()} has been called because an Exception was
     *         raised)
     */
    boolean validate();

    /**
     * Gets the connector for this dispatcher
     * 
     * @return the connector for this dispatcher
     */
    Connector getConnector();

    /**
     * @return the endpoint which we are dispatching events to
     */
    OutboundEndpoint getEndpoint();
    
    MuleMessage createMuleMessage(Object transportMessage, String encoding) throws MuleException;

    MuleMessage createMuleMessage(Object transportMessage) throws MuleException;
}
