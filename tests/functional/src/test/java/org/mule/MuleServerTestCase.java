/*
 * (c) 2003-2014 MuleSoft, Inc. This software is protected under international copyright
 * law. All use of this software is subject to MuleSoft's Master Subscription Agreement
 * (or other master license agreement) separately entered into in writing between you and
 * MuleSoft. If such an agreement is not in place, you may not use the software.
 */
package org.mule;

import java.security.Permission;

import org.mule.tck.junit4.AbstractMuleTestCase;
import org.mule.util.ClassUtils;
import org.mule.util.FilenameUtils;
import org.mule.util.JdkVersionUtils;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class MuleServerTestCase extends AbstractMuleTestCase
{

    @Test
    public void testMuleServer() throws Exception
    {
        MuleServer muleServer = new MuleServer();
        assertEquals(ClassUtils.getResource("mule-config.xml", MuleServer.class).toString(),
            muleServer.getConfigurationResources());
        assertEquals(MuleServer.CLASSNAME_DEFAULT_CONFIG_BUILDER, MuleServer.getConfigBuilderClassName());
        muleServer.initialize();
    }

    @Test
    public void testMuleServerResource() throws Exception
    {
        MuleServer muleServer = new MuleServer("org/mule/test/spring/config1/test-xml-mule2-config.xml");
        assertEquals("org/mule/test/spring/config1/test-xml-mule2-config.xml", muleServer.getConfigurationResources());
        assertEquals(MuleServer.CLASSNAME_DEFAULT_CONFIG_BUILDER, MuleServer.getConfigBuilderClassName());
        muleServer.initialize();
    }

    @Test
    public void testMuleServerConfigArg() throws Exception
    {
        MuleServer muleServer = new MuleServer(new String[]{"-config",
            "org/mule/test/spring/config1/test-xml-mule2-config.xml"});
        assertEquals("org/mule/test/spring/config1/test-xml-mule2-config.xml", muleServer.getConfigurationResources());
        assertEquals(MuleServer.CLASSNAME_DEFAULT_CONFIG_BUILDER, MuleServer.getConfigBuilderClassName());
        muleServer.initialize();
    }

    @Test
    public void testMuleServerMultipleSpringConfigArgs() throws Exception
    {
        MuleServer muleServer = new MuleServer(new String[]{"-config",
            "mule-config.xml,org/mule/test/spring/config1/test-xml-mule2-config.xml"});
        assertEquals("mule-config.xml,org/mule/test/spring/config1/test-xml-mule2-config.xml",
            muleServer.getConfigurationResources());
        assertEquals(MuleServer.CLASSNAME_DEFAULT_CONFIG_BUILDER, MuleServer.getConfigBuilderClassName());
        muleServer.initialize();
    }

    @Test
    public void testMuleServerBuilerArg() throws Exception
    {
        MuleServer muleServer = new MuleServer(new String[]{"-builder",
            "org.mule.config.spring.SpringXmlConfigurationBuilder"});
        assertEquals(ClassUtils.getResource("mule-config.xml", MuleServer.class).toString(),
            muleServer.getConfigurationResources());
        assertEquals("org.mule.config.spring.SpringXmlConfigurationBuilder", MuleServer.getConfigBuilderClassName());
        muleServer.initialize();
    }

    @Test
    public void testMuleServerSpringBuilerArg() throws Exception
    {
        MuleServer muleServer = new MuleServer(new String[]{"-builder", "spring"});
        assertEquals(ClassUtils.getResource("mule-config.xml", MuleServer.class).toString(),
            muleServer.getConfigurationResources());
        assertEquals("org.mule.config.spring.SpringXmlConfigurationBuilder", MuleServer.getConfigBuilderClassName());
        muleServer.initialize();
    }
    
    @Test
    public void testMuleServerAppConfig() throws Exception
    {
        MuleServer muleServer = new MuleServer(new String[]{
            "-config",
            "mule-config.xml",
            "-appconfig",
            "org/mule/test/spring/config1/test-app-config.properties"});
        muleServer.initialize();
        final String workingDirectory = MuleServer.muleContext.getConfiguration().getWorkingDirectory();
        assertTrue(FilenameUtils.separatorsToUnix(workingDirectory).endsWith("/target/.appT"));
    }
    
    @Test(expected=ExitException.class)
    public void testMuleServerJdkVersion()
    {
    	String javaVersion = System.setProperty("java.version", "1.5.0_12");
    	try
    	{
	    	try
	    	{
	    		JdkVersionUtils.validateJdk();
	    		fail("Test is invalid because the Jdk version or vendor is supposed to now be invalid");
	    	}
	    	catch (RuntimeException e)
	    	{
	    		// expected
	    	}
	    	SecurityManager manager = System.getSecurityManager();
	    	try
	    	{
	    		System.setSecurityManager(new NoExitSecurityManager());
		        MuleServer muleServer = new MuleServer();
		        fail("Jdk Version is invalid");
	    	}
	    	finally
	    	{
		        System.setSecurityManager(manager);
	    	}   
    	}
    	finally
    	{
    		System.setProperty("java.version", javaVersion);
    	}
    }
    
    private static final class NoExitSecurityManager extends SecurityManager
    {
    	@Override
    	public void checkPermission(Permission perm) {
    		// allow everything
    	}
    	
    	@Override
    	public void checkPermission(Permission perm, Object context) {
    		// allow everything
    	}
    	
    	@Override
    	public void checkExit(int status)
    	{
    		super.checkExit(status);
    		throw new ExitException(status);
    	}
    }
    
    private static class ExitException extends SecurityException 
    {
        public final int status;
        public ExitException(int status) 
        {
                super();
                this.status = status;
        }
    }

}
