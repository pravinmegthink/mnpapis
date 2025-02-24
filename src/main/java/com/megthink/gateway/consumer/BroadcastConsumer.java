package com.megthink.gateway.consumer;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import jakarta.jms.JMSException;
import jakarta.jms.Message;
import jakarta.jms.TextMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import com.megthink.gateway.dao.SCNoticeDao;
import com.megthink.gateway.form.BroadcastMapper;
import com.megthink.gateway.model.BCInfoType;
import com.megthink.gateway.model.BroadcastHistory;
import com.megthink.gateway.model.BroadcastStats;
import com.megthink.gateway.model.MasterNP;
import com.megthink.gateway.model.NPCData;
import com.megthink.gateway.model.NumberRangeOAFlagged;
import com.megthink.gateway.model.SCNotice;
import com.megthink.gateway.model.SDNotice;
import com.megthink.gateway.producer.JmsProducer;
import com.megthink.gateway.repository.BroadcastHistoryRepository;
import com.megthink.gateway.repository.MasterNPRepository;
import com.megthink.gateway.service.BroadcastStatsService;
import com.megthink.gateway.service.MasterNPService;
import com.megthink.gateway.service.RecoveryDBService;
import com.megthink.gateway.utils.NPOUtils;
import com.megthink.gateway.utils.ReadConfigFile;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.text.SimpleDateFormat;
import java.util.Date;

@Component
public class BroadcastConsumer {

	private static final Logger _logger = LoggerFactory.getLogger(BroadcastConsumer.class);

	Timestamp timestamp = new Timestamp(System.currentTimeMillis());

	@Autowired
	private MasterNPService masterNPService;
	@Autowired
	private MasterNPRepository masterNPRepository;
	@Autowired
	private SCNoticeDao scNoticeDao;
	@Autowired
	private BroadcastHistoryRepository broadcastHistoryRepository;
	@Autowired
	private JmsProducer jmsProducer;
	@Autowired
	private RecoveryDBService recoveryDBService;
	@Autowired
	private BroadcastStatsService statsService;

	@SuppressWarnings("deprecation")
	//@JmsListener(destination = "Z1BCQUEUE", concurrency = "20") // Broadcast queue
	public void receivedBroadcastZone1(Message message) {
		String sessionId = Long.toString(System.currentTimeMillis());
		// Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		_logger.info("[sessionId=" + sessionId
				+ "]: BroadcastConsumer.receivedBroadcastZone1()-Zone-1 Broadcast Soap Message - process start with timestamp:["
				+ new Timestamp(System.currentTimeMillis()) + "]");
		String requestId = null;
		int success = 0;
		int fail = 0;
		Boolean isReqSuccess = true;
		int incomingCount = 0;
		String messageType = "";
		try {
			TextMessage msg = (TextMessage) message;
			_logger.debug("[sessionId=" + sessionId
					+ "]: BroadcastConsumer.receivedBroadcastZone1()-Recieved Zone-1 Broadcast Message is :["
					+ msg.getText() + "]");
			_logger.debug("[sessionId=" + sessionId
					+ "]: BroadcastConsumer.receivedBroadcastZone1()-1 trying to convert String into json object :["
					+ msg.getText() + "]");

			ObjectMapper mapper = new ObjectMapper();
			JsonNode arrayNode = mapper.readTree(msg.getText().toString());
			if (arrayNode.isArray() && arrayNode.size() > 0) {
				JsonNode firstObject = arrayNode.get(0);
				if (firstObject.has("messageType")) {
					messageType = firstObject.get("messageType").asText();
					System.out.println("messageType: " + messageType);
				}
			}

			_logger.debug("[sessionId=" + sessionId
					+ "]: BroadcastConsumer.receivedBroadcastZone1()-2 converted String into json object :["
					+ msg.getText() + "]");
			String xml = null;
			boolean isDBConnectionLive = true;
			if (messageType.equals(ReadConfigFile.getProperties().getProperty("SCNOTICE_MESSAGE_TYPE"))) {
				_logger.info("[sessionId=" + sessionId
						+ "]: BroadcastConsumer.receivedBroadcastZone1()-SC-NOTICE Soap Message - process start with timestamp:["
						+ new Timestamp(System.currentTimeMillis()) + "]");
				mapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);
				SCNotice[] listOfSCNotices = mapper.readValue(msg.getText().toString(), SCNotice[].class);

				for (SCNotice scNoticeItem : listOfSCNotices) {
					try {
						success = 0;
						fail = 0;
						incomingCount = 0;
						// SCNotice scNotice = objectMapper.readValue(msg.getText(), SCNotice.class);
						String messageSenderTelco = ReadConfigFile.getProperties()
								.getProperty("MessageSenderTelco-ZOOM");
						String messageReceiverTelco = ReadConfigFile.getProperties()
								.getProperty("MessageReceiverTelco-mch1");
						scNoticeItem.setMessageSenderTelco(messageSenderTelco);
						scNoticeItem.setMessageReceiverTelco(messageReceiverTelco);
						try {
							requestId = scNoticeItem.getRequestId();
							List<MasterNP> masterList = new ArrayList<MasterNP>();
							List<BroadcastHistory> broadcastList = new ArrayList<BroadcastHistory>();
							int resetCnt = 0;
							for (BCInfoType bcInfo : scNoticeItem.getBcInfo()) {
								incomingCount++;
								try {
									// we remove country code digit (we define how much remove digit in configure
									// file
									String countryCode = ReadConfigFile.getProperties()
											.getProperty("zone1-countrycode");
									int codeLimit = Integer.parseInt(countryCode);
									String msisdn = bcInfo.getSubscriberNumber().substring(codeLimit);
									_logger.debug("[sessionId=" + sessionId
											+ "]: BroadcastConsumer.receivedBroadcastZone1()-SC-NOTICE msisdn:["
											+ msisdn + "] - going to check if already exist  with timestamp:["
											+ new Timestamp(System.currentTimeMillis()) + "]");
									String originalCarrier = scNoticeDao.getMasterNPOriginalCarrier(msisdn, sessionId);
									_logger.debug("[sessionId=" + sessionId
											+ "]: BroadcastConsumer.receivedBroadcastZone1()-SC-NOTICE msisdn:["
											+ msisdn + "] - checked if already exist  with timestamp:["
											+ new Timestamp(System.currentTimeMillis()) + "]");
									if (originalCarrier != "DBDown") {
										if (originalCarrier != null) {
											_logger.debug("[sessionId=" + sessionId
													+ "]: BroadcastConsumer.receivedBroadcastZone1()-SC-NOTICE msisdn:["
													+ msisdn + "] -  already exist  with timestamp:["
													+ new Timestamp(System.currentTimeMillis()) + "]");
											MasterNP masterNP = new MasterNP();
											masterNP.setRemark(bcInfo.getResultText());
											masterNP.setMsisdn(msisdn);
											masterNP.setArea(scNoticeItem.getLsa());
											masterNP.setService(bcInfo.getServiceType());
											masterNP.setRn(bcInfo.getRouteNumber());
											masterNP.setPresent_carrier(bcInfo.getRno());
											masterNP.setCarrier_history(bcInfo.getDno());
											masterNP.setOrginal_carrier(bcInfo.getNrh());
											masterNP.setActive("Y");
											masterNP.setRe_trans_date(timestamp);
											masterNP.setTransaction_date(bcInfo.getdCTime());
											_logger.debug("[sessionId=" + sessionId
													+ "]: BroadcastConsumer.receivedBroadcastZone1()-SC-NOTICE Soap Message - trying to get master_area with timestamp:["
													+ new Timestamp(System.currentTimeMillis()) + "]");
											String masterArea = scNoticeDao.getMasterNPArea(msisdn, sessionId);
											_logger.debug("[sessionId=" + sessionId
													+ "]: BroadcastConsumer.receivedBroadcastZone1()-SC-NOTICE Soap Message - successfully to get master_area with timestamp:["
													+ new Timestamp(System.currentTimeMillis()) + "]");
											_logger.debug("[sessionId=" + sessionId
													+ "]: BroadcastConsumer.receivedBroadcastZone1()-SC-NOTICE Soap Message - trying to get area with timestamp:["
													+ new Timestamp(System.currentTimeMillis()) + "]");
											String area = scNoticeDao.getArea(msisdn, sessionId);
											_logger.debug("[sessionId=" + sessionId
													+ "]: BroadcastConsumer.receivedBroadcastZone1()-SC-NOTICE Soap Message - successfully to get area with timestamp:["
													+ new Timestamp(System.currentTimeMillis()) + "]");
											if (bcInfo.getbCAction().equals("1")) {
												masterNP.setAction("INS");
												masterNP.setHistory_area(masterArea);
											} else {
												if ((originalCarrier.equals(bcInfo.getRno()))
														&& area.equals(scNoticeItem.getLsa())) {
													// action can be taken from bcAction
													masterNP.setAction("DLT");
													masterNP.setHistory_area(masterArea);
												} else {
													masterNP.setAction("UPD");
													masterNP.setHistory_area(masterArea);
												}
											}

											masterNP.setOriginal_area(area);
											// masterNP.setRequestId(requestId);
											_logger.debug("[sessionId=" + sessionId
													+ "]: BroadcastConsumer.receivedBroadcastZone1()-SC-NOTICE msisdn:["
													+ msisdn + "] - trying to update existing record  with timestamp:["
													+ new Timestamp(System.currentTimeMillis()) + "]");
											int flag = scNoticeDao.updateMasterNP(masterNP, sessionId);
											if (flag == 1) {
												_logger.info("[sessionId=" + sessionId
														+ "]: BroadcastConsumer.receivedBroadcastZone1()-SC-NOTICE msisdn:["
														+ msisdn
														+ "] - updated existing record into master_table_Z1_success_cnt1");
											} else {
												_logger.error("[sessionId=" + sessionId
														+ "]: BroadcastConsumer.receivedBroadcastZone1()-SC-NOTICE msisdn:["
														+ msisdn
														+ "] - fail to update existing record into master_table_Z1_fail_cnt1");
											}
											BroadcastHistory broadcastHistory = BroadcastMapper.mapToForm(masterNP,
													sessionId);
											broadcastHistory.setTransaction_id(scNoticeItem.getBatchId());
											broadcastHistory.setRequestId(requestId);
											broadcastHistory.setMch(1);
											broadcastList.add(broadcastHistory);
										} else {
											MasterNP masterNP = new MasterNP();
											masterNP.setRemark(bcInfo.getResultText());
											masterNP.setMsisdn(msisdn);
											masterNP.setArea(scNoticeItem.getLsa());
											masterNP.setService(bcInfo.getServiceType());
											masterNP.setRn(bcInfo.getRouteNumber());
											masterNP.setPresent_carrier(bcInfo.getRno());
											masterNP.setCarrier_history(bcInfo.getDno());
											masterNP.setOrginal_carrier(bcInfo.getNrh());
											masterNP.setActive("Y");
											masterNP.setRe_trans_date(timestamp);
											masterNP.setTransaction_date(bcInfo.getdCTime());
											masterNP.setAction("INS");
											_logger.debug("[sessionId=" + sessionId
													+ "]: BroadcastConsumer.receivedBroadcastZone1()-SC-NOTICE Soap Message - trying to get area with timestamp:["
													+ new Timestamp(System.currentTimeMillis()) + "]");
											String area = scNoticeDao.getArea(msisdn, sessionId);
											_logger.debug("[sessionId=" + sessionId
													+ "]: BroadcastConsumer.receivedBroadcastZone1()-SC-NOTICE Soap Message - successfully get area with timestamp:["
													+ new Timestamp(System.currentTimeMillis()) + "]");
											masterNP.setOriginal_area(area);
											// masterNP.setRequestId(requestId);
											masterNP.setFirst_trans_date(timestamp);
											BroadcastHistory broadcastHistory = BroadcastMapper.mapToForm(masterNP,
													sessionId);
											broadcastHistory.setTransaction_id(scNoticeItem.getBatchId());
											broadcastHistory.setRequestId(requestId);
											broadcastHistory.setMch(1);
											broadcastList.add(broadcastHistory);
											resetCnt++;
											masterList.add(masterNP);
											if (resetCnt == 10000) {
												_logger.debug("[sessionId=" + sessionId
														+ "]: BroadcastConsumer.receivedBroadcastZone1()-SC-NOTICE Soap Message - trying to store data into master_table with timestamp:["
														+ new Timestamp(System.currentTimeMillis()) + "]");
												masterNPRepository.saveAll(masterList);
												masterNPRepository.flush();
												masterList.clear();
												resetCnt = 0;
												_logger.info("[sessionId=" + sessionId
														+ "]: BroadcastConsumer.receivedBroadcastZone1()-SC-NOTICE Soap Message - successfully to store data into master_table_Z1_success_cnt"
														+ resetCnt);
											}
											if (broadcastList.size() == 10000) {
												_logger.debug("[sessionId=" + sessionId
														+ "]: BroadcastConsumer.receivedBroadcastZone1()-SC-NOTICE Soap Message - trying to store 10000 data into broadcast db with timestamp:["
														+ new Timestamp(System.currentTimeMillis()) + "]");
												broadcastHistoryRepository.saveAll(broadcastList);
												broadcastHistoryRepository.flush();
												broadcastList.clear();
												_logger.info("[sessionId=" + sessionId
														+ "]: BroadcastConsumer.receivedBroadcastZone1()-SC-NOTICE Soap Message - successfully to store 10000 data into broadcast db with timestamp:["
														+ new Timestamp(System.currentTimeMillis()) + "]");
											}
										}
										success++;
									} else {
										_logger.info(
												"[sessionId={}]: BroadcastConsumer.receivedBroadcastZone1() - SC-NOTICE Soap Message - Database is down. Timestamp: [{}]",
												sessionId, new Timestamp(System.currentTimeMillis()));
										isDBConnectionLive = false;
									}
								} catch (Exception e) {
									fail++;
									_logger.error("[sessionId=" + sessionId
											+ "]: BroadcastConsumer.receivedBroadcastZone1()-Exception occurs while processing msisdn:["
											+ bcInfo.getSubscriberNumber() + "]  with timestamp:["
											+ new Timestamp(System.currentTimeMillis()) + "]-error:" + e.getMessage());
									_logger.info("[sessionId=" + sessionId
											+ "]: BroadcastConsumer.receivedBroadcastZone1()-SC-NOTICE Soap Message - fail to store data into master_table_Z1_fail_cnt"
											+ resetCnt);
								}
							}
							if (isDBConnectionLive) {
								if (resetCnt < 10000) {
									_logger.debug("[sessionId=" + sessionId
											+ "]: BroadcastConsumer.receivedBroadcastZone1()-SC-NOTICE Soap Message - trying to store data into master_table db with timestamp:["
											+ new Timestamp(System.currentTimeMillis()) + "]");
									masterNPRepository.saveAll(masterList);
									masterList.clear();
									_logger.info("[sessionId=" + sessionId
											+ "]: BroadcastConsumer.receivedBroadcastZone1()-SC-NOTICE Soap Message - successfully to store data into master_table_Z1_success_cnt"
											+ resetCnt);
								}
								if (broadcastList.size() < 10000) {
									_logger.debug("[sessionId=" + sessionId
											+ "]: BroadcastConsumer.receivedBroadcastZone1()-SC-NOTICE Soap Message - trying to store data into broadcasthistory db with timestamp:["
											+ new Timestamp(System.currentTimeMillis()) + "]");
									broadcastHistoryRepository.saveAll(broadcastList);
									broadcastList.clear();
									_logger.debug("[sessionId=" + sessionId
											+ "]: BroadcastConsumer.receivedBroadcastZone1()-SC-NOTICE Soap Message - successfully to store data into broadcasthistory db with timestamp:["
											+ new Timestamp(System.currentTimeMillis()) + "]");
								}
							}

						} catch (Exception e) {
							isReqSuccess = false;
							_logger.error("[sessionId=" + sessionId
									+ "]: BroadcastConsumer.receivedBroadcastZone1()-SC-NOTICE Soap Message Error- exception occurs during process sc-notice error is :"
									+ e);
						}
						if (isReqSuccess && isDBConnectionLive) {
							_logger.debug("[sessionId=" + sessionId
									+ "]: BroadcastConsumer.receivedBroadcastZone1()-SC-NOTICE Soap Message - successfully store data into db with timestamp:["
									+ new Timestamp(System.currentTimeMillis()) + "]");
							String getRequestId = recoveryDBService.getRequestId(messageSenderTelco);
							String[] splitRequestId = getRequestId.split("[,]", 0);
							String reqId = splitRequestId[0];
							_logger.debug("[sessionId=" + sessionId
									+ "]: BroadcastConsumer.receivedBroadcastZone1()-SC-NOTICE Soap Message - trying to convert into SC-NOTICE-ANS with timestamp:["
									+ new Timestamp(System.currentTimeMillis()) + "]");
							xml = new NPOUtils().generateSCNoticeAnswer(scNoticeItem, success, fail, reqId, sessionId);
							_logger.debug("[sessionId=" + sessionId
									+ "]: BroadcastConsumer.receivedBroadcastZone1()-successfully converted into SC-NOTICE-ANS xml :["
									+ xml + "] with timestamp:[" + new Timestamp(System.currentTimeMillis()) + "]");
							int interval = jmsProducer.sentSCNoticeAnswerIntoInQueue(xml, sessionId, "1");
							if (interval == 1) {
								success = 0;
								fail = 0;
								incomingCount = 0;
								_logger.info("[sessionId=" + sessionId
										+ "]: BroadcastConsumer.receivedBroadcastZone1()-SC-NOTICE-ANS XML - Successfully submited into activemq with timestamp:["
										+ new Timestamp(System.currentTimeMillis()) + "]");
							} else {
								_logger.error("[sessionId=" + sessionId
										+ "]: BroadcastConsumer.receivedBroadcastZone1()-SC-NOTICE-ANS XML - Fail to submite into activemq with timestamp:["
										+ new Timestamp(System.currentTimeMillis()) + "]");
							}
						} else {
							_logger.error("[sessionId=" + sessionId
									+ "]: BroadcastConsumer.receivedBroadcastZone1()-Zone-1 Broadcast Soap Message - failed to process sc-notice with timestamp:["
									+ new Timestamp(System.currentTimeMillis()) + "]");
						}
					} catch (Exception e) {
						_logger.error("[sessionId=" + sessionId
								+ "]: BroadcastConsumer.receivedBroadcastZone1()-Zone-1 Broadcast Soap Message - failed to process sc-notice with error:["
								+ e.getMessage() + "]");
					}
				}
			}
			/* start code for SDNotice */
			else if (messageType.equals(ReadConfigFile.getProperties().getProperty("SDNOTICE_MESSAGE_TYPE"))) {
				_logger.info("[sessionId=" + sessionId
						+ "]: BroadcastConsumer.receivedBroadcastZone1()-SD-NOTICE Soap Message - process start with timestamp:["
						+ new Timestamp(System.currentTimeMillis()) + "]");
				mapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);
				SDNotice[] listOfSDNotices = mapper.readValue(msg.getText().toString(), SDNotice[].class);

