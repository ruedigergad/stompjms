/**
 * Copyright (C) 2010-2011, FuseSource Corp.  All rights reserved.
 *
 *     http://fusesource.com
 *
 * The software in this package is published under the terms of the
 * CDDL license a copy of which has been included with this distribution
 * in the license.txt file.
 */

package org.fusesource.stomp.jms;

import jakarta.jms.*;
import jakarta.jms.IllegalStateException;

/**
 * Implementation of a TopicSession
 */
public class StompJmsTopicSession extends StompJmsSession {
    /**
     * Constructor
     *
     * @param connection
     * @param acknowledgementMode
     */
    protected StompJmsTopicSession(StompJmsConnection connection, int acknowledgementMode, boolean forceAsyncSend) {
        super(connection, acknowledgementMode, forceAsyncSend);
    }

    /**
     * @param queue
     * @return
     * @throws JMSException
     * @see jakarta.jms.Session#createBrowser(jakarta.jms.Queue)
     */
    public QueueBrowser createBrowser(Queue queue) throws JMSException {
        throw new IllegalStateException("Operation not supported by a TopicSession");
    }

    /**
     * @param queue
     * @param messageSelector
     * @return
     * @throws JMSException
     * @see jakarta.jms.Session#createBrowser(jakarta.jms.Queue, java.lang.String)
     */
    public QueueBrowser createBrowser(Queue queue, String messageSelector) throws JMSException {
        throw new IllegalStateException("Operation not supported by a TopicSession");
    }

    /**
     * @param destination
     * @return
     * @throws JMSException
     * @see jakarta.jms.Session#createConsumer(jakarta.jms.Destination)
     */
    public MessageConsumer createConsumer(Destination destination) throws JMSException {
        if (destination instanceof Queue) {
            throw new IllegalStateException("Operation not supported by a TopicSession");
        }
        return super.createConsumer(destination);
    }

    /**
     * @param destination
     * @param messageSelector
     * @return
     * @throws JMSException
     * @see jakarta.jms.Session#createConsumer(jakarta.jms.Destination, java.lang.String)
     */
    public MessageConsumer createConsumer(Destination destination, String messageSelector) throws JMSException {
        if (destination instanceof Queue) {
            throw new IllegalStateException("Operation not supported by a TopicSession");
        }
        return super.createConsumer(destination, messageSelector);
    }

    /**
     * @param destination
     * @return
     * @throws JMSException
     * @see jakarta.jms.Session#createProducer(jakarta.jms.Destination)
     */
    public MessageProducer createProducer(Destination destination) throws JMSException {
        if (destination instanceof Queue) {
            throw new IllegalStateException("Operation not supported by a TopicSession");
        }
        return super.createProducer(destination);
    }

    /**
     * @param queueName
     * @return
     * @throws JMSException
     * @see jakarta.jms.Session#createQueue(java.lang.String)
     */
    public Queue createQueue(String queueName) throws JMSException {
        throw new IllegalStateException("Operation not supported by a TopicSession");
    }

    /**
     * @return
     * @throws JMSException
     * @see jakarta.jms.Session#createTemporaryQueue()
     */
    public TemporaryQueue createTemporaryQueue() throws JMSException {
        throw new IllegalStateException("Operation not supported by a TopicSession");
    }

    /**
     * @param queue
     * @return
     * @throws JMSException
     * @see jakarta.jms.QueueSession#createReceiver(jakarta.jms.Queue)
     */
    public QueueReceiver createReceiver(Queue queue) throws JMSException {
        throw new IllegalStateException("Operation not supported by a TopicSession");
    }

    /**
     * @param queue
     * @param messageSelector
     * @return
     * @throws JMSException
     * @see jakarta.jms.QueueSession#createReceiver(jakarta.jms.Queue, java.lang.String)
     */
    public QueueReceiver createReceiver(Queue queue, String messageSelector) throws JMSException {
        throw new IllegalStateException("Operation not supported by a TopicSession");
    }

    /**
     * @param queue
     * @return
     * @throws JMSException
     * @see jakarta.jms.QueueSession#createSender(jakarta.jms.Queue)
     */
    public QueueSender createSender(Queue queue) throws JMSException {
        throw new IllegalStateException("Operation not supported by a TopicSession");
    }
}
