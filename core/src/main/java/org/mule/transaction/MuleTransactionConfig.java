/*
 * (c) 2003-2014 MuleSoft, Inc. This software is protected under international copyright
 * law. All use of this software is subject to MuleSoft's Master Subscription Agreement
 * (or other master license agreement) separately entered into in writing between you and
 * MuleSoft. If such an agreement is not in place, you may not use the software.
 */
package org.mule.transaction;

import org.mule.api.MuleContext;
import org.mule.api.MuleRuntimeException;
import org.mule.api.context.MuleContextAware;
import org.mule.api.transaction.Transaction;
import org.mule.api.transaction.TransactionConfig;
import org.mule.api.transaction.TransactionFactory;
import org.mule.config.i18n.CoreMessages;
import org.mule.transaction.constraints.ConstraintFilter;
import org.mule.util.ClassUtils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * <p/> <code>MuleTransactionConfig</code> defines transaction configuration for a
 * transactional endpoint.
 */
public class MuleTransactionConfig implements TransactionConfig, MuleContextAware
{
    /**
     * logger used by this class
     */
    protected static final Log logger = LogFactory.getLog(MuleTransactionConfig.class);

    public static final String ACTION_NONE_STRING = "NONE";
    public static final String ACTION_ALWAYS_BEGIN_STRING = "ALWAYS_BEGIN";
    public static final String ACTION_BEGIN_OR_JOIN_STRING = "BEGIN_OR_JOIN";
    public static final String ACTION_ALWAYS_JOIN_STRING = "ALWAYS_JOIN";
    public static final String ACTION_JOIN_IF_POSSIBLE_STRING = "JOIN_IF_POSSIBLE";
    public static final String ACTION_NEVER_STRING = "NEVER";
    public static final String ACTION_INDIFFERENT_STRING = "INDIFFERENT";
    public static final String ACTION_NOT_SUPPORTED_STRING = "NOT_SUPPORTED";

    private TransactionFactory factory;

    private byte action = ACTION_DEFAULT;

    private ConstraintFilter constraint = null;

    private Integer timeout;

    private boolean interactWithExternal = false;

    public MuleTransactionConfig()
    {
    }

    public MuleTransactionConfig(byte action)
    {
        this.action = action;
    }

    public void setMuleContext(MuleContext context)
    {
        // override only if not set in config
        if (this.timeout == null)
        {
            this.timeout = context.getConfiguration().getDefaultTransactionTimeout();
        }
    }

    public TransactionFactory getFactory()
    {
        return factory;
    }

    public void setFactory(TransactionFactory factory)
    {
        if (factory == null)
        {
            throw new IllegalArgumentException("Transaction Factory cannot be null");
        }
        this.factory = factory;
    }

    public byte getAction()
    {
        return action;
    }

    public void setAction(byte action)
    {
        this.action = action;

    }

    public boolean isInteractWithExternal()
    {
        return interactWithExternal;
    }

    public void setInteractWithExternal(boolean interactWithExternal)
    {
        this.interactWithExternal = interactWithExternal;
    }

    public void setActionAsString(String action)
    {
        if (ACTION_ALWAYS_BEGIN_STRING.equals(action))
        {
            this.action = ACTION_ALWAYS_BEGIN;
        }
        else if (ACTION_BEGIN_OR_JOIN_STRING.equals(action))
        {
            this.action = ACTION_BEGIN_OR_JOIN;
        }
        else if (ACTION_ALWAYS_JOIN_STRING.equals(action))
        {
            this.action = ACTION_ALWAYS_JOIN;
        }
        else if (ACTION_JOIN_IF_POSSIBLE_STRING.equals(action))
        {
            this.action = ACTION_JOIN_IF_POSSIBLE;
        }
        else if (ACTION_NONE_STRING.equals(action))
        {
            this.action = ACTION_NONE;
        }
        else if (ACTION_NEVER_STRING.equals(action))
        {
            this.action = ACTION_NEVER;
        }
        else if (ACTION_INDIFFERENT_STRING.equals(action))
        {
            this.action = ACTION_INDIFFERENT;
        }
        else if (ACTION_NOT_SUPPORTED_STRING.equals(action))
        {
            this.action = ACTION_NOT_SUPPORTED;
        }
        else
        {
            throw new IllegalArgumentException("Action " + action + " is not recognised as a begin action.");
        }
    }

    public String getActionAsString()
    {
        switch (action)
        {
            case ACTION_ALWAYS_BEGIN:
                return ACTION_ALWAYS_BEGIN_STRING;
            case ACTION_BEGIN_OR_JOIN:
                return ACTION_BEGIN_OR_JOIN_STRING; 
            case ACTION_ALWAYS_JOIN:
                return ACTION_ALWAYS_JOIN_STRING;
            case ACTION_JOIN_IF_POSSIBLE:
                return ACTION_JOIN_IF_POSSIBLE_STRING;
            case ACTION_NONE:
                return ACTION_NONE_STRING;
            case ACTION_INDIFFERENT:
                return ACTION_INDIFFERENT_STRING;
            default :
                return ACTION_NEVER_STRING;
        }
    }

    /**
     * Will the result, at the end of running the transaction template, be an active transaction?
     */
    public boolean isTransacted()
    {
        if (action == ACTION_NEVER || action == ACTION_NONE || action == ACTION_NOT_SUPPORTED)
        {
            return false;
        }
        if (factory == null)
        {
            if (action != ACTION_INDIFFERENT)
            {
                // TODO use TransactionException here? This causes API changes as TE is a checked exception ...
                throw new MuleRuntimeException(CoreMessages.transactionFactoryIsMandatory(getActionAsString()));
            }

        }
        else if (!factory.isTransacted())
        {
            return false;
        }

        switch (action)
        {
            case ACTION_ALWAYS_BEGIN:
            case ACTION_ALWAYS_JOIN:
            case ACTION_BEGIN_OR_JOIN:
                return true;

            case ACTION_JOIN_IF_POSSIBLE:
            case ACTION_INDIFFERENT:
                return TransactionCoordination.getInstance().getTransaction() != null;

            default:
                // should not happen
                return false;

        }
    }
    
    public boolean isConfigured()
    {
        return factory != null;
    }

    public ConstraintFilter getConstraint()
    {
        if (constraint == null)
        {
            return null;
        }
        try
        {
            return (ConstraintFilter) constraint.clone();
        }
        catch (CloneNotSupportedException e)
        {
            logger.fatal("Failed to clone ConstraintFilter: " + e.getMessage(), e);
            return constraint;
        }
    }

    public void setConstraint(ConstraintFilter constraint)
    {
        this.constraint = constraint;
    }

    public int getTimeout()
    {
        return timeout == null ? 0 : timeout;
    }

    public void setTimeout(int timeout)
    {
        this.timeout = timeout;
    }

    public String toString()
    {
        StringBuffer buf = new StringBuffer();
        buf.append("Transaction{factory=")
            .append(factory)
            .append(", action=")
            .append(getActionAsString())
            .append(", timeout=")
            .append(timeout == null ? 0 : timeout)
            .append("}");
        return buf.toString();
    }
    
    public int hashCode()
    {
        return ClassUtils.hash(new Object[]{factory, action, constraint, timeout == null ? 0 : timeout});
    }

    public boolean equals(Object obj)
    {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        final MuleTransactionConfig other = (MuleTransactionConfig) obj;
        return ClassUtils.equal(factory, other.factory)
               && ClassUtils.equal(action, other.action)
               && ClassUtils.equal(constraint, other.constraint)
               && ClassUtils.equal(timeout, other.timeout);

    }
    
}
