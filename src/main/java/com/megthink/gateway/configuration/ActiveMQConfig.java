//package com.megthink.gateway.configuration;
//
//import org.apache.activemq.ActiveMQConnectionFactory;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
//import org.springframework.jms.core.JmsTemplate;
//import org.springframework.jms.support.converter.MappingJackson2MessageConverter;
//import org.springframework.jms.support.converter.MessageConverter;
//import org.springframework.jms.support.converter.MessageType;
//
//import com.megthink.gateway.utils.ReadConfigFile;
//
//import jakarta.jms.ConnectionFactory;
//
//@Configuration
//public class ActiveMQConfig {
//	//@Value("${active-mq.broker-url}")
//	private String brokerUrl  = ReadConfigFile.getProperties().getProperty("active-mq.broker-url");
//
//	@Bean
//	public ConnectionFactory connectionFactory() {
//		ActiveMQConnectionFactory activeMQConnectionFactory = new ActiveMQConnectionFactory();
//		activeMQConnectionFactory.setBrokerURL(brokerUrl);
//		return (ConnectionFactory) activeMQConnectionFactory;
//	}
//
//	@Bean
//	public JmsTemplate jmsTemplate() {
//		JmsTemplate jmsTemplate = new JmsTemplate();
//		jmsTemplate.setConnectionFactory(connectionFactory());
//		// jmsTemplate.setPubSubDomain(true); // enable for Pub Sub to topic. Not
//		// Required for Queue.
//		return jmsTemplate;
//	}
//
//	@Bean
//	public DefaultJmsListenerContainerFactory jmsListenerContainerFactory() {
//		DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
//		factory.setConnectionFactory(connectionFactory());
//		// factory.setPubSubDomain(true);
//		return factory;
//	}
//
//	@Bean
//	public MessageConverter messageConverter() {
//		MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
//		converter.setTargetType(MessageType.TEXT);
//		converter.setTypeIdPropertyName("_type");
//		return converter;
//	}
//}