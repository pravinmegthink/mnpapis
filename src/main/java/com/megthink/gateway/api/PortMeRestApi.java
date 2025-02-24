package com.megthink.gateway.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.megthink.gateway.api.response.PortMeAPIResponse;
import com.megthink.gateway.dao.NumberPlanDao;
import com.megthink.gateway.dao.PortMeDao;
import com.megthink.gateway.model.MSISDNUIDType;
import com.megthink.gateway.model.PortMe;
import com.megthink.gateway.model.PortMeTransactionDetails;
import com.megthink.gateway.model.SubscriberArrType;
import com.megthink.gateway.model.User;
import com.megthink.gateway.producer.JmsProducer;
import com.megthink.gateway.repository.PortMeRepository;
import com.megthink.gateway.service.CustomerDataService;
import com.megthink.gateway.service.FileStorageService;
import com.megthink.gateway.service.PersonCustomerService;
import com.megthink.gateway.service.PortMeService;
import com.megthink.gateway.service.PortMeTransactionDetailsService;
import com.megthink.gateway.service.SubscriberArrTypeService;
import com.megthink.gateway.service.UserService;
import com.megthink.gateway.utils.APIConst;
import com.megthink.gateway.utils.NPOUtils;
import com.megthink.gateway.utils.PortMeUtils;
import com.megthink.gateway.utils.ReadConfigFile;

