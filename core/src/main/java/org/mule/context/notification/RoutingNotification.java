/*
 * (c) 2003-2014 MuleSoft, Inc. This software is protected under international copyright
 * law. All use of this software is subject to MuleSoft's Master Subscription Agreement
 * (or other master license agreement) separately entered into in writing between you and
 * MuleSoft. If such an agreement is not in place, you may not use the software.
 */
package org.mule.context.notification;

import org.mule.api.MuleMessage;
import org.mule.api.context.notification.ServerNotification;

/**
 * Is fired by routers.  Currently only Async-Reply routers use this when an event is received for an
 * event group that has already been processed.
 */
public class RoutingNotification extends ServerNotification
{
    /**
     * Serial version
     */
    private static final long serialVersionUID = -6455441938378523145L;
    public static final int MISSED_ASYNC_REPLY = ROUTING_EVENT_ACTION_START_RANGE + 1;
    public static final int ASYNC_REPLY_TIMEOUT = ROUTING_EVENT_ACTION_START_RANGE + 2;
    public static final int CORRELATION_TIMEOUT = ROUTING_EVENT_ACTION_START_RANGE + 3;
    public static final int MISSED_AGGREGATION_GROUP_EVENT = ROUTING_EVENT_ACTION_START_RANGE + 4;

    
    static {
        registerAction("missed async reply", MISSED_ASYNC_REPLY);
        registerAction("async reply timeout", ASYNC_REPLY_TIMEOUT);
        registerAction("correlation timeout", CORRELATION_TIMEOUT);
        registerAction("missed aggregation group event", MISSED_AGGREGATION_GROUP_EVENT);
    }

    public RoutingNotification(MuleMessage resource, String identifier, int action)
    {
        super(cloneMessage(resource), action);
        resourceIdentifier = identifier;
    }

    @Override
    protected String getPayloadToString()
    {
        try
        {
            return ((MuleMessage)source).getPayloadAsString();
        }
        catch (Exception e)
        {
            return source.toString();
        }
    }

    @Override
    public String getType()
    {
        if (action == MISSED_ASYNC_REPLY)
        {
            return TYPE_WARNING;
        }
        else if (action == ASYNC_REPLY_TIMEOUT)
        {
            return TYPE_WARNING;
        }
        else if (action == MISSED_AGGREGATION_GROUP_EVENT)
        {
            return TYPE_WARNING;
        }
        else
        {
            return TYPE_INFO;
        }
    }

}
