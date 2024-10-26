/**
 * Copyright (C) 2012, FuseSource Corp.  All rights reserved.
 *
 *     http://fusesource.com
 *
 * The software in this package is published under the terms of the
 * CDDL license a copy of which has been included with this distribution
 * in the license.txt file.
 */
package org.fusesource.stomp.jms;

import jakarta.jms.*;
import javax.net.ssl.SSLContext;
import java.net.URI;

/**
 * @author <a href="http://hiramchirino.com">Hiram Chirino</a>
 */
public class StompJmsTopicConnection extends StompJmsConnection {

    public StompJmsTopicConnection(URI brokerURI, URI localURI, String userName, String password, SSLContext sslContext) throws JMSException {
        super(brokerURI, localURI, userName, password, sslContext);
    }

    @Override
    public ConnectionConsumer createConnectionConsumer(Queue queue, String messageSelector, ServerSessionPool sessionPool, int maxMessages) throws JMSException {
        throw new jakarta.jms.IllegalStateException("Operation not supported by a TopicConnection");
    }

    @Override
    public QueueSession createQueueSession(boolean transacted, int acknowledgeMode) throws JMSException {
        throw new jakarta.jms.IllegalStateException("Operation not supported by a TopicConnection");
    }

}
