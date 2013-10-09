/*
 * (c) 2003-2014 MuleSoft, Inc. This software is protected under international copyright
 * law. All use of this software is subject to MuleSoft's Master Subscription Agreement
 * (or other master license agreement) separately entered into in writing between you and
 * MuleSoft. If such an agreement is not in place, you may not use the software.
 */
package org.mule.example.errorhandler;

import org.mule.api.transformer.TransformerException;
import org.mule.transformer.AbstractTransformer;
import org.mule.transformer.types.DataTypeFactory;

/**
 * The <code>ErrorMessageToExceptionBean</code> transformer returns
 * the exception bean encapsulated by the ErrorMessage message payload.
 */
public class ErrorMessageToExceptionBean extends AbstractTransformer
{

    public ErrorMessageToExceptionBean()
    {
        registerSourceType(DataTypeFactory.create(ErrorMessage.class));
    }

    @Override
    public Object doTransform(Object src, String encoding) throws TransformerException
    {
        return ((ErrorMessage)src).getException();
    }

}
