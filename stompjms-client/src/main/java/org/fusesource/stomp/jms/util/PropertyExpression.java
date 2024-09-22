/**
 * Copyright (C) 2010-2011, FuseSource Corp.  All rights reserved.
 *
 *     http://fusesource.com
 *
 * The software in this package is published under the terms of the
 * CDDL license a copy of which has been included with this distribution
 * in the license.txt file.
 */

package org.fusesource.stomp.jms.util;

import org.fusesource.stomp.jms.StompJmsDestination;
import org.fusesource.stomp.jms.StompJmsExceptionSupport;
import org.fusesource.stomp.jms.message.StompJmsMessage;

import jakarta.jms.DeliveryMode;
import jakarta.jms.InvalidDestinationException;
import jakarta.jms.JMSException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


/**
 * Represents a property expression
 *
 * @version $Revision: 1.5 $
 */
public class PropertyExpression {

    private static final Map<String, SubExpression> JMS_PROPERTY_EXPRESSIONS = new HashMap<String, SubExpression>();

    interface SubExpression {
        Object evaluate(StompJmsMessage message);
    }

    static {
        JMS_PROPERTY_EXPRESSIONS.put("JMSDestination", new SubExpression() {

            public Object evaluate(StompJmsMessage message) {
                try {
                    StompJmsDestination dest = message.getStompJmsDestination();
                    if (dest == null) {
                        return null;
                    }
                    return dest.toString();
                } catch (JMSException e) {
                    return null;
                }
            }
        });
        JMS_PROPERTY_EXPRESSIONS.put("JMSReplyTo", new SubExpression() {

            public Object evaluate(StompJmsMessage message) {
                try {
                    StompJmsDestination dest = message.getStompJmsReplyTo();
                    if (dest == null) {
                        return null;
                    }
                    return dest.toString();
                } catch (JMSException e) {
                    return null;
                }
            }
        });
        JMS_PROPERTY_EXPRESSIONS.put("JMSType", new SubExpression() {

            public Object evaluate(StompJmsMessage message) {
                return message.getJMSType();
            }
        });
        JMS_PROPERTY_EXPRESSIONS.put("JMSDeliveryMode", new SubExpression() {

            public Object evaluate(StompJmsMessage message) {
                return Integer.valueOf(message.isPersistent() ? DeliveryMode.PERSISTENT : DeliveryMode.NON_PERSISTENT);
            }
        });
        JMS_PROPERTY_EXPRESSIONS.put("JMSPriority", new SubExpression() {

            public Object evaluate(StompJmsMessage message) {
                return Integer.valueOf(message.getJMSPriority());
            }
        });
        JMS_PROPERTY_EXPRESSIONS.put("JMSStompJmsMessageID", new SubExpression() {

            public Object evaluate(StompJmsMessage message) {
                if (message.getJMSMessageID() == null) {
                    return null;
                }
                return message.getJMSMessageID().toString();
            }
        });
        JMS_PROPERTY_EXPRESSIONS.put("JMSTimestamp", new SubExpression() {

            public Object evaluate(StompJmsMessage message) {
                return Long.valueOf(message.getJMSTimestamp());
            }
        });
        JMS_PROPERTY_EXPRESSIONS.put("JMSCorrelationID", new SubExpression() {

            public Object evaluate(StompJmsMessage message) {
                return message.getJMSCorrelationID();
            }
        });
        JMS_PROPERTY_EXPRESSIONS.put("JMSExpiration", new SubExpression() {

            public Object evaluate(StompJmsMessage message) {
                return Long.valueOf(message.getJMSExpiration());
            }
        });
        JMS_PROPERTY_EXPRESSIONS.put("JMSRedelivered", new SubExpression() {

            public Object evaluate(StompJmsMessage message) {
                return Boolean.valueOf(message.isRedelivered());
            }
        });
        JMS_PROPERTY_EXPRESSIONS.put("JMSXDeliveryCount", new SubExpression() {

            public Object evaluate(StompJmsMessage message) {
                return Integer.valueOf(message.getRedeliveryCounter() + 1);
            }
        });
    }

    private final String name;
    private final SubExpression jmsPropertyExpression;

    public PropertyExpression(String name) {
        this.name = name;
        jmsPropertyExpression = JMS_PROPERTY_EXPRESSIONS.get(name);
    }


    public Object evaluate(StompJmsMessage message) throws JMSException {
        if (jmsPropertyExpression != null) {
            return jmsPropertyExpression.evaluate(message);
        }
        try {
            return message.getProperties().get(name);
        } catch (IOException ioe) {
            throw StompJmsExceptionSupport.create(ioe);
        }
    }

    public String getName() {
        return name;
    }

    /**
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return name;
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    public int hashCode() {
        return name.hashCode();
    }

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object o) {

        if (o == null || !this.getClass().equals(o.getClass())) {
            return false;
        }
        return name.equals(((PropertyExpression) o).name);

    }

}
