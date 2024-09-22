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

import jakarta.jms.Topic;

/**
 * TemporaryQueue
 */
public class StompJmsTopic extends StompJmsDestination implements Topic {

    public StompJmsTopic() {
        super(null, null);
    }

    public StompJmsTopic(StompJmsConnection connection, String name) {
        this(connection.topicPrefix, name);
    }

    public StompJmsTopic copy() {
        final StompJmsTopic copy = new StompJmsTopic();
        copy.setProperties(getProperties());
        return copy;
    }


    /**
     * Constructor
     *
     * @param name
     */
    public StompJmsTopic(String type, String name) {
        super(type, name);
        this.topic = true;
    }

    /**
     * @return the name
     * @see jakarta.jms.Topic#getTopicName()
     */
    public String getTopicName() {
        return getName();
    }

}
