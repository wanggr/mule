/*
 * (c) 2003-2014 MuleSoft, Inc. This software is protected under international copyright
 * law. All use of this software is subject to MuleSoft's Master Subscription Agreement
 * (or other master license agreement) separately entered into in writing between you and
 * MuleSoft. If such an agreement is not in place, you may not use the software.
 */
package org.mule.transformer.simple;

import org.mule.api.MuleMessage;
import org.mule.api.lifecycle.InitialisationException;
import org.mule.api.transformer.TransformerException;
import org.mule.transformer.AbstractMessageTransformer;
import org.mule.transformer.types.DataTypeFactory;
import org.mule.util.IOUtils;

/**
 * Loads a template and parses its content to resolve expressions. The order in which attempts to load the resource is
 * the following: from the file system, from a URL or from the classpath.
 */
public class ParseTemplateTransformer extends AbstractMessageTransformer
{
    private String location;
    private String template;

    public ParseTemplateTransformer()
    {
        registerSourceType(DataTypeFactory.OBJECT);
        setReturnDataType(DataTypeFactory.OBJECT);
    }

    @Override
    public void initialise() throws InitialisationException
    {
        super.initialise();
        loadTemplate();
    }

    private void loadTemplate() throws InitialisationException
    {
        try
        {
            if(location == null)
            {
                throw new IllegalArgumentException("Location cannot be null");
            }
            template = IOUtils.getResourceAsString(location, this.getClass());

        }
        catch(Exception e)
        {
            throw new InitialisationException(e, this);
        }
    }


    @Override
    public Object transformMessage(MuleMessage message, String outputEncoding) throws TransformerException
    {
        if(template == null)
        {
            throw new IllegalArgumentException("Template cannot be null");
        }

        return muleContext.getExpressionManager().parse(template, message);
    }

    public void setLocation(String location)
    {
        this.location = location;
    }
}
