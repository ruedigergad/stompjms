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

import org.fusesource.hawtbuf.AsciiBuffer;
import org.fusesource.stomp.jms.message.StompJmsMessage;

import jakarta.jms.IllegalStateException;
import jakarta.jms.*;
import java.util.Enumeration;
import java.util.concurrent.atomic.AtomicBoolean;
import static org.fusesource.stomp.client.Constants.*;

/**
 * A client uses a <CODE>QueueBrowser</CODE> object to look at messages on a
 * queue without removing them. <p/>
 * <p/>
 * The <CODE>getEnumeration</CODE> method returns a <CODE>
 * java.util.Enumeration</CODE>
 * that is used to scan the queue's messages. It may be an enumeration of the
 * entire content of a queue, or it may contain only the messages matching a
 * message selector. <p/>
 * <p/>
 * Messages may be arriving and expiring while the scan is done. The JMS API
 * does not require the content of an enumeration to be a static snapshot of
 * queue content. Whether these changes are visible or not depends on the JMS
 * provider. <p/>
 * <p/>
 * A <CODE>QueueBrowser</CODE> can be created from either a <CODE>Session
 * </CODE>
 * or a <CODE>QueueSession</CODE>.
 *
 * @see jakarta.jms.Session#createBrowser
 * @see jakarta.jms.QueueSession#createBrowser
 * @see jakarta.jms.QueueBrowser
 * @see jakarta.jms.QueueReceiver
 */

public class StompJmsQueueBrowser implements QueueBrowser, Enumeration {

    private final StompJmsSession session;
    private final StompJmsDestination destination;
    private final String selector;

    private StompJmsMessageConsumer consumer;
    private boolean closed;
    private final AsciiBuffer id;
    private final AtomicBoolean browseDone = new AtomicBoolean(true);
    private Object semaphore = new Object();

    /**
     * Constructor for an StompJmsQueueBrowser - used internally
     *
     * @param session
     * @param id
     * @param destination
     * @param selector
     * @throws jakarta.jms.JMSException
     */
    protected StompJmsQueueBrowser(StompJmsSession session, AsciiBuffer id, StompJmsDestination destination, String selector) throws JMSException {
        this.session = session;
        this.id = id;
        this.destination = destination;
        this.selector = selector;
    }


    private StompJmsMessageConsumer createConsumer() throws JMSException {
        browseDone.set(false);

        StompJmsMessageConsumer rc = new StompJmsMessageConsumer(id, session, destination, selector) {

            @Override
            public boolean isBrowser() {
                return true;
            }

            public void onMessage(StompJmsMessage message) {
                if (message == null) {
                    browseDone.set(true);
                } else {
                    AsciiBuffer browser = message.getFrame().headerMap().get(BROWSER);
                    if (browser!=null && END.equals(browser)) {
                        browseDone.set(true);
                    } else {
                        super.onMessage(message);
                    }
                }
                notifyMessageAvailable();
            }
        };
        rc.init();
        return rc;
    }

    private void destroyConsumer() {
        if (consumer == null) {
            return;
        }
        try {
            if (session.getTransacted()) {
                session.commit();
            }
            consumer.close();
            consumer = null;
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    /**
     * Gets an enumeration for browsing the current queue messages in the order
     * they would be received.
     *
     * @return an enumeration for browsing the messages
     * @throws jakarta.jms.JMSException if the JMS provider fails to get the enumeration for
     *                                this browser due to some internal error.
     */

    public Enumeration getEnumeration() throws JMSException {
        checkClosed();
        if (consumer == null) {
            consumer = createConsumer();
        }
        return this;
    }

    private void checkClosed() throws IllegalStateException {
        if (closed) {
            throw new IllegalStateException("The Consumer is closed");
        }
    }

    /**
     * @return true if more messages to process
     */
    public boolean hasMoreElements() {
        while (true) {

            synchronized (this) {
                if (consumer == null) {
                    return false;
                }
            }

            if (consumer.getMessageQueueSize() > 0) {
                return true;
            }

            if (browseDone.get() || !session.isStarted()) {
                destroyConsumer();
                return false;
            }

            waitForMessage();
        }
    }

    /**
     * @return the next message
     */
    public Object nextElement() {
        while (true) {

            synchronized (this) {
                if (consumer == null) {
                    return null;
                }
            }

            try {
                Message answer = consumer.receiveNoWait();
                if (answer != null) {
                    return answer;
                }
            } catch (JMSException e) {
                this.session.getConnection().onException(e);
                return null;
            }

            if (browseDone.get() || !session.isStarted()) {
                destroyConsumer();
                return null;
            }

            waitForMessage();
        }
    }

    public synchronized void close() throws JMSException {
        destroyConsumer();
        closed = true;
    }

    /**
     * Gets the queue associated with this queue browser.
     *
     * @return the queue
     * @throws jakarta.jms.JMSException if the JMS provider fails to get the queue
     *                                associated with this browser due to some internal error.
     */

    public Queue getQueue() throws JMSException {
        return (Queue) destination;
    }

    public String getMessageSelector() throws JMSException {
        return selector;
    }

    // Implementation methods
    // -------------------------------------------------------------------------

    /**
     * Wait on a semaphore for a fixed amount of time for a message to come in.
     */
    protected void waitForMessage() {
        try {
            synchronized (semaphore) {
                semaphore.wait(2000);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    protected void notifyMessageAvailable() {
        synchronized (semaphore) {
            semaphore.notifyAll();
        }
    }

    public String toString() {
        return "StompJmsQueueBrowser { value=" + this.id + " }";
    }

}
