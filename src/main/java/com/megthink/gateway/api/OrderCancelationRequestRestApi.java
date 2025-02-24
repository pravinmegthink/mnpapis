package com.megthink.gateway.api;

import com.megthink.gateway.api.response.PortMeAPIResponse;
import com.megthink.gateway.model.MSISDNUIDType;
import com.megthink.gateway.model.OrderCancellation;
import com.megthink.gateway.model.PortMeTransactionDetails;
import com.megthink.gateway.model.User;
import com.megthink.gateway.producer.JmsProducer;
import com.megthink.gateway.service.PortMeService;
import com.megthink.gateway.service.PortMeTransactionDetailsService;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OrderCancelationRequestRestApi {

	private static final Logger _logger = LoggerFactory.getLogger(OrderCancelationRequestRestApi.class);

	Timestamp timestamp = new Timestamp(System.currentTimeMillis());

	@Autowired
	private UserService userService;
	@Autowired
	private PortMeTransactionDetailsService portMeTransactionService;
	@Autowired
	private JmsProducer jmsProducer;
	@Autowired
	private PortMeService portMeService;

	@PostMapping("/api/ordercancel")
	public ResponseEntity<?> orderCancel(@RequestBody OrderCancellation orderCancel) {
		_logger.info("Getting order cancellation data with request.");
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = userService.findUserByUsername(auth.getName());
		PortMeAPIResponse response = new PortMeAPIResponse();
		int current_status = 0;
		String requestId = null;
		String curReqType = ReadConfigFile.getProperties().getProperty("CANCEL_IN");
		String portme_in = ReadConfigFile.getProperties().getProperty("PORTME_IN");
		String msg;

		try {
			if (orderCancel == null || orderCancel.getMsisdnUID() == null || orderCancel.getMsisdnUID().isEmpty()) {
				return createBadRequestResponse("Something is wrong with the JSON data.");
			}

			requestId = PortMeUtils.randomUniqueUUID();
			_logger.info("Initiating order cancellation with requestId: " + requestId);
			current_status = 20;

			for (MSISDNUIDType msisdn : orderCancel.getMsisdnUID()) {
				savePortMeTransactionAndCancelOrder(msisdn.getMsisdn(), requestId, current_status, curReqType,
						portme_in);
			}

			_logger.info("Converting order cancellation into XML with requestId: " + requestId);
			String xml = new NPOUtils().convertJsonIntoOrderCancellation(orderCancel);
			_logger.info("Converted order cancellation into XML with requestId-" + requestId + "-" + xml);

			if (xml != null) {
				current_status = 21;

				for (MSISDNUIDType msisdn : orderCancel.getMsisdnUID()) {
					savePortMeTransactionAndCancelOrder(msisdn.getMsisdn(), requestId, current_status, portme_in,
							curReqType);
				}

				String sessionId = Long.toString(System.currentTimeMillis());
				int returnValue = jmsProducer.sendMessageToInQueue(xml, sessionId, 1);

				if (returnValue == 1) {
					_logger.info("Sent order cancellation request in JMS queue with requestId: " + requestId);
					current_status = 22;

					for (MSISDNUIDType msisdn : orderCancel.getMsisdnUID()) {
						savePortMeTransactionAndCancelOrder(msisdn.getMsisdn(), requestId, current_status, portme_in,
								curReqType);
					}

					_logger.info("Successfully received order cancellation request with requestId: " + requestId);
					return createSuccessResponse("Order cancellation request sent successfully.");
				} else {
					_logger.info("Successfully saved order cancellation data but not able to send to the queue: "
							+ requestId);
					return createSuccessResponse("Order cancellation data saved but could not be sent to the queue.");
				}
			}
		} catch (Exception e) {
			_logger.error("Something is wrong with the system with requestId: " + requestId + "-" + e.getMessage(), e);
			return createErrorResponse("Internal server error occurred.");
		}

		if (current_status != 0) {
			// Update current status in port_tx table
			portMeService.updatePortMeStatus(current_status, requestId, user.getUserId());
		}

		return createSuccessResponse("Order cancellation request processed.");
	}

	private void savePortMeTransactionAndCancelOrder(String msisdn, String requestId, int status, String reqType,
			String portmeType) {
		PortMeTransactionDetails portMeTransaction = new PortMeTransactionDetails();
		portMeTransaction.setRequestId(requestId);
		portMeTransaction.setStatus(status);
		portMeTransaction.setRequestType(reqType);
		portMeTransaction.setMsisdn(msisdn);
		portMeTransactionService.savePortMeTransactionDetails(portMeTransaction);
		portMeService.cancelOrderByMsisdn(status, msisdn, portmeType, reqType);
	}

	private ResponseEntity<?> createBadRequestResponse(String errorMessage) {
		PortMeAPIResponse errorBean = new PortMeAPIResponse();
		errorBean.setResponseCode(101);
		errorBean.setResponseMessage(errorMessage);
		String msg = PortMeUtils.generateJsonResponse(errorBean, "ERROR");
		return new ResponseEntity<>(msg, new HttpHeaders(), HttpStatus.BAD_REQUEST);
	}

	private ResponseEntity<?> createSuccessResponse(String successMessage) {
		PortMeAPIResponse successBean = new PortMeAPIResponse();
		successBean.setResponseCode(APIConst.successCode);
		successBean.setResponseMessage(successMessage);
		String msg = PortMeUtils.generateJsonResponse(successBean, "SUCCESS");
		return new ResponseEntity<>(msg, new HttpHeaders(), HttpStatus.OK);
	}

	private ResponseEntity<?> createErrorResponse(String errorMessage) {
		PortMeAPIResponse errorBean = new PortMeAPIResponse();
		errorBean.setResponseCode(APIConst.successCode1);
		errorBean.setResponseMessage(errorMessage);
		String msg = PortMeUtils.generateJsonResponse(errorBean, "ERROR");
		return new ResponseEntity<>(msg, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
	}

}