import java.sql.Timestamp;
import java.util.Base64;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class PortMeRestApi {

	private static final Logger _logger = LoggerFactory.getLogger(PortMeRestApi.class);

	Timestamp timestamp = new Timestamp(System.currentTimeMillis());

	@Autowired
	private UserService userService;
	@Autowired
	private ObjectMapper objectMapper;
	@Autowired
	private PortMeService portMeService;
	@Autowired
	private CustomerDataService customerDataService;
	@Autowired
	private PersonCustomerService personCustomerSerive;
	@Autowired
	private PortMeTransactionDetailsService portMeTransactionService;
	@Autowired
	private PortMeRepository portMeRepository;
	@Autowired
	private FileStorageService fileStorageService;
	@Autowired
	private SubscriberArrTypeService subscriberArrTypeService;
	@Autowired
	private JmsProducer jmsProducer;
	@Autowired
	private PortMeDao portMeDao;
	@Autowired
	private NumberPlanDao numberPlanDao;

	@PostMapping("/api/initportrequest")
	public ResponseEntity<?> schedulePortMe(@RequestParam("portme") String portme,
			@RequestParam(name = "bulkUpload", required = false) MultipartFile bulkUpload,
			@RequestParam(name = "docFile", required = false) MultipartFile docFile) {
		String sessionId = Long.toString(System.currentTimeMillis());
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = userService.findUserByUsername(auth.getName());
		PortMe portMeDetails = null;
		_logger.info("[sessionId=" + sessionId
				+ "]: PortMeRestApi.schedulePortMe() - portin request process start with timestamp:["
				+ new Timestamp(System.currentTimeMillis()) + "]");
		ResponseEntity response = null;
		int current_status = 0;
		String requestId = null;
		PortMeAPIResponse errorBean = new PortMeAPIResponse();
		PortMeTransactionDetails portMeTransaction = new PortMeTransactionDetails();
		String msg = null;
		String reqType = ReadConfigFile.getProperties().getProperty("PORTME_IN");
		String source = ReadConfigFile.getProperties().getProperty("PORTME_SOURCE");
		Boolean isFileProcess = true;
		int mch_type = 0;
		String referenceId = null;
		try {
			String binaryFile = null;
			// requestId = PortMeUtils.randomUniqueUUID();
			if (docFile != null) {
				_logger.debug("[sessionId=" + sessionId
						+ "]: PortMeRestApi.schedulePortMe() - trying to upload document file with timestamp:["
						+ new Timestamp(System.currentTimeMillis()) + "]");
				fileStorageService.storeFile(docFile, PortMeUtils.randomUniqueUUID() + ".pdf");
				binaryFile = Base64.getEncoder().encodeToString(docFile.getBytes());
				_logger.debug("[sessionId=" + sessionId
						+ "]: PortMeRestApi.schedulePortMe() - uploaded upload document file with timestamp:["
						+ new Timestamp(System.currentTimeMillis()) + "]");
			}
			portMeDetails = objectMapper.readValue(portme, PortMe.class);
			if (bulkUpload != null) {
				try {
					String hlr = portMeDetails.getSubscriberArrType().get(0).getHlr();
					String alternateNumber = portMeDetails.getSubscriberArrType().get(0).getDummyMSISDN();
					_logger.debug("[sessionId=" + sessionId
							+ "]: PortMeRestApi.schedulePortMe() - trying to convert msisdn file into list with timestamp:["
							+ new Timestamp(System.currentTimeMillis()) + "]");
					List<SubscriberArrType> listOfPlan = subscriberArrTypeService.processBulkFile(bulkUpload, hlr,
							alternateNumber);
					_logger.debug("[sessionId=" + sessionId
							+ "]: PortMeRestApi.schedulePortMe() -  converted msisdn file into list with timestamp:["
							+ new Timestamp(System.currentTimeMillis()) + "]");
					portMeDetails.setSubscriberArrType(listOfPlan);
				} catch (Exception e) {
					isFileProcess = false;
				}
			}
			if (isFileProcess) {
				if (portMeDetails == null) {
					errorBean.setResponseCode(APIConst.successCode1);
					errorBean.setResponseMessage(APIConst.successMsg1);
					msg = PortMeUtils.generateJsonResponse(errorBean, "ERROR");
					response = new ResponseEntity(msg, new HttpHeaders(), HttpStatus.BAD_REQUEST);
				} else {
					_logger.info("[sessionId=" + sessionId
							+ "]: PortMeRestApi.schedulePortMe() -  start portin request with timestamp:["
							+ new Timestamp(System.currentTimeMillis()) + "]");
					requestId = portMeRepository.getSynReqeustId(portMeDetails.getRno());
					referenceId = portMeService.getReferenceId(portMeDetails.getRno(), portMeDetails.getDno(),
							portMeDetails.getDno(), "PORT");
					current_status = 1;
					portMeDetails.setUserId(user.getUserId());
					portMeDetails.setRequestId(referenceId);
					portMeDetails.setSource(Integer.parseInt(source));
					portMeDetails.setRequest_type(reqType);
					portMeTransaction.setRequestId(referenceId);
					portMeTransaction.setStatus(current_status);
					portMeTransaction.setRequestType(reqType);
					if (portMeDetails.getSubscriberArrType().size() > 0) {
						_logger.debug("[sessionId=" + sessionId
								+ "]: PortMeRestApi.schedulePortMe() -  trying to store into transaction table with timestamp:["
								+ new Timestamp(System.currentTimeMillis()) + "]");
						portMeTransactionService.savePortMeTransactionDetails(portMeTransaction,
								portMeDetails.getSubscriberArrType());
						_logger.debug("[sessionId=" + sessionId
								+ "]: PortMeRestApi.schedulePortMe() -  PortMe Corporate Transactiondetails insterted into db with reqeustId with timestamp:["
								+ new Timestamp(System.currentTimeMillis()) + "]");
					} else {
						portMeTransaction.setMsisdn(portMeDetails.getSubscriberSequence().getSubscriberNumber());
						portMeTransactionService.savePortMeTransactionDetails(portMeTransaction);
						_logger.debug("[sessionId=" + sessionId
								+ "]: PortMeRestApi.schedulePortMe() -  PortMe personal Transactiondetails insterted into db with reqeustId with timestamp:["
								+ new Timestamp(System.currentTimeMillis()) + "]");
					}
					String remark = PortMeUtils.randomUUID(16, 0, 'S');
					String msisdns = null;
					if (portMeDetails.getSubscriberArrType().size() > 0) {
						msisdns = portMeDetails.getSubscriberArrType().get(0).getMsisdn();
					} else {
						msisdns = portMeDetails.getSubscriberSequence().getSubscriberNumber();
					}
					String lastArea = numberPlanDao.getDonorLSAID(msisdns);
					if (lastArea == null) {
						lastArea = numberPlanDao.getArea(msisdns);
					}
					mch_type = numberPlanDao.getMCHTypeByArea(lastArea);

					portMeDetails.setStatus(current_status);
					portMeDetails.setCustomerRequestTime(timestamp.toString());
					portMeDetails.setOrderDate(timestamp.toString());
					portMeDetails.setOriginalCarrier(portMeDetails.getDno());
					portMeDetails.setLast_area(lastArea);
					portMeDetails.setMch(mch_type);
					portMeDetails.setRemark(remark);
					portMeDetails.setRequestId(referenceId);
					portMeDetails.setTimeStamp(timestamp.toString());
					PortMe portMe = portMeService.savePortMe(portMeDetails);
					if (portMe.getPortId() != 0) {
						_logger.debug("[sessionId=" + sessionId
								+ "]: PortMeRestApi.schedulePortMe() -  successfully saved port me details with timestamp:["
								+ new Timestamp(System.currentTimeMillis()) + "]");
						if (portMeDetails.getSubscriberArrType().size() > 0) {
							/* insert MSISDN for corporate number */
							subscriberArrTypeService.saveMt(portMe.getPortId(), portMeDetails.getSubscriberArrType(),
									reqType, current_status);
						} else {
							/* insert MSISDN for personal number */
							subscriberArrTypeService.savePortMT(portMe.getPortId(),
									portMeDetails.getSubscriberSequence().getSubscriberNumber(), reqType,
									current_status, portMeDetails.getHlr(), portMeDetails.getDummyMSISDN());
						}
						if (portMeDetails.getCustomerData() != null) {
							portMeDetails.getCustomerData().setPortId(portMe.getPortId());
							customerDataService.saveCustomerData(portMeDetails.getCustomerData());
						} else {
							portMeDetails.getPersonCustomer().setPortId(portMe.getPortId());
							personCustomerSerive.savePersonCustomer(portMeDetails.getPersonCustomer());
						}
						/* going to validate MSISDN for corporate number */
						if (portMeDetails.getSubscriberArrType().size() > 0) {
							for (SubscriberArrType subscriber : portMeDetails.getSubscriberArrType()) {
								_logger.debug("[sessionId=" + sessionId
										+ "]: PortMeRestApi.schedulePortMe() -  Going to validate corporate msisdn ["
										+ subscriber.getMsisdn() + "] with timestamp:["
										+ new Timestamp(System.currentTimeMillis()) + "]");
								String msisdnValidate = portMeRepository.validateMSISDN(subscriber.getMsisdn());
								String[] validateMsisdnRsp = msisdnValidate.split("[,]", 0);
								_logger.debug("[sessionId=" + sessionId
										+ "]: PortMeRestApi.schedulePortMe() -  successfully get DNO info : "
										+ msisdnValidate + " for msisdn[" + subscriber.getMsisdn()
										+ "]  with timestamp:[" + new Timestamp(System.currentTimeMillis()) + "]");
								/*
								 * 1-> check for msisdn, donor(and rno, lrn let's do later, operatorid should be
								 * donor) from tbl_master_np if not exist then msisdn_range
								 */
								/*
								 * 2-> select rn from msisdn_range for rno=op_id and area = area(opcode=airtel,
								 * area = dl) jis me port hoga uska area
								 */
								if (validateMsisdnRsp[1].equals(portMeDetails.getRno())
										&& validateMsisdnRsp[4].equals(portMeDetails.getArea())) {
									subscriberArrTypeService.updatePortMtStatusByMsisdn(current_status,
											subscriber.getMsisdn(), reqType, 400);
									portMeTransaction = new PortMeTransactionDetails();
									portMeTransaction.setRequestId(referenceId);
									portMeTransaction.setStatus(19);
									portMeTransaction.setRequestType(reqType);
									portMeTransactionService.savePortMeTransactionDetails(portMeTransaction);
									errorBean.setResponseCode(APIConst.loggerCode1);
									errorBean.setResponseMessage(APIConst.loggerMsg9);
									msg = PortMeUtils.generateJsonResponse(errorBean, "ERROR");
									_logger.debug("[sessionId=" + sessionId
											+ "]: PortMeRestApi.schedulePortMe() - Number plan" + subscriber.getMsisdn()
											+ " porting from same operator to same lsa not allowed with timestamp:["
											+ new Timestamp(System.currentTimeMillis()) + "]");
									response = new ResponseEntity(msg, new HttpHeaders(), HttpStatus.OK);
								} else {
									/* checking MSISDN porting calculation days with porting date */
									Boolean portin_days = false;
									if (validateMsisdnRsp[3].equals("null")) {
										portin_days = true;
									} else {
										int no_of_day = portMeRepository.getNoOfDaysByMSISDN(subscriber.getMsisdn());
										String days = ReadConfigFile.getProperties().getProperty("PORTINDAYS");
										if (Integer.parseInt(days) >= no_of_day) {
											portin_days = true;
										} else {
											portin_days = false;
										}
									}
									if (portin_days == true) {
										String validateRNO = portMeRepository.validateRNO(portMeDetails.getRno(),
												portMeDetails.getArea());
										String[] validateRNORsp = validateRNO.split("[,]", 0);
										if (validateRNORsp[1].equals(portMeDetails.getRn())) {
											current_status = 2;
											portMeTransaction = new PortMeTransactionDetails();
											portMeTransaction.setRequestId(referenceId);
											portMeTransaction.setStatus(current_status);
											portMeTransaction.setRequestType(reqType);
											portMeTransaction.setMsisdn(subscriber.getMsisdn());
											portMeTransactionService.savePortMeTransactionDetails(portMeTransaction);
											subscriberArrTypeService.updatePortMtStatusByMsisdn(current_status,
													subscriber.getMsisdn(), reqType, 200);
											_logger.debug("[sessionId=" + sessionId
													+ "]: PortMeRestApi.schedulePortMe() - Number plan"
													+ subscriber.getMsisdn() + " exist in our table with timestamp:["
													+ new Timestamp(System.currentTimeMillis()) + "]");
										} else {
											portMeTransaction = new PortMeTransactionDetails();
											portMeTransaction.setRequestId(referenceId);
											portMeTransaction.setStatus(19);
											portMeTransaction.setRequestType(reqType);
											portMeTransactionService.savePortMeTransactionDetails(portMeTransaction);
											subscriberArrTypeService.updatePortMtStatusByMsisdn(current_status,
													subscriber.getMsisdn(), reqType, 400);
											errorBean.setResponseCode(APIConst.loggerCode1);
											errorBean.setResponseMessage(APIConst.loggerMsg7);
											msg = PortMeUtils.generateJsonResponse(errorBean, "ERROR");
											_logger.debug("[sessionId=" + sessionId
													+ "]: PortMeRestApi.schedulePortMe() - Number plan"
													+ subscriber.getMsisdn()
													+ " route info is not correct with timestamp:["
													+ new Timestamp(System.currentTimeMillis()) + "]");
											response = new ResponseEntity(msg, new HttpHeaders(), HttpStatus.OK);
										}
									} else {
										subscriberArrTypeService.updatePortMtStatusByMsisdn(current_status,
												subscriber.getMsisdn(), reqType, 400);
										portMeTransaction = new PortMeTransactionDetails();
										portMeTransaction.setRequestId(referenceId);
										portMeTransaction.setStatus(19);
										portMeTransaction.setRequestType(reqType);
										portMeTransactionService.savePortMeTransactionDetails(portMeTransaction);
										subscriberArrTypeService.updatePortMtStatusByMsisdn(current_status,
												subscriber.getMsisdn(), reqType, 400);
										errorBean.setResponseCode(APIConst.loggerCode1);
										errorBean.setResponseMessage(APIConst.loggerMsg8);
										msg = PortMeUtils.generateJsonResponse(errorBean, "ERROR");
										_logger.debug("[sessionId=" + sessionId
												+ "]: PortMeRestApi.schedulePortMe() - Number plan"
												+ subscriber.getMsisdn() + " does not allow to port with timestamp:["
												+ new Timestamp(System.currentTimeMillis()) + "]");
										response = new ResponseEntity(msg, new HttpHeaders(), HttpStatus.OK);
									}
								}
							}
						} else {
							/* going to validate MSISDN for personal number */
							String msisdn = portMeDetails.getSubscriberSequence().getSubscriberNumber();
							_logger.debug("[sessionId=" + sessionId
									+ "]: PortMeRestApi.schedulePortMe() - tring to get DNO, RNO for personal plan "
									+ msisdn + " with timestamp:[" + new Timestamp(System.currentTimeMillis()) + "]");
							String msisdnValidate = portMeRepository.validateMSISDN(msisdn);
							_logger.debug(
									"[sessionId=" + sessionId + "]: PortMeRestApi.schedulePortMe() - got DNO, RNO ["
											+ msisdnValidate + "] for personal plan " + msisdn + " with timestamp:["
											+ new Timestamp(System.currentTimeMillis()) + "]");
							String[] validateMsisdnRsp = msisdnValidate.split("[,]", 0);
							if (validateMsisdnRsp[1].equals(portMeDetails.getRno())
									&& validateMsisdnRsp[4].equals(portMeDetails.getArea())) {
								portMeTransaction = new PortMeTransactionDetails();
								portMeTransaction.setRequestId(referenceId);
								portMeTransaction.setStatus(19);
								portMeTransaction.setRequestType(reqType);
								portMeTransactionService.savePortMeTransactionDetails(portMeTransaction);
								subscriberArrTypeService.updatePortMtStatusByMsisdn(current_status, msisdn, reqType,
										400);
								errorBean.setResponseCode(APIConst.loggerCode1);
								errorBean.setResponseMessage(APIConst.loggerMsg9);
								msg = PortMeUtils.generateJsonResponse(errorBean, "ERROR");
								_logger.info("[sessionId=" + sessionId
										+ "]: PortMeRestApi.schedulePortMe() - Plan number [" + msisdn
										+ "] porting from same operator to same lsa not allowed with timestamp:["
										+ new Timestamp(System.currentTimeMillis()) + "]");
								response = new ResponseEntity(msg, new HttpHeaders(), HttpStatus.OK);
							} else {
								Boolean portin_days = false;
								if (validateMsisdnRsp[3] != null) {
									portin_days = true;
								} else {
									int no_of_day = portMeRepository.getNoOfDaysByMSISDN(msisdn);
									String days = ReadConfigFile.getProperties().getProperty("PORTINDAYS");
									if (Integer.parseInt(days) >= no_of_day) {
										portin_days = true;
									} else {
										portin_days = false;
									}
								}
								if (portin_days == true) {
									_logger.debug("[sessionId=" + sessionId
											+ "]: PortMeRestApi.schedulePortMe() - trying to get RN info for personal plan "
											+ msisdn + " with timestamp:[" + new Timestamp(System.currentTimeMillis())
											+ "]");
									String validateRNO = portMeRepository.validateRNO(portMeDetails.getRno(),
											portMeDetails.getArea());
									_logger.debug("[sessionId=" + sessionId
											+ "]: PortMeRestApi.schedulePortMe() - got RN info" + validateRNO
											+ " for personal plan " + msisdn + " with timestamp:["
											+ new Timestamp(System.currentTimeMillis()) + "]");
									String[] validateRNORsp = validateRNO.split("[,]", 0);
									if (validateRNORsp[1].equals(portMeDetails.getRn())) {
										current_status = 2;
										portMeTransaction = new PortMeTransactionDetails();
										portMeTransaction.setRequestId(referenceId);
										portMeTransaction.setStatus(current_status);
										portMeTransaction.setRequestType(reqType);
										portMeTransaction.setMsisdn(msisdn);
										portMeTransactionService.savePortMeTransactionDetails(portMeTransaction);
										subscriberArrTypeService.updatePortMtStatusByMsisdn(current_status, msisdn,
												reqType, 200);
									} else {
										portMeTransaction = new PortMeTransactionDetails();
										portMeTransaction.setRequestId(referenceId);
										portMeTransaction.setStatus(19);
										portMeTransaction.setRequestType(reqType);
										portMeTransactionService.savePortMeTransactionDetails(portMeTransaction);
										subscriberArrTypeService.updatePortMtStatusByMsisdn(current_status, msisdn,
												reqType, 400);
										errorBean.setResponseCode(APIConst.loggerCode1);
										errorBean.setResponseMessage(APIConst.loggerMsg7);
										msg = PortMeUtils.generateJsonResponse(errorBean, "ERROR");
										_logger.debug("[sessionId=" + sessionId
												+ "]: PortMeRestApi.schedulePortMe() - Plan number [" + msisdn
												+ "] route info is not correct with timestamp:["
												+ new Timestamp(System.currentTimeMillis()) + "]");
										response = new ResponseEntity(msg, new HttpHeaders(), HttpStatus.OK);
									}
								} else {
									portMeTransaction = new PortMeTransactionDetails();
									portMeTransaction.setRequestId(referenceId);
									portMeTransaction.setStatus(19);
									portMeTransaction.setRequestType(reqType);
									portMeTransactionService.savePortMeTransactionDetails(portMeTransaction);
									subscriberArrTypeService.updatePortMtStatusByMsisdn(current_status, msisdn, reqType,
											400);
									errorBean.setResponseCode(APIConst.loggerCode1);
									errorBean.setResponseMessage(APIConst.loggerMsg8);
									msg = PortMeUtils.generateJsonResponse(errorBean, "ERROR");

									_logger.info("[sessionId=" + sessionId
											+ "]: PortMeRestApi.schedulePortMe() - Plan number [" + msisdn
											+ "] does not allow to port with timestamp:["
											+ new Timestamp(System.currentTimeMillis()) + "]");
									response = new ResponseEntity(msg, new HttpHeaders(), HttpStatus.OK);
								}

							}
						}
						if (current_status == 2) {
							List<SubscriberArrType> subArr = subscriberArrTypeService
									.findSubArrByPortIdAndResultCode(portMe.getPortId(), 200);
							if (subArr.size() > 0) {
								_logger.info("[sessionId=" + sessionId
										+ "]: PortMeRestApi.schedulePortMe() - going to convert portme request into xml with timestamp:["
										+ new Timestamp(System.currentTimeMillis()) + "]");
								String msisdn = null;
								if (portMeDetails.getSubscriberSequence() != null
										&& portMeDetails.getSubscriberArrType().size() == 0) {
									msisdn = portMeDetails.getSubscriberSequence().getSubscriberNumber();
								} else {
									portMeDetails.setSubscriberArrType(subArr);
									msisdn = portMeDetails.getSubscriberArrType().get(0).getMsisdn();
								}
								String area = numberPlanDao.getArea(msisdn);
								// mch_type = numberPlanDao.getMCHTypeByArea(area);
								String xml = null;
								if (mch_type == 1) {
									xml = new NPOUtils().convertJsonIntoInitPortRequest(portMeDetails, binaryFile,
											referenceId, requestId);
								} else {
									String donorLSAID = numberPlanDao.getDonorLSAID(msisdn);
									if (donorLSAID == null) {
										donorLSAID = area;
									}
									String messageSenderTelco = ReadConfigFile.getProperties()
											.getProperty("MessageSenderTelco-ZOOM");
									String transactionId = portMeService.getTransactionId(messageSenderTelco, area);
									portMeDetails.setLast_area(donorLSAID);
									xml = new NPOUtils().convertJsonIntoInitPortRequestZone2(portMeDetails, binaryFile,
											transactionId);
								}
								_logger.info("[sessionId=" + sessionId
										+ "]: PortMeRestApi.schedulePortMe() - converted portme request into xml :["
										+ xml + "]");
								int returnvalue = jmsProducer.sendMessageToInQueue(xml, sessionId, mch_type);
								if (returnvalue == 1) {
									_logger.info("[sessionId=" + sessionId
											+ "]: PortMeRestApi.schedulePortMe() - sent portin request in jms queue");
									current_status = 3;
									portMeTransaction = new PortMeTransactionDetails();
									portMeTransaction.setRequestId(referenceId);
									portMeTransaction.setStatus(current_status);
									portMeTransaction.setRequestType(reqType);
									if (subArr.size() > 0) {
										portMeTransactionService.savePortMeTransactionDetails(portMeTransaction,
												subArr);
										for (SubscriberArrType list : subArr) {
											subscriberArrTypeService.updatePortMtStatusByMsisdn(current_status,
													list.getMsisdn(), reqType, 200);
										}
									}
									_logger.info("[sessionId=" + sessionId
											+ "]: PortMeRestApi.schedulePortMe() - Successfully Received portin request");
									errorBean.setId(portMe.getPortId());
									errorBean.setResponseCode(APIConst.successCode);
									errorBean.setResponseMessage(APIConst.successMsg);
									msg = PortMeUtils.generateJsonResponse(errorBean, "SUCCESS");
									response = new ResponseEntity(msg, new HttpHeaders(), HttpStatus.OK);
								} else {
									_logger.info("[sessionId=" + sessionId
											+ "]: PortMeRestApi.schedulePortMe() - Successfully saved data but not able to send queue");
									errorBean.setId(portMe.getPortId());
									errorBean.setResponseCode(APIConst.successCode2);
									errorBean.setResponseMessage(APIConst.successMsg2);
									msg = PortMeUtils.generateJsonResponse(errorBean, "SUCCESS");
									response = new ResponseEntity(msg, new HttpHeaders(), HttpStatus.OK);
								}
							} else {
								_logger.info("[sessionId=" + sessionId
										+ "]: PortMeRestApi.schedulePortMe() - Number plan data is null with requestId : "
										+ requestId);
								errorBean.setId(portMe.getPortId());
								errorBean.setResponseCode(APIConst.successCode3);
								errorBean.setResponseMessage(APIConst.successMsg3);
								msg = PortMeUtils.generateJsonResponse(errorBean, "SUCCESS");
								response = new ResponseEntity(msg, new HttpHeaders(), HttpStatus.OK);
							}
						}
					} else {
						portMeTransaction = new PortMeTransactionDetails();
						portMeTransaction.setRequestId(referenceId);
						portMeTransaction.setStatus(19);
						portMeTransaction.setRequestType(reqType);
						portMeTransactionService.savePortMeTransactionDetails(portMeTransaction);
						_logger.info("[sessionId=" + sessionId
								+ "]: PortMeRestApi.schedulePortMe() - PortMe details unsaved with with requestId : "
								+ requestId);
						errorBean.setResponseCode(APIConst.successCode1);
						errorBean.setResponseMessage(APIConst.successMsg1);
						msg = PortMeUtils.generateJsonResponse(errorBean, "ERROR");
						response = new ResponseEntity(msg, new HttpHeaders(), HttpStatus.OK);
					}
				}
			} else {
				errorBean.setResponseCode(APIConst.successCode102);
				errorBean.setResponseMessage(APIConst.successMsg102);
				msg = PortMeUtils.generateJsonResponse(errorBean, "ERROR");
				response = new ResponseEntity(msg, new HttpHeaders(), HttpStatus.BAD_REQUEST);
			}

		} catch (Exception e) {
			portMeTransaction = new PortMeTransactionDetails();
			portMeTransaction.setRequestId(referenceId);
			portMeTransaction.setStatus(19);
			portMeTransaction.setRequestType(reqType);
			portMeTransactionService.savePortMeTransactionDetails(portMeTransaction);
			errorBean.setResponseCode(APIConst.successCode1);
			errorBean.setResponseMessage(APIConst.successMsg1);
			msg = PortMeUtils.generateJsonResponse(errorBean, "ERROR");
			_logger.error("[sessionId=" + sessionId
					+ "]: PortMeRestApi.schedulePortMe() - Something is wrong with the system with requestId : "
					+ requestId + " " + e.getMessage());
			// e.printStackTrace();
			response = new ResponseEntity(msg, new HttpHeaders(), HttpStatus.OK);
		}
		if (current_status != 0) {
			// going to update current status in port_tx table
			portMeService.updatePortMeStatus(current_status, referenceId, user.getUserId(), mch_type);
		}
		return response;
	}

	@PostMapping("/api/connectionanswer")
	public ResponseEntity<?> sendConnectionAnswer(@RequestParam("portmeanswer") String portmeanswer) {
		String sessionId = Long.toString(System.currentTimeMillis());
		_logger.info("received portme connection answer request");
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = userService.findUserByUsername(auth.getName());
		PortMe portMeDetails = null;
		ResponseEntity response = null;
		PortMeAPIResponse errorBean = new PortMeAPIResponse();
		String msg = null;
		int current_status = 0;
		PortMeTransactionDetails portMeTransaction = new PortMeTransactionDetails();
		String reqType = ReadConfigFile.getProperties().getProperty("PORTME_IN");
		String source = ReadConfigFile.getProperties().getProperty("PORTME_SOURCE");
		String referenceId = null;
		String requestId = null;
		try {
			portMeDetails = objectMapper.readValue(portmeanswer, PortMe.class);
			if (portMeDetails == null) {
				errorBean.setResponseCode(APIConst.successCode1);
				errorBean.setResponseMessage(APIConst.successMsg1);
				msg = PortMeUtils.generateJsonResponse(errorBean, "ERROR");
				response = new ResponseEntity(msg, new HttpHeaders(), HttpStatus.BAD_REQUEST);
			} else {
				if (portMeDetails.getMsisdnUID().size() > 0) {
					current_status = 15;
					portMeDetails.setSource(Integer.parseInt(source));
					for (MSISDNUIDType msisdnuid : portMeDetails.getMsisdnUID()) {
						referenceId = msisdnuid.getRequestId();
						String lastArea = numberPlanDao.getDonorLSAID(msisdnuid.getMsisdn());
						requestId = portMeRepository.getSynReqeustId(lastArea);
						_logger.info("received portme connection answer request with requestId : " + requestId);
						portMeTransaction = new PortMeTransactionDetails();
						portMeTransaction.setRequestId(referenceId);
						portMeTransaction.setStatus(current_status);
						portMeTransaction.setRequestType(reqType);
						portMeTransaction.setMsisdn(msisdnuid.getMsisdn());
						portMeTransactionService.savePortMeTransactionDetails(portMeTransaction);
						subscriberArrTypeService.updatePortMtStatusByMsisdn(current_status, msisdnuid.getMsisdn(),
								reqType, 200);
					}
					_logger.info(
							"trying convert portme connection answer request into xml with requestId : " + referenceId);
					String area = numberPlanDao.getArea(portMeDetails.getMsisdnUID().get(0).getMsisdn());
					int mch_type = numberPlanDao.getMCHTypeByArea(area);
					String xml = null;
					if (mch_type == 1) {
						portMeDetails.setRequestId(referenceId);
						xml = new NPOUtils().convertJsonIntoConnectionAnswer(portMeDetails, mch_type, requestId);
					} else {
						String donorLSAID = numberPlanDao
								.getDonorLSAID(portMeDetails.getSubscriberArrType().get(0).getMsisdn());
						if (donorLSAID == null) {
							donorLSAID = area;
						}
						String messageSenderTelco = ReadConfigFile.getProperties()
								.getProperty("MessageSenderTelco-ZOOM");
						String transactionId = portMeService.getTransactionId(messageSenderTelco, area);
						portMeDetails.setLast_area(donorLSAID);
						String routeInfo = numberPlanDao.getRouteInfo(portMeDetails.getMsisdnUID().get(0).getMsisdn());
						portMeDetails.setRn(routeInfo);
						xml = new NPOUtils().convertJsonIntoConnectionAnswer(portMeDetails, mch_type, transactionId);
					}
					_logger.info("convert portme connection answer request into xml with requestId:" + requestId + xml);
					int returnvalue = jmsProducer.sendMessageToInQueue(xml, sessionId, mch_type);
					if (returnvalue == 1) {
						_logger.info("sent portme connection answer request in jms queue with requestId-" + requestId);
						_logger.info(
								"Successfully Received portmeconnectionanswer request with requestId:" + requestId);
						errorBean.setResponseCode(APIConst.successCode);
						errorBean.setResponseMessage(APIConst.successMsg);
						msg = PortMeUtils.generateJsonResponse(errorBean, "SUCCESS");
						response = new ResponseEntity(msg, new HttpHeaders(), HttpStatus.OK);
					} else {
						_logger.info("Portme connection answer request saved successfully but not able to send queue");
						errorBean.setResponseCode(APIConst.successCode);
						errorBean.setResponseMessage(APIConst.successMsg2);
						msg = PortMeUtils.generateJsonResponse(errorBean, "SUCCESS");
						response = new ResponseEntity(msg, new HttpHeaders(), HttpStatus.OK);
					}
				} else {
					_logger.info("Portme connection answer request is size zero with requestId :" + requestId);
					errorBean.setResponseCode(400);
					errorBean.setResponseMessage("PortMe connection answer request is size zero");
					msg = PortMeUtils.generateJsonResponse(errorBean, "SUCCESS");
					response = new ResponseEntity(msg, new HttpHeaders(), HttpStatus.OK);
				}
			}
		} catch (Exception e) {
			_logger.error("error Portme connection answer request with requestId :" + requestId + e.getMessage());
			portMeTransaction = new PortMeTransactionDetails();
			portMeTransaction.setRequestId(referenceId);
			portMeTransaction.setStatus(19);
			portMeTransaction.setRequestType(reqType);
			portMeTransactionService.savePortMeTransactionDetails(portMeTransaction);
			errorBean.setResponseCode(APIConst.successCode1);
			errorBean.setResponseMessage(APIConst.successMsg1);
			msg = PortMeUtils.generateJsonResponse(errorBean, "ERROR");
			// e.printStackTrace();
			response = new ResponseEntity(msg, new HttpHeaders(), HttpStatus.BAD_REQUEST);
		}
		if (current_status != 0) {
			// inserting current status in port_tx table
			portMeService.updatePortMeStatus(current_status, referenceId, user.getUserId());
		}
		return response;

	}

	@PostMapping("/api/portapproval")
	public ResponseEntity<?> sendPortApprovalInfo(@RequestParam("portMeApproval") String portMeApproval) {
		String sessionId = Long.toString(System.currentTimeMillis());
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = userService.findUserByUsername(auth.getName());
		ResponseEntity response = null;
		PortMeAPIResponse errorBean = new PortMeAPIResponse();
		PortMeTransactionDetails portMeTransaction = new PortMeTransactionDetails();
		String requestId = null;
		String referenceId = null;
		String msg = null;
		int current_status = 0;
		String reqType = ReadConfigFile.getProperties().getProperty("PORTME_OUT");
		String source = ReadConfigFile.getProperties().getProperty("PORTME_SOURCE");
		PortMe portMeDetails = null;
		int mch_type = 0;
		String area = null;
		try {
			portMeDetails = objectMapper.readValue(portMeApproval, PortMe.class);
			if (portMeDetails == null) {
				_logger.info("Recieved port approval request is null");
				errorBean.setResponseCode(APIConst.successCode1);
				errorBean.setResponseMessage(APIConst.successMsg1);
				msg = PortMeUtils.generateJsonResponse(errorBean, "ERROR");
				response = new ResponseEntity(msg, new HttpHeaders(), HttpStatus.BAD_REQUEST);
			} else {
				referenceId = portMeDetails.getRequestId();
				portMeDetails.setSource(Integer.parseInt(source));
				_logger.info("Recieved port approval request with requestId-" + requestId);
				portMeTransaction.setRequestId(referenceId);
				current_status = 4;
				portMeTransaction.setStatus(current_status);
				portMeTransaction.setRequestType(reqType);
				if (portMeDetails.getSubscriberArrType().size() > 0) {
					String msisdn = portMeDetails.getSubscriberArrType().get(0).getMsisdn();
					List<SubscriberArrType> subscriberList = subscriberArrTypeService
							.getSubscriberDatailByMsisdnAndRequestType(msisdn, reqType);
					portMeDetails.getSubscriberArrType().clear();
					portMeDetails.getSubscriberArrType().addAll(subscriberList);
				} else {
					int portId = portMeService.getPortIdByRequestId(referenceId, reqType);
					List<SubscriberArrType> subscriberList = subscriberArrTypeService
							.findSubArrByPortIdAndResultCode(portId, 200);
					portMeDetails.getSubscriberArrType().clear();
					portMeDetails.getSubscriberArrType().addAll(subscriberList);
				}
				if (portMeDetails.getSubscriberArrType().size() > 0) {
					area = numberPlanDao.getArea(portMeDetails.getSubscriberArrType().get(0).getMsisdn());
					mch_type = numberPlanDao.getMCHTypeByArea(area);
					requestId = portMeRepository.getSynReqeustId(area);
					for (SubscriberArrType subAuth : portMeDetails.getSubscriberArrType()) {
						portMeService.updatePortAprovalReqeust(current_status, subAuth.getMsisdn(), reqType,
								subAuth.getResultCode());
					}
					portMeTransactionService.savePortMeTransactionDetails(portMeTransaction,
							portMeDetails.getSubscriberArrType());
				}
				// going to convert into xml format
				_logger.info("trying to convert port approval request into xml with requestId-" + requestId);
				String xml = null;
				if (mch_type == 1) {
					xml = new NPOUtils().convertJsonIntoPortApproval(portMeDetails, mch_type, requestId);
				} else {
					String donorLSAID = numberPlanDao
							.getDonorLSAID(portMeDetails.getSubscriberArrType().get(0).getMsisdn());
					if (donorLSAID == null) {
						donorLSAID = area;
					}
					String messageSenderTelco = ReadConfigFile.getProperties().getProperty("MessageSenderTelco-ZOOM");
					String transactionId = portMeService.getTransactionId(messageSenderTelco, area);
					portMeDetails.setLast_area(donorLSAID);
					String routeInfo = numberPlanDao.getRouteInfo(referenceId);
					portMeDetails.setRn(routeInfo);
					xml = new NPOUtils().convertJsonIntoPortApproval(portMeDetails, mch_type, transactionId);
				}
				_logger.info("convert port approval request into xml with requestId-" + requestId + xml);
				int returnvalue = jmsProducer.sendMessageToInQueue(xml, sessionId, mch_type);
				if (returnvalue == 1) {
					_logger.info("Successfully sent portme approval request in jms queue with requestId-" + requestId);
					errorBean.setResponseCode(APIConst.successCode);
					errorBean.setResponseMessage(APIConst.successMsg);
					msg = PortMeUtils.generateJsonResponse(errorBean, "SUCCESS");
					response = new ResponseEntity(msg, new HttpHeaders(), HttpStatus.OK);
				} else {
					_logger.info("Successfully saved port approval data but not able to send jms queue with requestId-"
							+ requestId);
					errorBean.setId(1);
					errorBean.setResponseCode(APIConst.successCode2);
					errorBean.setResponseMessage(APIConst.successMsg2);
					msg = PortMeUtils.generateJsonResponse(errorBean, "SUCCESS");
					response = new ResponseEntity(msg, new HttpHeaders(), HttpStatus.OK);
				}

			}
		} catch (Exception e) {
			_logger.error("Something is wrong port approval request with requestId-" + requestId + e.getMessage());
			portMeTransaction = new PortMeTransactionDetails();
			portMeTransaction.setRequestId(referenceId);
			portMeTransaction.setStatus(19);
			portMeTransaction.setRequestType(reqType);
			portMeTransactionService.savePortMeTransactionDetails(portMeTransaction);
			errorBean.setResponseCode(APIConst.successCode1);
			errorBean.setResponseMessage(APIConst.successMsg1);
			msg = PortMeUtils.generateJsonResponse(errorBean, "ERROR");
			// e.printStackTrace();
			response = new ResponseEntity(msg, new HttpHeaders(), HttpStatus.BAD_REQUEST);
		}
		if (current_status != 0) {
			// inserting current status in port_tx table
			portMeService.updatePortMeStats(current_status, referenceId, user.getUserId(), reqType);
		}
		return response;

	}

	@PostMapping("/api/disconnectionanswer")
	public ResponseEntity<?> sendDisConnectionAnswer(@RequestParam("portDisconnection") String disconnection) {
		String sessionId = Long.toString(System.currentTimeMillis());
		_logger.info("Recieved port disconnection answer request");
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = userService.findUserByUsername(auth.getName());
		PortMeAPIResponse errorBean = new PortMeAPIResponse();
		PortMeTransactionDetails portMeTransaction = new PortMeTransactionDetails();
		ResponseEntity response = null;
		String requestId = null;
		String referenceId = null;
		String msg = null;
		int current_status = 0;
		String reqType = ReadConfigFile.getProperties().getProperty("PORTME_OUT");
		String source = ReadConfigFile.getProperties().getProperty("PORTME_SOURCE");
		PortMe portMeDetails = null;
		int mch_type = 0;
		try {
			portMeDetails = objectMapper.readValue(disconnection, PortMe.class);
			if (portMeDetails == null) {
				_logger.info("Recieved port disconnection answer request is null");
				errorBean.setResponseCode(APIConst.successCode1);
				errorBean.setResponseMessage(APIConst.successMsg1);
				msg = PortMeUtils.generateJsonResponse(errorBean, "ERROR");
				response = new ResponseEntity(msg, new HttpHeaders(), HttpStatus.BAD_REQUEST);
			} else {
				current_status = 15;
				portMeDetails.setSource(Integer.parseInt(source));
				if (portMeDetails.getMsisdnUID().size() > 0) {
					if (portMeDetails.getMsisdnUID().get(0).getMsisdn() != null) {
						String msisdn = portMeDetails.getMsisdnUID().get(0).getMsisdn();
						referenceId = portMeDetails.getMsisdnUID().get(0).getRequestId();
						List<SubscriberArrType> subscriberList = subscriberArrTypeService
								.getSubscriberDatailByMsisdnAndRequestType(msisdn, reqType);
						portMeDetails.getSubscriberArrType().clear();
						portMeDetails.getSubscriberArrType().addAll(subscriberList);
					} else {
						referenceId = portMeDetails.getMsisdnUID().get(0).getRequestId();
						PortMe portMe = portMeService.findByRequestId(referenceId);
						List<SubscriberArrType> subscriberList = subscriberArrTypeService
								.findSubArrByPortIdAndResultCode(portMe.getPortId(), 200);
						portMeDetails.getSubscriberArrType().clear();
						portMeDetails.getSubscriberArrType().addAll(subscriberList);
					}
				}
				portMeDetails.setRequestId(referenceId);
				if (portMeDetails.getSubscriberArrType().size() > 0) {
					for (SubscriberArrType msisdnuid : portMeDetails.getSubscriberArrType()) {
						portMeTransaction = new PortMeTransactionDetails();
						portMeTransaction.setRequestId(referenceId);
						portMeTransaction.setStatus(current_status);
						portMeTransaction.setRequestType(reqType);
						portMeTransaction.setMsisdn(msisdnuid.getMsisdn());
						portMeTransactionService.savePortMeTransactionDetails(portMeTransaction);
						subscriberArrTypeService.updatePortMtStatusByMsisdn(current_status, msisdnuid.getMsisdn(),
								reqType, 200);
					}
					_logger.info("processing port disconnection answer with requestId-" + referenceId);
				}
				// going to convert into xml format
				_logger.info("trying to convert port disconnection answer into xml with requestId-" + referenceId);
				String area = numberPlanDao.getArea(portMeDetails.getSubscriberArrType().get(0).getMsisdn());
				mch_type = numberPlanDao.getMCHTypeByArea(area);
				String xml = null;
				if (mch_type == 1) {
					requestId = portMeRepository.getSynReqeustId(area);
					xml = new NPOUtils().convertJsonIntoPortDisconAns(portMeDetails, mch_type, requestId);
				} else {
					String donorLSAID = numberPlanDao
							.getDonorLSAID(portMeDetails.getSubscriberArrType().get(0).getMsisdn());
					if (donorLSAID == null) {
						donorLSAID = area;
					}
					String messageSenderTelco = ReadConfigFile.getProperties().getProperty("MessageSenderTelco-ZOOM");
					String transactionId = portMeService.getTransactionId(messageSenderTelco, area);
					portMeDetails.setLast_area(donorLSAID);
					String routeInfo = numberPlanDao.getRouteInfo(referenceId);
					portMeDetails.setRn(routeInfo);
					xml = new NPOUtils().convertJsonIntoPortDisconAns(portMeDetails, mch_type, transactionId);
				}
				_logger.info("converted port disconnection answer with requestId-" + requestId + xml);
				int returnvalue = jmsProducer.sendMessageToInQueue(xml, sessionId, mch_type);
				if (returnvalue == 1) {
					_logger.info("sent portme disconnection request in jms queue with requestId-" + requestId);
					_logger.info("processed port disconnection answer with requestId-" + requestId);
					errorBean.setResponseCode(APIConst.successCode);
					errorBean.setResponseMessage(APIConst.successMsg);
					msg = PortMeUtils.generateJsonResponse(errorBean, "SUCCESS");
					response = new ResponseEntity(msg, new HttpHeaders(), HttpStatus.OK);
				} else {
					_logger.info(
							"Successfully saved port disconnection answer data but not able to send jmsqueue with requestId-"
									+ requestId);
					errorBean.setResponseCode(APIConst.successCode2);
					errorBean.setResponseMessage(APIConst.successMsg2);
					msg = PortMeUtils.generateJsonResponse(errorBean, "SUCCESS");
					response = new ResponseEntity(msg, new HttpHeaders(), HttpStatus.OK);
				}
			}
		} catch (Exception e) {
			_logger.error("get error port disconnection answer with requestId-" + requestId + e.getMessage());
			portMeTransaction = new PortMeTransactionDetails();
			portMeTransaction.setRequestId(referenceId);
			portMeTransaction.setStatus(19);
			portMeTransaction.setRequestType(reqType);
			portMeTransactionService.savePortMeTransactionDetails(portMeTransaction);
			errorBean.setResponseCode(APIConst.successCode1);
			errorBean.setResponseMessage(APIConst.successMsg1);
			msg = PortMeUtils.generateJsonResponse(errorBean, "ERROR");
			// e.printStackTrace();
			response = new ResponseEntity(msg, new HttpHeaders(), HttpStatus.BAD_REQUEST);
		}
		if (current_status != 0) {
			// inserting current status in port_tx table
			portMeService.updatePortMeStatus(current_status, referenceId, user.getUserId());
		}
		return response;
	}

	/* start code shobhit */
	@GetMapping(value = "getrn")
	public String getrn(@RequestParam(name = "op_id") String opId, @RequestParam(name = "area") String area) {
		String Rn = portMeDao.getRnbyOpIdandarea(opId, area);
		return Rn;
	}

	@GetMapping(value = "getmsisdn")
	public String getmsisdn(@RequestParam(name = "msisdn") String msisdn) {
		String dno = portMeDao.getdonorbymsisdn(msisdn);
		return dno;
	}

	/* end code shobhit */
}