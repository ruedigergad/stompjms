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

import org.apache.activemq.apollo.broker.Broker;
import org.apache.activemq.apollo.broker.BrokerFactory;
import org.apache.activemq.apollo.util.ServiceControl;

import jakarta.jms.*;
import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Test cases used to test the JMS message consumer.
 *
 * @version $Revision$
 */
public class JmsTestSupport extends CombinationTestSupport {

    static final String BROKER_CONFIG = "org/fusesource/stomp/jms/apollo-stomp.xml";
    static final private AtomicLong TEST_COUNTER = new AtomicLong();
    public String userName;
    public String password;
    public String messageTextPrefix = "";
    protected int port = -1;
    protected ConnectionFactory factory;
    protected StompJmsConnection connection;
    protected Broker broker;

    protected List<Connection> connections = Collections.synchronizedList(new ArrayList<Connection>());

    // /////////////////////////////////////////////////////////////////
    //
    // Test support methods.
    //
    // /////////////////////////////////////////////////////////////////
    protected StompJmsDestination createDestination(String type) throws JMSException {
        String testMethod = getName();
        if (testMethod.indexOf(" ") > 0) {
            testMethod = testMethod.substring(0, testMethod.indexOf(" "));
        }

        String name = type + "TEST." + getClass().getName() + "." + testMethod + "." + TEST_COUNTER.getAndIncrement();
        return StompJmsDestination.createDestination(connection, name);
    }

    protected StompJmsDestination reCreateDestination(String type) throws JMSException {
        String testMethod = getName();
        if (testMethod.indexOf(" ") > 0) {
            testMethod = testMethod.substring(0, testMethod.indexOf(" "));
        }

        String name = type + "TEST." + getClass().getName() + "." + testMethod + "." + TEST_COUNTER.get();
        return StompJmsDestination.createDestination(connection, name);
    }

    protected void sendMessages(Destination destination, int count) throws Exception {
        ConnectionFactory factory = createConnectionFactory();
        Connection connection = factory.createConnection();
        connection.start();
        sendMessages(connection, destination, count);
        connection.close();
    }

    protected void sendMessages(Connection connection, Destination destination, int count) throws JMSException {
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        sendMessages(session, destination, count);
        session.close();
    }

    protected void sendMessages(Session session, Destination destination, int count) throws JMSException {
        sendMessages(session, destination, count, true);
    }
    protected void sendMessages(Session session, Destination destination, int count, boolean persistent) throws JMSException {
        MessageProducer producer = session.createProducer(destination);
        producer.setDeliveryMode(persistent ? DeliveryMode.PERSISTENT : DeliveryMode.NON_PERSISTENT);
        for (int i = 0; i < count; i++) {
            producer.send(session.createTextMessage(messageTextPrefix + i));
        }
        producer.close();
    }

    protected ConnectionFactory createConnectionFactory() throws Exception {
        StompJmsConnectionFactory result = new StompJmsConnectionFactory();
        result.setBrokerURI("tcp://localhost:" + this.port);
        return result;
    }

    protected Broker createBroker() throws Exception {
        URL resource = getClass().getClassLoader().getResource(BROKER_CONFIG);
        return BrokerFactory.createBroker(resource.toURI().toString());
    }

    protected void setUp() throws Exception {
        super.setUp();

        if (System.getProperty("basedir") == null) {
            File file = new File(".");
            System.setProperty("basedir", file.getAbsolutePath());
        }

        broker = createBroker();
        ServiceControl.start(broker, "Starting Apollo Broker");
        this.port = ((InetSocketAddress)broker.get_socket_address()).getPort();
        factory = createConnectionFactory();
        connection = (StompJmsConnection) factory.createConnection(userName, password);
        connections.add(connection);
    }

    protected void tearDown() throws Exception {
        for (Iterator<Connection> iter = connections.iterator(); iter.hasNext();) {
            Connection conn = iter.next();
            try {
                conn.close();
            } catch (Throwable e) {
            }
            iter.remove();
        }
        ServiceControl.stop(broker, "Stopped Apollo Broker");
        super.tearDown();
    }

    protected void safeClose(Connection c) {
        try {
            c.close();
        } catch (Throwable e) {
        }
    }

    protected void safeClose(Session s) {
        try {
            s.close();
        } catch (Throwable e) {
        }
    }

    protected void safeClose(MessageConsumer c) {
        try {
            c.close();
        } catch (Throwable e) {
        }
    }

    protected void safeClose(MessageProducer p) {
        try {
            p.close();
        } catch (Throwable e) {
        }
    }

    protected void profilerPause(String prompt) throws IOException {
        if (System.getProperty("profiler") != null) {
            pause(prompt);
        }
    }

    protected void reconnect() throws Exception {

    	if (connection != null) {
    		safeClose(connection);
    		connections.remove(connection);
    	}

        connection = (StompJmsConnection) factory.createConnection(userName, password);
        connections.add(connection);
    }

    protected void pause(String prompt) throws IOException {
        System.out.println();
        System.out.println(prompt + "> Press enter to continue: ");
        while (System.in.read() != '\n') {
        }
    }
}
