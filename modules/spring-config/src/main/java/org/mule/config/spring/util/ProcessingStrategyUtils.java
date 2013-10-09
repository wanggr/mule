/*
 * (c) 2003-2014 MuleSoft, Inc. This software is protected under international copyright
 * law. All use of this software is subject to MuleSoft's Master Subscription Agreement
 * (or other master license agreement) separately entered into in writing between you and
 * MuleSoft. If such an agreement is not in place, you may not use the software.
 */
package org.mule.config.spring.util;

import org.mule.construct.flow.DefaultFlowProcessingStrategy;
import org.mule.processor.strategy.AsynchronousProcessingStrategy;
import org.mule.processor.strategy.QueuedAsynchronousProcessingStrategy;
import org.mule.processor.strategy.QueuedThreadPerProcessorProcessingStrategy;
import org.mule.processor.strategy.SynchronousProcessingStrategy;
import org.mule.processor.strategy.ThreadPerProcessorProcessingStrategy;

import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.w3c.dom.Element;

public class ProcessingStrategyUtils
{

    private static String PROCESSING_STRATEGY_ATTRIBUTE_NAME = "processingStrategy";

    public static String DEFAULT_PROCESSING_STRATEGY = "default";
    public static String SYNC_PROCESSING_STRATEGY = "synchronous";
    public static String ASYNC_PROCESSING_STRATEGY = "asynchronous";
    public static String QUEUED_ASYNC_PROCESSING_STRATEGY = "queued-asynchronous";
    public static String THREAD_PER_PROCESSOR_PROCESSING_STRATEGY = "thread-per-processor";
    public static String QUEUED_THREAD_PER_PROCESSOR_PROCESSING_STRATEGY = "queued-thread-per-processor";

    public static void configureProcessingStrategy(Element element,
                                                   BeanDefinitionBuilder builder,
                                                   String defaultStrategy)
    {
        String processingStrategy = element.getAttribute(PROCESSING_STRATEGY_ATTRIBUTE_NAME);
        if (DEFAULT_PROCESSING_STRATEGY.equals(processingStrategy))
        {
            builder.addPropertyValue(PROCESSING_STRATEGY_ATTRIBUTE_NAME, new DefaultFlowProcessingStrategy());
        }
        else if (SYNC_PROCESSING_STRATEGY.equals(processingStrategy))
        {
            builder.addPropertyValue(PROCESSING_STRATEGY_ATTRIBUTE_NAME, new SynchronousProcessingStrategy());
        }
        else if (ASYNC_PROCESSING_STRATEGY.equals(processingStrategy))
        {
            builder.addPropertyValue(PROCESSING_STRATEGY_ATTRIBUTE_NAME, new AsynchronousProcessingStrategy());
        }
        else if (QUEUED_ASYNC_PROCESSING_STRATEGY.equals(processingStrategy))
        {
            builder.addPropertyValue(PROCESSING_STRATEGY_ATTRIBUTE_NAME,
                new QueuedAsynchronousProcessingStrategy());
        }
        else if (THREAD_PER_PROCESSOR_PROCESSING_STRATEGY.equals(processingStrategy))
        {
            builder.addPropertyValue(PROCESSING_STRATEGY_ATTRIBUTE_NAME,
                new ThreadPerProcessorProcessingStrategy());
        }
        else if (QUEUED_THREAD_PER_PROCESSOR_PROCESSING_STRATEGY.equals(processingStrategy))
        {
            builder.addPropertyValue(PROCESSING_STRATEGY_ATTRIBUTE_NAME,
                new QueuedThreadPerProcessorProcessingStrategy());
        }
        else if (null != processingStrategy && !processingStrategy.isEmpty())
        {
            builder.addPropertyValue(PROCESSING_STRATEGY_ATTRIBUTE_NAME, new RuntimeBeanReference(
                processingStrategy));
        }
    }

}
