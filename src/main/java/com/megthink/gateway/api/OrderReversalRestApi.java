package com.megthink.gateway.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.megthink.gateway.api.response.PortMeAPIResponse;
import com.megthink.gateway.dao.NumberPlanDao;
import com.megthink.gateway.model.MSISDNUIDType;
import com.megthink.gateway.model.OrderReversal;
import com.megthink.gateway.model.PortMe;
import com.megthink.gateway.model.PortMeTransactionDetails;
import com.megthink.gateway.model.User;
import com.megthink.gateway.producer.JmsProducer;
import com.megthink.gateway.service.PortMeService;
import com.megthink.gateway.service.PortMeTransactionDetailsService;
import com.megthink.gateway.service.SubscriberArrTypeService;
import com.megthink.gateway.service.UserService;
import com.megthink.gateway.utils.APIConst;
import com.megthink.gateway.utils.NPOUtils;
import com.megthink.gateway.utils.PortMeUtils;
import com.megthink.gateway.utils.ReadConfigFile;

import java.sql.Timestamp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OrderReversalRestApi {

	private static final Logger _logger = LoggerFactory.getLogger(OrderReversalRestApi.class);

	Timestamp timestamp = new Timestamp(System.currentTimeMillis());

	@Autowired
	private UserService userService;
	@Autowired
	private ObjectMapper objectMapper;
	@Autowired
	private PortMeService portMeService;
	@Autowired
	private PortMeTransactionDetailsService portMeTransactionService;
	@Autowired
	private JmsProducer jmsProducer;
	@Autowired
	private SubscriberArrTypeService subscriberArrTypeService;
	@Autowired
	private NumberPlanDao numberPlanDao;

	@PostMapping("/api/orderreversal")
	public ResponseEntity<?> doOrderReversal(@RequestParam("orderReversal") String reversal) {
		_logger.info("getting order reversal data with reque");
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = userService.findUserByUsername(auth.getName());
		ResponseEntity response = null;
		int current_status = 0;
		String requestId = null;
		PortMeAPIResponse errorBean = new PortMeAPIResponse();
		PortMeTransactionDetails portMeTransaction = new PortMeTransactionDetails();
		String msg = null;
		String reqType = ReadConfigFile.getProperties().getProperty("REVERSAL_IN");
		int mch_type = 0;
		try {
			OrderReversal orderReversal = objectMapper.readValue(reversal, OrderReversal.class);
			if (orderReversal == null) {
				errorBean.setResponseCode(APIConst.successCode1);
				errorBean.setResponseMessage(APIConst.successMsg1);
				msg = PortMeUtils.generateJsonResponse(errorBean, "ERROR");
				response = new ResponseEntity(msg, new HttpHeaders(), HttpStatus.BAD_REQUEST);
			} else {
				requestId = PortMeUtils.randomUniqueUUID();
				_logger.info("Initiate order reversal with requestId : " + requestId);
				if (orderReversal.getMsisdnUID().size() == 0) {

				} else {
					int isExist = portMeService.isExistReqeust(orderReversal.getMsisdnUID().get(0).getMsisdn(),
							reqType);
					if (isExist == 0) {
						current_status = 1;
						PortMe portReversal = portMeService.getReversalDetails(orderReversal.getId(),
								orderReversal.getMsisdnUID().get(0).getMsisdn()).get(0);
						portReversal.setRequest_type(reqType);
						portReversal.setRequestId(requestId);
						portReversal.setStatus(current_status);
						portReversal.setUserId(user.getUserId());
						PortMe portMe = portMeService.savePortMe(portReversal);
						orderReversal.setRequestId(requestId);
						orderReversal.setSource(portReversal.getSource());
						if (orderReversal.getMsisdnUID().size() > 0) {
							for (MSISDNUIDType msisdn : orderReversal.getMsisdnUID()) {
								portMeTransaction = new PortMeTransactionDetails();
								portMeTransaction.setRequestId(orderReversal.getRequestId());
								portMeTransaction.setStatus(current_status);
								portMeTransaction.setRequestType(reqType);
								portMeTransaction.setMsisdn(msisdn.getMsisdn());
								portMeTransactionService.savePortMeTransactionDetails(portMeTransaction);
								subscriberArrTypeService.insertWithQuery(portMe.getPortId(), msisdn.getMsisdn(),
										reqType, current_status);
							}
							/* Trying to convert into xml */
							String area = numberPlanDao.getArea(orderReversal.getMsisdnUID().get(0).getMsisdn());
							mch_type = numberPlanDao.getMCHTypeByArea(area);
							_logger.info("going to convert order reversal into xml with requestId:" + requestId);
							String xml = null;
							if (mch_type == 1) {
								xml = new NPOUtils().convertJsonIntoOrderReversal(orderReversal, mch_type, null, null);
							} else {
								String donorLSAID = numberPlanDao
										.getDonorLSAID(orderReversal.getMsisdnUID().get(0).getMsisdn());
								if (donorLSAID == null) {
									donorLSAID = area;
								}
								String messageSenderTelco = ReadConfigFile.getProperties()
										.getProperty("MessageSenderTelco-ZOOM");
								String transactionId = portMeService.getTransactionId(messageSenderTelco, area);

								// String routeInfo = numberPlanDao.getRouteInfo(requestId);

								xml = new NPOUtils().convertJsonIntoOrderReversal(orderReversal, mch_type,
										transactionId, donorLSAID);
							}
							_logger.info("converted order reversal into xml with requestId-" + requestId + "-" + xml);
							if (xml != null) {
								current_status = 2;
								for (MSISDNUIDType msisdn : orderReversal.getMsisdnUID()) {
									portMeTransaction = new PortMeTransactionDetails();
									portMeTransaction.setRequestId(orderReversal.getRequestId());
									portMeTransaction.setStatus(current_status);
									portMeTransaction.setRequestType(reqType);
									portMeTransaction.setMsisdn(msisdn.getMsisdn());
									portMeTransactionService.savePortMeTransactionDetails(portMeTransaction);
									subscriberArrTypeService.updatePortMtStatusByMsisdn(current_status,
											msisdn.getMsisdn(), reqType, 200);
								}
							}
							if (current_status == 2) {
								String sessionId = Long.toString(System.currentTimeMillis());
								int returnvalue = jmsProducer.sendMessageToInQueue(xml, sessionId, 1);
								if (returnvalue == 1) {
									_logger.info(
											"sent order reversal reqeust in jms queue with requestId-" + requestId);
									current_status = 3;
									for (MSISDNUIDType msisdn : orderReversal.getMsisdnUID()) {
										portMeTransaction = new PortMeTransactionDetails();
										portMeTransaction.setRequestId(orderReversal.getRequestId());
										portMeTransaction.setStatus(current_status);
										portMeTransaction.setRequestType(reqType);
										portMeTransaction.setMsisdn(msisdn.getMsisdn());
										portMeTransactionService.savePortMeTransactionDetails(portMeTransaction);
										subscriberArrTypeService.updatePortMtStatusByMsisdn(current_status,
												msisdn.getMsisdn(), reqType, 200);
									}
									_logger.info(
											"Successfully Received order reversal request with requestId-" + requestId);
									errorBean.setResponseCode(APIConst.successCode);
									errorBean.setResponseMessage(APIConst.successMsg);
									msg = PortMeUtils.generateJsonResponse(errorBean, "SUCCESS");
									response = new ResponseEntity(msg, new HttpHeaders(), HttpStatus.OK);
								} else {
									_logger.info("Successfully saved order reversal data but not able to send queue-"
											+ requestId);
									errorBean.setResponseCode(APIConst.successCode2);
									errorBean.setResponseMessage(APIConst.successMsg2);
									msg = PortMeUtils.generateJsonResponse(errorBean, "SUCCESS");
									response = new ResponseEntity(msg, new HttpHeaders(), HttpStatus.OK);
								}
							}
						}
					} else {
						_logger.info("This data is already exist" + requestId);
						errorBean.setResponseCode(APIConst.successCode104);
						errorBean.setResponseMessage(APIConst.successMsg4);
						msg = PortMeUtils.generateJsonResponse(errorBean, "SUCCESS");
						response = new ResponseEntity(msg, new HttpHeaders(), HttpStatus.OK);
					}
				}
			}
		} catch (Exception e) {
			_logger.error("Something is wrong with the system with requestId : " + requestId + "-" + e.getMessage(), e);
			return createErrorResponse("Internal server error occurred.");
		}
		if (current_status != 0) {
			portMeService.updatePortMeStatus(current_status, requestId, user.getUserId());
		}
		return response;
	}

	private ResponseEntity<?> createErrorResponse(String errorMessage) {
		PortMeAPIResponse errorBean = new PortMeAPIResponse();
		errorBean.setResponseCode(APIConst.successCode1);
		errorBean.setResponseMessage(errorMessage);
		String msg = PortMeUtils.generateJsonResponse(errorBean, "ERROR");
		return new ResponseEntity<>(msg, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
	}
}