				for (SDNotice sdNoticeItem : listOfSDNotices) {
					try {
						success = 0;
						fail = 0;
						incomingCount = 0;
						requestId = sdNoticeItem.getRequestId();
						String messageSenderTelco = ReadConfigFile.getProperties()
								.getProperty("MessageSenderTelco-ZOOM");
						String messageReceiverTelco = ReadConfigFile.getProperties()
								.getProperty("MessageReceiverTelco-mch1");
						sdNoticeItem.setMessageSenderTelco(messageSenderTelco);
						sdNoticeItem.setMessageReceiverTelco(messageReceiverTelco);
						List<BroadcastHistory> broadcastList = new ArrayList<BroadcastHistory>();
						try {
							for (BCInfoType bcInfo : sdNoticeItem.getBcInfo()) {
								incomingCount++;
								try {
									// we remove country code digit (we define how much remove digit in configure
									// file
									String countryCode = ReadConfigFile.getProperties()
											.getProperty("zone1-countrycode");
									int codeLimit = Integer.parseInt(countryCode);
									String msisdn = bcInfo.getSubscriberNumber().substring(codeLimit);
									MasterNP masterNP = new MasterNP();
									masterNP.setRemark(bcInfo.getResultText());
									masterNP.setMsisdn(msisdn);
									masterNP.setArea(sdNoticeItem.getLsa());
									masterNP.setService(bcInfo.getServiceType());
									masterNP.setRn(bcInfo.getRouteNumber());
									masterNP.setPresent_carrier(bcInfo.getRno());
									masterNP.setCarrier_history(bcInfo.getDno());
									String masterArea = scNoticeDao.getMasterNPArea(msisdn, sessionId);
									if (masterArea != "DBDown") {
										masterNP.setHistory_area(masterArea);
										masterNP.setOrginal_carrier(bcInfo.getRno());
										masterNP.setActive("N");
										masterNP.setTransaction_date(bcInfo.getdCTime());
										masterNP.setDisconnection_date(bcInfo.getdCTime());
										masterNP.setRe_trans_date(timestamp);
										masterNP.setAction("DLT");
										masterNP.setOriginal_area(sdNoticeItem.getLsa());
										// masterNP.setRequestId(requestId);
										int flag = 0;
										try {
											flag = scNoticeDao.updateSDMasterNP(masterNP, sessionId);
										} catch (Exception e) {
											// add flag for retry message
											// read the configurable file for number of retry and set into message type
											// for
											// retry queue
											// in retry consumer if exception re-occurs keep putting -1 till it get zero
											_logger.error("[sessionId=" + sessionId
													+ "]: BroadcastConsumer.receivedBroadcastZone1()-Zone-1 Broadcast Soap Message - DB Exception occurs while proccess message with timestamp:["
													+ new Timestamp(System.currentTimeMillis()) + "], error is :" + e);
											String retryCount = ReadConfigFile.getProperties()
													.getProperty("retry_queue_count");
											int ackId = jmsProducer.sendZone1MessageRetry(msg.getText().toString(),
													sessionId, retryCount);
											if (ackId == 1) {
												_logger.info("[sessionId=" + sessionId
														+ "]: BroadcastConsumer.receivedBroadcastZone1() - Successfully submited retry sd-notice message into retryIncomingQueueZ1 activemq with timestamp:["
														+ new Timestamp(System.currentTimeMillis()) + "]");
											} else {
												_logger.error("[sessionId=" + sessionId
														+ "]: BroadcastConsumer.receivedBroadcastZone1() - Failed to submit retry sd-notice message into retryIncomingQueueZ1 activemq with timestamp:["
														+ new Timestamp(System.currentTimeMillis()) + "]");
											}
										}
										if (flag == 1) {
											_logger.info("[sessionId=" + sessionId
													+ "]: BroadcastConsumer.receivedBroadcastZone1()-SD-NOTICE msisdn:["
													+ msisdn
													+ "] - updated existing record into master_table_Z1_success_cnt1");
										} else {
											_logger.error("[sessionId=" + sessionId
													+ "]: BroadcastConsumer.receivedBroadcastZone1()-SD-NOTICE msisdn:["
													+ msisdn
													+ "] - fail to update existing record into master_table_Z1_fail_cnt1");
										}
										BroadcastHistory broadcastHistory = BroadcastMapper.mapToForm(masterNP,
												sessionId);
										broadcastHistory.setFirst_trans_date(timestamp);
										broadcastHistory.setDisconnection_date(bcInfo.getdCTime());
										broadcastHistory.setTransaction_id(sdNoticeItem.getBatchId());
										broadcastHistory.setRequestId(requestId);
										broadcastHistory.setMch(1);
										broadcastList.add(broadcastHistory);
										if (broadcastList.size() == 100000) {
											broadcastHistoryRepository.saveAll(broadcastList);
											broadcastHistoryRepository.flush();
											broadcastList.clear();
										}
										success++;
									} else {
										_logger.info(
												"[sessionId={}]: BroadcastConsumer.receivedBroadcastZone1() - SD-NOTICE Soap Message - Database is down. Timestamp: [{}]",
												sessionId, new Timestamp(System.currentTimeMillis()));
										isDBConnectionLive = false;
									}
								} catch (Exception e) {
									fail++;
									_logger.error("[sessionId=" + sessionId
											+ "]: BroadcastConsumer.receivedBroadcastZone1()-SD-NOTICE Soap Message Error- exception occurs during process sd-notice error is :"
											+ e);
								}
							}
							if (isDBConnectionLive) {
								if (broadcastList.size() < 100000) {
									broadcastHistoryRepository.saveAll(broadcastList);
									broadcastList.clear();
								}
							}
						} catch (Exception e) {
							isReqSuccess = false;
							_logger.error("[sessionId=" + sessionId
									+ "]: BroadcastConsumer.receivedBroadcastZone1()-SD-NOTICE Soap Message Error- exception occurs during process sd-notice error is :"
									+ e);
						}
						if (isReqSuccess && isDBConnectionLive) {
							_logger.info("[sessionId=" + sessionId
									+ "]: BroadcastConsumer.receivedBroadcastZone1()-SD-NOTICE Soap Message - successfully store data into history db with timestamp:["
									+ new Timestamp(System.currentTimeMillis()) + "]");
							String getRequestId = recoveryDBService.getRequestId(messageSenderTelco);
							_logger.debug("[sessionId=" + sessionId
									+ "]: BroadcastConsumer.receivedBroadcastZone1()-SD-NOTICE Soap Message - trying to convert into SC-NOTICE-ANS with timestamp:["
									+ new Timestamp(System.currentTimeMillis()) + "]");
							xml = new NPOUtils().generateSDNoticeAnswer(sdNoticeItem, success, fail, getRequestId);
							_logger.debug("[sessionId=" + sessionId
									+ "]: BroadcastConsumer.receivedBroadcastZone1()-successfully converted into SD-NOTICE-ANS xml :["
									+ xml + "] with timestamp:[" + new Timestamp(System.currentTimeMillis()) + "]");
							int interval = jmsProducer.sentSDNoticeAnswerIntoInQueue(xml, sessionId, "1");
							if (interval == 1) {
								success = 0;
								fail = 0;
								incomingCount = 0;
								_logger.info("[sessionId=" + sessionId
										+ "]: BroadcastConsumer.receivedBroadcastZone1()-SD-NOTICE-ANS XML - Successfully submited into activemq with timestamp:["
										+ new Timestamp(System.currentTimeMillis()) + "]");
							} else {
								_logger.error("[sessionId=" + sessionId
										+ "]: BroadcastConsumer.receivedBroadcastZone1()-SD-NOTICE-ANS XML - Fail to submite into activemq with timestamp:["
										+ new Timestamp(System.currentTimeMillis()) + "]");
							}
						}
					} catch (Exception e) {
						_logger.error("[sessionId=" + sessionId
								+ "]: BroadcastConsumer.receivedBroadcastZone1()-Zone-1 Broadcast Soap Message - failed to process sd-notice with error:["
								+ e.getMessage() + "]");
					}
				}
			} else {
				_logger.error("[sessionId=" + sessionId
						+ "]: BroadcastConsumer.receivedBroadcastZone1()-SD-NOTICE-ANS XML - Failed to process sd-notice with timestamp:["
						+ new Timestamp(System.currentTimeMillis()) + "]");
			}
			_logger.info("[sessionId=" + sessionId
					+ "]: BroadcastConsumer.receivedBroadcastZone1()-Zone-1 Broadcast Soap Message - process end with timestamp:["
					+ new Timestamp(System.currentTimeMillis()) + "]");
			BroadcastStats statsCount = new BroadcastStats();
			statsCount.setId(generateRandomNumber());
			statsCount.setCount(incomingCount);
			statsCount.setMch("Zone 1");
			statsService.saveBroadcastStats(statsCount);
			// } else { comment because we change code
			if (!isDBConnectionLive) {
				// write code for retry mechanism

				// add flag for retry message
				// read the configurable file for number of retry and set into message type for
				// retry queue
				// in retry consumer if exception re-occurs keep putting -1 till it get zero
				_logger.error("[sessionId=" + sessionId
						+ "]: BroadcastConsumer.receivedBroadcastZone1()-Zone-1 Broadcast Soap Message - DB Exception occurs while proccess message with timestamp:["
						+ new Timestamp(System.currentTimeMillis()) + "]");
				String retryCount = ReadConfigFile.getProperties().getProperty("retry_queue_count");
				int ackId = jmsProducer.sendZone1MessageRetry(msg.getText().toString(), sessionId, retryCount);
				if (ackId == 1) {
					_logger.info("[sessionId=" + sessionId
							+ "]: BroadcastConsumer.receivedBroadcastZone1() - Successfully submited retry Broadcast message into retryIncomingQueueZ1 activemq with timestamp:["
							+ new Timestamp(System.currentTimeMillis()) + "]");
				} else {
					_logger.error("[sessionId=" + sessionId
							+ "]: BroadcastConsumer.receivedBroadcastZone1() - Failed to submit retry Broadcast message into retryIncomingQueueZ1 activemq with timestamp:["
							+ new Timestamp(System.currentTimeMillis()) + "]");
				}
			}
		} catch (

		JMSException e) {
			_logger.error("[sessionId=" + sessionId
					+ "]: BroadcastConsumer.receivedBroadcastZone1()-Zone-1 Broadcast Soap Message - Exception occurs whitle processing with timestamp:["
					+ new Timestamp(System.currentTimeMillis()) + "], error is :" + e);
		} catch (JsonProcessingException e) {
			_logger.error("[sessionId=" + sessionId
					+ "]: BroadcastConsumer.receivedBroadcastZone1()-Zone-1 Broadcast Soap Message - Exception occurs whitle processing with timestamp:["
					+ new Timestamp(System.currentTimeMillis()) + "], error is :" + e);
		}
	}

	@SuppressWarnings("deprecation")
	//@JmsListener(destination = "Z2BCQUEUE", concurrency = "20") // NPCMessageData Broadcast queue this is zone2
	public void receiveNPCMessageData(Message message) {
		String sessionId = Long.toString(System.currentTimeMillis());
		_logger.info("[sessionId=" + sessionId
				+ "]: BroadcastConsumer.receiveNPCMessageData()-Zone-2 Broadcast Soap Message - process start with timestamp:["
				+ new Timestamp(System.currentTimeMillis()) + "]");
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
		Boolean isReqSuccess = true;
		int incomingCount = 0;
		try {
			TextMessage msg = (TextMessage) message;
			_logger.debug("[sessionId=" + sessionId
					+ "]: BroadcastConsumer.receiveNPCMessageData()-Recieved Zone-2 Broadcast Message is :["
					+ msg.getText() + "]");
			ObjectMapper objectMapper = new ObjectMapper();
			objectMapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);
			NPCData npcData = objectMapper.readValue(msg.getText(), NPCData.class);
			String requestId = npcData.getMessageHeader().getTransactionID();
			_logger.debug("[sessionId=" + sessionId
					+ "]: BroadcastConsumer.receiveNPCMessageData()-Zone-2 NPCMessageData Soap Message - process start with timestamp:["
					+ new Timestamp(System.currentTimeMillis()) + "]");
			boolean isDBConnectionLive = true;
			MasterNP masterNP = new MasterNP();
			if (npcData.getMessageHeader().getMessageID() == 1010) {
				incomingCount++;
				List<BroadcastHistory> broadcastList = new ArrayList<BroadcastHistory>();
				_logger.info("[sessionId=" + sessionId
						+ "]: BroadcastConsumer.receiveNPCMessageData()-NPCMessageData Soap Message - process start of messageId: ["
						+ npcData.getMessageHeader().getMessageID() + "] with timestamp:["
						+ new Timestamp(System.currentTimeMillis()) + "]");
				try {
					String comments = npcData.getNPCMessage().getPortActivatedBroadcast().getComments();
					masterNP.setRemark(comments);
					for (NumberRangeOAFlagged numberRangeOAFlagged : npcData.getNPCMessage().getPortActivatedBroadcast()
							.getNumberRangeOAFlagged()) {

						String numberFrom = numberRangeOAFlagged.getNumberFrom();
						// we remove country code digit (we define how much remove digit in configure
						// file
						String countryCode = ReadConfigFile.getProperties().getProperty("zone2-countrycode");
						int codeLimit = Integer.parseInt(countryCode);
						String msisdn = numberFrom.substring(codeLimit);
						masterNP.setMsisdn(msisdn);
						String areaSplit = null;
						try {
							areaSplit = comments.substring(0, 2);
							masterNP.setArea(areaSplit);
						} catch (Exception e) {
							_logger.error("[sessionId=" + sessionId
									+ "]: BroadcastConsumer.receiveNPCMessageData()-exception occur while getting area from comments string with timestamp:["
									+ new Timestamp(System.currentTimeMillis()) + "]");
						}
						masterNP.setPresent_carrier(npcData.getNPCMessage().getPortActivatedBroadcast().getRecipient());
						masterNP.setCarrier_history(npcData.getNPCMessage().getPortActivatedBroadcast().getDonor());
						_logger.debug("[sessionId=" + sessionId
								+ "]: BroadcastConsumer.receiveNPCMessageData()-Trying to get operator_id for NPCMessageData 1010 with timestamp:["
								+ new Timestamp(System.currentTimeMillis()) + "]");
						String opId = scNoticeDao.getOperatorIdByMsisdn(msisdn);
						if (opId != "DBDown") {
							_logger.debug("[sessionId=" + sessionId
									+ "]: BroadcastConsumer.receiveNPCMessageData()-get operator_id : [" + opId
									+ "] for NPCMessageData 1010 with timestamp:["
									+ new Timestamp(System.currentTimeMillis()) + "]");
							masterNP.setOrginal_carrier(opId);
							masterNP.setActive("Y");
							masterNP.setRn(npcData.getNPCMessage().getPortActivatedBroadcast().getRoute());
							Date parsedDate = dateFormat.parse(npcData.getMessageHeader().getMsgCreateTimeStamp());
							Timestamp timestamps = new Timestamp(parsedDate.getTime());
							masterNP.setTransaction_date(timestamps);
							masterNP.setOriginal_area(npcData.getMessageHeader().getlSAID());
							_logger.debug("[sessionId=" + sessionId
									+ "]: BroadcastConsumer.receiveNPCMessageData()-Trying to get master_area for NPCMessageData 1010 with timestamp:["
									+ new Timestamp(System.currentTimeMillis()) + "]");
							MasterNP masterItem = masterNPService.findByMsisdn(msisdn);
							_logger.debug("[sessionId=" + sessionId
									+ "]: BroadcastConsumer.receiveNPCMessageData()-successfully get master_area for NPCMessageData 1010 with timestamp:["
									+ new Timestamp(System.currentTimeMillis()) + "]");
							if (masterItem != null) {
								if (masterItem.getAction().equals("DLT")) {
									masterNP.setAction("INS");
									masterNP.setHistory_area(masterItem.getArea());
								} else {
									if (numberRangeOAFlagged.getOrigAssigneeFlag().equalsIgnoreCase("Y")) {
										masterNP.setAction("DLT");
										masterNP.setHistory_area(masterItem.getArea());
									} else {
										masterNP.setAction("UPD");
										masterNP.setHistory_area(masterItem.getArea());
									}
								}
								_logger.debug("[sessionId=" + sessionId
										+ "]: BroadcastConsumer.receiveNPCMessageData()-Trying to update master db for NPCMessageData 1010 with timestamp:["
										+ new Timestamp(System.currentTimeMillis()) + "]");
								int flag = scNoticeDao.updateMasterNPFor1010Zone2(masterNP, sessionId);
								if (flag == 1) {
									_logger.info("[sessionId=" + sessionId
											+ "]:  BroadcastConsumer.receiveNPCMessageData()-NPCMessageData 1010 msisdn:["
											+ msisdn
											+ "] - successfully update existing record into master_table_Z2_success_cnt1");
								} else {
									_logger.error("[sessionId=" + sessionId
											+ "]:  BroadcastConsumer.receiveNPCMessageData()-NPCMessageData 1010 msisdn:["
											+ msisdn
											+ "] - failed to update existing record into master_table_Z2_fail_cnt1");
								}
							} else {
								masterNP.setAction("INS");
								// String area = scNoticeDao.getArea(msisdn, sessionId);
								masterNP.setOriginal_area(npcData.getMessageHeader().getlSAID());
								masterNP.setFirst_trans_date(timestamps);
								masterNP.setRe_trans_date(timestamp);
								_logger.debug("[sessionId=" + sessionId
										+ "]: BroadcastConsumer.receiveNPCMessageData()-NPCMessageData 1010 msisdn:["
										+ msisdn + "] - trying to insert into master db  with timestamp:["
										+ new Timestamp(System.currentTimeMillis()) + "]");
								masterNP = masterNPService.saveMasterNP(masterNP);
								_logger.info("[sessionId=" + sessionId
										+ "]: BroadcastConsumer.receiveNPCMessageData()-NPCMessageData 1010 msisdn:["
										+ msisdn + "] - successfully inserted into master_table_Z2_success_cnt1");
							}
							BroadcastHistory broadcastHistory = BroadcastMapper.mapToForm(masterNP, sessionId);
							broadcastHistory.setTransaction_id(requestId);
							broadcastHistory.setRn(npcData.getNPCMessage().getPortActivatedBroadcast().getRoute());
							broadcastHistory.setRequestId(requestId);
							broadcastHistory.setMch(2);
							broadcastList.add(broadcastHistory);
							_logger.debug("[sessionId=" + sessionId
									+ "]: BroadcastConsumer.receiveNPCMessageData()-NPCMessageData 1010 msisdn:["
									+ msisdn + "] - trying to insert into history db  with timestamp:["
									+ new Timestamp(System.currentTimeMillis()) + "]");
							broadcastHistoryRepository.saveAll(broadcastList);
							broadcastHistoryRepository.flush();
							_logger.debug("[sessionId=" + sessionId
									+ "]: BroadcastConsumer.receiveNPCMessageData()-NPCMessageData 1010 msisdn:["
									+ msisdn + "] - successfully inserted into history db  with timestamp:["
									+ new Timestamp(System.currentTimeMillis()) + "]");
							broadcastList.clear();
							_logger.info("[sessionId=" + sessionId
									+ "]: BroadcastConsumer.receiveNPCMessageData()-NPCMessageData Soap Message - process end of messageId: ["
									+ npcData.getMessageHeader().getMessageID() + "] with timestamp:["
									+ new Timestamp(System.currentTimeMillis()) + "]");
						} else {
							_logger.info(
									"[sessionId={}]: BroadcastConsumer.receiveNPCMessageData() - NPCMessageData Soap Message - Database is down. Timestamp: [{}]",
									sessionId, new Timestamp(System.currentTimeMillis()));
							isDBConnectionLive = false;
						}
					}
				} catch (Exception e) {
					isReqSuccess = false;
					_logger.error("[sessionId=" + sessionId
							+ "]: BroadcastConsumer.receiveNPCMessageData()-NPCMessageData Soap Message - exception occurs during process messageId: ["
							+ npcData.getMessageHeader().getMessageID() + "] , error is :" + e);
				}
				if (isDBConnectionLive) {
					BroadcastStats statsCount = new BroadcastStats();
					statsCount.setId(generateRandomNumber());
					statsCount.setCount(incomingCount);
					statsCount.setMch("Zone 2");
					statsService.saveBroadcastStats(statsCount);
				}
			} else if (npcData.getMessageHeader().getMessageID() == 5004) {
				incomingCount++;
				_logger.info("[sessionId=" + sessionId
						+ "]: BroadcastConsumer.receiveNPCMessageData()-NPCMessageData Soap Message - process start of messageId: ["
						+ npcData.getMessageHeader().getMessageID() + "] with timestamp:["
						+ new Timestamp(System.currentTimeMillis()) + "]");
				try {
					List<BroadcastHistory> broadcastList = new ArrayList<BroadcastHistory>();
					String comments = npcData.getNPCMessage().getNumReturnBroadcast().getComments();
					masterNP.setRemark(comments);
					for (NumberRangeOAFlagged numberRangeOAFlagged : npcData.getNPCMessage().getNumReturnBroadcast()
							.getNumberRangeOAFlagged()) {
						// String numberFrom =
						// npcData.getNPCMessage().getNumReturnBroadcast().getNumberRangeOAFlagged()
						// .getNumberFrom();
						String numberFrom = numberRangeOAFlagged.getNumberFrom();
						// we remove country code digit (we define how much remove digit in configure
						// file
						String countryCode = ReadConfigFile.getProperties().getProperty("zone2-countrycode");
						int codeLimit = Integer.parseInt(countryCode);
						String msisdn = numberFrom.substring(codeLimit);
						masterNP.setMsisdn(msisdn);
						String areaSpli = comments.substring(0, 2);
						masterNP.setArea(areaSpli);
						masterNP.setRn(npcData.getNPCMessage().getNumReturnBroadcast().getRoute());
						masterNP.setPresent_carrier(
								npcData.getNPCMessage().getNumReturnBroadcast().getOriginalAssignee());
						masterNP.setCarrier_history(npcData.getNPCMessage().getNumReturnBroadcast().getLastRecipient());
						masterNP.setActive("N");
						// need to add this
						_logger.debug("[sessionId=" + sessionId
								+ "]: BroadcastConsumer.receiveNPCMessageData()-NPCMessageData 5004 msisdn:[" + msisdn
								+ "] - trying to get master area from master db  with timestamp:["
								+ new Timestamp(System.currentTimeMillis()) + "]");
						String masterArea = scNoticeDao.getMasterNPArea(msisdn, sessionId);
						if (masterArea != "DBDown") {
							_logger.debug("[sessionId=" + sessionId
									+ "]: BroadcastConsumer.receiveNPCMessageData()-NPCMessageData 5004 msisdn:["
									+ msisdn + "] - successfully get master area from master db  with timestamp:["
									+ new Timestamp(System.currentTimeMillis()) + "]");
							masterNP.setHistory_area(masterArea);
							masterNP.setOrginal_carrier(
									npcData.getNPCMessage().getNumReturnBroadcast().getOriginalAssignee());
							Date parsedDate = dateFormat.parse(npcData.getMessageHeader().getMsgCreateTimeStamp());
							Timestamp timestamps = new Timestamp(parsedDate.getTime());
							masterNP.setDisconnection_date(timestamps);
							masterNP.setRe_trans_date(timestamp);
							masterNP.setOriginal_area(npcData.getMessageHeader().getlSAID());
							masterNP.setTransaction_date(timestamps);
							masterNP.setAction("DLT");
							_logger.debug("[sessionId=" + sessionId
									+ "]: BroadcastConsumer.receiveNPCMessageData()-NPCMessageData Soap Message - trying to updated data with timestamp:["
									+ new Timestamp(System.currentTimeMillis()) + "]");
							int flag = scNoticeDao.updateMasterNPFor5004Zone2(masterNP, sessionId);
							if (flag == 1) {
								_logger.info("[sessionId=" + sessionId
										+ "]: BroadcastConsumer.receiveNPCMessageData()-NPCMessageData 5004 msisdn:["
										+ msisdn + "] - updated existing record into master_table_Z2_success_cnt1");
							} else {
								_logger.error("[sessionId=" + sessionId
										+ "]: BroadcastConsumer.receiveNPCMessageData()-NPCMessageData 5004 msisdn:["
										+ msisdn + "] - fail to update existing record into master_table_Z2_fail_cnt1");
							}
							BroadcastHistory broadcastHistory = BroadcastMapper.mapToForm(masterNP, sessionId);
							broadcastHistory.setTransaction_id(requestId);
							broadcastHistory.setRequestId(requestId);
							broadcastHistory.setDisconnection_date(timestamps);
							broadcastHistory.setMch(2);
							broadcastList.add(broadcastHistory);
							_logger.debug("[sessionId=" + sessionId
									+ "]: BroadcastConsumer.receiveNPCMessageData()-NPCMessageData 5004 msisdn:["
									+ msisdn + "] - start to insert into history db  with timestamp:["
									+ new Timestamp(System.currentTimeMillis()) + "]");
							broadcastHistoryRepository.saveAll(broadcastList);
							broadcastHistoryRepository.flush();
							_logger.debug("[sessionId=" + sessionId
									+ "]: BroadcastConsumer.receiveNPCMessageData()-NPCMessageData 5004 msisdn:["
									+ msisdn + "] - successfully to insert into history db  with timestamp:["
									+ new Timestamp(System.currentTimeMillis()) + "]");
							broadcastList.clear();
							_logger.info("[sessionId=" + sessionId
									+ "]: BroadcastConsumer.receiveNPCMessageData()-NPCMessageData Soap Message - process end of messageId: ["
									+ npcData.getMessageHeader().getMessageID() + "] with timestamp:["
									+ new Timestamp(System.currentTimeMillis()) + "]");
						} else {
							_logger.info(
									"[sessionId={}]: BroadcastConsumer.receiveNPCMessageData() - NPCMessageData Soap Message - Database is down. Timestamp: [{}]",
									sessionId, new Timestamp(System.currentTimeMillis()));
							isDBConnectionLive = false;
						}
					}
				} catch (Exception e) {
					isReqSuccess = false;
					_logger.error("[sessionId=" + sessionId
							+ "]: BroadcastConsumer.receiveNPCMessageData()-NPCMessageData Soap Message - exception occurs during process messageId: ["
							+ npcData.getMessageHeader().getMessageID() + "] , error is :" + e);
				}
				if (isDBConnectionLive) {
					BroadcastStats statsCount = new BroadcastStats();
					statsCount.setId(generateRandomNumber());
					statsCount.setCount(incomingCount);
					statsCount.setMch("Zone 2");
					statsService.saveBroadcastStats(statsCount);
				}
			} else if (npcData.getMessageHeader().getMessageID() == 6002) {
				_logger.info("[sessionId=" + sessionId
						+ "]: BroadcastConsumer.receiveNPCMessageData()-NPCMessageData Soap Message - process start of messageId: ["
						+ npcData.getMessageHeader().getMessageID() + "] with timestamp:["
						+ new Timestamp(System.currentTimeMillis()) + "]");
				try {
					String path = npcData.getNPCMessage().getSynchronisationResponse().getLocation();
					String fileName = npcData.getNPCMessage().getSynchronisationResponse().getContactDetails();
					int flag = recoveryDBService.updateSynchronisationResponse(path, fileName, requestId, sessionId);

					if (flag == 1) {
						_logger.info("[sessionId=" + sessionId
								+ "]: BroadcastConsumer.receiveNPCMessageData()-NPCMessageData synchronisationResponse [6002] - updated response  with timestamp:["
								+ new Timestamp(System.currentTimeMillis()) + "]");
					} else {
						_logger.error("[sessionId=" + sessionId
								+ "]: BroadcastConsumer.receiveNPCMessageData()-NPCMessageData synchronisationResponse [6002] - fail to update response  with timestamp:["
								+ new Timestamp(System.currentTimeMillis()) + "]");
					}
				} catch (Exception e) {
					isReqSuccess = false;
					_logger.error("[sessionId=" + sessionId
							+ "]: BroadcastConsumer.receiveNPCMessageData()-NPCMessageData Soap Message - exception occurs during process messageId: ["
							+ npcData.getMessageHeader().getMessageID() + "] , error is :" + e);
				}
			}
			if (isReqSuccess) {
				_logger.info("[sessionId=" + sessionId
						+ "]: BroadcastConsumer.receiveNPCMessageData()-NPCMessageData - Successfully to process NPCMessageData with timestamp:["
						+ new Timestamp(System.currentTimeMillis()) + "]");
			} else {
				_logger.error("[sessionId=" + sessionId
						+ "]: BroadcastConsumer.receiveNPCMessageData()-SD-NPCMessageData - Failed to process NPCMessageData with timestamp:["
						+ new Timestamp(System.currentTimeMillis()) + "]");
			}
			_logger.info("[sessionId=" + sessionId
					+ "]: BroadcastConsumer.receiveNPCMessageData()-NPCMessageData Soap Message - process end of messageId: ["
					+ npcData.getMessageHeader().getMessageID() + "] with timestamp:["
					+ new Timestamp(System.currentTimeMillis()) + "]");
			// } else { comment because change code for retry code
			if (!isDBConnectionLive) {
				// add flag for retry message
				// read the configurable file for number of retry and set into message type for
				// retry queue
				// in retry consumer if exception re-occurs keep putting -1 till it get zero
				_logger.error("[sessionId=" + sessionId
						+ "]: BroadcastConsumer.receivedBroadcastZone2()-Zone-2 Broadcast Soap Message - DB Exception occurs while proccess message with timestamp:["
						+ new Timestamp(System.currentTimeMillis()) + "]");
				String retryCount = ReadConfigFile.getProperties().getProperty("retry_queue_count");
				int ackId = jmsProducer.sendZone2MessageRetry(msg.getText().toString(), sessionId, retryCount);
				if (ackId == 1) {
					_logger.info("[sessionId=" + sessionId
							+ "]: BroadcastConsumer.receivedBroadcastZone2() - Successfully submited retry Broadcas message into retryIncomingQueueZ2 activemq with timestamp:["
							+ new Timestamp(System.currentTimeMillis()) + "]");
				} else {
					_logger.error("[sessionId=" + sessionId
							+ "]: BroadcastConsumer.receivedBroadcastZone1() - Failed to submit retry Broadcas message into retryIncomingQueueZ2 activemq with timestamp:["
							+ new Timestamp(System.currentTimeMillis()) + "]");
				}
			}
		} catch (

		Exception e) {
			_logger.error("[sessionId=" + sessionId
					+ "]: BroadcastConsumer.receiveNPCMessageData()-NPCMessageData Soap Message - Exception occurs while processing with timestamp:["
					+ new Timestamp(System.currentTimeMillis()) + "], error is :" + e);
		}
		_logger.info("[sessionId=" + sessionId
				+ "]: BroadcastConsumer.receiveNPCMessageData()-Zone-2 Broadcast Soap Message - process end with timestamp:["
				+ new Timestamp(System.currentTimeMillis()) + "]");
	}

	/* start code for retry mechanism for broadcast message process */
	@SuppressWarnings("deprecation")
	//@JmsListener(destination = "retryIncomingQueueZ1") // Broadcast queue
	public void receivedRetryBroadcastZone1(Message message) {
		String sessionId = Long.toString(System.currentTimeMillis());
		// Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		_logger.info("[sessionId=" + sessionId
				+ "]: BroadcastConsumer.receivedRetryBroadcastZone1()-Zone-1 Broadcast Soap Message - process start with timestamp:["
				+ new Timestamp(System.currentTimeMillis()) + "]");
		String requestId = null;
		int success = 0;
		int fail = 0;
		Boolean isReqSuccess = true;
		int incomingCount = 0;
		String messageType = "";
		try {
			TextMessage msg = (TextMessage) message;
			_logger.debug("[sessionId=" + sessionId
					+ "]: BroadcastConsumer.receivedRetryBroadcastZone1()-Recieved Zone-1 Broadcast Message is :["
					+ msg.getText() + "]");
			_logger.debug("[sessionId=" + sessionId
					+ "]: BroadcastConsumer.receivedRetryBroadcastZone1()-1 trying to convert String into json object :["
					+ msg.getText() + "]");
			ObjectMapper mapper = new ObjectMapper();
			JsonNode arrayNode = mapper.readTree(msg.getText().toString());
			if (arrayNode.isArray() && arrayNode.size() > 0) {
				JsonNode firstObject = arrayNode.get(0);
				if (firstObject.has("messageType")) {
					messageType = firstObject.get("messageType").asText();
					System.out.println("messageType: " + messageType);
				}
			}
			_logger.debug("[sessionId=" + sessionId
					+ "]: BroadcastConsumer.receivedRetryBroadcastZone1()-2 converted String into json object :["
					+ msg.getText() + "]");
			String xml = null;

			boolean isDBConnectionLive = true;

			if (messageType.equals(ReadConfigFile.getProperties().getProperty("SCNOTICE_MESSAGE_TYPE"))) {
				_logger.info("[sessionId=" + sessionId
						+ "]: BroadcastConsumer.receivedRetryBroadcastZone1()-SC-NOTICE Soap Message - process start with timestamp:["
						+ new Timestamp(System.currentTimeMillis()) + "]");
				mapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);
				SCNotice[] listOfSCNotices = mapper.readValue(msg.getText().toString(), SCNotice[].class);

				for (SCNotice scNoticeItem : listOfSCNotices) {
					String messageSenderTelco = ReadConfigFile.getProperties().getProperty("MessageSenderTelco-ZOOM");
					String messageReceiverTelco = ReadConfigFile.getProperties()
							.getProperty("MessageReceiverTelco-mch1");
					scNoticeItem.setMessageSenderTelco(messageSenderTelco);
					scNoticeItem.setMessageReceiverTelco(messageReceiverTelco);
					try {
						requestId = scNoticeItem.getRequestId();
						List<MasterNP> masterList = new ArrayList<MasterNP>();
						List<BroadcastHistory> broadcastList = new ArrayList<BroadcastHistory>();
						int resetCnt = 0;
						for (BCInfoType bcInfo : scNoticeItem.getBcInfo()) {
							incomingCount++;
							try {
								// we remove country code digit (we define how much remove digit in configure
								// file
								String countryCode = ReadConfigFile.getProperties().getProperty("zone1-countrycode");
								int codeLimit = Integer.parseInt(countryCode);
								String msisdn = bcInfo.getSubscriberNumber().substring(codeLimit);
								_logger.debug("[sessionId=" + sessionId
										+ "]: BroadcastConsumer.receivedRetryBroadcastZone1()-SC-NOTICE msisdn:["
										+ msisdn + "] - going to check if already exist  with timestamp:["
										+ new Timestamp(System.currentTimeMillis()) + "]");
								String originalCarrier = scNoticeDao.getMasterNPOriginalCarrier(msisdn, sessionId);
								_logger.debug("[sessionId=" + sessionId
										+ "]: BroadcastConsumer.receivedRetryBroadcastZone1()-SC-NOTICE msisdn:["
										+ msisdn + "] - checked if already exist  with timestamp:["
										+ new Timestamp(System.currentTimeMillis()) + "]");
								if (originalCarrier != "DBDown") {
									if (originalCarrier != null) {
										_logger.debug("[sessionId=" + sessionId
												+ "]: BroadcastConsumer.receivedRetryBroadcastZone1()-SC-NOTICE msisdn:["
												+ msisdn + "] -  already exist  with timestamp:["
												+ new Timestamp(System.currentTimeMillis()) + "]");
										MasterNP masterNP = new MasterNP();
										masterNP.setRemark(bcInfo.getResultText());
										masterNP.setMsisdn(msisdn);
										masterNP.setArea(scNoticeItem.getLsa());
										masterNP.setService(bcInfo.getServiceType());
										masterNP.setRn(bcInfo.getRouteNumber());
										masterNP.setPresent_carrier(bcInfo.getRno());
										masterNP.setCarrier_history(bcInfo.getDno());
										masterNP.setOrginal_carrier(bcInfo.getNrh());
										masterNP.setActive("Y");
										masterNP.setTransaction_date(bcInfo.getdCTime());
										_logger.debug("[sessionId=" + sessionId
												+ "]: BroadcastConsumer.receivedRetryBroadcastZone1()-SC-NOTICE Soap Message - trying to get master_area with timestamp:["
												+ new Timestamp(System.currentTimeMillis()) + "]");
										String masterArea = scNoticeDao.getMasterNPArea(msisdn, sessionId);
										_logger.debug("[sessionId=" + sessionId
												+ "]: BroadcastConsumer.receivedRetryBroadcastZone1()-SC-NOTICE Soap Message - successfully to get master_area with timestamp:["
												+ new Timestamp(System.currentTimeMillis()) + "]");
										_logger.debug("[sessionId=" + sessionId
												+ "]: BroadcastConsumer.receivedRetryBroadcastZone1()-SC-NOTICE Soap Message - trying to get area with timestamp:["
												+ new Timestamp(System.currentTimeMillis()) + "]");
										String area = scNoticeDao.getArea(msisdn, sessionId);
										_logger.debug("[sessionId=" + sessionId
												+ "]: BroadcastConsumer.receivedRetryBroadcastZone1()-SC-NOTICE Soap Message - successfully to get area with timestamp:["
												+ new Timestamp(System.currentTimeMillis()) + "]");
										if (bcInfo.getbCAction().equals("1")) {
											masterNP.setAction("INS");
											masterNP.setHistory_area(masterArea);
										} else {
											if ((originalCarrier.equals(bcInfo.getRno()))
													&& area.equals(scNoticeItem.getLsa())) {
												// action can be taken from bcAction
												masterNP.setAction("DLT");
												masterNP.setHistory_area(masterArea);
											} else {
												masterNP.setAction("UPD");
												masterNP.setHistory_area(masterArea);
											}
										}

										masterNP.setOriginal_area(area);
										// masterNP.setRequestId(requestId);
										_logger.debug("[sessionId=" + sessionId
												+ "]: BroadcastConsumer.receivedRetryBroadcastZone1()-SC-NOTICE msisdn:["
												+ msisdn + "] - trying to update existing record  with timestamp:["
												+ new Timestamp(System.currentTimeMillis()) + "]");
										int flag = scNoticeDao.updateMasterNP(masterNP, sessionId);
										if (flag == 1) {
											_logger.info("[sessionId=" + sessionId
													+ "]: BroadcastConsumer.receivedRetryBroadcastZone1()-SC-NOTICE msisdn:["
													+ msisdn
													+ "] - updated existing record into master_table_Z1_success_cnt1");
										} else {
											_logger.error("[sessionId=" + sessionId
													+ "]: BroadcastConsumer.receivedRetryBroadcastZone1()-SC-NOTICE msisdn:["
													+ msisdn
													+ "] - fail to update existing record into master_table_Z1_fail_cnt1");
										}
										BroadcastHistory broadcastHistory = BroadcastMapper.mapToForm(masterNP,
												sessionId);
										broadcastHistory.setTransaction_id(scNoticeItem.getBatchId());
										broadcastHistory.setRequestId(requestId);
										broadcastHistory.setMch(1);
										broadcastList.add(broadcastHistory);
									} else {
										MasterNP masterNP = new MasterNP();
										masterNP.setRemark(bcInfo.getResultText());
										masterNP.setMsisdn(msisdn);
										masterNP.setArea(scNoticeItem.getLsa());
										masterNP.setService(bcInfo.getServiceType());
										masterNP.setRn(bcInfo.getRouteNumber());
										masterNP.setPresent_carrier(bcInfo.getRno());
										masterNP.setCarrier_history(bcInfo.getDno());
										masterNP.setOrginal_carrier(bcInfo.getNrh());
										masterNP.setActive("Y");
										masterNP.setTransaction_date(bcInfo.getdCTime());
										masterNP.setAction("INS");
										_logger.debug("[sessionId=" + sessionId
												+ "]: BroadcastConsumer.receivedRetryBroadcastZone1()-SC-NOTICE Soap Message - trying to get area with timestamp:["
												+ new Timestamp(System.currentTimeMillis()) + "]");
										String area = scNoticeDao.getArea(msisdn, sessionId);
										_logger.debug("[sessionId=" + sessionId
												+ "]: BroadcastConsumer.receivedRetryBroadcastZone1()-SC-NOTICE Soap Message - successfully get area with timestamp:["
												+ new Timestamp(System.currentTimeMillis()) + "]");
										masterNP.setOriginal_area(area);
										// masterNP.setRequestId(requestId);
										masterNP.setFirst_trans_date(timestamp);
										BroadcastHistory broadcastHistory = BroadcastMapper.mapToForm(masterNP,
												sessionId);
										broadcastHistory.setTransaction_id(scNoticeItem.getBatchId());
										broadcastHistory.setRequestId(requestId);
										broadcastHistory.setMch(1);
										broadcastList.add(broadcastHistory);
										resetCnt++;
										masterList.add(masterNP);
										if (resetCnt == 10000) {
											_logger.debug("[sessionId=" + sessionId
													+ "]: BroadcastConsumer.receivedRetryBroadcastZone1()-SC-NOTICE Soap Message - trying to store data into master db with timestamp:["
													+ new Timestamp(System.currentTimeMillis()) + "]");
											masterNPRepository.saveAll(masterList);
											masterNPRepository.flush();
											masterList.clear();
											resetCnt = 0;
											_logger.info("[sessionId=" + sessionId
													+ "]: BroadcastConsumer.receivedRetryBroadcastZone1()-SC-NOTICE Soap Message - successfully to store data into master_table_Z1_success_cnt"
													+ resetCnt);
										}
										if (broadcastList.size() == 10000) {
											_logger.debug("[sessionId=" + sessionId
													+ "]: BroadcastConsumer.receivedRetryBroadcastZone1()-SC-NOTICE Soap Message - trying to store 10000 data into broadcast db with timestamp:["
													+ new Timestamp(System.currentTimeMillis()) + "]");
											broadcastHistoryRepository.saveAll(broadcastList);
											broadcastHistoryRepository.flush();
											broadcastList.clear();
											_logger.debug("[sessionId=" + sessionId
													+ "]: BroadcastConsumer.receivedRetryBroadcastZone1()-SC-NOTICE Soap Message - successfully to store 10000 data into broadcast db with timestamp:["
													+ new Timestamp(System.currentTimeMillis()) + "]");
										}
									}
									success++;
								} else {
									_logger.info(
											"[sessionId={}]: BroadcastConsumer.receivedRetryBroadcastZone1() - SC-NOTICE Soap Message - Database is down. Timestamp: [{}]",
											sessionId, new Timestamp(System.currentTimeMillis()));
									isDBConnectionLive = false;
								}
							} catch (Exception e) {
								fail++;
								_logger.error("[sessionId=" + sessionId
										+ "]: BroadcastConsumer.receivedRetryBroadcastZone1()-Exception occurs while processing msisdn:["
										+ bcInfo.getSubscriberNumber() + "]  with timestamp:["
										+ new Timestamp(System.currentTimeMillis()) + "]-error:" + e.getMessage());
							}
						}
						if (isDBConnectionLive) {
							if (resetCnt < 10000) {
								_logger.debug("[sessionId=" + sessionId
										+ "]: BroadcastConsumer.receivedRetryBroadcastZone1()-SC-NOTICE Soap Message - trying to store data into master_table db with timestamp:["
										+ new Timestamp(System.currentTimeMillis()) + "]");
								masterNPRepository.saveAll(masterList);
								masterList.clear();
								_logger.info("[sessionId=" + sessionId
										+ "]: BroadcastConsumer.receivedRetryBroadcastZone1()-SC-NOTICE Soap Message - successfully to store data into master_table_Z1_success_cnt"
										+ resetCnt);
							}
							if (broadcastList.size() < 10000) {
								_logger.debug("[sessionId=" + sessionId
										+ "]: BroadcastConsumer.receivedRetryBroadcastZone1()-SC-NOTICE Soap Message - trying to store data into broadcasthistory db with timestamp:["
										+ new Timestamp(System.currentTimeMillis()) + "]");
								broadcastHistoryRepository.saveAll(broadcastList);
								broadcastList.clear();
								_logger.debug("[sessionId=" + sessionId
										+ "]: BroadcastConsumer.receivedRetryBroadcastZone1()-SC-NOTICE Soap Message - successfully to store data into broadcasthistory db with timestamp:["
										+ new Timestamp(System.currentTimeMillis()) + "]");
							}
						}
					} catch (Exception e) {
						isReqSuccess = false;
						_logger.error("[sessionId=" + sessionId
								+ "]: BroadcastConsumer.receivedRetryBroadcastZone1()-SC-NOTICE Soap Message Error- exception occurs during process sc-notice error is :"
								+ e);
					}
					if (isReqSuccess && isDBConnectionLive) {
						_logger.info("[sessionId=" + sessionId
								+ "]: BroadcastConsumer.receivedRetryBroadcastZone1()-SC-NOTICE Soap Message - successfully store data into db with timestamp:["
								+ new Timestamp(System.currentTimeMillis()) + "]");
						String getRequestId = recoveryDBService.getRequestId(messageSenderTelco);
						String[] splitRequestId = getRequestId.split("[,]", 0);
						String reqId = splitRequestId[0];
						_logger.info("[sessionId=" + sessionId
								+ "]: BroadcastConsumer.receivedRetryBroadcastZone1()-SC-NOTICE Soap Message - trying to convert into SC-NOTICE-ANS with timestamp:["
								+ new Timestamp(System.currentTimeMillis()) + "]");
						xml = new NPOUtils().generateSCNoticeAnswer(scNoticeItem, success, fail, reqId, sessionId);
						_logger.info("[sessionId=" + sessionId
								+ "]: BroadcastConsumer.receivedRetryBroadcastZone1()-successfully converted into SC-NOTICE-ANS xml :["
								+ xml + "] with timestamp:[" + new Timestamp(System.currentTimeMillis()) + "]");
						int interval = jmsProducer.sentSCNoticeAnswerIntoInQueue(xml, sessionId, "1");
						if (interval == 1) {
							_logger.info("[sessionId=" + sessionId
									+ "]: BroadcastConsumer.receivedRetryBroadcastZone1()-SC-NOTICE-ANS XML - Successfully submited into activemq with timestamp:["
									+ new Timestamp(System.currentTimeMillis()) + "]");
						} else {
							_logger.error("[sessionId=" + sessionId
									+ "]: BroadcastConsumer.receivedRetryBroadcastZone1()-SC-NOTICE-ANS XML - Fail to submite into activemq with timestamp:["
									+ new Timestamp(System.currentTimeMillis()) + "]");
						}
					} else {
						_logger.error("[sessionId=" + sessionId
								+ "]: BroadcastConsumer.receivedRetryBroadcastZone1()-Zone-1 Broadcast Soap Message - failed to process sc-notice with timestamp:["
								+ new Timestamp(System.currentTimeMillis()) + "]");
					}
				}
			}
			/* start code for SDNotice */
			else if (messageType.equals(ReadConfigFile.getProperties().getProperty("SDNOTICE_MESSAGE_TYPE"))) {
				_logger.info("[sessionId=" + sessionId
						+ "]: BroadcastConsumer.receivedRetryBroadcastZone1()-SD-NOTICE Soap Message - process start with timestamp:["
						+ new Timestamp(System.currentTimeMillis()) + "]");
				mapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);
				SDNotice[] listOfSDNotices = mapper.readValue(msg.getText().toString(), SDNotice[].class);
				for (SDNotice sdNoticeItem : listOfSDNotices) {
					requestId = sdNoticeItem.getRequestId();
					String messageSenderTelco = ReadConfigFile.getProperties().getProperty("MessageSenderTelco-ZOOM");
					String messageReceiverTelco = ReadConfigFile.getProperties()
							.getProperty("MessageReceiverTelco-mch1");
					sdNoticeItem.setMessageSenderTelco(messageSenderTelco);
					sdNoticeItem.setMessageReceiverTelco(messageReceiverTelco);
					List<BroadcastHistory> broadcastList = new ArrayList<BroadcastHistory>();
					try {
						for (BCInfoType bcInfo : sdNoticeItem.getBcInfo()) {
							incomingCount++;
							try {
								// we remove country code digit (we define how much remove digit in configure
								// file
								String countryCode = ReadConfigFile.getProperties().getProperty("zone1-countrycode");
								int codeLimit = Integer.parseInt(countryCode);
								String msisdn = bcInfo.getSubscriberNumber().substring(codeLimit);
								MasterNP masterNP = new MasterNP();
								masterNP.setRemark(bcInfo.getResultText());
								masterNP.setMsisdn(msisdn);
								masterNP.setArea(sdNoticeItem.getLsa());
								masterNP.setService(bcInfo.getServiceType());
								masterNP.setRn(bcInfo.getRouteNumber());
								masterNP.setPresent_carrier(bcInfo.getRno());
								masterNP.setCarrier_history(bcInfo.getDno());
								String masterArea = scNoticeDao.getMasterNPArea(msisdn, sessionId);
								if (masterArea != "DBDown") {
									masterNP.setHistory_area(masterArea);
									masterNP.setOrginal_carrier(bcInfo.getRno());
									masterNP.setActive("N");
									masterNP.setTransaction_date(bcInfo.getdCTime());
									masterNP.setDisconnection_date(bcInfo.getdCTime());
									masterNP.setRe_trans_date(timestamp);
									masterNP.setAction("DLT");
									masterNP.setOriginal_area(sdNoticeItem.getLsa());
									// masterNP.setRequestId(requestId);
									int flag = 0;
									try {
										flag = scNoticeDao.updateSDMasterNP(masterNP, sessionId);
									} catch (Exception e) {
										// add flag for retry message
										// read the configurable file for number of retry and set into message type for
										// retry queue
										// in retry consumer if exception re-occurs keep putting -1 till it get zero
										_logger.error("[sessionId=" + sessionId
												+ "]: BroadcastConsumer.receivedRetryBroadcastZone1()-Zone-1 Broadcast Soap Message - DB Exception occurs while proccess message with timestamp:["
												+ new Timestamp(System.currentTimeMillis()) + "], error is :" + e);
										String retryCount = ReadConfigFile.getProperties()
												.getProperty("retry_queue_count");
										int ackId = jmsProducer.sendZone1MessageRetry(msg.getText().toString(),
												sessionId, retryCount);
										if (ackId == 1) {
											_logger.info("[sessionId=" + sessionId
													+ "]: BroadcastConsumer.receivedRetryBroadcastZone1() - Successfully submited retry sd-notice message into retryIncomingQueueZ1 activemq with timestamp:["
													+ new Timestamp(System.currentTimeMillis()) + "]");
										} else {
											_logger.error("[sessionId=" + sessionId
													+ "]: BroadcastConsumer.receivedRetryBroadcastZone1() - Failed to submit retry sd-notice message into retryIncomingQueueZ1 activemq with timestamp:["
													+ new Timestamp(System.currentTimeMillis()) + "]");
										}
									}
									if (flag == 1) {
										_logger.info("[sessionId=" + sessionId
												+ "]: BroadcastConsumer.receivedRetryBroadcastZone1()-SD-NOTICE msisdn:["
												+ msisdn
												+ "] - updated existing record into master_table_Z1_success_cnt1");
									} else {
										_logger.error("[sessionId=" + sessionId
												+ "]: BroadcastConsumer.receivedRetryBroadcastZone1()-SD-NOTICE msisdn:["
												+ msisdn
												+ "] - fail to update existing record into master_table_Z1_fail_cnt1");
									}
									BroadcastHistory broadcastHistory = BroadcastMapper.mapToForm(masterNP, sessionId);
									broadcastHistory.setFirst_trans_date(timestamp);
									broadcastHistory.setDisconnection_date(bcInfo.getdCTime());
									broadcastHistory.setTransaction_id(sdNoticeItem.getBatchId());
									broadcastHistory.setRequestId(requestId);
									broadcastHistory.setMch(1);
									broadcastList.add(broadcastHistory);
									if (broadcastList.size() == 100000) {
										broadcastHistoryRepository.saveAll(broadcastList);
										broadcastHistoryRepository.flush();
										broadcastList.clear();
									}
									success++;
								} else {
									_logger.info(
											"[sessionId={}]: BroadcastConsumer.receivedRetryBroadcastZone1() - SD-NOTICE Soap Message - Database is down. Timestamp: [{}]",
											sessionId, new Timestamp(System.currentTimeMillis()));
									isDBConnectionLive = false;
								}
							} catch (Exception e) {
								fail++;
								_logger.error("[sessionId=" + sessionId
										+ "]: BroadcastConsumer.receivedRetryBroadcastZone1()-SD-NOTICE Soap Message Error- exception occurs during process sd-notice error is :"
										+ e);
							}
						}
						if (isDBConnectionLive) {
							if (broadcastList.size() < 100000) {
								broadcastHistoryRepository.saveAll(broadcastList);
								broadcastList.clear();
							}
						}
					} catch (Exception e) {
						isReqSuccess = false;
						_logger.error("[sessionId=" + sessionId
								+ "]: BroadcastConsumer.receivedRetryBroadcastZone1()-SD-NOTICE Soap Message Error- exception occurs during process sd-notice error is :"
								+ e);
					}
					if (isReqSuccess && isDBConnectionLive) {
						_logger.info("[sessionId=" + sessionId
								+ "]: BroadcastConsumer.receivedRetryBroadcastZone1()-SD-NOTICE Soap Message - successfully store data into history db with timestamp:["
								+ new Timestamp(System.currentTimeMillis()) + "]");
						String getRequestId = recoveryDBService.getRequestId(messageSenderTelco);
						_logger.debug("[sessionId=" + sessionId
								+ "]: BroadcastConsumer.receivedRetryBroadcastZone1()-SD-NOTICE Soap Message - trying to convert into SC-NOTICE-ANS with timestamp:["
								+ new Timestamp(System.currentTimeMillis()) + "]");
						xml = new NPOUtils().generateSDNoticeAnswer(sdNoticeItem, success, fail, getRequestId);
						_logger.debug("[sessionId=" + sessionId
								+ "]: BroadcastConsumer.receivedRetryBroadcastZone1()-successfully converted into SD-NOTICE-ANS xml :["
								+ xml + "] with timestamp:[" + new Timestamp(System.currentTimeMillis()) + "]");
						int interval = jmsProducer.sentSDNoticeAnswerIntoInQueue(xml, sessionId, "1");
						if (interval == 1) {
							_logger.info("[sessionId=" + sessionId
									+ "]: BroadcastConsumer.receivedRetryBroadcastZone1()-SD-NOTICE-ANS XML - Successfully submited into activemq with timestamp:["
									+ new Timestamp(System.currentTimeMillis()) + "]");
						} else {
							_logger.error("[sessionId=" + sessionId
									+ "]: BroadcastConsumer.receivedRetryBroadcastZone1()-SD-NOTICE-ANS XML - Fail to submite into activemq with timestamp:["
									+ new Timestamp(System.currentTimeMillis()) + "]");
						}
					}
				}
			} else {
				_logger.error("[sessionId=" + sessionId
						+ "]: BroadcastConsumer.receivedRetryBroadcastZone1()-SD-NOTICE-ANS XML - Failed to process sd-notice with timestamp:["
						+ new Timestamp(System.currentTimeMillis()) + "]");
			}
			_logger.info("[sessionId=" + sessionId
					+ "]: BroadcastConsumer.receivedRetryBroadcastZone1()-Zone-1 Broadcast Soap Message - process end with timestamp:["
					+ new Timestamp(System.currentTimeMillis()) + "]");
			BroadcastStats statsCount = new BroadcastStats();
			statsCount.setId(generateRandomNumber());
			statsCount.setCount(incomingCount);
			statsCount.setMch("Zone 1");
			statsService.saveBroadcastStats(statsCount);
			// } else { comment because we change code
			if (!isDBConnectionLive) {
				// add flag for retry message
				// read the configurable file for number of retry and set into message type for
				// retry queue
				// in retry consumer if exception re-occurs keep putting -1 till it get zero
				int retry_count = Integer.parseInt(message.getJMSType());
				if (retry_count != 0) {
					_logger.error("[sessionId=" + sessionId
							+ "]: BroadcastConsumer.receiveRetryNPCMessageData()- Broadcast Soap Message - DB Exception occurs while proccess message with timestamp:["
							+ new Timestamp(System.currentTimeMillis()) + "]");
					int count = retry_count - 1;
					String retryMsgCount = Integer.toString(count);
					int ackId = jmsProducer.sendZone2MessageRetry(msg.getText().toString(), sessionId, retryMsgCount);
					if (ackId == 1) {
						_logger.info("[sessionId=" + sessionId
								+ "]: BroadcastConsumer.receiveRetryNPCMessageData() - Successfully submited retry Broadcas message into retryIncomingQueueZ2 activemq with timestamp:["
								+ new Timestamp(System.currentTimeMillis()) + "] - retryCount :[" + count + "]");
					} else {
						_logger.info("[sessionId=" + sessionId
								+ "]: BroadcastConsumer.receiveRetryNPCMessageData() - Failed to submit retry Broadcas message into retryIncomingQueueZ2 activemq with timestamp:["
								+ new Timestamp(System.currentTimeMillis()) + "]");
					}
				}
			}
		} catch (JMSException e) {
			_logger.error("[sessionId=" + sessionId
					+ "]: BroadcastConsumer.receivedRetryBroadcastZone1()-Zone-1 Broadcast Soap Message - Exception occurs whitle processing with timestamp:["
					+ new Timestamp(System.currentTimeMillis()) + "], error is :" + e);
		} catch (JsonProcessingException e) {
			_logger.error("[sessionId=" + sessionId
					+ "]: BroadcastConsumer.receivedRetryBroadcastZone1()-Zone-1 Broadcast Soap Message - Exception occurs whitle processing with timestamp:["
					+ new Timestamp(System.currentTimeMillis()) + "], error is :" + e);
		}
	}

	@SuppressWarnings("deprecation")
	//@JmsListener(destination = "retryIncomingQueueZ2") // NPCMessageData Broadcast queue this is zone 2
	public void receiveRetryNPCMessageData(Message message) {
		String sessionId = Long.toString(System.currentTimeMillis());
		_logger.info("[sessionId=" + sessionId
				+ "]: BroadcastConsumer.receiveRetryNPCMessageData()-Zone-2 Broadcast Soap Message - process start with timestamp:["
				+ new Timestamp(System.currentTimeMillis()) + "]");
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
		Boolean isReqSuccess = true;
		int incomingCount = 0;
		try {
			TextMessage msg = (TextMessage) message;
			_logger.debug("[sessionId=" + sessionId
					+ "]: BroadcastConsumer.receiveRetryNPCMessageData()-Recieved Zone-2 Broadcast Message is :["
					+ msg.getText() + "]");
			ObjectMapper objectMapper = new ObjectMapper();
			objectMapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);
			NPCData npcData = objectMapper.readValue(msg.getText(), NPCData.class);
			String requestId = npcData.getMessageHeader().getTransactionID();
			_logger.debug("[sessionId=" + sessionId
					+ "]: BroadcastConsumer.receiveRetryNPCMessageData()-Zone-2 NPCMessageData Soap Message - process start with timestamp:["
					+ new Timestamp(System.currentTimeMillis()) + "]");

			boolean isDBConnectionLive = true;

			MasterNP masterNP = new MasterNP();
			if (npcData.getMessageHeader().getMessageID() == 1010) {
				incomingCount++;
				List<BroadcastHistory> broadcastList = new ArrayList<BroadcastHistory>();
				_logger.info("[sessionId=" + sessionId
						+ "]: BroadcastConsumer.receiveRetryNPCMessageData()-NPCMessageData Soap Message - process start of messageId: ["
						+ npcData.getMessageHeader().getMessageID() + "] with timestamp:["
						+ new Timestamp(System.currentTimeMillis()) + "]");
				try {
					String comments = npcData.getNPCMessage().getPortActivatedBroadcast().getComments();
					masterNP.setRemark(comments);
					for (NumberRangeOAFlagged numberRangeOAFlagged : npcData.getNPCMessage().getPortActivatedBroadcast()
							.getNumberRangeOAFlagged()) {

						String numberFrom = numberRangeOAFlagged.getNumberFrom();
						// we remove country code digit (we define how much remove digit in configure
						// file
						String countryCode = ReadConfigFile.getProperties().getProperty("zone2-countrycode");
						int codeLimit = Integer.parseInt(countryCode);
						String msisdn = numberFrom.substring(codeLimit);
						masterNP.setMsisdn(msisdn);
						String areaSplit = null;
						try {
							areaSplit = comments.substring(0, 2);
							masterNP.setArea(areaSplit);
						} catch (Exception e) {
							_logger.error("[sessionId=" + sessionId
									+ "]: BroadcastConsumer.receiveRetryNPCMessageData()-exception occur while getting area from comments string with timestamp:["
									+ new Timestamp(System.currentTimeMillis()) + "]");
						}
						masterNP.setPresent_carrier(npcData.getNPCMessage().getPortActivatedBroadcast().getRecipient());
						masterNP.setCarrier_history(npcData.getNPCMessage().getPortActivatedBroadcast().getDonor());
						_logger.debug("[sessionId=" + sessionId
								+ "]: BroadcastConsumer.receiveRetryNPCMessageData()-Trying to get operator_id for NPCMessageData 1010 with timestamp:["
								+ new Timestamp(System.currentTimeMillis()) + "]");
						String opId = scNoticeDao.getOperatorIdByMsisdn(msisdn);
						if (opId != "DBDown") {
							_logger.debug("[sessionId=" + sessionId
									+ "]: BroadcastConsumer.receiveRetryNPCMessageData()-got operator_id : [" + opId
									+ "] for NPCMessageData 1010 with timestamp:["
									+ new Timestamp(System.currentTimeMillis()) + "]");
							masterNP.setOrginal_carrier(opId);
							masterNP.setActive("Y");
							masterNP.setRn(npcData.getNPCMessage().getPortActivatedBroadcast().getRoute());
							Date parsedDate = dateFormat.parse(npcData.getMessageHeader().getMsgCreateTimeStamp());
							Timestamp timestamp = new Timestamp(parsedDate.getTime());
							masterNP.setTransaction_date(timestamp);
							masterNP.setOriginal_area(npcData.getMessageHeader().getlSAID());
							_logger.debug("[sessionId=" + sessionId
									+ "]: BroadcastConsumer.receiveRetryNPCMessageData()-Trying to get master_area for NPCMessageData 1010 with timestamp:["
									+ new Timestamp(System.currentTimeMillis()) + "]");
							MasterNP masterItem = masterNPService.findByMsisdn(msisdn);
							_logger.debug("[sessionId=" + sessionId
									+ "]: BroadcastConsumer.receiveRetryNPCMessageData()-successfully get master_area for NPCMessageData 1010 with timestamp:["
									+ new Timestamp(System.currentTimeMillis()) + "]");
							if (masterItem != null) {
								if (masterItem.getAction().equals("DLT")) {
									masterNP.setAction("INS");
									masterNP.setHistory_area(masterItem.getArea());
								} else {
									if (numberRangeOAFlagged.getOrigAssigneeFlag().equalsIgnoreCase("Y")) {
										masterNP.setAction("DLT");
										masterNP.setHistory_area(masterItem.getArea());
									} else {
										masterNP.setAction("UPD");
										masterNP.setHistory_area(masterItem.getArea());
									}
								}
								_logger.debug("[sessionId=" + sessionId
										+ "]: BroadcastConsumer.receiveRetryNPCMessageData()-Trying to update master db for NPCMessageData 1010 with timestamp:["
										+ new Timestamp(System.currentTimeMillis()) + "]");
								int flag = scNoticeDao.updateMasterNPFor1010Zone2(masterNP, sessionId);
								if (flag == 1) {
									_logger.info("[sessionId=" + sessionId
											+ "]:  BroadcastConsumer.receiveRetryNPCMessageData()-NPCMessageData 1010 msisdn:["
											+ msisdn
											+ "] - successfully update existing record into master_table_Z2_success_cnt1");
								} else {
									_logger.error("[sessionId=" + sessionId
											+ "]:  BroadcastConsumer.receiveRetryNPCMessageData()-NPCMessageData 1010 msisdn:["
											+ msisdn
											+ "] - failed to update existing record int master_table_Z2_fail_cnt1");
								}
							} else {
								masterNP.setAction("INS");
								// String area = scNoticeDao.getArea(msisdn, sessionId);
								masterNP.setOriginal_area(npcData.getMessageHeader().getlSAID());
								masterNP.setFirst_trans_date(timestamp);
								_logger.debug("[sessionId=" + sessionId
										+ "]: BroadcastConsumer.receiveRetryNPCMessageData()-NPCMessageData 1010 msisdn:["
										+ msisdn + "] - trying to insert into master db  with timestamp:["
										+ new Timestamp(System.currentTimeMillis()) + "]");
								masterNPService.saveMasterNP(masterNP);
								_logger.info("[sessionId=" + sessionId
										+ "]: BroadcastConsumer.receiveRetryNPCMessageData()-NPCMessageData 1010 msisdn:["
										+ msisdn + "] - successfully inserted into master_table_Z2_success_cnt1");
							}
							BroadcastHistory broadcastHistory = BroadcastMapper.mapToForm(masterNP, sessionId);
							broadcastHistory.setTransaction_id(requestId);
							broadcastHistory.setRn(npcData.getNPCMessage().getPortActivatedBroadcast().getRoute());
							broadcastHistory.setRequestId(requestId);
							broadcastHistory.setMch(2);
							broadcastList.add(broadcastHistory);
							_logger.debug("[sessionId=" + sessionId
									+ "]: BroadcastConsumer.receiveRetryNPCMessageData()-NPCMessageData 1010 msisdn:["
									+ msisdn + "] - trying to insert into history db  with timestamp:["
									+ new Timestamp(System.currentTimeMillis()) + "]");
							broadcastHistoryRepository.saveAll(broadcastList);
							broadcastHistoryRepository.flush();
							_logger.debug("[sessionId=" + sessionId
									+ "]: BroadcastConsumer.receiveRetryNPCMessageData()-NPCMessageData 1010 msisdn:["
									+ msisdn + "] - successfully inserted into history db  with timestamp:["
									+ new Timestamp(System.currentTimeMillis()) + "]");
							broadcastList.clear();
							_logger.info("[sessionId=" + sessionId
									+ "]: BroadcastConsumer.receiveRetryNPCMessageData()-NPCMessageData Soap Message - process end of messageId: ["
									+ npcData.getMessageHeader().getMessageID() + "] with timestamp:["
									+ new Timestamp(System.currentTimeMillis()) + "]");
						} else {
							_logger.info(
									"[sessionId={}]: BroadcastConsumer.receiveRetryNPCMessageData() - NPCMessageData Soap Message - Database is down. Timestamp: [{}]",
									sessionId, new Timestamp(System.currentTimeMillis()));
							isDBConnectionLive = false;
						}
					}
				} catch (Exception e) {
					isReqSuccess = false;
					_logger.error("[sessionId=" + sessionId
							+ "]: BroadcastConsumer.receiveRetryNPCMessageData()-NPCMessageData Soap Message - exception occurs during process messageId: ["
							+ npcData.getMessageHeader().getMessageID() + "] , error is :" + e);
				}
				if (isDBConnectionLive) {
					BroadcastStats statsCount = new BroadcastStats();
					statsCount.setId(generateRandomNumber());
					statsCount.setCount(incomingCount);
					statsCount.setMch("Zone 2");
					statsService.saveBroadcastStats(statsCount);
				}
			} else if (npcData.getMessageHeader().getMessageID() == 5004) {
				incomingCount++;
				_logger.info("[sessionId=" + sessionId
						+ "]: BroadcastConsumer.receiveRetryNPCMessageData()-NPCMessageData Soap Message - process start of messageId: ["
						+ npcData.getMessageHeader().getMessageID() + "] with timestamp:["
						+ new Timestamp(System.currentTimeMillis()) + "]");
				try {
					List<BroadcastHistory> broadcastList = new ArrayList<BroadcastHistory>();
					String comments = npcData.getNPCMessage().getNumReturnBroadcast().getComments();
					masterNP.setRemark(comments);
					for (NumberRangeOAFlagged numberRangeOAFlagged : npcData.getNPCMessage().getNumReturnBroadcast()
							.getNumberRangeOAFlagged()) {
						String numberFrom = numberRangeOAFlagged.getNumberFrom();
						// we remove country code digit (we define how much remove digit in configure
						// file
						String countryCode = ReadConfigFile.getProperties().getProperty("zone2-countrycode");
						int codeLimit = Integer.parseInt(countryCode);
						String msisdn = numberFrom.substring(codeLimit);
						masterNP.setMsisdn(msisdn);
						String areaSpli = comments.substring(0, 2);
						masterNP.setArea(areaSpli);
						masterNP.setRn(npcData.getNPCMessage().getNumReturnBroadcast().getRoute());
						masterNP.setPresent_carrier(
								npcData.getNPCMessage().getNumReturnBroadcast().getOriginalAssignee());
						masterNP.setCarrier_history(npcData.getNPCMessage().getNumReturnBroadcast().getLastRecipient());
						masterNP.setActive("N");
						// need to add this
						_logger.debug("[sessionId=" + sessionId
								+ "]: BroadcastConsumer.receiveRetryNPCMessageData()-NPCMessageData 5004 msisdn:["
								+ msisdn + "] - trying to get master area from master db  with timestamp:["
								+ new Timestamp(System.currentTimeMillis()) + "]");
						String masterArea = scNoticeDao.getMasterNPArea(msisdn, sessionId);
						if (masterArea != "DBDown") {
							_logger.debug("[sessionId=" + sessionId
									+ "]: BroadcastConsumer.receiveRetryNPCMessageData()-NPCMessageData 5004 msisdn:["
									+ msisdn + "] - successfully get master area from master db  with timestamp:["
									+ new Timestamp(System.currentTimeMillis()) + "]");
							masterNP.setHistory_area(masterArea);
							masterNP.setOrginal_carrier(
									npcData.getNPCMessage().getNumReturnBroadcast().getOriginalAssignee());
							Date parsedDate = dateFormat.parse(npcData.getMessageHeader().getMsgCreateTimeStamp());
							Timestamp timestamps = new Timestamp(parsedDate.getTime());
							masterNP.setDisconnection_date(timestamps);
							masterNP.setRe_trans_date(timestamp);
							masterNP.setOriginal_area(npcData.getMessageHeader().getlSAID());
							masterNP.setTransaction_date(timestamps);
							masterNP.setAction("DLT");
							_logger.debug("[sessionId=" + sessionId
									+ "]: BroadcastConsumer.receiveRetryNPCMessageData()-NPCMessageData Soap Message - trying to updated data with timestamp:["
									+ new Timestamp(System.currentTimeMillis()) + "]");
							int flag = scNoticeDao.updateMasterNPFor5004Zone2(masterNP, sessionId);
							if (flag == 1) {
								_logger.info("[sessionId=" + sessionId
										+ "]: BroadcastConsumer.receiveRetryNPCMessageData()-NPCMessageData 5004 msisdn:["
										+ msisdn + "] - updated existing record into master_table_Z2_success_cnt1");
							} else {
								_logger.error("[sessionId=" + sessionId
										+ "]: BroadcastConsumer.receiveRetryNPCMessageData()-NPCMessageData 5004 msisdn:["
										+ msisdn + "] - fail to update existing record into master_table_Z2_fail_cnt1");
							}
							BroadcastHistory broadcastHistory = BroadcastMapper.mapToForm(masterNP, sessionId);
							broadcastHistory.setTransaction_id(requestId);
							broadcastHistory.setRequestId(requestId);
							broadcastHistory.setDisconnection_date(timestamp);
							broadcastHistory.setMch(2);
							broadcastList.add(broadcastHistory);
							_logger.debug("[sessionId=" + sessionId
									+ "]: BroadcastConsumer.receiveRetryNPCMessageData()-NPCMessageData 5004 msisdn:["
									+ msisdn + "] - start to insert into history db  with timestamp:["
									+ new Timestamp(System.currentTimeMillis()) + "]");
							broadcastHistoryRepository.saveAll(broadcastList);
							broadcastHistoryRepository.flush();
							_logger.debug("[sessionId=" + sessionId
									+ "]: BroadcastConsumer.receiveRetryNPCMessageData()-NPCMessageData 5004 msisdn:["
									+ msisdn + "] - successfully to insert into history db  with timestamp:["
									+ new Timestamp(System.currentTimeMillis()) + "]");
							broadcastList.clear();
							_logger.info("[sessionId=" + sessionId
									+ "]: BroadcastConsumer.receiveRetryNPCMessageData()-NPCMessageData Soap Message - process end of messageId: ["
									+ npcData.getMessageHeader().getMessageID() + "] with timestamp:["
									+ new Timestamp(System.currentTimeMillis()) + "]");
						} else {
							_logger.info(
									"[sessionId={}]: BroadcastConsumer.receiveRetryNPCMessageData() - NPCMessageData Soap Message - Database is down. Timestamp: [{}]",
									sessionId, new Timestamp(System.currentTimeMillis()));
							isDBConnectionLive = false;
						}
					}
				} catch (Exception e) {
					isReqSuccess = false;
					_logger.error("[sessionId=" + sessionId
							+ "]: BroadcastConsumer.receiveRetryNPCMessageData()-NPCMessageData Soap Message - exception occurs during process messageId: ["
							+ npcData.getMessageHeader().getMessageID() + "] , error is :" + e);
				}
				if (isDBConnectionLive) {
					BroadcastStats statsCount = new BroadcastStats();
					statsCount.setId(generateRandomNumber());
					statsCount.setCount(incomingCount);
					statsCount.setMch("Zone 2");
					statsService.saveBroadcastStats(statsCount);
				}
			} else if (npcData.getMessageHeader().getMessageID() == 6002) {
				_logger.info("[sessionId=" + sessionId
						+ "]: BroadcastConsumer.receiveRetryNPCMessageData()-NPCMessageData Soap Message - process start of messageId: ["
						+ npcData.getMessageHeader().getMessageID() + "] with timestamp:["
						+ new Timestamp(System.currentTimeMillis()) + "]");
				try {
					String path = npcData.getNPCMessage().getSynchronisationResponse().getLocation();
					String fileName = npcData.getNPCMessage().getSynchronisationResponse().getContactDetails();
					int flag = recoveryDBService.updateSynchronisationResponse(path, fileName, requestId, sessionId);

					if (flag == 1) {
						_logger.info("[sessionId=" + sessionId
								+ "]: BroadcastConsumer.receiveRetryNPCMessageData()-NPCMessageData synchronisationResponse [6002] - updated response  with timestamp:["
								+ new Timestamp(System.currentTimeMillis()) + "]");
					} else {
						_logger.error("[sessionId=" + sessionId
								+ "]: BroadcastConsumer.receiveRetryNPCMessageData()-NPCMessageData synchronisationResponse [6002] - fail to update response  with timestamp:["
								+ new Timestamp(System.currentTimeMillis()) + "]");
					}
				} catch (Exception e) {
					isReqSuccess = false;
					_logger.error("[sessionId=" + sessionId
							+ "]: BroadcastConsumer.receiveRetryNPCMessageData()-NPCMessageData Soap Message - exception occurs during process messageId: ["
							+ npcData.getMessageHeader().getMessageID() + "] , error is :" + e);
				}
			}
			if (isReqSuccess) {
				_logger.debug("[sessionId=" + sessionId
						+ "]: BroadcastConsumer.receiveRetryNPCMessageData()-NPCMessageData - Successfully to process NPCMessageData with timestamp:["
						+ new Timestamp(System.currentTimeMillis()) + "]");
			} else {
				_logger.error("[sessionId=" + sessionId
						+ "]: BroadcastConsumer.receiveRetryNPCMessageData()-SD-NPCMessageData - Failed to process NPCMessageData with timestamp:["
						+ new Timestamp(System.currentTimeMillis()) + "]");
			}
			_logger.debug("[sessionId=" + sessionId
					+ "]: BroadcastConsumer.receiveRetryNPCMessageData()-NPCMessageData Soap Message - process end of messageId: ["
					+ npcData.getMessageHeader().getMessageID() + "] with timestamp:["
					+ new Timestamp(System.currentTimeMillis()) + "]");
			// } else { comment because change code for retry code
			if (!isDBConnectionLive) {
				// add flag for retry message
				// read the configurable file for number of retry and set into message type for
				// retry queue
				// in retry consumer if exception re-occurs keep putting -1 till it get zero
				int retry_count = Integer.parseInt(message.getJMSType());
				if (retry_count != 0) {
					_logger.error("[sessionId=" + sessionId
							+ "]: BroadcastConsumer.receiveRetryNPCMessageData()- Broadcast Soap Message - DB Exception occurs while proccess message with timestamp:["
							+ new Timestamp(System.currentTimeMillis()) + "]");
					int count = retry_count - 1;
					String retryMsgCount = Integer.toString(count);
					int ackId = jmsProducer.sendZone2MessageRetry(msg.getText().toString(), sessionId, retryMsgCount);
					if (ackId == 1) {
						_logger.info("[sessionId=" + sessionId
								+ "]: BroadcastConsumer.receiveRetryNPCMessageData() - Successfully submited retry Broadcas message into retryIncomingQueueZ2 activemq with timestamp:["
								+ new Timestamp(System.currentTimeMillis()) + "] - retryCount :[" + count + "]");
					} else {
						_logger.info("[sessionId=" + sessionId
								+ "]: BroadcastConsumer.receiveRetryNPCMessageData() - Failed to submit retry Broadcas message into retryIncomingQueueZ2 activemq with timestamp:["
								+ new Timestamp(System.currentTimeMillis()) + "]");
					}
				}
			}
		} catch (Exception e) {
			_logger.error("[sessionId=" + sessionId
					+ "]: BroadcastConsumer.receiveRetryNPCMessageData()-NPCMessageData Soap Message - Exception occurs while processing with timestamp:["
					+ new Timestamp(System.currentTimeMillis()) + "], error is :" + e);
		}
		_logger.info("[sessionId=" + sessionId
				+ "]: BroadcastConsumer.receiveRetryNPCMessageData()-Zone-2 Broadcast Soap Message - process end with timestamp:["
				+ new Timestamp(System.currentTimeMillis()) + "]");
	}

	public static int generateRandomNumber() {
		Random random = new Random();
		return random.nextInt(500000000 - 1000 + 1) + 1000;
	}
}
