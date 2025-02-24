package com.megthink.gateway.consumer;

import java.sql.Timestamp;
import java.util.Base64;
import java.util.List;

import jakarta.jms.JMSException;
import jakarta.jms.Message;
import jakarta.jms.TextMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import com.megthink.gateway.dao.BillingResolutionDao;
import com.megthink.gateway.dao.NumberPlanDao;
import com.megthink.gateway.model.BillingResolution;
import com.megthink.gateway.model.InitAck;
import com.megthink.gateway.model.NPO;
import com.megthink.gateway.model.NPOA;
import com.megthink.gateway.model.NPOS;
import com.megthink.gateway.model.NPOSA;
import com.megthink.gateway.model.NPOSAACK;
import com.megthink.gateway.model.NPOSPR;
import com.megthink.gateway.model.NPOT;
import com.megthink.gateway.model.NVP;
import com.megthink.gateway.model.OrderCancellation;
import com.megthink.gateway.model.OrderReversal;
import com.megthink.gateway.model.PortMe;
import com.megthink.gateway.model.PortMeTransactionDetails;
import com.megthink.gateway.model.RSP;
import com.megthink.gateway.model.RecoveryDB;
import com.megthink.gateway.model.RecoveryDBResponse;
import com.megthink.gateway.model.SC;
import com.megthink.gateway.model.SCInfo;
import com.megthink.gateway.model.SD;
import com.megthink.gateway.model.SDInfo;
import com.megthink.gateway.model.SubscriberResult;
import com.megthink.gateway.model.SubscriberSequence;
import com.megthink.gateway.model.TerminateSim;
import com.megthink.gateway.model.TerminateSimMT;
import com.megthink.gateway.model.TerminateSimTransactionDetails;
import com.megthink.gateway.producer.JmsProducer;
import com.megthink.gateway.service.AuthorService;
import com.megthink.gateway.service.BillingResolutionService;
import com.megthink.gateway.service.BroadcastHistoryService;
import com.megthink.gateway.service.CorporateCustomerService;
import com.megthink.gateway.service.PersonCustomerService;
import com.megthink.gateway.service.PortMeService;
import com.megthink.gateway.service.PortMeTransactionDetailsService;
import com.megthink.gateway.service.RecoveryDBService;
import com.megthink.gateway.service.SubscriberArrTypeService;
import com.megthink.gateway.service.SubscriberAuthorizationService;
import com.megthink.gateway.service.TerminateSimMTService;
import com.megthink.gateway.service.TerminateSimService;
import com.megthink.gateway.service.TerminateSimTransactionDetailsService;
import com.megthink.gateway.utils.NPOUtils;
import com.megthink.gateway.utils.PortMeUtils;
import com.megthink.gateway.utils.ReadConfigFile;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class PortMeZ1Consumer {

	private static final Logger _logger = LoggerFactory.getLogger(PortMeZ1Consumer.class);

	Timestamp timestamp = new Timestamp(System.currentTimeMillis());

	@Autowired
	private PortMeService portMeService;
	@Autowired
	private SubscriberAuthorizationService subscriberAuthService;
	@Autowired
	private CorporateCustomerService corporateCustomerService;
	@Autowired
	private PersonCustomerService personCustomerSerive;
	@Autowired
	private AuthorService authorService;
	@Autowired
	private PortMeTransactionDetailsService portMeTransactionService;
	@Autowired
	private TerminateSimService terminateSimService;
	@Autowired
	private TerminateSimTransactionDetailsService terminateTransactionService;
	@Autowired
	private TerminateSimMTService terminateSimMTService;
	@Autowired
	private BroadcastHistoryService broadcastHistoryService;
	@Autowired
	private RecoveryDBService recoveryDBService;
	@Autowired
	private SubscriberArrTypeService subscriberArrTypeService;
	@Autowired
	private BillingResolutionService billingResolutionService;
	@Autowired
	private BillingResolutionDao billingResolutionDao;
	@Autowired
	private NumberPlanDao numberPlanDao;
	@Autowired
	private JmsProducer jmsProducer;

	@SuppressWarnings("deprecation")
	//@JmsListener(destination = "Z1INQueue")
	public void receiveMessage(Message message) {
		String sessionId = Long.toString(System.currentTimeMillis());
		_logger.info("[sessionId=" + sessionId
				+ "]: PortMeConsumer.receiveMessage()- Recieved Soap Message - process start with timestamp:["
				+ new Timestamp(System.currentTimeMillis()) + "]");
		int current_status = 0;
		String requestId = null;
		String messageType = "";
		try {
			PortMeTransactionDetails portMeTransaction = new PortMeTransactionDetails();
			TextMessage msg = (TextMessage) message;
			_logger.info("Received Message: " + msg.getText());

			ObjectMapper mapper = new ObjectMapper();
			JsonNode arrayNode = mapper.readTree(msg.getText().toString());
			if (arrayNode.isArray() && arrayNode.size() > 0) {
				JsonNode firstObject = arrayNode.get(0);
				if (firstObject.has("messageType")) {
					messageType = firstObject.get("messageType").asText();
					System.out.println("messageType: " + messageType);
				} else if (firstObject.has("MessageType")) {
					messageType = firstObject.get("MessageType").asText();
					System.out.println("messageType: " + messageType);
				}
			}

			String xml = null;
			/* start code for NPO SOAP request with jms out queue */
			if (messageType.equals(ReadConfigFile.getProperties().getProperty("NPO_MESSAGE_TYPE"))) {
				_logger.info("[sessionId=" + sessionId
						+ "]: PortMeConsumer.receiveMessage()-NPO Soap Message - process start with timestamp:["
						+ new Timestamp(System.currentTimeMillis()) + "]");
				mapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);
				NPO[] listOfNPO = mapper.readValue(msg.getText().toString(), NPO[].class);
				for (NPO npo : listOfNPO) {
					_logger.info("get NPO message from jms outqueue : " + listOfNPO.length);
					requestId = npo.getReferenceId();
					int mch_type = 0;
					String msisdns = null;
					if (npo.getlOAImage() != null) {
						Base64.Decoder decoder = Base64.getDecoder();
						String dStr = new String(decoder.decode(npo.getlOAImage()));
						System.out.println(dStr);
					}
					npo.setRequestId(requestId);
					String portme_out = ReadConfigFile.getProperties().getProperty("PORTME_OUT");
					_logger.info("start processing of NPO message from jms outqueue with requestId-" + requestId);
					current_status = 3;
					portMeTransaction.setRequestId(npo.getReferenceId());
					portMeTransaction.setStatus(current_status);
					portMeTransaction.setRequestType(portme_out);
					PortMe portMe = new PortMe();
					if (npo.getSubscriberAuthSequence() != null) {
						if (npo.getSubscriberAuthSequence().getSubscriberAuthorization().size() > 0) {
							portMeTransactionService.savePortMeTransactionDetailStat(portMeTransaction,
									npo.getSubscriberAuthSequence().getSubscriberAuthorization());
							msisdns = npo.getSubscriberAuthSequence().getSubscriberAuthorization().get(0)
									.getSubscriberNumber();
							portMe.setCompanyCode(
									npo.getSubscriberAuthSequence().getSubscriberAuthorization().get(0).getOwnerId());
							portMe.setDataType(
									npo.getSubscriberAuthSequence().getSubscriberAuthorization().get(0).getTypeOfId());
						}
					} else {
						for (SubscriberSequence item : npo.getSubscriberSequence()) {
							portMeTransaction.setMsisdn(item.getSubscriberNumber());
							portMeTransactionService.savePortMeTransactionDetails(portMeTransaction);
							msisdns = item.getSubscriberNumber();
						}

						String lastArea = numberPlanDao.getDonorLSAID(msisdns);
						if (lastArea == null) {
							lastArea = numberPlanDao.getArea(msisdns);
						}
						portMe.setLast_area(lastArea);
						mch_type = numberPlanDao.getMCHTypeByArea(lastArea);
					}
					portMe.setSource(Integer.valueOf(npo.getMessageSenderTelco()));
					portMe.setRequestId(npo.getReferenceId());
					portMe.setTimeStamp(npo.getTimestamp());
					portMe.setDno(npo.getDonorTelco());
					portMe.setArea(npo.getLsa());
					portMe.setRn(npo.getRouteNumber());
					portMe.setService(npo.getServiceType());
					portMe.setStatus(current_status);
					portMe.setMch(mch_type);
					portMe.setOrderDate(timestamp.toString());
					portMe.setOriginalCarrier(npo.getDonorTelco());
					portMe.setRno(npo.getRecipientTelco());
					portMe.setCustomerRequestTime(timestamp.toString());
					portMe.setRequest_type(portme_out);
					PortMe port = portMeService.savePortMe(portMe);
					String hlr = null;
					if (port.getPortId() != 0) {
						if (npo.getCorporateCustomer() != null) {
							npo.getCorporateCustomer().setPortId(port.getPortId());
							corporateCustomerService.saveCorporateCustomer(npo.getCorporateCustomer());
						} else {
							npo.getPersonCustomer().setPortId(port.getPortId());
							personCustomerSerive.savePersonCustomer(npo.getPersonCustomer());
							hlr = npo.getPersonCustomer().getOwnerId();
						}

						if (npo.getSubscriberAuthSequence() != null) {
							if (npo.getSubscriberAuthSequence().getSubscriberAuthorization().size() > 0) {
								subscriberAuthService.saveMt(portMe.getPortId(),
										npo.getSubscriberAuthSequence().getSubscriberAuthorization(), portme_out,
										current_status, npo.getOrderedApprovalTime(), npo.getOrderedTransferTime());
							}
						} else {
							for (SubscriberSequence item : npo.getSubscriberSequence()) {
								portMeTransaction.setMsisdn(item.getSubscriberNumber());
								subscriberArrTypeService.createPortMTNPO(port.getPortId(), item.getSubscriberNumber(),
										portme_out, current_status, npo.getOrderedApprovalTime(),
										npo.getOrderedTransferTime(), hlr);
							}
						}

						npo.getAuthor().setPortId(port.getPortId());
						authorService.saveAuthor(npo.getAuthor());

						// going to convert into xml format
						_logger.info("trying to convert NPO message into xml with requestId-" + requestId);
						/* This xml is used for client if he want to get XML */
						xml = "NPO SOAP";// new NPOUtils().generatePortingOutXML(npo, "");// should be porting out, this
											// should be
						// configure flag yes or not for generate
						_logger.info("converted NPO message into xml with requestId-" + requestId);
						sessionId = Long.toString(System.currentTimeMillis());
						int interval = jmsProducer.sentIntoInternalInQ(xml, sessionId);
						if (interval == 1) {
							current_status = 3;
							if (current_status != 0) {
								// going to update current status in port_tx table
								portMeService.updatePortMeStatus(current_status, requestId);
							}
						}
					}
				}
				_logger.info("[sessionId=" + sessionId
						+ "]: PortMeConsumer.receiveMessage()-NPO Soap Message - process end with timestamp:["
						+ new Timestamp(System.currentTimeMillis()) + "]");
			}
			/* end NPO SOAP Request */
			/* start code for NPOA Soap request with JMS out queue */
			else if (messageType.equals(ReadConfigFile.getProperties().getProperty("NPOA_MESSAGE_TYPE"))) {
				_logger.info("[sessionId=" + sessionId
						+ "]: PortMeConsumer.receiveMessage()-NPOA Soap Message - process start with timestamp:["
						+ new Timestamp(System.currentTimeMillis()) + "]");
				mapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);
				NPOA[] listOfNPOA = mapper.readValue(msg.getText().toString(), NPOA[].class);
				for (NPOA npoa : listOfNPOA) {
					_logger.info("GET NPOA from jms outqueue : " + npoa);
					requestId = npoa.getReferenceId();
					String portme_in = ReadConfigFile.getProperties().getProperty("PORTME_IN");
					_logger.info("start processing of NPOA message from jms outqueue with requestId-" + requestId);
					current_status = 9;
					portMeTransaction.setRequestId(npoa.getRequestId());
					portMeTransaction.setStatus(current_status);
					portMeTransaction.setRequestType(portme_in);
					if (npoa.getSubscriberResult().size() > 0) {
						portMeTransactionService.savePortMeTransactionDetail(portMeTransaction,
								npoa.getSubscriberResult());
						for (SubscriberResult subRestult : npoa.getSubscriberResult()) {
							portMeService.updateNPOARequest(current_status, subRestult.getSubscriberNumber(), portme_in,
									subRestult.getResultCode());
						}
					}
					_logger.info("trying to convert NPOA message into xml with requestId-" + requestId);
					xml = "NPOA SOAP";// new NPOUtils().convertNPOASoapIntoXML(npoa,"");// porting confirmation
					int interval = jmsProducer.sentIntoInternalInQ(xml, sessionId);
					if (interval == 1) {
						current_status = 9;
						_logger.info("processed NPOA Soap Reqeust with requestId-" + requestId);
					}
					if (current_status != 0) {
						portMeService.updatePortMeStatus(current_status, requestId);
					}
				}
				_logger.info("[sessionId=" + sessionId
						+ "]: PortMeConsumer.receiveMessage()-NPOA Soap Message - process end with timestamp:["
						+ new Timestamp(System.currentTimeMillis()) + "]");
			}
			/* end code for NPOA Request */
			/* start code for NPOT Soap (TERMINATE SIM) request with JMS out queue */
			else if (messageType.equals(ReadConfigFile.getProperties().getProperty("NPOT_MESSAGE_TYPE"))) {
				_logger.info("[sessionId=" + sessionId
						+ "]: PortMeConsumer.receiveMessage()-NPOT Soap Message - process start with timestamp:["
						+ new Timestamp(System.currentTimeMillis()) + "]");
				mapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);
				NPOT[] listOfNPOT = mapper.readValue(msg.getText().toString(), NPOT[].class);
				for (NPOT npot : listOfNPOT) {
					_logger.info("GET NPOT from jms outqueue : " + npot);
					String terminate_out = ReadConfigFile.getProperties().getProperty("TERMINATE_OUT");
					requestId = PortMeUtils.randomUniqueUUID();
					int resultCode = 200;
					_logger.info("start processing of NPOT message from jms outqueue with requestId-" + requestId);
					current_status = 3;
					TerminateSim terminationDetails = new TerminateSim();
					terminationDetails.setRequestId(requestId);
					terminationDetails.setSource(Integer.parseInt(npot.getMessageSenderTelco()));
					terminationDetails.setTimeStamp(npot.getTimestamp());
					terminationDetails.setService(npot.getReferenceId());
					terminationDetails.setRn(npot.getLsa());
					terminationDetails.setStatus(current_status);
					TerminateSim terminateSim = terminateSimService.saveTerminateSim(terminationDetails);
					for (SubscriberSequence msisdn : npot.getSubscriberSequence()) {
						TerminateSimTransactionDetails terminationTransaction = new TerminateSimTransactionDetails();
						terminationTransaction.setRequestId(requestId);
						terminationTransaction.setStatus(current_status);
						terminationTransaction.setRequestType(terminate_out);
						terminationTransaction.setMsisdn(msisdn.getSubscriberNumber());
						terminateTransactionService.saveTerminateSimTransactionDetails(terminationTransaction);
						TerminateSimMT terminateSIMMt = new TerminateSimMT();
						terminateSIMMt.setTerminateId(terminateSim.getTerminateId());
						terminateSIMMt.setRequest_type(terminate_out);
						terminateSIMMt.setStatus(current_status);
						terminateSIMMt.setSubscriberNumber(msisdn.getSubscriberNumber());
						terminateSIMMt.setResultCode(resultCode);
						terminateSimMTService.saveTerminateSimMT(terminateSIMMt);
					}
					// going to convert into xml format

					_logger.info("trying to convert NPOT request into xml with requestId : " + requestId);
					xml = null;// new NPOUtils().convertNPOTSoapIntoXML(npot);
					_logger.info("convert portme termination request into xml with requestId:" + requestId + xml);
					sessionId = Long.toString(System.currentTimeMillis());
					int interval = jmsProducer.sentIntoInternalInQ(xml, sessionId);
					if (interval == 1) {
						_logger.info("Sent NPOT xml request in jms queue with requestId-" + requestId);
						current_status = 3;
						_logger.info("processed NPOT with requestId-" + requestId);

					} else {
						_logger.info("Unsent Termination MSISDN request in jms queue with requestId-" + requestId);
					}
					if (current_status != 0) {
						terminateSimService.updateTerminateSIM(terminateSim.getTerminateId(), current_status);
					}
				}
				_logger.info("[sessionId=" + sessionId
						+ "]: PortMeConsumer.receiveMessage()-NPOT Soap Message - process start with timestamp:["
						+ new Timestamp(System.currentTimeMillis()) + "]");
			}
			/* end code for NPOT */
			else if (messageType.equals(ReadConfigFile.getProperties().getProperty("INIT_ACK_MESSAGE_TYPE"))) {
				_logger.info("[sessionId=" + sessionId
						+ "]: PortMeConsumer.receiveMessage()-INIT_ACK_MESSAGE Soap Message - process start with timestamp:["
						+ new Timestamp(System.currentTimeMillis()) + "]");
				mapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);
				InitAck initAck = mapper.readValue(msg.getText(), InitAck.class);
				_logger.info("got INIT_ACK data from outqueue : " + initAck);
				requestId = initAck.getRequestId();
				String portme_in = ReadConfigFile.getProperties().getProperty("PORTME_IN");
				current_status = 6;
				List<String> listOfMsisdn = portMeService.getListOfMSISDN(requestId);
				for (String msisdn : listOfMsisdn) {
					portMeTransaction = new PortMeTransactionDetails();
					portMeTransaction.setRequestId(requestId);
					portMeTransaction.setStatus(current_status);
					portMeTransaction.setRequestType(portme_in);
					portMeTransaction.setMsisdn(msisdn);
					portMeTransactionService.savePortMeTransactionDetails(portMeTransaction);
					subscriberArrTypeService.updatePortMtStatusByMsisdn(current_status, msisdn, portme_in, 200);
				}
				// going to convert into xml format
				_logger.info("converting INIT_ACK into xml config file");
				xml = new NPOUtils().convertInitAckIntoXML(initAck);
				sessionId = Long.toString(System.currentTimeMillis());
				int interval = jmsProducer.sentIntoInternalInQ(xml, sessionId);
				if (interval == 1) {
					current_status = 6;
					_logger.info("Send INIT_ACK xml into internalIn jms queue : " + xml);
				}
				if (current_status != 0) {
					portMeService.updatePortMeStatus(current_status, requestId);
				}
				_logger.info("[sessionId=" + sessionId
						+ "]: PortMeConsumer.receiveMessage()-INIT_ACK_MESSAGE Soap Message - process end with timestamp:["
						+ new Timestamp(System.currentTimeMillis()) + "]");
			} else if (messageType.equals(ReadConfigFile.getProperties().getProperty("DISCONNECT_ACK_MESSAGE_TYPE"))) {
				_logger.info("[sessionId=" + sessionId
						+ "]: PortMeConsumer.receiveMessage()-DISCONNECT_ACK_MESSAGE Soap Message - process start with timestamp:["
						+ new Timestamp(System.currentTimeMillis()) + "]");
				InitAck initAck = mapper.readValue(msg.getText(), InitAck.class);
				_logger.info("got DISCONNECT_ACK_MESSAGE data from outqueue : " + initAck);
				requestId = initAck.getRequestId();
				String portme_out = ReadConfigFile.getProperties().getProperty("PORTME_OUT");
				current_status = 18;
				List<String> listOfMsisdn = portMeService.getListOfMSISDN(requestId);
				for (String msisdn : listOfMsisdn) {
					portMeTransaction = new PortMeTransactionDetails();
					portMeTransaction.setRequestId(requestId);
					portMeTransaction.setStatus(current_status);
					portMeTransaction.setRequestType(portme_out);
					portMeTransaction.setMsisdn(msisdn);
					portMeTransactionService.savePortMeTransactionDetails(portMeTransaction);
					subscriberArrTypeService.updatePortMtStatusByMsisdn(current_status, msisdn, portme_out, 200);
				}
				// going to convert into xml format
				xml = new NPOUtils().convertInitAckIntoXML(initAck);
				sessionId = Long.toString(System.currentTimeMillis());
				int interval = jmsProducer.sentIntoInternalInQ(xml, sessionId);
				if (interval == 1) {
					current_status = 18;
					_logger.info("Send DISCONNECT_ACK xml into internalIn jms queue : " + xml);
				}
				if (current_status != 0) {
					portMeService.updatePortMeStatus(current_status, requestId);
				}
				_logger.info("[sessionId=" + sessionId
						+ "]: PortMeConsumer.receiveMessage()-DISCONNECT_ACK_MESSAGE Soap Message - process end with timestamp:["
						+ new Timestamp(System.currentTimeMillis()) + "]");
			} else if (messageType.equals(ReadConfigFile.getProperties().getProperty("CON_ACK_MESSAGE_TYPE"))) {
				_logger.info("[sessionId=" + sessionId
						+ "]: PortMeConsumer.receiveMessage()-CON_ACK_MESSAGE Soap Message - process start with timestamp:["
						+ new Timestamp(System.currentTimeMillis()) + "]");
				InitAck initAck = mapper.readValue(msg.getText(), InitAck.class);
				requestId = initAck.getRequestId();
				String portme_in = ReadConfigFile.getProperties().getProperty("PORTME_IN");
				current_status = 18;
				List<String> listOfMsisdn = portMeService.getListOfMSISDN(requestId);
				for (String msisdn : listOfMsisdn) {
					portMeTransaction = new PortMeTransactionDetails();
					portMeTransaction.setRequestId(requestId);
					portMeTransaction.setStatus(current_status);
					portMeTransaction.setRequestType(portme_in);
					portMeTransaction.setMsisdn(msisdn);
					portMeTransactionService.savePortMeTransactionDetails(portMeTransaction);
					subscriberArrTypeService.updatePortMtStatusByMsisdn(current_status, msisdn, portme_in, 200);
				}
				// going to convert into xml format
				xml = new NPOUtils().convertInitAckIntoXML(initAck);
				sessionId = Long.toString(System.currentTimeMillis());
				int interval = jmsProducer.sentIntoInternalInQ(xml, sessionId);
				if (interval == 1) {
					current_status = 18;
					_logger.info("Send CON_ACK xml into internalIn jms queue : " + xml);
				}
				if (current_status != 0) {
					portMeService.updatePortMeStatus(current_status, requestId);
				}
				_logger.info("[sessionId=" + sessionId
						+ "]: PortMeConsumer.receiveMessage()-CON_ACK_MESSAGE Soap Message - process end with timestamp:["
						+ new Timestamp(System.currentTimeMillis()) + "]");
			}
			// getting order cancellation ack
			else if (messageType.equals(ReadConfigFile.getProperties().getProperty("CONCEL_ACK_MESSAGE_TYPE"))) {
				_logger.info("[sessionId=" + sessionId
						+ "]: PortMeConsumer.receiveMessage()-CANCEL_ACK_MESSAGE Soap Message - process start with timestamp:["
						+ new Timestamp(System.currentTimeMillis()) + "]");
				InitAck cancelAck = mapper.readValue(msg.getText(), InitAck.class);
				_logger.info("got ORDER CAN_ACK data from outqueue : " + cancelAck);
				requestId = cancelAck.getRequestId();
				String portme_in = ReadConfigFile.getProperties().getProperty("CANCEL_IN");
				current_status = 6;
				List<String> listOfMsisdn = portMeService.getListOfMSISDN(requestId);
				for (String msisdn : listOfMsisdn) {
					portMeTransaction = new PortMeTransactionDetails();
					portMeTransaction.setRequestId(requestId);
					portMeTransaction.setStatus(current_status);
					portMeTransaction.setRequestType(portme_in);
					portMeTransaction.setMsisdn(msisdn);
					portMeTransactionService.savePortMeTransactionDetails(portMeTransaction);
					subscriberArrTypeService.updatePortMtStatusByMsisdn(current_status, msisdn, portme_in, 200);
				}
				// going to convert into xml format
				_logger.info("converting order cancellation ack into xml config file");
				xml = new NPOUtils().convertInitAckIntoXML(cancelAck);
				sessionId = Long.toString(System.currentTimeMillis());
				int interval = jmsProducer.sentIntoInternalInQ(xml, sessionId);
				if (interval == 1) {
					current_status = 6;
					_logger.info("Send order cancellation ack xml into internalIn jms queue : " + xml);
				}
				if (current_status != 0) {
					portMeService.updatePortMeStatus(current_status, requestId);
				}
				_logger.info("[sessionId=" + sessionId
						+ "]: PortMeConsumer.receiveMessage()-CANCEL_ACK_MESSAGE Soap Message - process end with timestamp:["
						+ new Timestamp(System.currentTimeMillis()) + "]");
			}
			/* start acknowledgement code for order reversal */
			else if (messageType.equals(ReadConfigFile.getProperties().getProperty("REVERSAL_ACK_MESSAGE_TYPE"))) {
				_logger.info("[sessionId=" + sessionId
						+ "]: PortMeConsumer.receiveMessage()-REVERSAL_ACK_MESSAGE Soap Message - process start with timestamp:["
						+ new Timestamp(System.currentTimeMillis()) + "]");
				InitAck canAck = mapper.readValue(msg.getText(), InitAck.class);
				_logger.info("got ORDER REVERSAL_ACK data from outqueue : " + canAck);
				requestId = canAck.getRequestId();
				String portme_in = ReadConfigFile.getProperties().getProperty("REVERSAL_IN");
				current_status = 6;
				List<String> listOfMsisdn = portMeService.getListOfMSISDN(requestId);
				for (String msisdn : listOfMsisdn) {
					portMeTransaction = new PortMeTransactionDetails();
					portMeTransaction.setRequestId(requestId);
					portMeTransaction.setStatus(current_status);
					portMeTransaction.setRequestType(portme_in);
					portMeTransaction.setMsisdn(msisdn);
					portMeTransactionService.savePortMeTransactionDetails(portMeTransaction);
					subscriberArrTypeService.updatePortMtStatusByMsisdn(current_status, msisdn, portme_in, 200);
				}
				// going to convert into xml format
				_logger.info("converting order REVERSAL ack into xml config file");
				xml = new NPOUtils().convertInitAckIntoXML(canAck);
				sessionId = Long.toString(System.currentTimeMillis());
				int interval = jmsProducer.sentIntoInternalInQ(xml, sessionId);
				if (interval == 1) {
					current_status = 6;
					_logger.info("Send order REVERSAL ack xml into internalIn jms queue : " + xml);
				}
				if (current_status != 0) {
					portMeService.updatePortMeStatus(current_status, requestId);
				}
				_logger.info("[sessionId=" + sessionId
						+ "]: PortMeConsumer.receiveMessage()-REVERSAL_ACK_MESSAGE Soap Message - process end with timestamp:["
						+ new Timestamp(System.currentTimeMillis()) + "]");
			}
			/* start code ORDER Reversal confirmation */
			else if (messageType.equals(ReadConfigFile.getProperties().getProperty("REVERSAL_MESSAGE_TYPE"))) {
				_logger.info("[sessionId=" + sessionId
						+ "]: PortMeConsumer.receiveMessage()-ORDER REVERSAL_MESSAGE Soap Message - process start with timestamp:["
						+ new Timestamp(System.currentTimeMillis()) + "]");
				OrderReversal orderReversal = mapper.readValue(msg.getText(), OrderReversal.class);
				_logger.info("got ORDER REVERSAL Confirmation from outqueue : " + orderReversal);
				requestId = PortMeUtils.randomUniqueUUID();
				String reqType = ReadConfigFile.getProperties().getProperty("REVERSAL_OUT");
				current_status = 1;
				String msisdn = orderReversal.getSubscriberSequence().getSubscriberNumber();
				PortMe portReversal = new PortMe();
				portReversal.setRequest_type(reqType);
				portReversal.setRequestId(requestId);
				portReversal.setStatus(current_status);
				PortMe portMe = portMeService.savePortMe(portReversal);
				orderReversal.setRequestId(requestId);
				orderReversal.setSource(portReversal.getSource());
				portMeTransaction = new PortMeTransactionDetails();
				portMeTransaction.setRequestId(orderReversal.getRequestId());
				portMeTransaction.setStatus(current_status);
				portMeTransaction.setRequestType(reqType);
				portMeTransaction.setMsisdn(msisdn);
				portMeTransactionService.savePortMeTransactionDetails(portMeTransaction);
				subscriberArrTypeService.insertWithQuery(portMe.getPortId(), msisdn, reqType, current_status);
				_logger.info("[sessionId=" + sessionId
						+ "]: PortMeConsumer.receiveMessage()-ORDER REVERSAL_MESSAGE Soap Message - process end with timestamp:["
						+ new Timestamp(System.currentTimeMillis()) + "]");

			}
			/* start code for recovery DB ACKNOWLEDGE */
			else if (messageType.equals(ReadConfigFile.getProperties().getProperty("RECOVERY_DB_ACK_MESSAGE_TYPE"))) {
				_logger.info("[sessionId=" + sessionId
						+ "]: PortMeConsumer.receiveMessage()-RECOVERY_DB_ACK_MESSAGE Soap Message - process start with timestamp:["
						+ new Timestamp(System.currentTimeMillis()) + "]");
				InitAck ack = mapper.readValue(msg.getText(), InitAck.class);
				_logger.info("got ORDER REVERSAL_ACK data from outqueue : " + ack);
				requestId = ack.getRequestId();
				int success = recoveryDBService.updateAckOfRecoveryDB(requestId, ack.getResultCode());
				if (success == 1) {
					_logger.info("updated acknowledge of recovery db reqeust with requestId : " + requestId);
				} else {
					_logger.info("unable to updated acknowledge of recovery db reqeust with requestId : " + requestId);
				}
				_logger.info("[sessionId=" + sessionId
						+ "]: PortMeConsumer.receiveMessage()-RECOVERY_DB_ACK_MESSAGE Soap Message - process end with timestamp:["
						+ new Timestamp(System.currentTimeMillis()) + "]");
			}

			/* start code for NPO Response */

			/* start code for NPOAResponse Soap request with JMS out queue */
			else if (messageType.equals(ReadConfigFile.getProperties().getProperty("NPORsp_MESSAGE_TYPE"))) {
				_logger.info("[sessionId=" + sessionId
						+ "]: PortMeConsumer.receiveMessage()-NPORsp Soap Message - process start with timestamp:["
						+ new Timestamp(System.currentTimeMillis()) + "]");
				mapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);
				RSP[] list = mapper.readValue(msg.getText(), RSP[].class);
				for (RSP npoarsp : list) {
					_logger.info("GET NPO_RSP from jms outqueue : " + npoarsp);
					requestId = npoarsp.getReferenceId();
					String portme_out = ReadConfigFile.getProperties().getProperty("PORTME_IN");
					_logger.info(
							"start processing of NPOResponse message from jms outqueue with requestId-" + requestId);
					current_status = 6;
					portMeTransaction.setRequestId(requestId);
					portMeTransaction.setStatus(current_status);
					portMeTransaction.setRequestType(portme_out);
					_logger.debug("start process to update data");
					if (npoarsp.getSubscriberResult().size() > 0) {
						portMeTransactionService.savePortMeTransactionDetail(portMeTransaction,
								npoarsp.getSubscriberResult());
						for (SubscriberResult subResult : npoarsp.getSubscriberResult()) {
							portMeService.updatePortMtResultCodeByMsisdn(current_status,
									subResult.getSubscriberNumber(), portme_out, subResult.getResultCode());
						}
					}
					_logger.debug("successfully updated data");
					// going to convert into xml format
					_logger.info("converting NPO_RSP into xml config file");
					_logger.info("trying to convert NPOResponse message into xml with requestId-" + requestId);
					xml = "NPORSP";// new NPOUtils().convertJsonIntoNPOARsp(npoarsp);
					int interval = jmsProducer.sentIntoInternalInQ(xml, sessionId);
					if (interval == 1) {
						current_status = 6;
						_logger.info("processed NPOResponse with requestId-" + requestId);
					}
					if (current_status != 0) {
						portMeService.updatePortMeStatus(current_status, requestId);
					}
					_logger.info("[sessionId=" + sessionId
							+ "]: PortMeConsumer.receiveMessage()-NPORsp Soap Message - process end with timestamp:["
							+ new Timestamp(System.currentTimeMillis()) + "]");
				}
			}

			/* start code for NPOAResponse Soap request with JMS out queue */
			else if (messageType.equals(ReadConfigFile.getProperties().getProperty("NPOARsp_MESSAGE_TYPE"))) {
				_logger.info("[sessionId=" + sessionId
						+ "]: PortMeConsumer.receiveMessage()-NPOARsp Soap Message - process start with timestamp:["
						+ new Timestamp(System.currentTimeMillis()) + "]");
				mapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);
				RSP[] list = mapper.readValue(msg.getText(), RSP[].class);
				for (RSP npoarsp : list) {
					_logger.info("GET NPOA_RSP from jms outqueue : " + npoarsp);
					requestId = npoarsp.getRequestId();
					String portme_out = ReadConfigFile.getProperties().getProperty("PORTME_OUT");
					_logger.info(
							"start processing of NPOAResponse message from jms outqueue with requestId-" + requestId);
					current_status = 9;
					portMeTransaction.setRequestId(npoarsp.getRequestId());
					portMeTransaction.setStatus(current_status);
					portMeTransaction.setRequestType(portme_out);
					if (npoarsp.getSubscriberResult().size() > 0) {
						portMeTransactionService.savePortMeTransactionDetail(portMeTransaction,
								npoarsp.getSubscriberResult());
						for (SubscriberResult subResult : npoarsp.getSubscriberResult()) {
							portMeService.updateNPOARsp(current_status, subResult.getSubscriberNumber(), portme_out,
									subResult.getResultCode());
						}
					}
					// going to convert into xml format
					_logger.info("converting NPOA_RSP into xml config file");

					_logger.info("trying to convert NPOAResponse message into xml with requestId-" + requestId);
					xml = "NPOARSP";// new NPOUtils().convertJsonIntoNPOARsp(npoarsp);
					sessionId = Long.toString(System.currentTimeMillis());
					int interval = jmsProducer.sentIntoInternalInQ(xml, sessionId);
					if (interval == 1) {
						current_status = 9;
						_logger.info("processed NPOAResponse with requestId-" + requestId);
					}
					if (current_status != 0) {
						portMeService.updatePortMeStatus(current_status, requestId);
					}
					_logger.info("[sessionId=" + sessionId
							+ "]: PortMeConsumer.receiveMessage()-NPOARsp Soap Message - process end with timestamp:["
							+ new Timestamp(System.currentTimeMillis()) + "]");
				}
			}
			/* end code for NPOAResponse */
			/* start code for NPOT TERMINATE ACK request with jms out queue */
			else if (messageType.equals(ReadConfigFile.getProperties().getProperty("TERMINATE_ACK_MESSAGE_TYPE"))) {
				_logger.info("[sessionId=" + sessionId
						+ "]: PortMeConsumer.receiveMessage()-TERMINATE_ACK Soap Message - process start with timestamp:["
						+ new Timestamp(System.currentTimeMillis()) + "]");
				InitAck terminateAck = mapper.readValue(msg.getText(), InitAck.class);
				_logger.info("got NPOT TERMINATE_ACK data from outqueue : " + terminateAck);
				requestId = terminateAck.getRequestId();
				String terminate_in = ReadConfigFile.getProperties().getProperty("TERMINATE_IN");
				current_status = 6;
				List<String> listOfMsisdn = portMeService.getListOfMSISDN(requestId);
				for (String msisdn : listOfMsisdn) {
					portMeTransaction = new PortMeTransactionDetails();
					portMeTransaction.setRequestId(requestId);
					portMeTransaction.setStatus(current_status);
					portMeTransaction.setRequestType(terminate_in);
					portMeTransaction.setMsisdn(msisdn);
					portMeTransactionService.savePortMeTransactionDetails(portMeTransaction);
					subscriberArrTypeService.updatePortMtStatusByMsisdn(current_status, msisdn, terminate_in, 200);
				}
				// going to convert into xml format
				_logger.info("converting NPOT (TERMINATE SIM) ACK into xml config file");
				xml = new NPOUtils().convertInitAckIntoXML(terminateAck);
				sessionId = Long.toString(System.currentTimeMillis());
				int interval = jmsProducer.sentIntoInternalInQ(xml, sessionId);
				if (interval == 1) {
					current_status = 6;
					_logger.info("Send NPOT_ACK (TERMINATE SIM) xml into internalIn jms queue : " + xml);
				}
				if (current_status != 0) {
					portMeService.updatePortMeStatus(current_status, requestId);
				}
				_logger.info("[sessionId=" + sessionId
						+ "]: PortMeConsumer.receiveMessage()-TERMINATE_ACK Soap Message - process end with timestamp:["
						+ new Timestamp(System.currentTimeMillis()) + "]");
			}
			// getting order cancellation soap request from external system
			else if (messageType.equals(ReadConfigFile.getProperties().getProperty("CANCEL_MESSAGE_TYPE"))) {
				_logger.info("[sessionId=" + sessionId
						+ "]: PortMeConsumer.receiveMessage()-PORT CANCEL Soap Message - process start with timestamp:["
						+ new Timestamp(System.currentTimeMillis()) + "]");
				OrderCancellation orderCancel = mapper.readValue(msg.getText(), OrderCancellation.class);
				_logger.info("got ORDER cancel reqeust from external system : " + orderCancel);
				requestId = PortMeUtils.randomUniqueUUID();
				String curReqType = ReadConfigFile.getProperties().getProperty("CANCEL_OUT");
				String preReqType = ReadConfigFile.getProperties().getProperty("CANCEL_IN");
				current_status = 1;
				String msisdn = orderCancel.getSubscriberSequence().getSubscriberNumber();
				portMeTransaction = new PortMeTransactionDetails();
				portMeTransaction.setRequestId(orderCancel.getRequestId());
				portMeTransaction.setStatus(current_status);
				portMeTransaction.setRequestType(curReqType);
				portMeTransaction.setMsisdn(msisdn);
				portMeTransactionService.savePortMeTransactionDetails(portMeTransaction);
				portMeService.cancelOrderByMsisdn(current_status, msisdn, preReqType, curReqType);
				_logger.info("[sessionId=" + sessionId
						+ "]: PortMeConsumer.receiveMessage()-PORT CANCEL Soap Message - process end with timestamp:["
						+ new Timestamp(System.currentTimeMillis()) + "]");
			}
			// end order cancellation soap request from external system
			// getting order NPOS(Suspension Recipient Request) soap request from external
			// system
			else if (messageType.equals(ReadConfigFile.getProperties().getProperty("NPOS_MESSAGE_TYPE"))) {
				_logger.info("[sessionId=" + sessionId
						+ "]: PortMeConsumer.receiveMessage()-NPOS (Suspension Recipient Request) Soap Message - process start with timestamp:["
						+ new Timestamp(System.currentTimeMillis()) + "]");
				mapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);
				NPOS[] listOfNPOS = mapper.readValue(msg.getText().toString(), NPOS[].class);
				for (NPOS npos : listOfNPOS) {
					String susRecipientIn = ReadConfigFile.getProperties().getProperty("SUS_RECIPIENT_IN");
					BillingResolution billingResolution = new BillingResolution();
					billingResolution.setTransactionId(npos.getReferenceId());
					billingResolution.setMsisdn(npos.getSubscriberNumber());
					billingResolution.setBill_date(npos.getBillDate());
					billingResolution.setDue_date(npos.getDueDate());
					billingResolution.setAmount(npos.getAmount());
					billingResolution.setComments(npos.getRemark());
					billingResolution.setRequest_type(susRecipientIn);
					billingResolution.setStatus(1);// it shoudld be config file
					billingResolution.setCreated_date(timestamp);
					billingResolution.setUpdated_date(timestamp);
					billingResolutionService.saveBillingResolution(billingResolution);
				}
				_logger.info("[sessionId=" + sessionId
						+ "]: PortMeConsumer.receiveMessage()-NPOS (Suspension Recipient Request) Soap Message - process end with timestamp:["
						+ new Timestamp(System.currentTimeMillis()) + "]");
			}
			// END order NPOS soap request from external system
			// getting order NPOSPR(Recipient Suspension Cancel) soap request from external
			// system
			else if (messageType.equals(ReadConfigFile.getProperties().getProperty("NPOSPR_MESSAGE_TYPE"))) {
				_logger.info("[sessionId=" + sessionId
						+ "]: PortMeConsumer.receiveMessage()-NPOSPR(Recipient Suspension Cancel) Soap Message - process start with timestamp:["
						+ new Timestamp(System.currentTimeMillis()) + "]");
				mapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);
				NPOSPR[] listOfNPOSPR = mapper.readValue(msg.getText().toString(), NPOSPR[].class);
				for (NPOSPR npospr : listOfNPOSPR) {
					_logger.info("got NPOSPR (Recipient Suspension Cancel reqeust) from external system : " + npospr);
					// UPDATE
					String susRecipientIn = ReadConfigFile.getProperties().getProperty("SUS_RECIPIENT_IN");
					BillingResolution billingResolution = new BillingResolution();
					billingResolution.setTransactionId(npospr.getReferenceId());
					billingResolution.setRequest_type(susRecipientIn);
					billingResolution.setStatus(2);
					billingResolution.setReason(npospr.getResultCode());
					billingResolution.setUpdated_date(timestamp);
					billingResolution.setCanceled_date(timestamp);
					billingResolutionDao.updateNPOSPR(billingResolution, sessionId);
				}
				_logger.info("[sessionId=" + sessionId
						+ "]: PortMeConsumer.receiveMessage()-NPOSPR(Recipient Suspension Cancel) Soap Message - process end with timestamp:["
						+ new Timestamp(System.currentTimeMillis()) + "]");
			}
			// END order NPOSPR soap request from external system
			// getting order NPOSAACK(Recipient Suspension Acknowledge) soap request from
			// external system
			else if (messageType.equals(ReadConfigFile.getProperties().getProperty("NPOSAACK_MESSAGE_TYPE"))) {
				_logger.info("[sessionId=" + sessionId
						+ "]: PortMeConsumer.receiveMessage()-NPOSAACK(Recipient Suspension Acknowledge) Soap Message - process start with timestamp:["
						+ new Timestamp(System.currentTimeMillis()) + "]");
				mapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);
				NPOSAACK[] list = mapper.readValue(msg.getText().toString(), NPOSAACK[].class);
				for (NPOSAACK nposaack : list) {
					String susRecipientIn = ReadConfigFile.getProperties().getProperty("SUS_RECIPIENT_IN");
					BillingResolution billingResolution = new BillingResolution();
					billingResolution.setTransactionId(nposaack.getReferenceId());
					billingResolution.setStatus(3);
					billingResolution.setRequest_type(susRecipientIn);
					billingResolution.setReason(nposaack.getResultCode());
					billingResolutionDao.updateNPOSAACK(billingResolution, sessionId);
				}
				_logger.info("[sessionId=" + sessionId
						+ "]: PortMeConsumer.receiveMessage()-NPOSAACK(Recipient Suspension Acknowledge) Soap Message - process end with timestamp:["
						+ new Timestamp(System.currentTimeMillis()) + "]");
			}
			// END order NPOSAACK soap request from external system
			// getting order NPOSA soap request from external system
			else if (messageType.equals(ReadConfigFile.getProperties().getProperty("NPOSA_MESSAGE_TYPE"))) {
				_logger.info("[sessionId=" + sessionId
						+ "]: PortMeConsumer.receiveMessage()-NPOSA Soap Message - process start with timestamp:["
						+ new Timestamp(System.currentTimeMillis()) + "]");
				mapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);
				NPOSA[] list = mapper.readValue(msg.getText().toString(), NPOSA[].class);
				for (NPOSA nposa : list) {
					String susDonorIn = ReadConfigFile.getProperties().getProperty("SUS_DONOR_IN");
					BillingResolution billingResolution = new BillingResolution();
					billingResolution.setTransactionId(nposa.getReferenceId());
					billingResolution.setStatus(4);
					billingResolution.setRequest_type(susDonorIn);
					billingResolution.setReason(nposa.getResultCode());
					billingResolutionDao.updateNPOSA(billingResolution, sessionId);
					_logger.info("got NPOSA reqeust from external system");
				}
				_logger.info("[sessionId=" + sessionId
						+ "]: PortMeConsumer.receiveMessage()-NPOSA Soap Message - process end with timestamp:["
						+ new Timestamp(System.currentTimeMillis()) + "]");
			}
			// END order NPOSA soap request from external system
			// getting order NPOTA soap request from external system
			else if (messageType.equals(ReadConfigFile.getProperties().getProperty("NPOTA_MESSAGE_TYPE"))) {
				_logger.info("[sessionId=" + sessionId
						+ "]: PortMeConsumer.receiveMessage()-NPOTA Soap Message - process start with timestamp:["
						+ new Timestamp(System.currentTimeMillis()) + "]");
				// mapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);
				// NPOTA[] list = mapper.readValue(msg.getText().toString(), NPOTA[].class);
				// UPDATE TERMINATE TABLE TERMINATE IN
				_logger.info("[sessionId=" + sessionId
						+ "]: PortMeConsumer.receiveMessage()-NPOTA Soap Message - process end with timestamp:["
						+ new Timestamp(System.currentTimeMillis()) + "]");
			}
			// END order NPOTA soap request from external system
			// getting order NPOTA soap request from external system
			else if (messageType.equals(ReadConfigFile.getProperties().getProperty("NPV_MESSAGE_TYPE"))) {
				_logger.info("[sessionId=" + sessionId
						+ "]: PortMeConsumer.receiveMessage()-NPV Soap Message - process start with timestamp:["
						+ new Timestamp(System.currentTimeMillis()) + "]");
				mapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);
				NVP[] list = mapper.readValue(msg.getText().toString(), NVP[].class);
				for (NVP nvp : list) {
					// INSERT INTO SUBSCRIBER_VALIDATION
					// TODO : NEED TO BE IMPLEMENTED
					_logger.info("got NPOTA reqeust from external system : " + nvp);
				}
				_logger.info("[sessionId=" + sessionId
						+ "]: PortMeConsumer.receiveMessage()-NPV Soap Message - process end with timestamp:["
						+ new Timestamp(System.currentTimeMillis()) + "]");
			}
			// END order NPOTA soap request from external system
			// gettinng recovery DB partial response from external system
			else if (messageType
					.equals(ReadConfigFile.getProperties().getProperty("RECOVERY_DB_PARTIAL_MESSAGE_TYPE"))) {
				_logger.info("[sessionId=" + sessionId
						+ "]: PortMeConsumer.receiveMessage()-RECOVERY_DB_PARTIAL Soap Message - process start with timestamp:["
						+ new Timestamp(System.currentTimeMillis()) + "]");
				mapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);
				RecoveryDBResponse partialResponse = mapper.readValue(msg.getText(), RecoveryDBResponse.class);
				_logger.info("[sessionId=" + sessionId
						+ "]: PortMConsumser.recievedMessage recieved recovery DB Partial response from mch with timestamp:["
						+ new Timestamp(System.currentTimeMillis()) + "]");
				RecoveryDB recoveryDB = new RecoveryDB();
				recoveryDB.setRequest_id(partialResponse.getRequestId());
				recoveryDB.setResult_code(partialResponse.getResultCode());
				recoveryDB.setFile_name(partialResponse.getDataFileName());
				int success = recoveryDBService.updateRecoveryDB(recoveryDB);
				if (success != 0) {
					_logger.info("[sessionId=" + sessionId
							+ "]: PortMConsumser.recievedMessage updated recovery DB Partial response into db with timestamp:["
							+ new Timestamp(System.currentTimeMillis()) + "]");
				} else {
					_logger.info("[sessionId=" + sessionId
							+ "]: PortMConsumser.recievedMessage fail to update recovery DB Partial response into db with timestamp:["
							+ new Timestamp(System.currentTimeMillis()) + "]");
				}
				_logger.info("[sessionId=" + sessionId
						+ "]: PortMeConsumer.receiveMessage()-RECOVERY_DB_PARTIAL Soap Message - process end with timestamp:["
						+ new Timestamp(System.currentTimeMillis()) + "]");
			}
			// gettinng recovery DB full response from external system
			else if (messageType.equals(ReadConfigFile.getProperties().getProperty("RECOVERY_DB_FULL_MESSAGE_TYPE"))) {
				_logger.info("[sessionId=" + sessionId
						+ "]: PortMeConsumer.receiveMessage()-RECOVERY_DB_FULL Soap Message - process start with timestamp:["
						+ new Timestamp(System.currentTimeMillis()) + "]");
				mapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);
				RecoveryDBResponse partialResponse = mapper.readValue(msg.getText(), RecoveryDBResponse.class);
				_logger.info("[sessionId=" + sessionId
						+ "]: PortMConsumser.recievedMessage recieved recovery DB Full response from mch with timestamp:["
						+ new Timestamp(System.currentTimeMillis()) + "]");
				RecoveryDB recoveryDB = new RecoveryDB();
				recoveryDB.setRequest_id(partialResponse.getRequestId());
				recoveryDB.setResult_code(partialResponse.getResultCode());
				recoveryDB.setFile_name(partialResponse.getDataFileName());
				int success = recoveryDBService.updateRecoveryDB(recoveryDB);
				if (success != 0) {
					_logger.info("[sessionId=" + sessionId
							+ "]: PortMConsumser.recievedMessage updated recovery DB Full response into db with timestamp:["
							+ new Timestamp(System.currentTimeMillis()) + "]");
				} else {
					_logger.info("[sessionId=" + sessionId
							+ "]: PortMConsumser.recievedMessage fail to update recovery DB Full response into db with timestamp:["
							+ new Timestamp(System.currentTimeMillis()) + "]");
				}
				_logger.info("[sessionId=" + sessionId
						+ "]: PortMeConsumer.receiveMessage()-RECOVERY_DB_FULL Soap Message - process end with timestamp:["
						+ new Timestamp(System.currentTimeMillis()) + "]");
			}

			/* start acknowledgement code for SC-NOTICE-ANSWER */
			else if (messageType.equals(ReadConfigFile.getProperties().getProperty("SCNOTICEANS_ACK_MESSAGE_TYPE"))) {
				_logger.info("[sessionId=" + sessionId
						+ "]: PortMeConsumer.receiveMessage()-SC-NOTICE-ANSWER_ACK Soap Message - process start with timestamp:["
						+ new Timestamp(System.currentTimeMillis()) + "]");
				mapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);
				InitAck scNoticeAck = mapper.readValue(msg.getText(), InitAck.class);
				_logger.info("got SC-NOTICE-ANSWER_ACK data from outqueue : " + scNoticeAck);
				String transactionId = scNoticeAck.getBatchId();
				broadcastHistoryService.updateBroadcastHistory(transactionId, scNoticeAck.getResultCode());
				_logger.info("[sessionId=" + sessionId
						+ "]: PortMeConsumer.receiveMessage()-SC-NOTICE-ANSWER_ACK Soap Message - process end with timestamp:["
						+ new Timestamp(System.currentTimeMillis()) + "]");
			}
			/* start acknowledgement code for SD-NOTICE-ANSWER */
			else if (messageType.equals(ReadConfigFile.getProperties().getProperty("SDNOTICEANS_ACK_MESSAGE_TYPE"))) {
				_logger.info("[sessionId=" + sessionId
						+ "]: PortMeConsumer.receiveMessage()-SD-NOTICE-ANSWER_ACK Soap Message - process start with timestamp:["
						+ new Timestamp(System.currentTimeMillis()) + "]");
				mapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);
				InitAck sdNoticeAck = mapper.readValue(msg.getText(), InitAck.class);
				_logger.info("got SD-NOTICE-ANSWER_ACK data from outqueue : " + sdNoticeAck);
				String transactionId = sdNoticeAck.getBatchId();
				broadcastHistoryService.updateBroadcastHistory(transactionId, sdNoticeAck.getResultCode());
				_logger.info("[sessionId=" + sessionId
						+ "]: PortMeConsumer.receiveMessage()-SD-NOTICE-ANSWER_ACK Soap Message - process end with timestamp:["
						+ new Timestamp(System.currentTimeMillis()) + "]");
			}
		} catch (JsonProcessingException | JMSException | JSONException e) {
			_logger.error("Received Message with error : " + e.getMessage() + "RequestId - " + requestId, e);
		}
		if (current_status != 0) {
			// going to update current status in port_tx table
		}
	}

	@SuppressWarnings("deprecation")
	//@JmsListener(destination = "Z1ActivationQueue") // SC and SD data getting from activation queue
	public void receiveScSdTypeDetails(Message message) {
		String sessionId = Long.toString(System.currentTimeMillis());
		_logger.info("[sessionId=" + sessionId
				+ "]: PortMeConsumer.receiveScSdTypeDetails()- Recieved Soap Message - process start with timestamp:["
				+ new Timestamp(System.currentTimeMillis()) + "]");
		int current_status = 0;
		String requestId = null;
		String messageType = "";
		try {
			PortMeTransactionDetails portMeTransaction = new PortMeTransactionDetails();
			TextMessage msg = (TextMessage) message;
			_logger.info("Received Message: " + msg.getText());
			ObjectMapper mapper = new ObjectMapper();
			JsonNode arrayNode = mapper.readTree(msg.getText().toString());
			if (arrayNode.isArray() && arrayNode.size() > 0) {
				JsonNode firstObject = arrayNode.get(0);
				if (firstObject.has("messageType")) {
					messageType = firstObject.get("messageType").asText();
					System.out.println("messageType: " + messageType);
				}
			}
			String xml = null;
			if (messageType.equals(ReadConfigFile.getProperties().getProperty("SD_MESSAGE_TYPE"))) {
				_logger.info("[sessionId=" + sessionId
						+ "]: PortMeConsumer.receiveScSdTypeDetails()- SD Soap Message - process start with timestamp:["
						+ new Timestamp(System.currentTimeMillis()) + "]");
				mapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);
				SD[] list = mapper.readValue(msg.getText().toString(), SD[].class);
				for (SD sdType : list) {
					_logger.info("Received Message SD xml from jms ActivationQueue : " + xml);
					current_status = 12;
					String portme_out = ReadConfigFile.getProperties().getProperty("PORTME_OUT");
					_logger.debug("Start updating data into database");
					if (sdType.getSDInfo().size() > 0) {
						for (SDInfo sdInfo : sdType.getSDInfo()) {
							requestId = sdInfo.getReferenceId();
							portMeTransaction = new PortMeTransactionDetails();
							portMeTransaction.setRequestId(requestId);
							portMeTransaction.setStatus(current_status);
							portMeTransaction.setRequestType(portme_out);
							portMeTransaction.setMsisdn(sdInfo.getSubscriberNumber());
							portMeTransactionService.savePortMeTransactionDetails(portMeTransaction);
							subscriberArrTypeService.updatePortMtStatusByMsisdn(current_status,
									sdInfo.getSubscriberNumber(), portme_out, 200);
						}
					}
					_logger.debug("successfully updated data into database");
					// going to convert into xml format
					_logger.info("trying to convert SD message into xml with reqeustId-" + requestId);
					xml = new NPOUtils().convertSDTypeIntoXML(sdType);
					int interval = jmsProducer.sentIntoInternalInQ(xml, sessionId);
					if (interval == 1) {
						_logger.info("successfully sent SD Soap Response into queue with requestId-" + requestId);
					} else {
						_logger.info("unable to send SD Soap Response into queue with requestId-" + requestId);
					}
					if (current_status != 0) {
						portMeService.updatePortMeStatus(current_status, requestId);
					}
				}
				_logger.info("[sessionId=" + sessionId
						+ "]: PortMeConsumer.receiveScSdTypeDetails()- SD Soap Message - process end with timestamp:["
						+ new Timestamp(System.currentTimeMillis()) + "]");
			} else if (messageType.equals(ReadConfigFile.getProperties().getProperty("SC_MESSAGE_TYPE"))) {
				_logger.info("[sessionId=" + sessionId
						+ "]: PortMeConsumer.receiveScSdTypeDetails()- SC Soap Message - process start with timestamp:["
						+ new Timestamp(System.currentTimeMillis()) + "]");
				mapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);
				SC[] list = mapper.readValue(msg.getText().toString(), SC[].class);
				for (SC scType : list) {
					_logger.info("Received Message SC xml : " + xml);
					current_status = 12;
					String portme_in = ReadConfigFile.getProperties().getProperty("PORTME_IN");
					_logger.debug("Start to proccessing to insert data into database");
					if (scType.getSCInfo().size() > 0) {
						for (SCInfo scInfo : scType.getSCInfo()) {
							requestId = scInfo.getReferenceId();
							portMeTransaction = new PortMeTransactionDetails();
							portMeTransaction.setRequestId(requestId);
							portMeTransaction.setStatus(current_status);
							portMeTransaction.setRequestType(portme_in);
							portMeTransaction.setMsisdn(scInfo.getSubscriberNumber());
							portMeTransactionService.savePortMeTransactionDetails(portMeTransaction);
							subscriberArrTypeService.updatePortMtStatusByMsisdn(current_status,
									scInfo.getSubscriberNumber(), portme_in, 200);
						}
					}
					_logger.debug("successfully processed to insert data into database");
					// going to convert into xml format
					_logger.info("trying to convert SC Soap message into xml with requestId-" + requestId);
					xml = new NPOUtils().convertSCTypeIntoXML(scType);
					int interval = jmsProducer.sentIntoInternalInQ(xml, sessionId);
					if (interval == 1) {
						_logger.info("Successfully sent SC Soap Response into queue with requestId-" + requestId);
					} else {
						_logger.info("unable to sent SC Soap Response into queue with requestId-" + requestId);
					}
					if (current_status != 0) {
						portMeService.updatePortMeStatus(current_status, requestId);
					}
				}
				_logger.info("[sessionId=" + sessionId
						+ "]: PortMeConsumer.receiveScSdTypeDetails()- SC Soap Message - process end with timestamp:["
						+ new Timestamp(System.currentTimeMillis()) + "]");
			}
		} catch (JMSException e) {
			handleException("Received Message with JMS error:", e, requestId);
		} catch (JSONException e) {
			handleException("Received Message with JSON error:", e, requestId);
		} catch (JsonMappingException e) {
			handleException("Received Message with JSON MAPPING error:", e, requestId);
		} catch (JsonProcessingException e) {
			handleException("Received Message with JSON Processing error:", e, requestId);
		}
	}

	private void handleException(String message, Exception e, String requestId) {
		_logger.error(message + " " + e.getMessage() + " RequestId - " + requestId, e);
	}
}
