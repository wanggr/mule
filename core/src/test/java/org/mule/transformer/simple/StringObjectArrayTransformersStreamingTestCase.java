/*
 * (c) 2003-2014 MuleSoft, Inc. This software is protected under international copyright
 * law. All use of this software is subject to MuleSoft's Master Subscription Agreement
 * (or other master license agreement) separately entered into in writing between you and
 * MuleSoft. If such an agreement is not in place, you may not use the software.
 */
package org.mule.transformer.simple;

import org.mule.util.IOUtils;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

public class StringObjectArrayTransformersStreamingTestCase extends StringObjectArrayTransformersTestCase
{

    public Object getTestData()
    {
        String testData = (String) super.getTestData();
        return new ByteArrayInputStream(testData.getBytes());
    }

    public boolean compareRoundtripResults(Object src, Object result)
    {
        InputStream input = (InputStream) src;
        String expected = IOUtils.toString(input);
        
        return expected.equals(result);
    }
    
}


