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
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import com.megthink.gateway.dao.BillingResolutionDao;
import com.megthink.gateway.model.BillingResolution;
import com.megthink.gateway.model.InitAck;
import com.megthink.gateway.model.NPCData;
import com.megthink.gateway.model.NPO;
import com.megthink.gateway.model.NonpaymentDisconnReq;
import com.megthink.gateway.model.NonpaymentDisconnResp;
import com.megthink.gateway.model.NpdAckResponse;
import com.megthink.gateway.model.NpdCancelRequest;
import com.megthink.gateway.model.NumberRange;
import com.megthink.gateway.model.NumberRangeFlagged;
import com.megthink.gateway.model.OrderCancellation;
import com.megthink.gateway.model.OrderReversal;
import com.megthink.gateway.model.PortCancelNotification;
import com.megthink.gateway.model.PortDeactWithRte;
import com.megthink.gateway.model.PortExecute;
import com.megthink.gateway.model.PortMe;
import com.megthink.gateway.model.PortMeTransactionDetails;
import com.megthink.gateway.model.PortRequest;
import com.megthink.gateway.model.PortRespWithFlag;
import com.megthink.gateway.model.PortTerminated;
import com.megthink.gateway.model.TerminateSim;
import com.megthink.gateway.model.TerminateSimMT;
import com.megthink.gateway.model.TerminateSimTransactionDetails;
import com.megthink.gateway.producer.JmsProducer;
import com.megthink.gateway.service.BillingResolutionService;
import com.megthink.gateway.service.PortMeService;
import com.megthink.gateway.service.PortMeTransactionDetailsService;
import com.megthink.gateway.service.SubscriberArrTypeService;
import com.megthink.gateway.service.TerminateSimMTService;
import com.megthink.gateway.service.TerminateSimService;
import com.megthink.gateway.service.TerminateSimTransactionDetailsService;
import com.megthink.gateway.utils.NPOUtils;
import com.megthink.gateway.utils.PortMeUtils;
import com.megthink.gateway.utils.ReadConfigFile;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;

@Component
public class PortMeZ2Consumer {

	private static final Logger _logger = LoggerFactory.getLogger(PortMeZ2Consumer.class);

	Timestamp timestamp = new Timestamp(System.currentTimeMillis());

	@Autowired
	private PortMeService portMeService;
	@Autowired
	private PortMeTransactionDetailsService portMeTransactionService;
	@Autowired
	private TerminateSimService terminateSimService;
	@Autowired
	private TerminateSimTransactionDetailsService terminateTransactionService;
	@Autowired
	private TerminateSimMTService terminateSimMTService;
	@Autowired
	private SubscriberArrTypeService subscriberArrTypeService;
	@Autowired
	private BillingResolutionService billingResolutionService;
	@Autowired
	private BillingResolutionDao billingResolutionDao;
	@Autowired
	private JmsProducer jmsProducer;

