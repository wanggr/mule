/*
 * (c) 2003-2014 MuleSoft, Inc. This software is protected under international copyright
 * law. All use of this software is subject to MuleSoft's Master Subscription Agreement
 * (or other master license agreement) separately entered into in writing between you and
 * MuleSoft. If such an agreement is not in place, you may not use the software.
 */
package org.mule.tck.functional;

import org.mule.api.MuleEventContext;
import org.mule.api.transaction.Transaction;
import org.mule.api.transaction.TransactionException;
import org.mule.config.i18n.MessageFactory;

/**
 * This service is useful for unit tests involving transactionality because it
 * will roll back the current transaction upon message arrival.  
 */
public class TransactionalFunctionalTestComponent extends FunctionalTestComponent
{
    private boolean expectTransaction = true;
    private boolean rollback = true;

    /** {@inheritDoc} */
    public Object onCall(MuleEventContext context) throws Exception
    {
        Object replyMessage = super.onCall(context);

        if (expectTransaction)
        {
            // Verify transaction has begun.
            Transaction currentTx = context.getCurrentTransaction();
            if (currentTx == null || !currentTx.isBegun())
            {    
                context.setStopFurtherProcessing(true);
                throw new TransactionException(MessageFactory.createStaticMessage("Trying to roll back transaction but no transaction is underway."));
            }            

            if (rollback)
            {
                // Mark the transaction for rollback.
                logger.info("@@@@ Rolling back transaction @@@@");
                currentTx.setRollbackOnly();
            }        
        }
        
        return replyMessage;
    }

    public boolean isRollback()
    {
        return rollback;
    }

    public void setRollback(boolean rollback)
    {
        this.rollback = rollback;
    }

    public boolean isExpectTransaction()
    {
        return expectTransaction;
    }

    public void setExpectTransaction(boolean expectTransaction)
    {
        this.expectTransaction = expectTransaction;
    }
}

