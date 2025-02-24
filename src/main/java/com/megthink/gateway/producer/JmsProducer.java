package com.megthink.gateway.producer;

import java.sql.Timestamp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;
import com.megthink.gateway.utils.ReadConfigFile;

@Component
public class JmsProducer {

	private static final Logger _logger = LoggerFactory.getLogger(JmsProducer.class);

	@Autowired
	JmsTemplate jmsTemplate;

	// @Value("${active-mq.internalinqueue}")
	private String internalInQueue = ReadConfigFile.getProperties().getProperty("active-mq.internalinqueue");
	// @Value("${active-mq.inqueue}")
	private String inQueue = ReadConfigFile.getProperties().getProperty("active-mq.inqueue");
	private String z2InQueue = ReadConfigFile.getProperties().getProperty("active-mq.z2inqueue");
	// @Value("${active-mq.retryZone1Queue}")
	private String retryZone1Queue = ReadConfigFile.getProperties().getProperty("active-mq.retryZone1Queue");
	// @Value("${active-mq.retryZone2Queue}")
	private String retryZone2Queue = ReadConfigFile.getProperties().getProperty("active-mq.retryZone2Queue");

	public int sentIntoInternalInQ(String message, String sessionId) {
		int success = 0;
		try {
			_logger.info("Attempting Send message to internalInQueue: [" + internalInQueue + "] with sessionID: ["
					+ sessionId + "]");
			jmsTemplate.convertAndSend(internalInQueue, message, m -> {
				m.setJMSType("1");
				return m;
			});
			success = 1;
		} catch (Exception e) {
			success = 0;
			_logger.error("Recieved Exception during send Message:JmsProducer()-sentIntoInternalInQ() with sessionID: ["
					+ sessionId + "]");
		}
		return success;
	}

	public int sentRecoveryDBRequestIntoInQ(String message, String sessionId, String zoneType) {
		int success = 0;
		try {
			_logger.info(
					"[sessionId=" + sessionId + "]: Attempting Send RDBRequest message into activemq with timestamp:["
							+ new Timestamp(System.currentTimeMillis()) + "]");
			jmsTemplate.convertAndSend(inQueue, message, m -> {
				m.setJMSType(zoneType);
				return m;
			});
			success = 1;
		} catch (Exception e) {
			success = 0;
			_logger.error("[sessionId=" + sessionId
					+ "]: JmsProducer.sentRecoveryDBRequestIntoInQ().Exception occur while Attempting Send RDBRequest message into activemq: with timestamp:["
					+ new Timestamp(System.currentTimeMillis()) + "]-" + e);
		}
		return success;
	}

	public int sendMessageToInQueue(String message, String sessionId, int mch_type) {
		int success = 0;
		try {
			_logger.info("[sessionId=" + sessionId
					+ "]: JmsProducer.sendMessageToInQueue() - Attempting Send message to inQueue:" + inQueue + "]");
			if (mch_type == 1) {
				jmsTemplate.convertAndSend(inQueue, message, m -> {
					m.setJMSType("1");
					return m;
				});
			} else {
				jmsTemplate.convertAndSend(z2InQueue, message, m -> {
					m.setJMSType("1");
					return m;
				});
			}
			success = 1;
		} catch (Exception e) {
			success = 0;
			_logger.error("Recieved Exception during send Message JmsProducer()-sendMessageToInQueue(): " + e);
		}
		return success;
	}

	public int sentSCNoticeAnswerIntoInQueue(String message, String sessionId, String zoneType) {
		int success = 0;
		try {
			_logger.info("[sessionId=" + sessionId + "]: Attempting Send message into activemq with timestamp:["
					+ new Timestamp(System.currentTimeMillis()) + "]");
			jmsTemplate.convertAndSend(inQueue, message, m -> {
				m.setJMSType(zoneType);
				return m;
			});
			success = 1;
		} catch (Exception e) {
			success = 0;
			_logger.error("[sessionId=" + sessionId
					+ "]: JmsProducer.sentSCNoticeAnswerIntoInQueue().Exception occur while Attempting Send message into activemq: with timestamp:["
					+ new Timestamp(System.currentTimeMillis()) + "]-" + e);
		}
		return success;
	}

	public int sentSDNoticeAnswerIntoInQueue(String message, String sessionId, String zoneType) {
		int success = 0;
		try {
			_logger.info("[sessionId=" + sessionId + "]: Attempting Send message into activemq with timestamp:["
					+ new Timestamp(System.currentTimeMillis()) + "]");
			jmsTemplate.convertAndSend(inQueue, message, m -> {
				m.setJMSType(zoneType);
				return m;
			});
			success = 1;
		} catch (Exception e) {
			success = 0;
			_logger.error("[sessionId=" + sessionId
					+ "]: JmsProducer.sentSDNoticeAnswerIntoInQueue().Exception occur while Attempting Send message into activemq: with timestamp:["
					+ new Timestamp(System.currentTimeMillis()) + "]-" + e);
		}
		return success;
	}

