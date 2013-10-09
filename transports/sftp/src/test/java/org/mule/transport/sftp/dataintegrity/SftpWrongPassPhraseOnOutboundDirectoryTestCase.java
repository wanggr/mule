/*
 * (c) 2003-2014 MuleSoft, Inc. This software is protected under international copyright
 * law. All use of this software is subject to MuleSoft's Master Subscription Agreement
 * (or other master license agreement) separately entered into in writing between you and
 * MuleSoft. If such an agreement is not in place, you may not use the software.
 */
package org.mule.transport.sftp.dataintegrity;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.mule.api.MuleException;
import org.mule.api.endpoint.ImmutableEndpoint;
import org.mule.api.transport.DispatchException;
import org.mule.module.client.MuleClient;
import org.mule.transport.sftp.SftpClient;

import com.jcraft.jsch.SftpException;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;
import org.junit.runners.Parameterized.Parameters;

/**
 * Verify that the original file is not lost if the password for the outbound
 * endpoint is wrong
 */
public class SftpWrongPassPhraseOnOutboundDirectoryTestCase extends AbstractSftpDataIntegrityTestCase
{
    private static String INBOUND_ENDPOINT_NAME = "inboundEndpoint";

    public SftpWrongPassPhraseOnOutboundDirectoryTestCase(ConfigVariant variant, String configResources)
    {
        super(variant, configResources);
    }

    @Parameters
    public static Collection<Object[]> parameters()
    {
        return Arrays.asList(new Object[][]{
            {ConfigVariant.SERVICE, "dataintegrity/sftp-wrong-passphrase-config-service.xml"},
            {ConfigVariant.FLOW, "dataintegrity/sftp-wrong-passphrase-config-flow.xml"}});
    }

    // @Override
    // protected void doSetUp() throws Exception
    // {
    // super.doSetUp();
    //
    // // Delete the in & outbound directories
    // initEndpointDirectory(INBOUND_ENDPOINT_NAME);
    // }

    /**
     * The outbound directory doesn't exist. The source file should still exist
     * 
     * @throws Exception
     */
    @Test
    public void testWrongPassPhraseOnOutboundDirectory() throws Exception
    {
        MuleClient muleClient = new MuleClient(muleContext);
        assertTrue(muleContext.isStarted());
        // Send an file to the SFTP server, which the inbound-outboundEndpoint then
        // can pick up
        final Exception exception = dispatchAndWaitForException(new DispatchParameters(INBOUND_ENDPOINT_NAME,
            null), "sftp", "service");
        assertNotNull(exception);
        assertTrue("expected DispatchException, but got " + exception.getClass().toString(),
            exception instanceof DispatchException);
        assertTrue("expected IOException, but got " + exception.getCause().getClass().toString(),
            exception.getCause() instanceof IOException);
        assertTrue("wrong message : " + exception.getCause().getMessage(), exception.getCause()
            .getMessage()
            .startsWith("Error during login to"));

        SftpClient sftpClient = getSftpClient(muleClient, INBOUND_ENDPOINT_NAME);
        try
        {
            ImmutableEndpoint endpoint = (ImmutableEndpoint) muleClient.getProperty(INBOUND_ENDPOINT_NAME);
            assertTrue("The inbound file should still exist",
                super.verifyFileExists(sftpClient, endpoint.getEndpointURI(), FILENAME));
        }
        finally
        {
            sftpClient.disconnect();
        }
    }

    /**
     * Ensures that the directory exists and is writable by deleting the directory
     * and then recreate it. Overrides inherited behavior to use working credentials.
     */
    protected void initEndpointDirectory(String endpointName)
        throws MuleException, IOException, SftpException
    {
        // MuleClient muleClient = new MuleClient(muleContext);
        // SftpClient sftpClient = getSftpClient(muleClient, endpointName);
        // try
        // {
        // ChannelSftp channelSftp = sftpClient.getChannelSftp();
        // try
        // {
        // recursiveDelete(muleClient, sftpClient, endpointName, "");
        // }
        // catch (IOException e)
        // {
        // if (logger.isErrorEnabled())
        // logger.error("Failed to recursivly delete endpoint " + endpointName, e);
        // }
        //
        // String path = getPathByEndpoint(muleClient, sftpClient, endpointName);
        // channelSftp.mkdir(path);
        // }
        // finally
        // {
        // sftpClient.disconnect();
        // if (logger.isDebugEnabled()) logger.debug("Done init endpoint directory: "
        // + endpointName);
        // }
    }

}
