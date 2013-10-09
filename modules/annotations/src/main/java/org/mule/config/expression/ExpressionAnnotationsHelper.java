/*
 * (c) 2003-2014 MuleSoft, Inc. This software is protected under international copyright
 * law. All use of this software is subject to MuleSoft's Master Subscription Agreement
 * (or other master license agreement) separately entered into in writing between you and
 * MuleSoft. If such an agreement is not in place, you may not use the software.
 */
package org.mule.config.expression;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mule.api.MuleContext;
import org.mule.api.expression.ExpressionAnnotationParser;
import org.mule.api.lifecycle.InitialisationException;
import org.mule.api.registry.RegistrationException;
import org.mule.api.transformer.TransformerException;
import org.mule.config.AnnotationsParserFactory;
import org.mule.config.i18n.AnnotationsMessages;
import org.mule.expression.transformers.ExpressionArgument;
import org.mule.expression.transformers.ExpressionTransformer;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * TODO
 */
public class ExpressionAnnotationsHelper
{
    protected static Log logger = LogFactory.getLog(ExpressionAnnotationsHelper.class);

    public static ExpressionTransformer getTransformerForMethodWithAnnotations(Method method, MuleContext context) throws TransformerException, InitialisationException
    {
        ExpressionTransformer trans = new ExpressionTransformer();
        trans.setMuleContext(context);

        Annotation[][] annotations = method.getParameterAnnotations();

        for (int i = 0; i < annotations.length; i++)
        {
            Annotation[] annotation = annotations[i];
            for (int j = 0; j < annotation.length; j++)
            {
                Annotation ann = annotation[j];
                ExpressionArgument arg = parseAnnotation(ann, method.getParameterTypes()[i], context);

                if (arg != null)
                {
                    trans.addArgument(arg);
                }
            }
        }
        trans.initialise();
        return trans;
    }

    static synchronized ExpressionArgument parseAnnotation(Annotation annotation, 
        Class<?> paramType, MuleContext muleContext)
    {
        AnnotationsParserFactory factory;
        try
        {
            factory = muleContext.getRegistry().lookupObject(AnnotationsParserFactory.class);
        }
        catch (RegistrationException e)
        {
            //TODO better exception message
            throw new IllegalArgumentException(AnnotationsMessages.noParserFoundForAnnotation(annotation).getMessage());
        }
        
        ExpressionAnnotationParser parser = factory.getExpressionParser(annotation);
        if (parser == null)
        {
            if (logger.isDebugEnabled())
            {
                logger.debug(AnnotationsMessages.noParserFoundForAnnotation(annotation).getMessage());
            }
            return null;
        }
        return parser.parse(annotation, paramType);
    }
}
