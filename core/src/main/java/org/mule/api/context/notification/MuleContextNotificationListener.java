/*
 * (c) 2003-2014 MuleSoft, Inc. This software is protected under international copyright
 * law. All use of this software is subject to MuleSoft's Master Subscription Agreement
 * (or other master license agreement) separately entered into in writing between you and
 * MuleSoft. If such an agreement is not in place, you may not use the software.
 */
package org.mule.api.context.notification;

import org.mule.context.notification.MuleContextNotification;


/**
 * <code>MuleContextNotificationListener</code> is an observer interface that objects
 * can implement and then register themselves with the Mule manager to be notified
 * when a Manager event occurs.
 */
public interface MuleContextNotificationListener<T extends MuleContextNotification> extends ServerNotificationListener<MuleContextNotification>
{
    // no methods
}