	@SuppressWarnings("deprecation")
	//@JmsListener(destination = "Z2INQueue")
	public void receiveMessage(Message message) {
		String sessionId = Long.toString(System.currentTimeMillis());
		_logger.info("[sessionId=" + sessionId
				+ "]: PortMeZ2Consumer.receiveMessage()- Recieved Z2 Soap Message - process start with timestamp:["
				+ new Timestamp(System.currentTimeMillis()) + "]");
		System.out.println("Received Z2 Message from queue : " + message.toString());
		int current_status = 0;
		String requestId = null;
		try {
			PortMeTransactionDetails portMeTransaction = new PortMeTransactionDetails();
			TextMessage msg = (TextMessage) message;
			_logger.info("Received Message: " + msg.getText());
			JSONObject jsonObj = new JSONObject(msg.getText().toString());
			ObjectMapper objectMapper = new ObjectMapper();
			objectMapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);
			String xml = null;
			NPCData npcData = objectMapper.readValue(msg.getText(), NPCData.class);
			/* start code for NPO SOAP request with jms out queue */
			if (npcData.getMessageHeader().getMessageID() == 1001) {
				PortRequest portRequest = objectMapper.readValue(msg.getText(), PortRequest.class);
				_logger.info("get NPO message from jms outqueue : ");
				requestId = PortMeUtils.randomUniqueUUID();
				if (portRequest.getDocumentFileName() != null) {
					Base64.Decoder decoder = Base64.getDecoder();
					String dStr = new String(decoder.decode(portRequest.getDocumentFileName()));
				}
				String portme_out = ReadConfigFile.getProperties().getProperty("PORTME_OUT");
				_logger.info("start processing of NPO message from jms outqueue with requestId-" + requestId);
				current_status = 1;
				portMeTransaction.setRequestId(requestId);
				portMeTransaction.setStatus(current_status);
				portMeTransaction.setRequestType(portme_out);
				NPO npo = new NPO();
				npo.setRequestId(requestId);
				for (NumberRange numberRange : portRequest.getNumberRange()) {
					portMeTransaction.setMsisdn(numberRange.getNumberFrom());
					portMeTransactionService.savePortMeTransactionDetails(portMeTransaction);
				}
				PortMe portMe = new PortMe();
				portMe.setSource(Integer.valueOf(npo.getMessageSenderTelco()));
				portMe.setRequestId(npo.getRequestId());
				portMe.setTimeStamp(npo.getTimestamp());
				portMe.setDno(npo.getDonorTelco());
				portMe.setArea(npo.getLsa());
				portMe.setRn(npo.getRouteNumber());
				portMe.setService(npo.getServiceType());
				portMe.setStatus(current_status);
				portMe.setRequest_type(portme_out);
				PortMe port = portMeService.savePortMe(portMe);
				if (port.getPortId() != 0) {
					for (NumberRange numberRange : portRequest.getNumberRange()) {
						subscriberArrTypeService.insertWithQuery(port.getPortId(), numberRange.getNumberFrom(),
								portme_out, current_status);
					}

					// going to convert into xml format
					_logger.info("trying to convert NPO message into xml with requestId-" + requestId);
					current_status = 2;
					portMeTransaction = new PortMeTransactionDetails();
					portMeTransaction.setRequestId(portMe.getRequestId());
					portMeTransaction.setStatus(current_status);
					portMeTransaction.setRequestType(portme_out);
					for (NumberRange numberRange : portRequest.getNumberRange()) {
						subscriberArrTypeService.updatePortMtStatusByMsisdn(current_status, numberRange.getNumberFrom(),
								portme_out, 200);
						portMeTransaction.setMsisdn(numberRange.getNumberFrom());
					}
					portMeTransactionService.savePortMeTransactionDetails(portMeTransaction);
					xml = "PORTREQEUST ZONE2";// new NPOUtils().convertNPOSoapIntoXML(npo , "");
					_logger.info("converted NPO message into xml with requestId-" + requestId);
					int interval = jmsProducer.sentIntoInternalInQ(xml, sessionId);
					if (interval == 1) {
						current_status = 3;
						portMeTransaction = new PortMeTransactionDetails();
						portMeTransaction.setRequestId(portMe.getRequestId());
						portMeTransaction.setStatus(current_status);
						portMeTransaction.setRequestType(portme_out);
						for (NumberRange numberRange : portRequest.getNumberRange()) {
							subscriberArrTypeService.updatePortMtStatusByMsisdn(current_status,
									numberRange.getNumberFrom(), portme_out, 200);
							portMeTransaction.setMsisdn(numberRange.getNumberFrom());
						}
						portMeTransactionService.savePortMeTransactionDetails(portMeTransaction);
						if (current_status != 0) {
							// going to update current status in port_tx table
							portMeService.updatePortMeStatus(current_status, requestId);
						}
					}
				}
			}
			/* end NPO SOAP Request */
			/* start code for NPOA Soap request with JMS out queue */
			else if (npcData.getMessageHeader().getMessageID() == 1003) {
				PortRespWithFlag npoa = objectMapper.readValue(msg.getText(), PortRespWithFlag.class);
				_logger.info("GET NPOA from jms outqueue : " + npoa);
				requestId = npcData.getMessageHeader().getTransactionID();
				String portme_in = ReadConfigFile.getProperties().getProperty("PORTME_IN");
				_logger.info("start processing of NPOA message from jms outqueue with requestId-" + requestId);
				current_status = 7;
				portMeTransaction.setRequestId(requestId);
				portMeTransaction.setStatus(current_status);
				portMeTransaction.setRequestType(portme_in);
				for (NumberRangeFlagged ranges : npoa.getNumberRangeFlagged()) {
					// portMeTransactionService.savePortMeTransactionDetail(portMeTransaction,
					// npoa.getSubscriberResult());
					portMeService.updateNPOARsp(current_status, ranges.getNumberFrom(), portme_in,
							200);
				}
				// going to convert into xml format
				_logger.info("converting NPOA into xml config file");
				current_status = 8;
				portMeTransaction = new PortMeTransactionDetails();
				portMeTransaction.setRequestId(requestId);
				portMeTransaction.setStatus(current_status);
				portMeTransaction.setRequestType(portme_in);
				for (NumberRangeFlagged ranges : npoa.getNumberRangeFlagged()) {
					// portMeTransactionService.savePortMeTransactionDetail(portMeTransaction,
					// npoa.getSubscriberResult());
					subscriberArrTypeService.updatePortMtStatusByMsisdn(current_status, ranges.getNumberFrom(),
							portme_in, 200);
				}
				_logger.info("trying to convert NPOA message into xml with requestId-" + requestId);
				xml = null;// new NPOUtils().convertNPOASoapIntoXML(npoa);
				sessionId = Long.toString(System.currentTimeMillis());
				int interval = jmsProducer.sentIntoInternalInQ(xml, sessionId);
				if (interval == 1) {
					current_status = 9;
					portMeTransaction = new PortMeTransactionDetails();
					portMeTransaction.setRequestId(requestId);
					portMeTransaction.setStatus(current_status);
					portMeTransaction.setRequestType(portme_in);
					for (NumberRangeFlagged ranges : npoa.getNumberRangeFlagged()) {
						// portMeTransactionService.savePortMeTransactionDetail(portMeTransaction,
						// npoa.getSubscriberResult());
						subscriberArrTypeService.updatePortMtStatusByMsisdn(current_status, ranges.getNumberFrom(),
								portme_in, 200);
					}
					_logger.info("processed NPOA Soap Reqeust with requestId-" + requestId);
				}
				if (current_status != 0) {
					portMeService.updatePortMeStatus(current_status, requestId);
				}
			}
			/* end code for NPOA Request */
			/* start code for NPOT Soap (TERMINATE SIM) request with JMS out queue */
			else if (npcData.getMessageHeader().getMessageID() == 9001) {
				PortTerminated npot = objectMapper.readValue(msg.getText(), PortTerminated.class);
				_logger.info("GET NPOT from jms outqueue : " + npot);
				String terminate_out = ReadConfigFile.getProperties().getProperty("TERMINATE_OUT");
				requestId = PortMeUtils.randomUniqueUUID();
				int resultCode = 200;
				_logger.info("start processing of NPOT message from jms outqueue with requestId-" + requestId);
				current_status = 1;
				TerminateSim terminationDetails = new TerminateSim();
				terminationDetails.setRequestId(requestId);
				terminationDetails.setSource(npcData.getMessageHeader().getMessageID());
				terminationDetails.setTimeStamp(timestamp.toString());
				// terminationDetails.setService("");
				terminationDetails.setRn(npot.getDonorLSAID());
				terminationDetails.setStatus(current_status);
				TerminateSim terminateSim = terminateSimService.saveTerminateSim(terminationDetails);
				for (NumberRange item : npot.getNumberRange()) {
					TerminateSimTransactionDetails terminationTransaction = new TerminateSimTransactionDetails();
					terminationTransaction.setRequestId(requestId);
					terminationTransaction.setStatus(current_status);
					terminationTransaction.setRequestType(terminate_out);
					terminationTransaction.setMsisdn(item.getNumberFrom());
					terminateTransactionService.saveTerminateSimTransactionDetails(terminationTransaction);
					TerminateSimMT terminateSIMMt = new TerminateSimMT();
					terminateSIMMt.setTerminateId(terminateSim.getTerminateId());
					terminateSIMMt.setRequest_type(terminate_out);
					terminateSIMMt.setStatus(current_status);
					terminateSIMMt.setSubscriberNumber(item.getNumberFrom());
					terminateSIMMt.setResultCode(resultCode);
					terminateSimMTService.saveTerminateSimMT(terminateSIMMt);
				}
				// going to convert into xml format
				current_status = 2;
				for (NumberRange item : npot.getNumberRange()) {
					TerminateSimTransactionDetails terminationTransaction = new TerminateSimTransactionDetails();
					terminationTransaction.setRequestId(requestId);
					terminationTransaction.setStatus(current_status);
					terminationTransaction.setRequestType(terminate_out);
					terminationTransaction.setMsisdn(item.getNumberFrom());
					terminateTransactionService.saveTerminateSimTransactionDetails(terminationTransaction);
					terminateSimMTService.updateTerminateSIMMT(current_status, item.getNumberFrom(), terminate_out,
							resultCode);
				}
				_logger.info("trying to convert NPOT request into xml with requestId : " + requestId);
				xml = null;// new NPOUtils().convertNPOTSoapIntoXML(npot);
				_logger.info("convert portme termination request into xml with requestId:" + requestId + xml);
				sessionId = Long.toString(System.currentTimeMillis());
				int interval = jmsProducer.sentIntoInternalInQ(xml, sessionId);
				if (interval == 1) {
					_logger.info("Sent NPOT xml request in jms queue with requestId-" + requestId);
					current_status = 3;
					for (NumberRange item : npot.getNumberRange()) {
						TerminateSimTransactionDetails terminationTransaction = new TerminateSimTransactionDetails();
						terminationTransaction.setRequestId(requestId);
						terminationTransaction.setStatus(current_status);
						terminationTransaction.setRequestType(terminate_out);
						terminationTransaction.setMsisdn(item.getNumberFrom());
						terminateTransactionService.saveTerminateSimTransactionDetails(terminationTransaction);
						terminateSimMTService.updateTerminateSIMMT(current_status, item.getNumberFrom(), terminate_out,
								resultCode);
					}
					_logger.info("processed NPOT with requestId-" + requestId);

				} else {
					_logger.info("Unsent Termination MSISDN request in jms queue with requestId-" + requestId);
				}
				if (current_status != 0) {
					terminateSimService.updateTerminateSIM(terminateSim.getTerminateId(), current_status);
				}
			}
			/* end code for NPOT */
			else if (jsonObj.getString("messageType")
					.equals(ReadConfigFile.getProperties().getProperty("INIT_ACK_MESSAGE_TYPE"))) {
				InitAck initAck = objectMapper.readValue(msg.getText(), InitAck.class);
				_logger.info("got INIT_ACK data from outqueue : " + initAck);
				requestId = initAck.getRequestId();
				String portme_in = ReadConfigFile.getProperties().getProperty("PORTME_IN");
				current_status = 4;
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
				current_status = 5;
				for (String msisdn : listOfMsisdn) {
					portMeTransaction = new PortMeTransactionDetails();
					portMeTransaction.setRequestId(requestId);
					portMeTransaction.setStatus(current_status);
					portMeTransaction.setRequestType(portme_in);
					portMeTransaction.setMsisdn(msisdn);
					portMeTransactionService.savePortMeTransactionDetails(portMeTransaction);
					subscriberArrTypeService.updatePortMtStatusByMsisdn(current_status, msisdn, portme_in, 200);
				}
				_logger.info("converting INIT_ACK into xml config file");
				xml = new NPOUtils().convertInitAckIntoXML(initAck);
				sessionId = Long.toString(System.currentTimeMillis());
				int interval = jmsProducer.sentIntoInternalInQ(xml, sessionId);
				if (interval == 1) {
					current_status = 6;
					for (String msisdn : listOfMsisdn) {
						portMeTransaction = new PortMeTransactionDetails();
						portMeTransaction.setRequestId(requestId);
						portMeTransaction.setStatus(current_status);
						portMeTransaction.setRequestType(portme_in);
						portMeTransaction.setMsisdn(msisdn);
						portMeTransactionService.savePortMeTransactionDetails(portMeTransaction);
						subscriberArrTypeService.updatePortMtStatusByMsisdn(current_status, msisdn, portme_in, 200);
					}
					_logger.info("Send INIT_ACK xml into internalIn jms queue : " + xml);
				}
				if (current_status != 0) {
					portMeService.updatePortMeStatus(current_status, requestId);
				}
			} else if (jsonObj.getString("messageType")
					.equals(ReadConfigFile.getProperties().getProperty("DISCONNECT_ACK_MESSAGE_TYPE"))) {
				InitAck initAck = objectMapper.readValue(msg.getText(), InitAck.class);
				_logger.info("got DISCONNECT_ACK_MESSAGE data from outqueue : " + initAck);
				requestId = initAck.getRequestId();
				String portme_out = ReadConfigFile.getProperties().getProperty("PORTME_OUT");
				current_status = 16;
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
				current_status = 17;
				for (String msisdn : listOfMsisdn) {
					portMeTransaction = new PortMeTransactionDetails();
					portMeTransaction.setRequestId(requestId);
					portMeTransaction.setStatus(current_status);
					portMeTransaction.setRequestType(portme_out);
					portMeTransaction.setMsisdn(msisdn);
					portMeTransactionService.savePortMeTransactionDetails(portMeTransaction);
					subscriberArrTypeService.updatePortMtStatusByMsisdn(current_status, msisdn, portme_out, 200);
				}
				xml = new NPOUtils().convertInitAckIntoXML(initAck);
				sessionId = Long.toString(System.currentTimeMillis());
				int interval = jmsProducer.sentIntoInternalInQ(xml, sessionId);
				if (interval == 1) {
					current_status = 18;
					for (String msisdn : listOfMsisdn) {
						portMeTransaction = new PortMeTransactionDetails();
						portMeTransaction.setRequestId(requestId);
						portMeTransaction.setStatus(current_status);
						portMeTransaction.setRequestType(portme_out);
						portMeTransaction.setMsisdn(msisdn);
						portMeTransactionService.savePortMeTransactionDetails(portMeTransaction);
						subscriberArrTypeService.updatePortMtStatusByMsisdn(current_status, msisdn, portme_out, 200);
					}
					_logger.info("Send DISCONNECT_ACK xml into internalIn jms queue : " + xml);
				}
				if (current_status != 0) {
					portMeService.updatePortMeStatus(current_status, requestId);
				}
			} else if (jsonObj.getString("messageType")
					.equals(ReadConfigFile.getProperties().getProperty("CON_ACK_MESSAGE_TYPE"))) {
				InitAck initAck = objectMapper.readValue(msg.getText(), InitAck.class);
				requestId = initAck.getRequestId();
				String portme_in = ReadConfigFile.getProperties().getProperty("PORTME_IN");
				current_status = 16;
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
				current_status = 17;
				for (String msisdn : listOfMsisdn) {
					portMeTransaction = new PortMeTransactionDetails();
					portMeTransaction.setRequestId(requestId);
					portMeTransaction.setStatus(current_status);
					portMeTransaction.setRequestType(portme_in);
					portMeTransaction.setMsisdn(msisdn);
					portMeTransactionService.savePortMeTransactionDetails(portMeTransaction);
					subscriberArrTypeService.updatePortMtStatusByMsisdn(current_status, msisdn, portme_in, 200);
				}
				xml = new NPOUtils().convertInitAckIntoXML(initAck);
				sessionId = Long.toString(System.currentTimeMillis());
				int interval = jmsProducer.sentIntoInternalInQ(xml, sessionId);
				if (interval == 1) {
					current_status = 18;
					for (String msisdn : listOfMsisdn) {
						portMeTransaction = new PortMeTransactionDetails();
						portMeTransaction.setRequestId(requestId);
						portMeTransaction.setStatus(current_status);
						portMeTransaction.setRequestType(portme_in);
						portMeTransaction.setMsisdn(msisdn);
						portMeTransactionService.savePortMeTransactionDetails(portMeTransaction);
						subscriberArrTypeService.updatePortMtStatusByMsisdn(current_status, msisdn, portme_in, 200);
					}
					_logger.info("Send CON_ACK xml into internalIn jms queue : " + xml);
				}
				if (current_status != 0) {
					portMeService.updatePortMeStatus(current_status, requestId);
				}
			}
			// getting order cancellation ack
			else if (jsonObj.getString("messageType")
					.equals(ReadConfigFile.getProperties().getProperty("CONCEL_ACK_MESSAGE_TYPE"))) {

				InitAck cancelAck = objectMapper.readValue(msg.getText(), InitAck.class);
				_logger.info("got ORDER CAN_ACK data from outqueue : " + cancelAck);
				requestId = cancelAck.getRequestId();
				String portme_in = ReadConfigFile.getProperties().getProperty("CANCEL_IN");
				current_status = 4;
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
				current_status = 5;
				for (String msisdn : listOfMsisdn) {
					portMeTransaction = new PortMeTransactionDetails();
					portMeTransaction.setRequestId(requestId);
					portMeTransaction.setStatus(current_status);
					portMeTransaction.setRequestType(portme_in);
					portMeTransaction.setMsisdn(msisdn);
					portMeTransactionService.savePortMeTransactionDetails(portMeTransaction);
					subscriberArrTypeService.updatePortMtStatusByMsisdn(current_status, msisdn, portme_in, 200);
				}
				_logger.info("converting order cancellation ack into xml config file");
				xml = new NPOUtils().convertInitAckIntoXML(cancelAck);
				sessionId = Long.toString(System.currentTimeMillis());
				int interval = jmsProducer.sentIntoInternalInQ(xml, sessionId);
				if (interval == 1) {
					current_status = 6;
					for (String msisdn : listOfMsisdn) {
						portMeTransaction = new PortMeTransactionDetails();
						portMeTransaction.setRequestId(requestId);
						portMeTransaction.setStatus(current_status);
						portMeTransaction.setRequestType(portme_in);
						portMeTransaction.setMsisdn(msisdn);
						portMeTransactionService.savePortMeTransactionDetails(portMeTransaction);
						subscriberArrTypeService.updatePortMtStatusByMsisdn(current_status, msisdn, portme_in, 200);
					}
					_logger.info("Send order cancellation ack xml into internalIn jms queue : " + xml);
				}
				if (current_status != 0) {
					portMeService.updatePortMeStatus(current_status, requestId);
				}

			}

			// getting order cancellation soap request from external system
			else if (npcData.getMessageHeader().getMessageID() == 3002) {
				_logger.info("[sessionId=" + sessionId
						+ "]: PortMez2Consumer.receiveMessage()-PORT CANCEL Soap Message - process start with timestamp:["
						+ new Timestamp(System.currentTimeMillis()) + "]");
				PortCancelNotification orderCancel = objectMapper.readValue(msg.getText(),
						PortCancelNotification.class);
				_logger.info("got ORDER cancel reqeust from external system : " + orderCancel);
				requestId = PortMeUtils.randomUniqueUUID();
				String curReqType = ReadConfigFile.getProperties().getProperty("CANCEL_OUT");
				String preReqType = ReadConfigFile.getProperties().getProperty("CANCEL_IN");
				current_status = 1;
				for (NumberRange ranges : orderCancel.getNumberRange()) {
					String msisdn = ranges.getNumberFrom();
					portMeTransaction = new PortMeTransactionDetails();
					portMeTransaction.setRequestId(requestId);
					portMeTransaction.setStatus(current_status);
					portMeTransaction.setRequestType(curReqType);
					portMeTransaction.setMsisdn(msisdn);
					portMeTransactionService.savePortMeTransactionDetails(portMeTransaction);
					portMeService.cancelOrderByMsisdn(current_status, msisdn, preReqType, curReqType);
					_logger.info("[sessionId=" + sessionId
							+ "]: PortMeConsumer.receiveMessage()-PORT CANCEL Soap Message - process end with timestamp:["
							+ new Timestamp(System.currentTimeMillis()) + "]");
				}
			}
			// end order cancellation soap request from external system

			// getting order NPOS(Suspension Recipient Request) soap request from external
			// system
			else if (npcData.getMessageHeader().getMessageID() == 7002) {
				_logger.info("got NPOS (Suspension Recipient Request) from external system");
				NonpaymentDisconnReq npos = objectMapper.readValue(msg.getText(), NonpaymentDisconnReq.class);
				String susRecipientIn = ReadConfigFile.getProperties().getProperty("SUS_RECIPIENT_IN");
				for (NumberRange ranges : npos.getNumberRange()) {
					BillingResolution billingResolution = new BillingResolution();
					billingResolution.setTransactionId(npcData.getMessageHeader().getTransactionID());
					billingResolution.setMsisdn(ranges.getNumberFrom());
					billingResolution.setBill_date(npos.getBillDate());
					billingResolution.setDue_date(npos.getBillDueDate());
					billingResolution.setAmount(npos.getBillAmount());
					billingResolution.setComments(npos.getComments());
					billingResolution.setRequest_type(susRecipientIn);
					billingResolution.setStatus(1);
					billingResolution.setCreated_date(timestamp);
					billingResolution.setUpdated_date(timestamp);
					billingResolutionService.saveBillingResolution(billingResolution);
				}
			}
			// END order NPOS soap request from external system
			else if (npcData.getMessageHeader().getMessageID() == 7004) {
				NonpaymentDisconnResp nposa = objectMapper.readValue(msg.getText(), NonpaymentDisconnResp.class);
				_logger.info("got nposa from external system");
				for (NumberRange ranges : nposa.getNumberRange()) {
					String susRecipientIn = ReadConfigFile.getProperties().getProperty("SUS_DONOR_IN");
					BillingResolution billingResolution = new BillingResolution();
					billingResolution.setTransactionId(npcData.getMessageHeader().getTransactionID());
					billingResolution.setRequest_type(susRecipientIn);
					billingResolution.setStatus(2);
					billingResolution.setReason(nposa.getReasonCode());
					billingResolutionDao.updateNPOSA(billingResolution, sessionId);
				}
			} else if (npcData.getMessageHeader().getMessageID() == 7008) {
				NpdCancelRequest nposcancel = objectMapper.readValue(msg.getText(), NpdCancelRequest.class);
				_logger.info("got NPOS (Recipient Suspension Cancel reqeust) from external system");
				for (NumberRange ranges : nposcancel.getNumberRange()) {
					String susRecipientIn = ReadConfigFile.getProperties().getProperty("SUS_RECIPIENT_IN");
					BillingResolution billingResolution = new BillingResolution();
					billingResolution.setTransactionId(npcData.getMessageHeader().getTransactionID());
					billingResolution.setRequest_type(susRecipientIn);
					billingResolution.setStatus(2);
					billingResolution.setReason(ranges.getReasonCode());
					billingResolutionDao.updateNPOSPR(billingResolution, sessionId);
				}
			} else if (npcData.getMessageHeader().getMessageID() == 7006) {
				NpdAckResponse nposa = objectMapper.readValue(msg.getText(), NpdAckResponse.class);
				_logger.info("got NPOSPR (Bill paid ack) from external system");
				for (NumberRange ranges : nposa.getNumberRange()) {
					String susRecipientIn = ReadConfigFile.getProperties().getProperty("SUS_RECIPIENT_IN");
					BillingResolution billingResolution = new BillingResolution();
					billingResolution.setTransactionId(npcData.getMessageHeader().getTransactionID());
					billingResolution.setRequest_type(susRecipientIn);
					billingResolution.setStatus(2);
					billingResolution.setReason(ranges.getReasonCode());
					billingResolutionDao.updateNPOSAACK(billingResolution, sessionId);
				}
			}
			// END order NPOSPR soap request from external system
		} catch (JsonProcessingException | JMSException | JSONException e) {
			_logger.error("Received Message with error : " + e.getMessage() + "RequestId - " + requestId, e);
		}
		if (current_status != 0) {
			// going to update current status in port_tx table
		}
	}

	//@JmsListener(destination = "mchZ2ActivationQueue") // SC and SD data getting from activation queue
	public void receiveSCSDMessage(Message message) {
		int current_status = 0;
		String requestId = null;
		try {
			PortMeTransactionDetails portMeTransaction = new PortMeTransactionDetails();
			TextMessage msg = (TextMessage) message;
			_logger.info("Received Message: " + msg.getText());
			ObjectMapper objectMapper = new ObjectMapper();
			objectMapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);
			String xml = null;
			NPCData npcData = objectMapper.readValue(msg.getText(), NPCData.class);
			Gson gson = new Gson();
			if (npcData.getMessageHeader().getMessageID() == 1006) {
				PortExecute sdType = gson.fromJson(msg.getText(), PortExecute.class);
				_logger.info("Received Message SD xml from jms ActivationQueue : " + xml);
				current_status = 10;
				String portme_out = ReadConfigFile.getProperties().getProperty("PORTME_OUT");

				for (NumberRange sdInfo : sdType.getNumberRange()) {
					requestId = npcData.getMessageHeader().getTransactionID();
					portMeTransaction = new PortMeTransactionDetails();
					portMeTransaction.setRequestId(requestId);
					portMeTransaction.setStatus(current_status);
					portMeTransaction.setRequestType(portme_out);
					portMeTransaction.setMsisdn(sdInfo.getNumberFrom());
					portMeTransactionService.savePortMeTransactionDetails(portMeTransaction);
					subscriberArrTypeService.updatePortMtStatusByMsisdn(current_status, sdInfo.getNumberFrom(),
							portme_out, 200);
				}

				_logger.info("start processing SD message from jms ActivationQueue with reqeustId-" + requestId);
				// going to convert into xml format
				current_status = 11;
				_logger.info("converting SD type into xml config file");
				for (NumberRange sdInfo : sdType.getNumberRange()) {
					portMeTransaction = new PortMeTransactionDetails();
					portMeTransaction.setRequestId(requestId);
					portMeTransaction.setStatus(current_status);
					portMeTransaction.setRequestType(portme_out);
					portMeTransaction.setMsisdn(sdInfo.getNumberFrom());
					portMeTransactionService.savePortMeTransactionDetails(portMeTransaction);
					subscriberArrTypeService.updatePortMtStatusByMsisdn(current_status, sdInfo.getNumberFrom(),
							portme_out, 200);
				}
				_logger.info("trying to convert SD message into xml with reqeustId-" + requestId);
				xml = "SD info zone 2";// new NPOUtils().convertSDTypeIntoXML(sdType);
				String sessionId = Long.toString(System.currentTimeMillis());
				int interval = jmsProducer.sentIntoInternalInQ(xml, sessionId);
				if (interval == 1) {
					current_status = 12;
					for (NumberRange sdInfo : sdType.getNumberRange()) {
						portMeTransaction = new PortMeTransactionDetails();
						portMeTransaction.setRequestId(requestId);
						portMeTransaction.setStatus(current_status);
						portMeTransaction.setRequestType(portme_out);
						portMeTransaction.setMsisdn(sdInfo.getNumberFrom());
						portMeTransactionService.savePortMeTransactionDetails(portMeTransaction);
						subscriberArrTypeService.updatePortMtStatusByMsisdn(current_status, sdInfo.getNumberFrom(),
								portme_out, 200);
					}
					_logger.info("Processed SD Soap message with requestId-" + requestId);
				}
				if (current_status != 0) {
					portMeService.updatePortMeStatus(current_status, requestId);
				}
			} else if (npcData.getMessageHeader().getMessageID() == 1008) {
				PortDeactWithRte scType = gson.fromJson(msg.getText(), PortDeactWithRte.class);
				_logger.info("Received Message SC xml : " + xml);
				current_status = 10;
				String portme_in = ReadConfigFile.getProperties().getProperty("PORTME_IN");
				for (NumberRange scInfo : scType.getNumberRange()) {
					requestId = npcData.getMessageHeader().getTransactionID();
					portMeTransaction = new PortMeTransactionDetails();
					portMeTransaction.setRequestId(requestId);
					portMeTransaction.setStatus(current_status);
					portMeTransaction.setRequestType(portme_in);
					portMeTransaction.setMsisdn(scInfo.getNumberFrom());
					portMeTransactionService.savePortMeTransactionDetails(portMeTransaction);
					subscriberArrTypeService.updatePortMtStatusByMsisdn(current_status, scInfo.getNumberFrom(),
							portme_in, 200);
				}
				_logger.info("start processing of SC Soap message with requestId-" + requestId);
				// going to convert into xml format
				current_status = 11;
				_logger.info("converting SC Type MESSAGE into xml config file");
				for (NumberRange scInfo : scType.getNumberRange()) {
					portMeTransaction = new PortMeTransactionDetails();
					portMeTransaction.setRequestId(requestId);
					portMeTransaction.setStatus(current_status);
					portMeTransaction.setRequestType(portme_in);
					portMeTransaction.setMsisdn(scInfo.getNumberFrom());
					portMeTransactionService.savePortMeTransactionDetails(portMeTransaction);
					subscriberArrTypeService.updatePortMtStatusByMsisdn(current_status, scInfo.getNumberFrom(),
							portme_in, 200);
				}
				_logger.info("trying to convert SC Soap message into xml with requestId-" + requestId);
				xml = "SC info zone2";// new NPOUtils().convertSCTypeIntoXML(scType);
				String sessionId = Long.toString(System.currentTimeMillis());
				int interval = jmsProducer.sentIntoInternalInQ(xml, sessionId);
				if (interval == 1) {
					current_status = 12;
					for (NumberRange scInfo : scType.getNumberRange()) {
						portMeTransaction = new PortMeTransactionDetails();
						portMeTransaction.setRequestId(requestId);
						portMeTransaction.setStatus(current_status);
						portMeTransaction.setRequestType(portme_in);
						portMeTransaction.setMsisdn(scInfo.getNumberFrom());
						portMeTransactionService.savePortMeTransactionDetails(portMeTransaction);
						subscriberArrTypeService.updatePortMtStatusByMsisdn(current_status, scInfo.getNumberFrom(),
								portme_in, 200);
					}
					_logger.info("Processed SC Soap Message with requestId-" + requestId);
				}
				if (current_status != 0) {
					portMeService.updatePortMeStatus(current_status, requestId);
				}
			}
		} catch (

		JMSException e) {
			handleException("Received Message with JMS error:", e, requestId);
		} catch (JSONException e) {
			handleException("Received Message with JSON error:", e, requestId);
		} catch (JsonMappingException e) {
			handleException("Received Message with JSON error:", e, requestId);
		} catch (JsonProcessingException e) {
			handleException("Received Message with JSON error:", e, requestId);
		}
	}

	private void handleException(String message, Exception e, String requestId) {
		_logger.error(message + " " + e.getMessage() + " RequestId - " + requestId, e);
	}
}