	/* we define this producer to retry message if db gone down */
	public int sendZone1MessageRetry(String message, String sessionId, String retryCount) {
		int success = 0;
		try {
			_logger.info("Attempting Send message to retryZone1Queue: " + retryZone1Queue + " with sessionID: ["
					+ sessionId + "]");
			jmsTemplate.convertAndSend(retryZone1Queue, message, m -> {
				m.setJMSType(retryCount);
				return m;
			});
			success = 1;
		} catch (Exception e) {
			success = 0;
			_logger.error("Recieved Exception during send Message JmsProducer()-sendZone1MessageRetry(): " + e);
		}
		return success;
	}

	public int sendZone2MessageRetry(String message, String sessionId, String retryCount) {
		int success = 0;
		try {
			_logger.info("Attempting Send message to retryZone2Queue: " + retryZone2Queue + " with sessionID: ["
					+ sessionId + "]");
			jmsTemplate.convertAndSend(retryZone2Queue, message, m -> {
				m.setJMSType(retryCount);
				return m;
			});
			success = 1;
		} catch (Exception e) {
			success = 0;
			_logger.error("Recieved Exception during send Message JmsProducer()-sendZone2MessageRetry(): " + e);
		}
		return success;
	}

	// @Autowired
	// JmsTemplate jmsTemplate;

	// @Value("${active-mq.internalinqueue}")
	// private String internalInQueue;
	// @Value("${active-mq.inqueue}")
	// private String inQueue;
	//
	// public int sentIntoInternalInQ(String message, String sessionId) {
	// int success = 0;
	// try {
	// _logger.info("Attempting Send message to internalInQueue: [" +
	// internalInQueue + "] with sessionID: ["
	// + sessionId + "]");
	// jmsTemplate.convertAndSend(internalInQueue, message, m -> {
	// m.setJMSType("1");
	// return m;
	// });
	// success = 1;
	// } catch (Exception e) {
	// success = 0;
	// _logger.error("Recieved Exception during send
	// Message:JmsProducer()-sentIntoInternalInQ() with sessionID: ["
	// + sessionId + "]");
	// }
	// return success;
	// }
	//
	// public int sentRecoveryDBRequestIntoInQ(String message, String sessionId,
	// String zoneType) {
	// int success = 0;
	// try {
	// _logger.info(
	// "[sessionId=" + sessionId + "]: Attempting Send RDBRequest message into
	// activemq with timestamp:["
	// + new Timestamp(System.currentTimeMillis()) + "]");
	// jmsTemplate.convertAndSend(inQueue, message, m -> {
	// m.setJMSType(zoneType);
	// return m;
	// });
	// success = 1;
	// } catch (Exception e) {
	// success = 0;
	// _logger.error("[sessionId=" + sessionId
	// + "]: JmsProducer.sentRecoveryDBRequestIntoInQ().Exception occur while
	// Attempting Send RDBRequest message into activemq: with timestamp:["
	// + new Timestamp(System.currentTimeMillis()) + "]-" + e);
	// }
	// return success;
	// }
	//
	// public int sendMessageToInQueue(String message, String sessionId) {
	// int success = 0;
	// try {
	// _logger.info("Attempting Send message to inQueue: " + inQueue + " with
	// sessionID: [" + sessionId + "]");
	// jmsTemplate.convertAndSend(inQueue, message, m -> {
	// m.setJMSType("1");
	// return m;
	// });
	// success = 1;
	// } catch (Exception e) {
	// success = 0;
	// _logger.error("Recieved Exception during send Message
	// JmsProducer()-sendMessageToInQueue(): " + e);
	// }
	// return success;
	// }
	//
	// public int sentSCNoticeAnswerIntoInQueue(String message, String sessionId,
	// String zoneType) {
	// int success = 0;
	// try {
	// _logger.info("[sessionId=" + sessionId + "]: Attempting Send message into
	// activemq with timestamp:["
	// + new Timestamp(System.currentTimeMillis()) + "]");
	// jmsTemplate.convertAndSend(inQueue, message, m -> {
	// m.setJMSType(zoneType);
	// return m;
	// });
	// success = 1;
	// } catch (Exception e) {
	// success = 0;
	// _logger.error("[sessionId=" + sessionId
	// + "]: JmsProducer.sentSCNoticeAnswerIntoInQueue().Exception occur while
	// Attempting Send message into activemq: with timestamp:["
	// + new Timestamp(System.currentTimeMillis()) + "]-" + e);
	// }
	// return success;
	// }
	//
	// public int sentSDNoticeAnswerIntoInQueue(String message, String sessionId,
	// String zoneType) {
	// int success = 0;
	// try {
	// _logger.info("[sessionId=" + sessionId + "]: Attempting Send message into
	// activemq with timestamp:["
	// + new Timestamp(System.currentTimeMillis()) + "]");
	// jmsTemplate.convertAndSend(inQueue, message, m -> {
	// m.setJMSType(zoneType);
	// return m;
	// });
	// success = 1;
	// } catch (Exception e) {
	// success = 0;
	// _logger.error("[sessionId=" + sessionId
	// + "]: JmsProducer.sentSDNoticeAnswerIntoInQueue().Exception occur while
	// Attempting Send message into activemq: with timestamp:["
	// + new Timestamp(System.currentTimeMillis()) + "]-" + e);
	// }
	// return success;
	// }
}