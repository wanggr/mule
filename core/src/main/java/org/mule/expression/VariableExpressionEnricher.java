/*
 * (c) 2003-2014 MuleSoft, Inc. This software is protected under international copyright
 * law. All use of this software is subject to MuleSoft's Master Subscription Agreement
 * (or other master license agreement) separately entered into in writing between you and
 * MuleSoft. If such an agreement is not in place, you may not use the software.
 */
package org.mule.expression;

import org.mule.api.MuleMessage;
import org.mule.api.expression.ExpressionEnricher;
import org.mule.api.transport.PropertyScope;

public class VariableExpressionEnricher implements ExpressionEnricher
{

    public static final String NAME = "variable";

    public void enrich(String expression, MuleMessage message, Object object)
    {
        message.setProperty(expression, object, PropertyScope.INVOCATION);
    }

    public String getName()
    {
        return NAME;
    }

    public void setName(String name)
    {
        throw new UnsupportedOperationException();
    }

}
