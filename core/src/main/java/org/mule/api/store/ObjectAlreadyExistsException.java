/*
 * (c) 2003-2014 MuleSoft, Inc. This software is protected under international copyright
 * law. All use of this software is subject to MuleSoft's Master Subscription Agreement
 * (or other master license agreement) separately entered into in writing between you and
 * MuleSoft. If such an agreement is not in place, you may not use the software.
 */
package org.mule.api.store;

import org.mule.config.i18n.Message;

public class ObjectAlreadyExistsException extends ObjectStoreException
{
    public ObjectAlreadyExistsException()
    {
        super();
    }

    public ObjectAlreadyExistsException(Message message, Throwable cause)
    {
        super(message, cause);
    }

    public ObjectAlreadyExistsException(Message message)
    {
        super(message);
    }

    public ObjectAlreadyExistsException(Throwable cause)
    {
        super(cause);
    }
}


