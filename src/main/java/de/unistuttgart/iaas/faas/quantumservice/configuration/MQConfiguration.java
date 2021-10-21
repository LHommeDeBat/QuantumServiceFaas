package de.unistuttgart.iaas.faas.quantumservice.configuration;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.Queue;
import javax.jms.Session;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibm.mq.jms.MQQueueConnectionFactory;
import com.ibm.msg.client.wmq.WMQConstants;
import de.unistuttgart.iaas.faas.quantumservice.messaging.EventReceiver;
import de.unistuttgart.iaas.faas.quantumservice.service.EventTriggerService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * This class configures IBMs MQ in case it is enabled using values from the application.yml files.
 */
@Configuration
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "messaging", name = "enabled", havingValue = "true")
public class MQConfiguration {

    @Value("${ibm.mq.queueManager}")
    private String queueManager;

    @Value("${ibm.mq.channel}")
    private String channel;

    @Value("${ibm.mq.host}")
    private String host;

    @Value("${ibm.mq.port}")
    private int port;

    @Value("${ibm.mq.user}")
    private String user;

    @Value("${ibm.mq.password}")
    private String password;

    @Value("${messaging.eventQueue}")
    private String eventQueue;

    private final IBMQProperties ibmqProperties;
    private final EventTriggerService eventTriggerService;
    private final ObjectMapper objectMapper;

    /**
     * This method creates a bean of a JMS ConnectionFactory.
     *
     * @return mqQueueConnectionFactory MQConnectionFactory
     * @throws JMSException Thrown if some JMS error occurs
     */
    @Bean
    public ConnectionFactory mqQueueConnectionFactory() throws JMSException {
        MQQueueConnectionFactory mqQueueConnectionFactory = new MQQueueConnectionFactory();
        mqQueueConnectionFactory.setHostName(host);
        mqQueueConnectionFactory.setPort(port);
        mqQueueConnectionFactory.setTransportType(WMQConstants.WMQ_CM_CLIENT);
        mqQueueConnectionFactory.setChannel(channel);

        mqQueueConnectionFactory.setQueueManager(queueManager);
        return mqQueueConnectionFactory;
    }

    /**
     * This method uses a ConnectionFactory to create and start a JMS Connection.
     *
     * @param mqQueueConnectionFactory ConnectionFactory that should be used to create a connection.
     * @return mqConnection MQConnection
     * @throws JMSException Thrown if some JMS error occurs
     */
    @Bean
    public Connection mqConnection(ConnectionFactory mqQueueConnectionFactory) throws JMSException {
        Connection connection = mqQueueConnectionFactory.createConnection(user, password);
        connection.start();
        return connection;
    }

    /**
     * This method uses a JMS Connection to create a JMS Session
     *
     * @param mqConnection Connection that should be used to create the session
     * @return mqSession MQSession
     * @throws JMSException Thrown if some JMS error occurs
     */
    @Bean
    public Session mqSession(Connection mqConnection) throws JMSException {
        return mqConnection.createSession();
    }

    /**
     * This method uses a JMS Session to create a JMS Queue.
     *
     * @param mqSession Session that is used to create the queue
     * @return mqQueue MQQueue
     * @throws JMSException Thrown if some JMS error occurs
     */
    @Bean
    public Queue mqQueue(Session mqSession) throws JMSException {
        return mqSession.createQueue(eventQueue);
    }

    /**
     * This method creates a JMS MessageConsumer that receives messages from a queue as a Event-Driven Consumer.
     *
     * @param mqSession Session that is used to create the message consumer
     * @param mqQueue Queue that the consumer is listening to
     * @return mqMessageConsumer MQMessageConsumer
     * @throws JMSException
     */
    @Bean
    public MessageConsumer mqMessageConsumer(Session mqSession, Queue mqQueue) throws JMSException {
        MessageConsumer mqMessageConsumer = mqSession.createConsumer(mqQueue);
        mqMessageConsumer.setMessageListener(new EventReceiver(ibmqProperties, eventTriggerService, objectMapper));
        return mqMessageConsumer;
    }
}
