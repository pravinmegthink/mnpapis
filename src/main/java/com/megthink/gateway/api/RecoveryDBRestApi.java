package com.megthink.gateway.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.megthink.gateway.api.response.PortMeAPIResponse;
import com.megthink.gateway.form.RecoveryDBForm;
import com.megthink.gateway.model.RecoveryDB;
import com.megthink.gateway.model.User;
import com.megthink.gateway.model.WebAccessTrace;
import com.megthink.gateway.producer.JmsProducer;
import com.megthink.gateway.service.RecoveryDBService;
import com.megthink.gateway.service.UserService;
import com.megthink.gateway.service.WebAccessTraceService;
import com.megthink.gateway.utils.APIConst;
import com.megthink.gateway.utils.NPOUtils;
import com.megthink.gateway.utils.PortMeUtils;
import com.megthink.gateway.utils.ReadConfigFile;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

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
public class RecoveryDBRestApi {

	private static final Logger _logger = LoggerFactory.getLogger(RecoveryDBRestApi.class);

	Timestamp timestamp = new Timestamp(System.currentTimeMillis());

	@Autowired
	private UserService userService;
	@Autowired
	private ObjectMapper objectMapper;
	@Autowired
	private JmsProducer jmsProducer;
	@Autowired
	private RecoveryDBService recoveryDBService;
	@Autowired
	private WebAccessTraceService webAccessTraceService;

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@PostMapping("/api/recoverydb")
	public ResponseEntity<?> processRecoveryRequest(@RequestParam("recovery") String recovery) {
		String sessionId = Long.toString(System.currentTimeMillis());
		_logger.info("[sessionId=" + sessionId
				+ "]: RecoveryDBRestApi.processRecoveryRequest() - process start with timestamp:["
				+ new Timestamp(System.currentTimeMillis()) + "]");
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = userService.findUserByUsername(auth.getName());
		WebAccessTrace logTrace = new WebAccessTrace();
		logTrace.setAction("/api/recoverydb");
		logTrace.setDesc("initiate requet to create recoverydb reqeust");
		logTrace.setUser_id(user.getUserId());
		saveWebAccessTrace(logTrace);
		_logger.info("[sessionId=" + sessionId
				+ "]: RecoveryDBRestApi.processRecoveryRequest() - start process by logedIn : [Username - "
				+ user.getUsername() + "] with timestamp:[" + new Timestamp(System.currentTimeMillis()) + "]");
		ResponseEntity response = null;
		PortMeAPIResponse errorBean = new PortMeAPIResponse();
		// String source = ReadConfigFile.getProperties().getProperty("PORTME_SOURCE");
		String msg = null;
		String xml;
		try {
			RecoveryDBForm recoveryDBForm = objectMapper.readValue(recovery, RecoveryDBForm.class);
			String messageSenderTelco = ReadConfigFile.getProperties().getProperty("MessageSenderTelco-ZOOM");
			if (recoveryDBForm.getZoneType().equals("Zone1")) {
				String messageReceiverTelco = ReadConfigFile.getProperties().getProperty("MessageReceiverTelco-mch1");
				_logger.info("[sessionId=" + sessionId
						+ "]: RecoveryDBRestApi.processRecoveryRequest() - zone1 process start with timestamp:["
						+ new Timestamp(System.currentTimeMillis()) + "]");
				String requestId = recoveryDBService.getRequestId(messageSenderTelco);
				RecoveryDB recoverydb = new RecoveryDB();
				recoveryDBForm.setRequestId(requestId);
				recoverydb.setRequest_id(requestId);
				recoverydb.setUserId(user.getUserId());
				recoverydb.setSubmit_date(timestamp);
				recoverydb.setUpdate_date(timestamp);
				recoverydb.setZone(recoveryDBForm.getZoneType());
				recoverydb.setRequest_type(recoveryDBForm.getRequestType());
				recoverydb.setStatus(0);
				recoverydb.setResult_code(0);
				if (recoveryDBForm.getRequestType().equals("Full")) {
					_logger.info("[sessionId=" + sessionId
							+ "]: RecoveryDBRestApi.processRecoveryRequest() - trying to convert zone1 recovery db full request into xml with timestamp:["
							+ new Timestamp(System.currentTimeMillis()) + "]");

					xml = new NPOUtils().convertZone1RecoveryFullReqIntoXML(recoveryDBForm, messageSenderTelco,
							messageReceiverTelco, sessionId);
					_logger.info("[sessionId=" + sessionId
							+ "]: RecoveryDBRestApi.processRecoveryRequest() - successfully convert zone1 recovery db full request into xml : ["
							+ xml + "] with timestamp:[" + new Timestamp(System.currentTimeMillis()) + "]");
				} else {
					if (recoveryDBForm.getIsLSA()) {
						recoverydb.setLsa(recoveryDBForm.getLsa());
					}
					if (recoveryDBForm.getIsMSISDN()) {
						recoverydb.setMsisdn(recoveryDBForm.getMsisdn());
					}
					if (recoveryDBForm.getIsTimestamp()) {
						String[] split = recoveryDBForm.getDateRange().split("\\-");
						if (split.length > 1) {
							String startDate = split[0];
							startDate = startDate.replace('/', '-');
							SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
							Date parsedStartDate = dateFormat.parse(startDate + " 00:00:00");
							Timestamp startTimestamp = new java.sql.Timestamp(parsedStartDate.getTime());
							recoverydb.setStart_date(startTimestamp);
							String endDate = split[1];
							endDate = endDate.replace('/', '-');
							Date parsedEndDate = dateFormat.parse(endDate + " 00:00:00");
							Timestamp endTimestamp = new java.sql.Timestamp(parsedEndDate.getTime());
							recoverydb.setEnd_date(endTimestamp);
						}
					}
					_logger.info("[sessionId=" + sessionId
							+ "]: RecoveryDBRestApi.processRecoveryRequest() - trying to convert zone1 recovery db partial request into xml with timestamp:["
							+ new Timestamp(System.currentTimeMillis()) + "]");
					xml = new NPOUtils().convertZone1RecoveryPartialReqIntoXML(recoveryDBForm, messageSenderTelco,
							messageReceiverTelco, sessionId);
					_logger.info("[sessionId=" + sessionId
							+ "]: RecoveryDBRestApi.processRecoveryRequest() - successfully convert zone1 recovery db partial request into xml : ["
							+ xml + "] with timestamp:[" + new Timestamp(System.currentTimeMillis()) + "]");
				}
				_logger.info("[sessionId=" + sessionId
						+ "]: RecoveryDBRestApi.processRecoveryRequest() - trying to store RDBRequest zone1 into database with timestamp:["
						+ new Timestamp(System.currentTimeMillis()) + "]");
				recoveryDBService.saveRecoveryDB(recoverydb);
				_logger.info("[sessionId=" + sessionId
						+ "]: RecoveryDBRestApi.processRecoveryRequest() - successfully to store RDBRequest zone1 into database with timestamp:["
						+ new Timestamp(System.currentTimeMillis()) + "]");
				int success = jmsProducer.sentRecoveryDBRequestIntoInQ(xml, sessionId, "1");
				if (success == 1) {
					_logger.info("[sessionId=" + sessionId
							+ "]: RecoveryDBRestApi.processRecoveryRequest() - Successfully submited RDBRequest zone1 xml:["
							+ xml + "] into activemq with timestamp:[" + new Timestamp(System.currentTimeMillis())
							+ "]");
					errorBean.setResponseCode(APIConst.successCode);
					errorBean.setResponseMessage(APIConst.successMsg);
					msg = PortMeUtils.generateJsonResponse(errorBean, "ERROR");
					response = new ResponseEntity(msg, new HttpHeaders(), HttpStatus.OK);
				} else {
					_logger.info("[sessionId=" + sessionId
							+ "]: RecoveryDBRestApi.processRecoveryRequest() - Fail submited RDBRequest zone1 xml:["
							+ xml + "] into activemq with timestamp:[" + new Timestamp(System.currentTimeMillis())
							+ "]");
					errorBean.setResponseCode(APIConst.successCode2);
					errorBean.setResponseMessage(APIConst.successMsg2);
					msg = PortMeUtils.generateJsonResponse(errorBean, "ERROR");
					response = new ResponseEntity(msg, new HttpHeaders(), HttpStatus.OK);
				}
				_logger.info("[sessionId=" + sessionId
						+ "]: RecoveryDBRestApi.processRecoveryRequest() - zone1 RDBRequest process end with timestamp:["
						+ new Timestamp(System.currentTimeMillis()) + "]");
			} else {
				_logger.info("[sessionId=" + sessionId
						+ "]: RecoveryDBRestApi.processRecoveryRequest() - zone2 RDBRequest process start with timestamp:["
						+ new Timestamp(System.currentTimeMillis()) + "]");
				//String messageReceiverTelco = ReadConfigFile.getProperties().getProperty("MessageReceiverTelco-mch2");
				String lsaId = null;//ReadConfigFile.getProperties().getProperty("recovery-db-zone2-lsaId");
				if (recoveryDBForm.getIsLSA()) {
					lsaId = recoveryDBForm.getLsa();
				} else {
					lsaId = recoveryDBForm.getLsa();
				}
				String transactionId = recoveryDBService.getTransactionId(messageSenderTelco, lsaId);
				// start code for zone2
				RecoveryDB recoverydb = new RecoveryDB();
				recoveryDBForm.setRequestId(transactionId);
				recoverydb.setRequest_id(transactionId);
				recoverydb.setUserId(user.getUserId());
				recoverydb.setSubmit_date(timestamp);
				recoverydb.setUpdate_date(timestamp);
				recoverydb.setZone(recoveryDBForm.getZoneType());
				recoverydb.setRequest_type(recoveryDBForm.getRequestType());
				recoverydb.setStatus(0);
				recoverydb.setResult_code(0);
				if (recoveryDBForm.getRequestType().equals("Full")) {
					recoverydb.setLsa(lsaId);
					_logger.info("[sessionId=" + sessionId
							+ "]: RecoveryDBRestApi.processRecoveryRequest() - trying to convert zone2 RDBFRequest into xml with timestamp:["
							+ new Timestamp(System.currentTimeMillis()) + "]");
					xml = new NPOUtils().convertZone2RecoveryFullReqIntoXML(recoveryDBForm, transactionId, lsaId,
							messageSenderTelco, sessionId);
					_logger.info("[sessionId=" + sessionId
							+ "]: RecoveryDBRestApi.processRecoveryRequest() - successfully convert zone2 RDBFRequest into xml : ["
							+ xml + "] with timestamp:[" + new Timestamp(System.currentTimeMillis()) + "]");
				} else {
					if (recoveryDBForm.getIsLSA()) {
						recoverydb.setLsa(recoveryDBForm.getLsa());
					}
					if (recoveryDBForm.getIsTimestamp()) {
						String[] split = recoveryDBForm.getDateRange().split("\\-");
						if (split.length > 1) {
							String startDate = split[0];
							startDate = startDate.replace('/', '-');
							SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
							Date parsedStartDate = dateFormat.parse(startDate + " 00:00:00");
							Timestamp startTimestamp = new java.sql.Timestamp(parsedStartDate.getTime());
							recoverydb.setStart_date(startTimestamp);
							String endDate = split[1];
							endDate = endDate.replace('/', '-');
							Date parsedEndDate = dateFormat.parse(endDate + " 00:00:00");
							Timestamp endTimestamp = new java.sql.Timestamp(parsedEndDate.getTime());
							recoverydb.setEnd_date(endTimestamp);
						}
					}
					_logger.info("[sessionId=" + sessionId
							+ "]: RecoveryDBRestApi.processRecoveryRequest() - trying to convert zone2 RDBPRequest into xml with timestamp:["
							+ new Timestamp(System.currentTimeMillis()) + "]");
					xml = new NPOUtils().convertZone2RecoveryPartialReqIntoXML(recoveryDBForm, transactionId,
							messageSenderTelco, sessionId);
					_logger.info("[sessionId=" + sessionId
							+ "]: RecoveryDBRestApi.processRecoveryRequest() - successfully converted zone2 RDBPRequest into xml : ["
							+ xml + "] with timestamp:[" + new Timestamp(System.currentTimeMillis()) + "]");
				}
				_logger.info("[sessionId=" + sessionId
						+ "]: RecoveryDBRestApi.processRecoveryRequest() - trying to store RDBRequest zone2 into database with timestamp:["
						+ new Timestamp(System.currentTimeMillis()) + "]");
				recoveryDBService.saveRecoveryDB(recoverydb);
				_logger.info("[sessionId=" + sessionId
						+ "]: RecoveryDBRestApi.processRecoveryRequest() - successfully to store RDBRequest zone2 into database with timestamp:["
						+ new Timestamp(System.currentTimeMillis()) + "]");
				int success = jmsProducer.sentRecoveryDBRequestIntoInQ(xml, sessionId, "2");
				if (success == 1) {
					_logger.info("[sessionId=" + sessionId
							+ "]: RecoveryDBRestApi.processRecoveryRequest() - Successfully submited RDBRequest zone2 xml:["
							+ xml + "] into activemq with timestamp:[" + new Timestamp(System.currentTimeMillis())
							+ "]");
					errorBean.setResponseCode(APIConst.successCode);
					errorBean.setResponseMessage(APIConst.successMsg);
					msg = PortMeUtils.generateJsonResponse(errorBean, "ERROR");
					response = new ResponseEntity(msg, new HttpHeaders(), HttpStatus.OK);
				} else {
					_logger.info("[sessionId=" + sessionId
							+ "]: RecoveryDBRestApi.processRecoveryRequest() - Fail to submited RDBRequest zone2 xml:["
							+ xml + "] into activemq with timestamp:[" + new Timestamp(System.currentTimeMillis())
							+ "]");
					errorBean.setResponseCode(APIConst.successCode2);
					errorBean.setResponseMessage(APIConst.successMsg2);
					msg = PortMeUtils.generateJsonResponse(errorBean, "ERROR");
					response = new ResponseEntity(msg, new HttpHeaders(), HttpStatus.OK);
				}
				_logger.info("[sessionId=" + sessionId
						+ "]: RecoveryDBRestApi.processRecoveryRequest() - zone2 RDBRequest process end with timestamp:["
						+ new Timestamp(System.currentTimeMillis()) + "]");
			}
		} catch (Exception e) {
			errorBean.setResponseCode(APIConst.successCode1);
			errorBean.setResponseMessage(APIConst.successMsg1);
			msg = PortMeUtils.generateJsonResponse(errorBean, "ERROR");
			_logger.info("[sessionId=" + sessionId
					+ "]: RecoveryDBRestApi.processRecoveryRequest() - Exception occur while processing RDBRequest with timestamp:["
					+ new Timestamp(System.currentTimeMillis()) + "], error-" + e);
			// e.printStackTrace();
			response = new ResponseEntity(msg, new HttpHeaders(), HttpStatus.OK);
		}
		_logger.info("[sessionId=" + sessionId
				+ "]: RecoveryDBRestApi.processRecoveryRequest() - process end with timestamp:["
				+ new Timestamp(System.currentTimeMillis()) + "]");
		return response;
	}

	public void saveWebAccessTrace(WebAccessTrace logTrace) {
		try {
			webAccessTraceService.saveWebAccessTrace(logTrace);
		} catch (Exception e) {
			_logger.error("Exception occurs while inserting WebAccessTrace" + e);
		}
	}
}