package com.megthink.gateway.utils;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.megthink.gateway.form.RecoveryDBForm;
import com.megthink.gateway.model.BillingResolution;
import com.megthink.gateway.model.InitAck;
import com.megthink.gateway.model.NPOT;
import com.megthink.gateway.model.MSISDNUIDType;
import com.megthink.gateway.model.MasterNP;
import com.megthink.gateway.model.NPO;
import com.megthink.gateway.model.NPOA;
import com.megthink.gateway.model.OrderCancellation;
import com.megthink.gateway.model.OrderReversal;
import com.megthink.gateway.model.PortMe;
import com.megthink.gateway.model.SC;
import com.megthink.gateway.model.SCInfo;
import com.megthink.gateway.model.SCNotice;
import com.megthink.gateway.model.SD;
import com.megthink.gateway.model.SDInfo;
import com.megthink.gateway.model.SDNotice;
import com.megthink.gateway.model.SubscriberSequence;
import com.megthink.gateway.model.SubscriberArrType;
import com.megthink.gateway.model.SubscriberAuthorization;
import com.megthink.gateway.model.SubscriberResult;
import com.megthink.gateway.model.TerminateSim;
import com.megthink.gateway.model.TerminateSimMT;
import com.megthink.gateway.xmlconverter.GlobalXMLGenerator;

public class NPOUtils {

	private String path = System.getProperty("user.home") + "/app-config/mnp/globalconfig.dat";

	private static final Logger _logger = LoggerFactory.getLogger(NPOUtils.class);

	Timestamp timestamp = new Timestamp(System.currentTimeMillis());

	public String convertJsonIntoInitPortRequest(PortMe portme, String binaryFile, String referenceId,
			String requestId) {
		try {
			String xmlMessage = "";
			String messageSenderTelco = ReadConfigFile.getProperties().getProperty("MessageSenderTelco-ZOOM");
			String messageReceiverTelco = ReadConfigFile.getProperties().getProperty("MessageReceiverTelco-mch1");
			xmlMessage = "<MNPFrame xmlns=\"MNPProtocol.xsd\">";
			xmlMessage = xmlMessage + "<NPO>";
			xmlMessage = xmlMessage + "<MessageSenderTelco>" + messageSenderTelco + "</MessageSenderTelco>";
			xmlMessage = xmlMessage + "<MessageReceiverTelco>" + messageReceiverTelco + "</MessageReceiverTelco>";
			xmlMessage = xmlMessage + "<RequestId>" + requestId + "</RequestId>";
			xmlMessage = xmlMessage + "<Timestamp>" + timestamp + "</Timestamp>";
			// xmlMessage = xmlMessage +
			// "<ReferenceId>893-M-230-20220608-A001282</ReferenceId>";
			xmlMessage = xmlMessage + "<ReferenceId>" + referenceId + "</ReferenceId>";

			xmlMessage = xmlMessage + "<RecipientTelco>" + portme.getSource() + "</RecipientTelco>";
			xmlMessage = xmlMessage + "<DonorTelco>" + portme.getDno() + "</DonorTelco>";
			xmlMessage = xmlMessage + "<ByLOA>" + portme.getDataType() + "</ByLOA>";

			xmlMessage = xmlMessage + "<LOAImage>" + binaryFile + "</LOAImage>";
			xmlMessage = xmlMessage + "<UndertakingAck>1</UndertakingAck>";
			if (portme.getSubscriberArrType().size() > 0) {
				xmlMessage = xmlMessage + "<SubscriberAuthSequence>";
				for (SubscriberArrType item : portme.getSubscriberArrType()) {
					xmlMessage = xmlMessage + "<SubscriberAuthorization>";
					xmlMessage = xmlMessage + "<SubscriberNumber>" + item.getMsisdn() + "</SubscriberNumber>";
					xmlMessage = xmlMessage + "<OwnerId>" + item.getPinCode() + "</OwnerId>";
					xmlMessage = xmlMessage + "<TypeOfId>10</TypeOfId>";
					xmlMessage = xmlMessage + "</SubscriberAuthorization>";
				}
				xmlMessage = xmlMessage + "</SubscriberAuthSequence>";
			} else {
				xmlMessage = xmlMessage + "<SubscriberSequence>";
				xmlMessage = xmlMessage + "<SubscriberNumber>" + portme.getSubscriberSequence().getSubscriberNumber()
						+ "</SubscriberNumber>";
				xmlMessage = xmlMessage + "</SubscriberSequence>";
			}
			xmlMessage = xmlMessage + "<LSA>" + portme.getArea() + "</LSA>";
			xmlMessage = xmlMessage + "<RouteNumber>" + portme.getRn() + "</RouteNumber>";
			xmlMessage = xmlMessage + "<ServiceType>" + portme.getService() + "</ServiceType>";
			if (portme.getCustomerData() != null) {
				xmlMessage = xmlMessage + "<CustomerData>";
				xmlMessage = xmlMessage + "<SubscriberId>" + portme.getCustomerData().getSubscriberId()
						+ "</SubscriberId>";
				xmlMessage = xmlMessage + "<Remark1>" + portme.getCustomerData().getRemark1() + "</Remark1>";
				xmlMessage = xmlMessage + "<Remark2>" + portme.getCustomerData().getRemark2() + "</Remark2>";
				xmlMessage = xmlMessage + "<Remark3>" + portme.getCustomerData().getRemark3() + "</Remark3>";
				xmlMessage = xmlMessage + "</CustomerData>";
			} else {
				xmlMessage = xmlMessage + "<PersonCustomer>";
				xmlMessage = xmlMessage + "<OwnerName>" + portme.getPersonCustomer().getOwnerName() + "</OwnerName>";
				xmlMessage = xmlMessage + "<OwnerId>" + portme.getPersonCustomer().getOwnerId() + "</OwnerId>";
				xmlMessage = xmlMessage + "<TypeOfId>" + portme.getPersonCustomer().getTypeOfId() + "</TypeOfId>";
				xmlMessage = xmlMessage + "<SignatureDate>" + timestamp + "</SignatureDate>";
				xmlMessage = xmlMessage + "</PersonCustomer>";
			}
			xmlMessage = xmlMessage + "<Author>";
			xmlMessage = xmlMessage + "<Name>xyz</Name>";
			xmlMessage = xmlMessage + "<Phone>12345</Phone>";
			xmlMessage = xmlMessage + "<Telefax>123456</Telefax>";
			xmlMessage = xmlMessage + "<Email>XYZ@xyz.com</Email>";
			xmlMessage = xmlMessage + "<Date>2009-07-22</Date>";
			xmlMessage = xmlMessage + "</Author>";
			xmlMessage = xmlMessage + "</NPO>";
			xmlMessage = xmlMessage + "</MNPFrame>";

			List results = GlobalXMLGenerator.transferToList(path, xmlMessage);
			if (results.size() > 1) {
				System.out.println("Result more than 1 items");
				for (int i = 1; i <= results.size(); i++) {
					System.out.println("Result " + i + ": XML[" + (String) ((Map) results.get(i - 1)).get("XML")
							+ "] Key[" + (String) ((Map) results.get(i - 1)).get("KEY") + "] MessageName["
							+ (String) ((Map) results.get(i - 1)).get("MESSAGE_NAME") + "]");
				}
				return "2";
			} else if (results.size() == 0) {
				System.out.println("No matched schema/selector found for the input XML");
				return "3";
			} else {
				System.out.println("Success");
				System.out.println((String) ((Map) results.get(0)).get("XML"));
				String xml = (String) ((Map) results.get(0)).get("XML");
				return xml;
			}
		} catch (

		Exception e) {
			System.out.println(e.getMessage());
			_logger.error(
					"NPOUtils.convertJsonIntoInitPortRequest() - unable to convert pojo into xml -" + e.getMessage());
		}
		return null;
	}

	public String convertJsonIntoInitPortRequestZone2(PortMe portme, String binaryFile, String transactionId) {
		try {
			String xmlMessage = "<NPCMessageData>";
			xmlMessage = xmlMessage + "<NPCData>";
			xmlMessage = xmlMessage + "<MessageHeader>";
			xmlMessage = xmlMessage + "<LSAID>" + portme.getLast_area() + "</LSAID>";
			xmlMessage = xmlMessage + "<PortType>MOBILE</PortType>";
			xmlMessage = xmlMessage + "<TransactionID>" + transactionId + "</TransactionID>";
			xmlMessage = xmlMessage + "<MessageID>1001</MessageID>";
			xmlMessage = xmlMessage + "<MsgCreateTimeStamp>" + getLocalDateTime() + "</MsgCreateTimeStamp>";
			xmlMessage = xmlMessage + "<Sender>" + portme.getSource() + "</Sender>";
			xmlMessage = xmlMessage + "</MessageHeader>";
			xmlMessage = xmlMessage + "<NPCMessage>";
			xmlMessage = xmlMessage + "<PortRequest>";
			if (portme.getSubscriberArrType().size() > 0) {
				for (SubscriberArrType ar : portme.getSubscriberArrType()) {
					xmlMessage = xmlMessage + "<NumberRange>";
					xmlMessage = xmlMessage + "<NumberFrom>" + ar.getMsisdn() + "</NumberFrom>";
					xmlMessage = xmlMessage + "<NumberTo>" + ar.getMsisdn() + "</NumberTo>";
					xmlMessage = xmlMessage + "<PortingCode>" + portme.getCompanyCode() + "</PortingCode>";// upc-conp
					xmlMessage = xmlMessage + "</NumberRange>";
				}
			} else {
				xmlMessage = xmlMessage + "<NumberRange>";
				xmlMessage = xmlMessage + "<NumberFrom>" + portme.getSubscriberSequence().getSubscriberNumber()
						+ "</NumberFrom>";
				xmlMessage = xmlMessage + "<NumberTo>" + portme.getSubscriberSequence().getSubscriberNumber()
						+ "</NumberTo>";
				xmlMessage = xmlMessage + "<PortingCode>" + portme.getCompanyCode() + "</PortingCode>";
				xmlMessage = xmlMessage + "</NumberRange>";
			}
			xmlMessage = xmlMessage + "<DonorLSAID>" + portme.getLast_area() + "</DonorLSAID>";// master is null then
																								// number plan
			xmlMessage = xmlMessage + "<Donor>" + portme.getDno() + "</Donor>";
			xmlMessage = xmlMessage + "<RecipientLSAID>" + portme.getArea() + "</RecipientLSAID>";
			xmlMessage = xmlMessage + "<Recipient>" + portme.getRno() + "</Recipient>";
			xmlMessage = xmlMessage + "<AccountPayType>" + portme.getService() + "</AccountPayType>";
			xmlMessage = xmlMessage + "<PortingCode>" + portme.getCompanyCode() + "</PortingCode>";
			if (portme.getCustomerData() != null) {
				// IF CORPORATE FLAG Y THEN WE ADD DOCUMENTFILENAME
				xmlMessage = xmlMessage + "<CorpPortFlag>Y</CorpPortFlag>";// Y/N when we put Y and N
				xmlMessage = xmlMessage + "<DocumentFileName>" + binaryFile + "</DocumentFileName>";// base64 bit upload
																									// document for
																									// corporate
			} else {
				xmlMessage = xmlMessage + "<CorpPortFlag>N</CorpPortFlag>";
			}
			xmlMessage = xmlMessage + "<SubRequestTime>" + getLocalDateTime() + "</SubRequestTime>";
			if (portme.getComment() != null) {
				xmlMessage = xmlMessage + "<Comments>" + portme.getComment() + "</Comments>";
			} else {
				xmlMessage = xmlMessage + "<Comments>Automatic</Comments>";
			}
			xmlMessage = xmlMessage + "</PortRequest>";
			xmlMessage = xmlMessage + "</NPCMessage>";
			xmlMessage = xmlMessage + "</NPCData>";
			xmlMessage = xmlMessage + "</NPCMessageData>";

			List results = GlobalXMLGenerator.transferToList(path, xmlMessage);
			if (results.size() > 1) {
				System.out.println("Result more than 1 items");
				for (int i = 1; i <= results.size(); i++) {
					System.out.println("Result " + i + ": XML[" + (String) ((Map) results.get(i - 1)).get("XML")
							+ "] Key[" + (String) ((Map) results.get(i - 1)).get("KEY") + "] MessageName["
							+ (String) ((Map) results.get(i - 1)).get("MESSAGE_NAME") + "]");
				}
				return "2";
			} else if (results.size() == 0) {
				System.out.println("No matched schema/selector found for the input XML");
				return "3";
			} else {
				System.out.println("Success");
				System.out.println((String) ((Map) results.get(0)).get("XML"));
				String xml = (String) ((Map) results.get(0)).get("XML");
				return xml;
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
			_logger.error(
					"NPOUtils.convertJsonIntoInitPortRequest() - unable to convert pojo into xml -" + e.getMessage());
		}
		return null;
	}

	public String convertPojoIntoXML(NPO npo) {
		try {
			String xmlMessage = "";
			xmlMessage = "<MNPFrame xmlns=\"MNPProtocol.xsd\">";
			xmlMessage = xmlMessage + "<NPO>";
			xmlMessage = xmlMessage + "<MessageSenderTelco>" + npo.getMessageSenderTelco() + "</MessageSenderTelco>";
			xmlMessage = xmlMessage + "<MessageReceiverTelco>" + npo.getMessageReceiverTelco()
					+ "</MessageReceiverTelco>";
			xmlMessage = xmlMessage + "<RequestId>" + npo.getRequestId() + "</RequestId>";
			xmlMessage = xmlMessage + "<Timestamp>" + npo.getTimestamp() + "</Timestamp>";
			xmlMessage = xmlMessage + "<ReferenceId>" + npo.getReferenceId() + "</ReferenceId>";

			xmlMessage = xmlMessage + "<RecipientTelco>" + npo.getRecipientTelco() + "</RecipientTelco>";
			xmlMessage = xmlMessage + "<DonorTelco>" + npo.getDonorTelco() + "</DonorTelco>";
			xmlMessage = xmlMessage + "<ByLOA>" + npo.getByLOA() + "</ByLOA>";

			if (npo.getSubscriberAuthSequence().getSubscriberAuthorization().size() > 0) {

				xmlMessage = xmlMessage + "<LOAImage>" + npo.getlOAImage() + "</LOAImage>";
				xmlMessage = xmlMessage + "<UndertakingAck>" + npo.getUndertakingAck() + "</UndertakingAck>";
				xmlMessage = xmlMessage + "<SubscriberAuthSequence>";
				for (SubscriberAuthorization subauth : npo.getSubscriberAuthSequence().getSubscriberAuthorization()) {
					xmlMessage = xmlMessage + "<SubscriberAuthorization>";
					xmlMessage = xmlMessage + "<SubscriberNumber>" + subauth.getSubscriberNumber()
							+ "</SubscriberNumber>";
					xmlMessage = xmlMessage + "<OwnerId>" + subauth.getOwnerId() + "</OwnerId>";
					xmlMessage = xmlMessage + "<TypeOfId>" + subauth.getTypeOfId() + "</TypeOfId>";
					xmlMessage = xmlMessage + "</SubscriberAuthorization>";
				}
				xmlMessage = xmlMessage + "</SubscriberAuthSequence>";

				xmlMessage = xmlMessage + "<LSA>" + npo.getLsa() + "</LSA>";
				xmlMessage = xmlMessage + "<RouteNumber>" + npo.getRouteNumber() + "</RouteNumber>";
				xmlMessage = xmlMessage + "<ServiceType>" + npo.getServiceType() + "</ServiceType>";
				xmlMessage = xmlMessage + "<CorporateCustomer>";
				xmlMessage = xmlMessage + "<CompanyName>" + npo.getCorporateCustomer().getCompanyName()
						+ "</CompanyName>";
				xmlMessage = xmlMessage + "<RegistrationCode>" + npo.getCorporateCustomer().getRegistrationCode()
						+ "</RegistrationCode>";
				xmlMessage = xmlMessage + "<AccountNumber>" + npo.getCorporateCustomer().getAccountNumber()
						+ "</AccountNumber>";
				xmlMessage = xmlMessage + "<SignatureDate>" + npo.getCorporateCustomer().getSignatureDate()
						+ "</SignatureDate>";
				xmlMessage = xmlMessage + "</CorporateCustomer>";

			} else {

				xmlMessage = xmlMessage + "<UndertakingAck>" + npo.getUndertakingAck() + "</UndertakingAck>";
				xmlMessage = xmlMessage + "<SubscriberSequence>";
				xmlMessage = xmlMessage + "<SubscriberNumber>"
						+ npo.getSubscriberSequence().get(0).getSubscriberNumber() + "</SubscriberNumber>";
				xmlMessage = xmlMessage + "</SubscriberSequence>";
				xmlMessage = xmlMessage + "<LSA>" + npo.getLsa() + "</LSA>";
				xmlMessage = xmlMessage + "<RouteNumber>" + npo.getRouteNumber() + "</RouteNumber>";
				xmlMessage = xmlMessage + "<ServiceType>" + npo.getServiceType() + "</ServiceType>";
				xmlMessage = xmlMessage + "<PersonCustomer>";
				xmlMessage = xmlMessage + "<OwnerName>" + npo.getPersonCustomer().getOwnerName() + "</OwnerName>";
				xmlMessage = xmlMessage + "<OwnerId>" + npo.getPersonCustomer().getOwnerId() + "</OwnerId>";
				xmlMessage = xmlMessage + "<TypeOfId>" + npo.getPersonCustomer().getTypeOfId() + "</TypeOfId>";
				xmlMessage = xmlMessage + "<SignatureDate>" + npo.getPersonCustomer().getSignatureDate()
						+ "</SignatureDate>";
				xmlMessage = xmlMessage + "</PersonCustomer>";

			}
			xmlMessage = xmlMessage + "<Author>";
			xmlMessage = xmlMessage + "<Name>" + npo.getAuthor().getName() + "</Name>";
			xmlMessage = xmlMessage + "<Phone>" + npo.getAuthor().getPhone() + "</Phone>";
			xmlMessage = xmlMessage + "<Telefax>" + npo.getAuthor().getTelefax() + "</Telefax>";
			xmlMessage = xmlMessage + "<Email>" + npo.getAuthor().getEmail() + "</Email>";
			xmlMessage = xmlMessage + "<Date>" + npo.getAuthor().getDate() + "</Date>";
			xmlMessage = xmlMessage + "</Author>";
			xmlMessage = xmlMessage + "</NPO>";
			xmlMessage = xmlMessage + "</MNPFrame>";
			List results = GlobalXMLGenerator.transferToList(path, xmlMessage);
			if (results.size() > 1) {
				System.out.println("Result more than 1 items");
				for (int i = 1; i <= results.size(); i++) {
					System.out.println("Result " + i + ": XML[" + (String) ((Map) results.get(i - 1)).get("XML")
							+ "] Key[" + (String) ((Map) results.get(i - 1)).get("KEY") + "] MessageName["
							+ (String) ((Map) results.get(i - 1)).get("MESSAGE_NAME") + "]");
				}
			} else if (results.size() == 0) {
				System.out.println("No matched schema/selector found for the input XML");
			} else {
				System.out.println("Success");
				System.out.println((String) ((Map) results.get(0)).get("XML"));
				String xml = (String) ((Map) results.get(0)).get("XML");
				return xml;
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
			_logger.error("NPOUtils.convertPojoIntoXML() - unable to convert pojo into xml -" + e.getMessage());
		}
		return null;
	}

	public String convertJsonIntoPortApproval(PortMe portme, int mch_type, String transactionId) {
		try {
			String xmlMessage = "";
			if (mch_type == 1) {
				String messageSenderTelco = ReadConfigFile.getProperties().getProperty("MessageSenderTelco-ZOOM");
				String messageReceiverTelco = ReadConfigFile.getProperties().getProperty("MessageReceiverTelco-mch1");
				xmlMessage = "<MNPFrame xmlns=\"MNPProtocol.xsd\">";
				xmlMessage = xmlMessage + "<NPOA>";
				xmlMessage = xmlMessage + "<MessageSenderTelco>" + messageSenderTelco + "</MessageSenderTelco>";
				xmlMessage = xmlMessage + "<MessageReceiverTelco>" + messageReceiverTelco + "</MessageReceiverTelco>";
				xmlMessage = xmlMessage + "<RequestId>" + transactionId + "</RequestId>";
				xmlMessage = xmlMessage + "<Timestamp>" + timestamp + "</Timestamp>";
				xmlMessage = xmlMessage + "<ReferenceId>" + portme.getRequestId() + "</ReferenceId>";
				xmlMessage = xmlMessage + "<OrderedTransferTime>" + timestamp + "</OrderedTransferTime>";
				xmlMessage = xmlMessage + "<ResultCode>" + portme.getRequestId() + "</ResultCode>";
				if (portme.getSubscriberArrType().size() > 0) {
					xmlMessage = xmlMessage + "<SubscriberResult>";
					for (SubscriberArrType ar : portme.getSubscriberArrType()) {
						xmlMessage = xmlMessage + "<SubscriberResult>";
						xmlMessage = xmlMessage + "<SubscriberNumber>" + ar.getMsisdn() + "</SubscriberNumber>";
						xmlMessage = xmlMessage + "<ResultCode>" + ar.getResultCode() + "</ResultCode>";
						xmlMessage = xmlMessage + "<ResultCode2>" + ar.getDummyMSISDN() + "</ResultCode2>";
						xmlMessage = xmlMessage + "<ResultCode3>" + ar.getHlr() + "</ResultCode3>";
						xmlMessage = xmlMessage + "<ResultText>" + ar.getSim() + "</ResultText>";
						xmlMessage = xmlMessage + "</SubscriberResult>";
					}
					xmlMessage = xmlMessage + "</SubscriberResult>";
				}
				xmlMessage = xmlMessage + "</NPOA>";
				xmlMessage = xmlMessage + "</MNPFrame>";
			} else {
				xmlMessage = "<NPCMessageData>";
				xmlMessage = xmlMessage + "<NPCData>";
				xmlMessage = xmlMessage + "<MessageHeader>";
				xmlMessage = xmlMessage + "<LSAID>" + portme.getArea() + "</LSAID>";
				xmlMessage = xmlMessage + "<PortType>MOBILE</PortType>";
				xmlMessage = xmlMessage + "<TransactionID>" + transactionId + "</TransactionID>";
				xmlMessage = xmlMessage + "<MessageID>1003</MessageID>";
				xmlMessage = xmlMessage + "<MsgCreateTimeStamp>" + getLocalDateTime() + "</MsgCreateTimeStamp>";
				xmlMessage = xmlMessage + "<Sender>" + portme.getSource() + "</Sender>";
				xmlMessage = xmlMessage + "</MessageHeader>";
				xmlMessage = xmlMessage + "<NPCMessage>";
				xmlMessage = xmlMessage + "<PortResponse>";
				if (portme.getSubscriberArrType().size() > 0) {
					for (SubscriberArrType ar : portme.getSubscriberArrType()) {
						xmlMessage = xmlMessage + "<NumberRangeFlagged>";
						xmlMessage = xmlMessage + "<NumberFrom>" + ar.getMsisdn() + "</NumberFrom>";
						xmlMessage = xmlMessage + "<NumberTo>" + ar.getPinCode() + "</NumberTo>";
						if (portme.getApproval() == "Accept") {
							xmlMessage = xmlMessage + "<RangeAccepted>0</RangeAccepted>";
						} else {
							xmlMessage = xmlMessage + "<RangeAccepted>1</RangeAccepted>";
							xmlMessage = xmlMessage + "<ReasonCode>1</ReasonCode>";
						}
						xmlMessage = xmlMessage + "</NumberRangeFlagged>";
					}
				} else {
					xmlMessage = xmlMessage + "<NumberRangeFlagged>";
					xmlMessage = xmlMessage + "<NumberFrom>" + portme.getSubscriberSequence().getSubscriberNumber()
							+ "</NumberFrom>";
					xmlMessage = xmlMessage + "<NumberTo>" + portme.getSubscriberSequence().getSubscriberNumber()
							+ "</NumberTo>";
					if (portme.getApproval() == "Accept") {
						xmlMessage = xmlMessage + "<RangeAccepted>0</RangeAccepted>";
					} else {
						xmlMessage = xmlMessage + "<RangeAccepted>1</RangeAccepted>";
						xmlMessage = xmlMessage + "<ReasonCode>1</ReasonCode>";
					}
					xmlMessage = xmlMessage + "</NumberRangeFlagged>";
				}
				xmlMessage = xmlMessage + "</PortResponse>";
				xmlMessage = xmlMessage + "</NPCMessage>";
				xmlMessage = xmlMessage + "</NPCData>";
				xmlMessage = xmlMessage + "</NPCMessageData>";
			}
			List results = GlobalXMLGenerator.transferToList(path, xmlMessage);
			if (results.size() > 1) {
				System.out.println("Result more than 1 items");
				for (int i = 1; i <= results.size(); i++) {
					System.out.println("Result " + i + ": XML[" + (String) ((Map) results.get(i - 1)).get("XML")
							+ "] Key[" + (String) ((Map) results.get(i - 1)).get("KEY") + "] MessageName["
							+ (String) ((Map) results.get(i - 1)).get("MESSAGE_NAME") + "]");
				}
				return "2";
			} else if (results.size() == 0) {
				System.out.println("No matched schema/selector found for the input XML");
				return "3";
			} else {
				System.out.println("Success");
				System.out.println((String) ((Map) results.get(0)).get("XML"));
				String xml = (String) ((Map) results.get(0)).get("XML");
				return xml;
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
			_logger.error(
					"NPOUtils.convertJsonIntoPortApproval() - unable to convert pojo into xml -" + e.getMessage());
		}
		return null;
	}

	public String convertJsonIntoNPOA(NPOA npoa) {
		try {
			String xmlMessage = "";
			xmlMessage = "<MNPFrame xmlns=\"MNPProtocol.xsd\">";
			xmlMessage = xmlMessage + "<NPOA>";
			xmlMessage = xmlMessage + "<MessageSenderTelco>" + npoa.getMessageSenderTelco() + "</MessageSenderTelco>";
			xmlMessage = xmlMessage + "<MessageReceiverTelco>" + npoa.getMessageReceiverTelco()
					+ "</MessageReceiverTelco>";
			xmlMessage = xmlMessage + "<RequestId>" + npoa.getRequestId() + "</RequestId>";
			xmlMessage = xmlMessage + "<Timestamp>" + timestamp + "</Timestamp>";
			xmlMessage = xmlMessage + "<ReferenceId>" + npoa.getReferenceId() + "</ReferenceId>";
			xmlMessage = xmlMessage + "<OrderedTransferTime>" + npoa.getOrderedTransferTime()
					+ "</OrderedTransferTime>";
			xmlMessage = xmlMessage + "<ResultCode>" + npoa.getResultCode() + "</ResultCode>";
			if (npoa.getSubscriberResult().size() > 0) {
				for (SubscriberResult subscriberResult : npoa.getSubscriberResult()) {
					xmlMessage = xmlMessage + "<SubscriberResult>";
					xmlMessage = xmlMessage + "<SubscriberNumber>" + subscriberResult.getSubscriberNumber()
							+ "</SubscriberNumber>";
					xmlMessage = xmlMessage + "<ResultCode>" + subscriberResult.getResultCode() + "</ResultCode>";
					xmlMessage = xmlMessage + "<ResultCode2>" + subscriberResult.getResultCode2() + "</ResultCode2>";
					xmlMessage = xmlMessage + "<ResultCode3>" + subscriberResult.getResultCode3() + "</ResultCode3>";
					xmlMessage = xmlMessage + "<ResultText>" + subscriberResult.getResultText() + "</ResultText>";
					xmlMessage = xmlMessage + "</SubscriberResult>";
				}
			}
			xmlMessage = xmlMessage + "</NPOA>";
			xmlMessage = xmlMessage + "</MNPFrame>";
			List results = GlobalXMLGenerator.transferToList(path, xmlMessage);
			if (results.size() > 1) {
				System.out.println("Result more than 1 items");
				for (int i = 1; i <= results.size(); i++) {
					System.out.println("Result " + i + ": XML[" + (String) ((Map) results.get(i - 1)).get("XML")
							+ "] Key[" + (String) ((Map) results.get(i - 1)).get("KEY") + "] MessageName["
							+ (String) ((Map) results.get(i - 1)).get("MESSAGE_NAME") + "]");
				}
				return "2";
			} else if (results.size() == 0) {
				System.out.println("No matched schema/selector found for the input XML");
				return "3";
			} else {
				System.out.println("Success");
				System.out.println((String) ((Map) results.get(0)).get("XML"));
				String xml = (String) ((Map) results.get(0)).get("XML");
				return xml;
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
			_logger.error("NPOUtils.convertJsonIntoNPOA() - unable to convert pojo into xml -" + e.getMessage());
		}
		return null;
	}

	public String convertJsonIntoNPOARsp(NPOA npoa) {
		try {
			String xmlMessage = "";
			xmlMessage = "<MNPFrame xmlns=\"MNPProtocol.xsd\">";
			xmlMessage = xmlMessage + "<NPOARsp>";
			xmlMessage = xmlMessage + "<MessageSenderTelco>" + npoa.getMessageSenderTelco() + "</MessageSenderTelco>";
			xmlMessage = xmlMessage + "<MessageReceiverTelco>" + npoa.getMessageReceiverTelco()
					+ "</MessageReceiverTelco>";
			xmlMessage = xmlMessage + "<RequestId>" + npoa.getRequestId() + "</RequestId>";
			xmlMessage = xmlMessage + "<Timestamp>" + timestamp + "</Timestamp>";
			xmlMessage = xmlMessage + "<ReferenceId>" + npoa.getReferenceId() + "</ReferenceId>";
			xmlMessage = xmlMessage + "<ResultCode>" + npoa.getResultCode() + "</ResultCode>";
			if (npoa.getSubscriberResult().size() > 0) {
				for (SubscriberResult subscirberResult : npoa.getSubscriberResult()) {
					xmlMessage = xmlMessage + "<SubscriberResult>";
					xmlMessage = xmlMessage + "<SubscriberNumber>" + subscirberResult.getSubscriberNumber()
							+ "</SubscriberNumber>";
					xmlMessage = xmlMessage + "<ResultCode>" + subscirberResult.getResultCode() + "</ResultCode>";
					xmlMessage = xmlMessage + "<ResultCode2>" + subscirberResult.getResultCode2() + "</ResultCode2>";
					xmlMessage = xmlMessage + "<ResultCode3>" + subscirberResult.getResultCode3() + "</ResultCode3>";
					xmlMessage = xmlMessage + "<ResultText>" + subscirberResult.getResultCode3() + "</ResultText>";
					xmlMessage = xmlMessage + "</SubscriberResult>";
				}
			}
			xmlMessage = xmlMessage + "<OrderedTransferTime>" + npoa.getOrderedTransferTime()
					+ "</OrderedTransferTime>";
			xmlMessage = xmlMessage + "<RecommendTransferTime>" + npoa.getRecommendTransferTime()
					+ "</RecommendTransferTime>";
			xmlMessage = xmlMessage + "<RecipientTelco>" + npoa.getRecipientTelco() + "</RecipientTelco>";
			xmlMessage = xmlMessage + "<LSA>" + npoa.getLsa() + "</LSA>";
			xmlMessage = xmlMessage + "</NPOARsp>";
			xmlMessage = xmlMessage + "</MNPFrame>";
			List results = GlobalXMLGenerator.transferToList(path, xmlMessage);
			if (results.size() > 1) {
				System.out.println("Result more than 1 items");
				for (int i = 1; i <= results.size(); i++) {
					System.out.println("Result " + i + ": XML[" + (String) ((Map) results.get(i - 1)).get("XML")
							+ "] Key[" + (String) ((Map) results.get(i - 1)).get("KEY") + "] MessageName["
							+ (String) ((Map) results.get(i - 1)).get("MESSAGE_NAME") + "]");
				}
				return "2";
			} else if (results.size() == 0) {
				System.out.println("No matched schema/selector found for the input XML");
				return "3";
			} else {
				System.out.println("Success");
				System.out.println((String) ((Map) results.get(0)).get("XML"));
				String xml = (String) ((Map) results.get(0)).get("XML");
				return xml;
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
			_logger.error("NPOUtils.convertJsonIntoNPOARsp() - unable to convert pojo into xml -" + e.getMessage());
		}
		return null;
	}

	public String convertJsonIntoPortDisconAns(PortMe portme, int mch_type, String transactionId) {
		try {
			String xmlMessage = "";
			if (mch_type == 1) {
				String messageSenderTelco = ReadConfigFile.getProperties().getProperty("MessageSenderTelco-ZOOM");
				String messageReceiverTelco = ReadConfigFile.getProperties().getProperty("MessageReceiverTelco-mch1");
				xmlMessage = "<MNPFrame xmlns=\"MNPProtocol.xsd\">";
				xmlMessage = xmlMessage + "<SDA>";
				xmlMessage = xmlMessage + "<MessageSenderTelco>" + messageSenderTelco + "</MessageSenderTelco>";
				xmlMessage = xmlMessage + "<MessageReceiverTelco>" + messageReceiverTelco + "</MessageReceiverTelco>";
				xmlMessage = xmlMessage + "<RequestId>" + transactionId + "</RequestId>";
				xmlMessage = xmlMessage + "<Timestamp>" + timestamp + "</Timestamp>";
				if (portme.getMsisdnUID().size() > 0) {
					// xmlMessage = xmlMessage + "<SubscriberResult>";
					for (SubscriberArrType msisdn : portme.getSubscriberArrType()) {
						xmlMessage = xmlMessage + "<SubscriberResult>";
						xmlMessage = xmlMessage + "<SubscriberNumber>" + msisdn.getMsisdn() + "</SubscriberNumber>";
						xmlMessage = xmlMessage + "<ResultCode>" + 200 + "</ResultCode>";
						xmlMessage = xmlMessage + "<ResultCode2>" + 200 + "</ResultCode2>";
						xmlMessage = xmlMessage + "<ResultCode3>" + 200 + "</ResultCode3>";
						xmlMessage = xmlMessage + "<ResultText>" + 200 + "</ResultText>";
						xmlMessage = xmlMessage + "</SubscriberResult>";
					}
					// xmlMessage = xmlMessage + "</SubscriberResult>";
				}
				xmlMessage = xmlMessage + "</SDA>";
				xmlMessage = xmlMessage + "</MNPFrame>";
			} else {
				xmlMessage = "<NPCMessageData>";
				xmlMessage = xmlMessage + "<NPCData>";
				xmlMessage = xmlMessage + "<MessageHeader>";
				xmlMessage = xmlMessage + "<LSAID>" + portme.getArea() + "</LSAID>";
				xmlMessage = xmlMessage + "<PortType>MOBILE</PortType>";
				xmlMessage = xmlMessage + "<TransactionID>" + transactionId + "</TransactionID>";
				xmlMessage = xmlMessage + "<MessageID>7011</MessageID>";
				xmlMessage = xmlMessage + "<MsgCreateTimeStamp>" + getLocalDateTime() + "</MsgCreateTimeStamp>";
				xmlMessage = xmlMessage + "<Sender>" + portme.getSource() + "</Sender>";
				xmlMessage = xmlMessage + "</MessageHeader>";
				xmlMessage = xmlMessage + "<NPCMessage>";
				xmlMessage = xmlMessage + "<PortDeact>";
				if (portme.getSubscriberArrType().size() > 0) {
					for (SubscriberArrType item : portme.getSubscriberArrType()) {
						xmlMessage = xmlMessage + "<NumberRange>";
						xmlMessage = xmlMessage + "<NumberFrom>" + item.getMsisdn() + "</NumberFrom>";
						xmlMessage = xmlMessage + "<NumberTo>" + item.getMsisdn() + "</NumberTo>";
						xmlMessage = xmlMessage + "</NumberRange>";
					}
				} else {
					xmlMessage = xmlMessage + "<NumberRange>";
					xmlMessage = xmlMessage + "<NumberFrom>" + portme.getSubscriberSequence().getSubscriberNumber()
							+ "</NumberFrom>";
					xmlMessage = xmlMessage + "<NumberTo>" + portme.getSubscriberSequence().getSubscriberNumber()
							+ "</NumberTo>";
					xmlMessage = xmlMessage + "</NumberRange>";
				}
				xmlMessage = xmlMessage + "</PortDeact>";
				xmlMessage = xmlMessage + "</NPCMessage>";
				xmlMessage = xmlMessage + "</NPCData>";
				xmlMessage = xmlMessage + "</NPCMessageData>";
			}
			List results = GlobalXMLGenerator.transferToList(path, xmlMessage);
			if (results.size() > 1) {
				System.out.println("Result more than 1 items");
				for (int i = 1; i <= results.size(); i++) {
					System.out.println("Result " + i + ": XML[" + (String) ((Map) results.get(i - 1)).get("XML")
							+ "] Key[" + (String) ((Map) results.get(i - 1)).get("KEY") + "] MessageName["
							+ (String) ((Map) results.get(i - 1)).get("MESSAGE_NAME") + "]");
				}
				return "2";
			} else if (results.size() == 0) {
				System.out.println("No matched schema/selector found for the input XML");
				return "3";
			} else {
				System.out.println("Success");
				System.out.println((String) ((Map) results.get(0)).get("XML"));
				String xml = (String) ((Map) results.get(0)).get("XML");
				return xml;
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
			_logger.error(
					"NPOUtils.convertJsonIntoPortDisconAns() - unable to convert pojo into xml -" + e.getMessage());
		}
		return null;
	}

	public String convertJsonIntoConnectionAnswer(PortMe portme, int mch_type, String transactionId) {
		try {
			String xmlMessage = "";
			if (mch_type == 1) {
				String messageSenderTelco = ReadConfigFile.getProperties().getProperty("MessageSenderTelco-ZOOM");
				String messageReceiverTelco = ReadConfigFile.getProperties().getProperty("MessageReceiverTelco-mch1");
				xmlMessage = "<MNPFrame xmlns=\"MNPProtocol.xsd\">";
				xmlMessage = xmlMessage + "<SCA>";
				xmlMessage = xmlMessage + "<MessageSenderTelco>" + messageSenderTelco + "</MessageSenderTelco>";
				xmlMessage = xmlMessage + "<MessageReceiverTelco>" + messageReceiverTelco + "</MessageReceiverTelco>";
				xmlMessage = xmlMessage + "<RequestId>" + transactionId + "</RequestId>";
				xmlMessage = xmlMessage + "<Timestamp>" + timestamp + "</Timestamp>";
				if (portme.getMsisdnUID().size() > 0) {
					for (MSISDNUIDType msisdn : portme.getMsisdnUID()) {
						xmlMessage = xmlMessage + "<SubscriberResult>";
						xmlMessage = xmlMessage + "<SubscriberNumber>" + msisdn.getMsisdn() + "</SubscriberNumber>";
						xmlMessage = xmlMessage + "<ResultCode>0</ResultCode>";
						xmlMessage = xmlMessage + "</SubscriberResult>";
					}
				}
				xmlMessage = xmlMessage + "</SCA>";
				xmlMessage = xmlMessage + "</MNPFrame>";
			} else {
				xmlMessage = "<NPCMessageData>";
				xmlMessage = xmlMessage + "<NPCData>";
				xmlMessage = xmlMessage + "<MessageHeader>";
				xmlMessage = xmlMessage + "<LSAID>" + portme.getArea() + "</LSAID>";
				xmlMessage = xmlMessage + "<PortType>MOBILE</PortType>";
				xmlMessage = xmlMessage + "<TransactionID>" + transactionId + "</TransactionID>";
				xmlMessage = xmlMessage + "<MessageID>1008</MessageID>";
				xmlMessage = xmlMessage + "<MsgCreateTimeStamp>" + getLocalDateTime() + "</MsgCreateTimeStamp>";
				xmlMessage = xmlMessage + "<Sender>" + portme.getSource() + "</Sender>";
				xmlMessage = xmlMessage + "</MessageHeader>";
				xmlMessage = xmlMessage + "<NPCMessage>";
				xmlMessage = xmlMessage + "<PortActivated>";
				if (portme.getSubscriberArrType().size() > 0) {
					for (SubscriberArrType item : portme.getSubscriberArrType()) {
						xmlMessage = xmlMessage + "<NumberRange>";
						xmlMessage = xmlMessage + "<NumberFrom>" + item.getMsisdn() + "</NumberFrom>";
						xmlMessage = xmlMessage + "<NumberTo>" + item.getMsisdn() + "</NumberTo>";
						xmlMessage = xmlMessage + "</NumberRange>";
					}
				} else {
					xmlMessage = xmlMessage + "<NumberRange>";
					xmlMessage = xmlMessage + "<NumberFrom>" + portme.getSubscriberSequence().getSubscriberNumber()
							+ "</NumberFrom>";
					xmlMessage = xmlMessage + "<NumberTo>" + portme.getSubscriberSequence().getSubscriberNumber()
							+ "</NumberTo>";
					xmlMessage = xmlMessage + "</NumberRange>";
				}
				xmlMessage = xmlMessage + "<Route>" + portme.getRn() + "</Route>";
				xmlMessage = xmlMessage + "</PortActivated>";
				xmlMessage = xmlMessage + "</NPCMessage>";
				xmlMessage = xmlMessage + "</NPCData>";
				xmlMessage = xmlMessage + "</NPCMessageData>";
			}
			List results = GlobalXMLGenerator.transferToList(path, xmlMessage);
			if (results.size() > 1) {
				System.out.println("Result more than 1 items");
				for (int i = 1; i <= results.size(); i++) {
					System.out.println("Result " + i + ": XML[" + (String) ((Map) results.get(i - 1)).get("XML")
							+ "] Key[" + (String) ((Map) results.get(i - 1)).get("KEY") + "] MessageName["
							+ (String) ((Map) results.get(i - 1)).get("MESSAGE_NAME") + "]");
				}
				return "2";
			} else if (results.size() == 0) {
				System.out.println("No matched schema/selector found for the input XML");
				return "3";
			} else {
				System.out.println("Success");
				System.out.println((String) ((Map) results.get(0)).get("XML"));
				String xml = (String) ((Map) results.get(0)).get("XML");
				return xml;
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
			_logger.error(
					"NPOUtils.convertJsonIntoConnectionAnswer() - unable to convert pojo into xml -" + e.getMessage());
		}
		return null;
	}

	public String convertSDTypeIntoXML(SD sdType) {
		try {
			String xmlMessage = "";
			xmlMessage = "<MNPFrame xmlns=\"MNPProtocol.xsd\">";
			xmlMessage = xmlMessage + "<SD>";
			xmlMessage = xmlMessage + "<MessageSenderTelco>" + sdType.getMessageSenderTelco() + "</MessageSenderTelco>";
			xmlMessage = xmlMessage + "<MessageReceiverTelco>" + sdType.getMessageReceiverTelco()
					+ "</MessageReceiverTelco>";
			xmlMessage = xmlMessage + "<RequestId>" + sdType.getRequestId() + "</RequestId>";
			xmlMessage = xmlMessage + "<Timestamp>" + timestamp + "</Timestamp>";
			if (sdType.getSDInfo().size() > 0) {
				for (SDInfo sdInfo : sdType.getSDInfo()) {
					xmlMessage = xmlMessage + "<SDInfo>";
					xmlMessage = xmlMessage + "<SubscriberNumber>" + sdInfo.getSubscriberNumber()
							+ "</SubscriberNumber>";
					xmlMessage = xmlMessage + "<ReferenceId>" + sdInfo.getReferenceId() + "</ReferenceId>";
					xmlMessage = xmlMessage + "</SDInfo>";
				}
			}
			xmlMessage = xmlMessage + "</SD>";
			xmlMessage = xmlMessage + "</MNPFrame>";
			List results = GlobalXMLGenerator.transferToList(path, xmlMessage);
			if (results.size() > 1) {
				System.out.println("Result more than 1 items");
				for (int i = 1; i <= results.size(); i++) {
					System.out.println("Result " + i + ": XML[" + (String) ((Map) results.get(i - 1)).get("XML")
							+ "] Key[" + (String) ((Map) results.get(i - 1)).get("KEY") + "] MessageName["
							+ (String) ((Map) results.get(i - 1)).get("MESSAGE_NAME") + "]");
				}
			} else if (results.size() == 0) {
				System.out.println("No matched schema/selector found for the input XML");
			} else {
				System.out.println("Success");
				System.out.println((String) ((Map) results.get(0)).get("XML"));
				String xml = (String) ((Map) results.get(0)).get("XML");
				return xml;
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
			_logger.error("NPOUtils.convertSDTypeIntoXML() - unable to convert pojo into xml -" + e.getMessage());
		}
		return null;
	}

	public String convertSCTypeIntoXML(SC scType) {
		try {
			String xmlMessage = "";
			xmlMessage = "<MNPFrame xmlns=\"MNPProtocol.xsd\">";
			xmlMessage = xmlMessage + "<SC>";
			xmlMessage = xmlMessage + "<MessageSenderTelco>" + scType.getMessageSenderTelco() + "</MessageSenderTelco>";
			xmlMessage = xmlMessage + "<MessageReceiverTelco>" + scType.getMessageReceiverTelco()
					+ "</MessageReceiverTelco>";
			xmlMessage = xmlMessage + "<RequestId>" + scType.getRequestId() + "</RequestId>";
			xmlMessage = xmlMessage + "<Timestamp>" + timestamp + "</Timestamp>";
			if (scType.getSCInfo().size() > 0) {
				for (SCInfo scInfo : scType.getSCInfo()) {
					xmlMessage = xmlMessage + "<SCInfo>";
					xmlMessage = xmlMessage + "<SubscriberNumber>" + scInfo.getSubscriberNumber()
							+ "</SubscriberNumber>";
					xmlMessage = xmlMessage + "<ReferenceId>" + scInfo.getReferenceId() + "</ReferenceId>";
					xmlMessage = xmlMessage + "</SCInfo>";
				}
			}
			xmlMessage = xmlMessage + "</SC>";
			xmlMessage = xmlMessage + "</MNPFrame>";
			List results = GlobalXMLGenerator.transferToList(path, xmlMessage);
			if (results.size() > 1) {
				System.out.println("Result more than 1 items");
				for (int i = 1; i <= results.size(); i++) {
					System.out.println("Result " + i + ": XML[" + (String) ((Map) results.get(i - 1)).get("XML")
							+ "] Key[" + (String) ((Map) results.get(i - 1)).get("KEY") + "] MessageName["
							+ (String) ((Map) results.get(i - 1)).get("MESSAGE_NAME") + "]");
				}
			} else if (results.size() == 0) {
				System.out.println("No matched schema/selector found for the input XML");
			} else {
				System.out.println("Success");
				System.out.println((String) ((Map) results.get(0)).get("XML"));
				String xml = (String) ((Map) results.get(0)).get("XML");
				return xml;
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
			_logger.error("NPOUtils.convertSCTypeIntoXML() - unable to convert pojo into xml -" + e.getMessage());
		}
		return null;
	}

	public String convertInitAckIntoXML(InitAck initAck) {
		try {
			String xmlMessage = "";
			xmlMessage = "<MNPFrame xmlns=\"MNPProtocol.xsd\">";
			xmlMessage = xmlMessage + "<ACKNOWLEDGEMENT>";
			xmlMessage = xmlMessage + "<Orginator>" + initAck.getOrginator() + "</Orginator>";
			xmlMessage = xmlMessage + "<TimeStamp>" + timestamp + "</TimeStamp>";
			xmlMessage = xmlMessage + "<RequestId>" + initAck.getRequestId() + "</RequestId>";
			xmlMessage = xmlMessage + "</ACKNOWLEDGEMENT>";
			xmlMessage = xmlMessage + "</MNPFrame>";
			List results = GlobalXMLGenerator.transferToList(path, xmlMessage);
			if (results.size() > 1) {
				System.out.println("Result more than 1 items");
				for (int i = 1; i <= results.size(); i++) {
					System.out.println("Result " + i + ": XML[" + (String) ((Map) results.get(i - 1)).get("XML")
							+ "] Key[" + (String) ((Map) results.get(i - 1)).get("KEY") + "] MessageName["
							+ (String) ((Map) results.get(i - 1)).get("MESSAGE_NAME") + "]");
				}
			} else if (results.size() == 0) {
				System.out.println("No matched schema/selector found for the input XML");
			} else {
				System.out.println("Success");
				System.out.println((String) ((Map) results.get(0)).get("XML"));
				String xml = (String) ((Map) results.get(0)).get("XML");
				return xml;
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
			_logger.error("NPOUtils.convertInitAckIntoXML() - unable to convert pojo into xml -" + e.getMessage());
		}
		return null;
	}

	public String convertJsonIntoTerminationSoap(TerminateSim termination, List<TerminateSimMT> listMSISDN,
			int mch_type, String area, MasterNP masterNP) {
		try {
			String xmlMessage = "";
			if (mch_type == 1) {
				String messageSenderTelco = ReadConfigFile.getProperties().getProperty("MessageSenderTelco-ZOOM");
				String messageReceiverTelco = ReadConfigFile.getProperties().getProperty("MessageReceiverTelco-mch1");
				xmlMessage = "<MNPFrame xmlns=\"MNPProtocol.xsd\">";
				xmlMessage = xmlMessage + "<NPOT>";
				xmlMessage = xmlMessage + "<MessageSenderTelco>" + messageSenderTelco + "</MessageSenderTelco>";
				xmlMessage = xmlMessage + "<MessageReceiverTelco>" + messageReceiverTelco + "</MessageReceiverTelco>";
				xmlMessage = xmlMessage + "<RequestId>" + termination.getRequestId() + "</RequestId>";
				xmlMessage = xmlMessage + "<Timestamp>" + timestamp + "</Timestamp>";
				xmlMessage = xmlMessage + "<ReferenceId>" + timestamp + "</ReferenceId>";
				if (listMSISDN.size() > 0) {
					xmlMessage = xmlMessage + "<SubscriberSequence>";
					for (TerminateSimMT msisdn : listMSISDN) {
						xmlMessage = xmlMessage + "<SubscriberSequence>";
						xmlMessage = xmlMessage + "<SubscriberNumber>" + msisdn.getSubscriberNumber()
								+ "</SubscriberNumber>";
						xmlMessage = xmlMessage + "</SubscriberSequence>";
					}
					xmlMessage = xmlMessage + "</SubscriberSequence>";
				}
				xmlMessage = xmlMessage + "<LSA>" + termination.getSource() + "</LSA>";
				xmlMessage = xmlMessage + "<OrderedTransferTime>" + termination.getTimeStamp()
						+ "</OrderedTransferTime>";
				xmlMessage = xmlMessage + "<OrderedApprovalTime>" + termination.getTimeStamp()
						+ "</OrderedApprovalTime>";
				xmlMessage = xmlMessage + "</NPOT>";
				xmlMessage = xmlMessage + "</MNPFrame>";
			} else {
				xmlMessage = "<NPCMessageData>";
				xmlMessage = xmlMessage + "<NPCData>";
				xmlMessage = xmlMessage + "<MessageHeader>";
				xmlMessage = xmlMessage + "<LSAID>" + area + "</LSAID>";
				xmlMessage = xmlMessage + "<PortType>MOBILE</PortType>";
				xmlMessage = xmlMessage + "<TransactionID>" + termination.getRequestId() + "</TransactionID>";
				xmlMessage = xmlMessage + "<MessageID>9001</MessageID>";
				xmlMessage = xmlMessage + "<MsgCreateTimeStamp>" + getLocalDateTime() + "</MsgCreateTimeStamp>";
				xmlMessage = xmlMessage + "<Sender>" + termination.getSource() + "</Sender>";
				xmlMessage = xmlMessage + "</MessageHeader>";
				xmlMessage = xmlMessage + "<NPCMessage>";
				xmlMessage = xmlMessage + "<PortTerminated>";
				for (TerminateSimMT msisdn : listMSISDN) {
					xmlMessage = xmlMessage + "<NumberRange>";
					xmlMessage = xmlMessage + "<NumberFrom>" + msisdn.getSubscriberNumber() + "</NumberFrom>";
					xmlMessage = xmlMessage + "<NumberTo>" + msisdn.getSubscriberNumber() + "</NumberTo>";
					xmlMessage = xmlMessage + "</NumberRange>";
				}
				xmlMessage = xmlMessage + "<DonorLSAID>" + masterNP.getArea() + "</DonorLSAID>";
				xmlMessage = xmlMessage + "<Donor>" + masterNP.getPresent_carrier() + "</Donor>";
				xmlMessage = xmlMessage + "<RecipientLSAID>" + masterNP.getArea() + "</RecipientLSAID>";
				xmlMessage = xmlMessage + "<Recipient>" + masterNP.getPresent_carrier() + "</Recipient>";
				xmlMessage = xmlMessage + "</PortTerminated>";
				xmlMessage = xmlMessage + "</NPCMessage>";
				xmlMessage = xmlMessage + "</NPCData>";
				xmlMessage = xmlMessage + "</NPCMessageData>";
			}
			List results = GlobalXMLGenerator.transferToList(path, xmlMessage);
			if (results.size() > 1) {
				System.out.println("Result more than 1 items");
				for (int i = 1; i <= results.size(); i++) {
					System.out.println("Result " + i + ": XML[" + (String) ((Map) results.get(i - 1)).get("XML")
							+ "] Key[" + (String) ((Map) results.get(i - 1)).get("KEY") + "] MessageName["
							+ (String) ((Map) results.get(i - 1)).get("MESSAGE_NAME") + "]");
				}
				return "2";
			} else if (results.size() == 0) {
				System.out.println("No matched schema/selector found for the input XML");
				return "3";
			} else {
				System.out.println("Success");
				System.out.println((String) ((Map) results.get(0)).get("XML"));
				String xml = (String) ((Map) results.get(0)).get("XML");
				return xml;
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
			_logger.error(
					"NPOUtils.convertJsonIntoPortMeTermination() - unable to convert pojo into xml -" + e.getMessage());
		}
		return null;
	}

	public String convertJsonIntoNPOT(NPOT npot) {
		try {
			String xmlMessage = "";
			xmlMessage = "<MNPFrame xmlns=\"MNPProtocol.xsd\">";
			xmlMessage = xmlMessage + "<NPOT>";
			xmlMessage = xmlMessage + "<MessageSenderTelco>" + npot.getMessageSenderTelco() + "</MessageSenderTelco>";
			xmlMessage = xmlMessage + "<MessageReceiverTelco>" + npot.getMessageReceiverTelco()
					+ "</MessageReceiverTelco>";
			xmlMessage = xmlMessage + "<RequestId>" + npot.getRequestId() + "</RequestId>";
			xmlMessage = xmlMessage + "<Timestamp>" + timestamp + "</Timestamp>";
			xmlMessage = xmlMessage + "<ReferenceId>" + npot.getReferenceId() + "</ReferenceId>";
			if (npot.getSubscriberSequence().size() > 0) {
				for (SubscriberSequence msisdn : npot.getSubscriberSequence()) {
					xmlMessage = xmlMessage + "<SubscriberSequence>";
					xmlMessage = xmlMessage + "<SubscriberNumber>" + msisdn.getSubscriberNumber()
							+ "</SubscriberNumber>";
					xmlMessage = xmlMessage + "</SubscriberSequence>";
				}
			}
			xmlMessage = xmlMessage + "<LSA>" + npot.getLsa() + "</LSA>";
			xmlMessage = xmlMessage + "<OrderedTransferTime>" + npot.getOrderedTransferTime()
					+ "</OrderedTransferTime>";
			xmlMessage = xmlMessage + "<OrderedApprovalTime>" + npot.getOrderedApprovalTime()
					+ "</OrderedApprovalTime>";
			xmlMessage = xmlMessage + "</NPOT>";
			xmlMessage = xmlMessage + "</MNPFrame>";
			List results = GlobalXMLGenerator.transferToList(path, xmlMessage);
			if (results.size() > 1) {
				System.out.println("Result more than 1 items");
				for (int i = 1; i <= results.size(); i++) {
					System.out.println("Result " + i + ": XML[" + (String) ((Map) results.get(i - 1)).get("XML")
							+ "] Key[" + (String) ((Map) results.get(i - 1)).get("KEY") + "] MessageName["
							+ (String) ((Map) results.get(i - 1)).get("MESSAGE_NAME") + "]");
				}
				return "2";
			} else if (results.size() == 0) {
				System.out.println("No matched schema/selector found for the input XML");
				return "3";
			} else {
				System.out.println("Success");
				System.out.println((String) ((Map) results.get(0)).get("XML"));
				String xml = (String) ((Map) results.get(0)).get("XML");
				return xml;
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
			_logger.error(
					"NPOUtils.convertJsonIntoPortMeTermination() - unable to convert pojo into xml -" + e.getMessage());
		}
		return null;
	}

	public String convertJsonIntoNPOTAConfirmation(TerminateSim termination, int mch_type) {
		try {
			String xmlMessage = "";
			if (mch_type == 1) {
				String messageSenderTelco = ReadConfigFile.getProperties().getProperty("MessageSenderTelco-ZOOM");
				String messageReceiverTelco = ReadConfigFile.getProperties().getProperty("MessageReceiverTelco-mch1");
				xmlMessage = "<MNPFrame xmlns=\"MNPProtocol.xsd\">";
				xmlMessage = xmlMessage + "<NPOTA>";
				xmlMessage = xmlMessage + "<MessageSenderTelco>" + messageSenderTelco + "</MessageSenderTelco>";
				xmlMessage = xmlMessage + "<MessageReceiverTelco>" + messageReceiverTelco + "</MessageReceiverTelco>";
				xmlMessage = xmlMessage + "<RequestId>" + termination.getRequestId() + "</RequestId>";
				xmlMessage = xmlMessage + "<Timestamp>" + timestamp + "</Timestamp>";
				xmlMessage = xmlMessage + "<ReferenceId>" + timestamp + "</ReferenceId>";
				xmlMessage = xmlMessage + "<ResultCode>" + termination.getApproval() + "</ResultCode>";
				if (termination.getMsisdnUID().size() > 0) {
					xmlMessage = xmlMessage + "<SubscriberResult>";
					for (MSISDNUIDType msisdn : termination.getMsisdnUID()) {
						xmlMessage = xmlMessage + "<SubscriberResult>";
						xmlMessage = xmlMessage + "<SubscriberNumber>" + msisdn.getMsisdn() + "</SubscriberNumber>";
						xmlMessage = xmlMessage + "<ResultCode>" + msisdn.getRequestId() + "</ResultCode>";
						xmlMessage = xmlMessage + "<ResultCode2>" + msisdn.getRequestId() + "</ResultCode2>";
						xmlMessage = xmlMessage + "<ResultCode3>" + msisdn.getRequestId() + "</ResultCode3>";
						xmlMessage = xmlMessage + "<ResultText>" + msisdn.getRequestId() + "</ResultText>";
						xmlMessage = xmlMessage + "</SubscriberResult>";
					}
					xmlMessage = xmlMessage + "</SubscriberResult>";
				}
				xmlMessage = xmlMessage + "</NPOTA>";
				xmlMessage = xmlMessage + "</MNPFrame>";
			} else {
				// we put here zone2 xml format
			}
			List results = GlobalXMLGenerator.transferToList(path, xmlMessage);
			if (results.size() > 1) {
				System.out.println("Result more than 1 items");
				for (int i = 1; i <= results.size(); i++) {
					System.out.println("Result " + i + ": XML[" + (String) ((Map) results.get(i - 1)).get("XML")
							+ "] Key[" + (String) ((Map) results.get(i - 1)).get("KEY") + "] MessageName["
							+ (String) ((Map) results.get(i - 1)).get("MESSAGE_NAME") + "]");
				}
				return "2";
			} else if (results.size() == 0) {
				System.out.println("No matched schema/selector found for the input XML");
				return "3";
			} else {
				System.out.println("Success");
				System.out.println((String) ((Map) results.get(0)).get("XML"));
				String xml = (String) ((Map) results.get(0)).get("XML");
				return xml;
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
			_logger.error("NPOUtils.convertJsonIntoNPOTAType() - unable to convert pojo into xml -" + e.getMessage());
		}
		return null;
	}

	public String convertJsonIntoOrderCancellation(OrderCancellation orderCancel) {
		try {
			String xmlMessage = "";
			xmlMessage = "<MNPFrame xmlns=\"MNPProtocol.xsd\">";
			xmlMessage = xmlMessage + "<CAN>";
			xmlMessage = xmlMessage + "<MessageSenderTelco>" + orderCancel.getSource() + "</MessageSenderTelco>";
			xmlMessage = xmlMessage + "<MessageReceiverTelco>999</MessageReceiverTelco>";
			xmlMessage = xmlMessage + "<RequestId>" + orderCancel.getRequestId() + "</RequestId>";
			xmlMessage = xmlMessage + "<Timestamp>" + timestamp + "</Timestamp>";
			xmlMessage = xmlMessage + "<ReferenceId>" + timestamp + "</ReferenceId>";
			if (orderCancel.getMsisdnUID().size() > 0) {
				xmlMessage = xmlMessage + "<SubscriberResult>";
				for (MSISDNUIDType msisdn : orderCancel.getMsisdnUID()) {
					xmlMessage = xmlMessage + "<SubscriberResult>";
					xmlMessage = xmlMessage + "<SubscriberNumber>" + msisdn.getMsisdn() + "</SubscriberNumber>";
					xmlMessage = xmlMessage + "</SubscriberResult>";
				}
				xmlMessage = xmlMessage + "</SubscriberResult>";
			}
			xmlMessage = xmlMessage + "<ReasonCode>" + orderCancel.getComment() + "</ReasonCode>";
			xmlMessage = xmlMessage + "<ReasonText>" + orderCancel.getComment() + "</ReasonText>";
			xmlMessage = xmlMessage + "</CAN>";
			xmlMessage = xmlMessage + "</MNPFrame>";
			List results = GlobalXMLGenerator.transferToList(path, xmlMessage);
			if (results.size() > 1) {
				System.out.println("Result more than 1 items");
				for (int i = 1; i <= results.size(); i++) {
					System.out.println("Result " + i + ": XML[" + (String) ((Map) results.get(i - 1)).get("XML")
							+ "] Key[" + (String) ((Map) results.get(i - 1)).get("KEY") + "] MessageName["
							+ (String) ((Map) results.get(i - 1)).get("MESSAGE_NAME") + "]");
				}
				return "2";
			} else if (results.size() == 0) {
				System.out.println("No matched schema/selector found for the input XML");
				return "3";
			} else {
				System.out.println("Success");
				System.out.println((String) ((Map) results.get(0)).get("XML"));
				String xml = (String) ((Map) results.get(0)).get("XML");
				return xml;
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
			_logger.error("NPOUtils.convertJsonIntoNPOTAType() - unable to convert pojo into xml -" + e.getMessage());
		}
		return null;
	}

	public String convertJsonIntoOrderReversal(OrderReversal reversal, int mch_type, String transactionId,
			String donorLSAID) {
		try {
			String xmlMessage = "";
			if (mch_type == 1) {
				String messageSenderTelco = ReadConfigFile.getProperties().getProperty("MessageSenderTelco-ZOOM");
				String messageReceiverTelco = ReadConfigFile.getProperties().getProperty("MessageReceiverTelco-mch1");
				xmlMessage = "<MNPFrame xmlns=\"MNPProtocol.xsd\">";
				xmlMessage = xmlMessage + "<REV>";
				xmlMessage = xmlMessage + "<MessageSenderTelco>" + messageSenderTelco + "</MessageSenderTelco>";
				xmlMessage = xmlMessage + "<MessageReceiverTelco>" + messageReceiverTelco + "</MessageReceiverTelco>";
				xmlMessage = xmlMessage + "<RequestId>" + reversal.getRequestId() + "</RequestId>";
				xmlMessage = xmlMessage + "<Timestamp>" + timestamp + "</Timestamp>";
				xmlMessage = xmlMessage + "<ReferenceId>" + timestamp + "</ReferenceId>";
				if (reversal.getMsisdnUID().size() > 0) {
					xmlMessage = xmlMessage + "<SubscriberResult>";
					for (MSISDNUIDType msisdn : reversal.getMsisdnUID()) {
						xmlMessage = xmlMessage + "<SubscriberResult>";
						xmlMessage = xmlMessage + "<SubscriberNumber>" + msisdn.getMsisdn() + "</SubscriberNumber>";
						xmlMessage = xmlMessage + "</SubscriberResult>";
					}
					xmlMessage = xmlMessage + "</SubscriberResult>";
				}
				xmlMessage = xmlMessage + "<ReasonCode>" + reversal.getComment() + "</ReasonCode>";
				xmlMessage = xmlMessage + "<ReasonText>" + reversal.getComment() + "</ReasonText>";
				xmlMessage = xmlMessage + "</REV>";
				xmlMessage = xmlMessage + "</MNPFrame>";
			} else {
				xmlMessage = "<NPCMessageData>";
				xmlMessage = xmlMessage + "<NPCData>";
				xmlMessage = xmlMessage + "<MessageHeader>";
				xmlMessage = xmlMessage + "<LSAID>" + donorLSAID + "</LSAID>";
				xmlMessage = xmlMessage + "<PortType>MOBILE</PortType>";
				xmlMessage = xmlMessage + "<TransactionID>" + transactionId + "</TransactionID>";
				xmlMessage = xmlMessage + "<MessageID>1008</MessageID>";
				xmlMessage = xmlMessage + "<MsgCreateTimeStamp>" + getLocalDateTime() + "</MsgCreateTimeStamp>";
				xmlMessage = xmlMessage + "<Sender>" + reversal.getSource() + "</Sender>";
				xmlMessage = xmlMessage + "</MessageHeader>";
				xmlMessage = xmlMessage + "<NPCMessage>";
				xmlMessage = xmlMessage + "<PortActivated>";
				if (reversal.getMsisdnUID().size() > 0) {
					for (MSISDNUIDType item : reversal.getMsisdnUID()) {
						xmlMessage = xmlMessage + "<NumberRange>";
						xmlMessage = xmlMessage + "<NumberFrom>" + item.getMsisdn() + "</NumberFrom>";
						xmlMessage = xmlMessage + "<NumberTo>" + item.getMsisdn() + "</NumberTo>";
						xmlMessage = xmlMessage + "<PortingCode>10</PortingCode>";
						xmlMessage = xmlMessage + "<ReasonCode>10</ReasonCode>";
						xmlMessage = xmlMessage + "</NumberRange>";
					}
				}
				xmlMessage = xmlMessage + "<Route>10</Route>";
				xmlMessage = xmlMessage + "</PortActivated>";
				xmlMessage = xmlMessage + "</NPCMessage>";
				xmlMessage = xmlMessage + "</NPCData>";
				xmlMessage = xmlMessage + "</NPCMessageData>";
			}
			List results = GlobalXMLGenerator.transferToList(path, xmlMessage);
			if (results.size() > 1) {
				System.out.println("Result more than 1 items");
				for (int i = 1; i <= results.size(); i++) {
					System.out.println("Result " + i + ": XML[" + (String) ((Map) results.get(i - 1)).get("XML")
							+ "] Key[" + (String) ((Map) results.get(i - 1)).get("KEY") + "] MessageName["
							+ (String) ((Map) results.get(i - 1)).get("MESSAGE_NAME") + "]");
				}
				return "2";
			} else if (results.size() == 0) {
				System.out.println("No matched schema/selector found for the input XML");
				return "3";
			} else {
				System.out.println("Success");
				System.out.println((String) ((Map) results.get(0)).get("XML"));
				String xml = (String) ((Map) results.get(0)).get("XML");
				return xml;
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
			_logger.error("NPOUtils.convertJsonIntoNPOTAType() - unable to convert pojo into xml -" + e.getMessage());
		}
		return null;
	}

	public String generateSCNoticeAnswer(SCNotice npo, int success, int fail, String requestId, String sessionId) {
		try {

			String xmlMessage = "";
			xmlMessage = "<MNPFrame xmlns=\"MNPProtocol.xsd\">";
			xmlMessage = xmlMessage + "<SC-NOTICEA>";
			xmlMessage = xmlMessage + "<MessageSenderTelco>" + npo.getMessageSenderTelco() + "</MessageSenderTelco>";
			xmlMessage = xmlMessage + "<MessageReceiverTelco>" + npo.getMessageReceiverTelco()
					+ "</MessageReceiverTelco>";
			// call store function to get reqeustId and set it
			xmlMessage = xmlMessage + "<RequestId>" + requestId + "</RequestId>";
			xmlMessage = xmlMessage + "<Timestamp>" + getTimeStamp() + "</Timestamp>";
			xmlMessage = xmlMessage + "<BatchId>" + npo.getBatchId() + "</BatchId>";// after send this give ack we will
																					// update transactionId(BatchId)
			xmlMessage = xmlMessage + "<Success>" + success + "</Success>";
			xmlMessage = xmlMessage + "<Fail>" + fail + "</Fail>";
			xmlMessage = xmlMessage + "</SC-NOTICEA>";
			xmlMessage = xmlMessage + "</MNPFrame>";
			List results = GlobalXMLGenerator.transferToList(path, xmlMessage);
			if (results.size() > 1) {
				System.out.println("Result more than 1 items");
				for (int i = 1; i <= results.size(); i++) {
					System.out.println("Result " + i + ": XML[" + (String) ((Map) results.get(i - 1)).get("XML")
							+ "] Key[" + (String) ((Map) results.get(i - 1)).get("KEY") + "] MessageName["
							+ (String) ((Map) results.get(i - 1)).get("MESSAGE_NAME") + "]");
				}
			} else if (results.size() == 0) {
				System.out.println("No matched schema/selector found for the input XML");
			} else {
				System.out.println("Success");
				System.out.println((String) ((Map) results.get(0)).get("XML"));
				String xml = (String) ((Map) results.get(0)).get("XML");
				return xml;
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
			_logger.error("[sessionId=" + sessionId
					+ "]: NPOUtils.generateSCNoticeAnswer().Exception occur while convert msg into SC-Notice-Ans: with timestamp:["
					+ new Timestamp(System.currentTimeMillis()) + "]-" + e);
		}
		return null;
	}

	public String generateSDNoticeAnswer(SDNotice npo, int success, int fail, String requestId) {
		try {
			String xmlMessage = "";
			xmlMessage = "<MNPFrame xmlns=\"MNPProtocol.xsd\">";
			xmlMessage = xmlMessage + "<SD-NOTICEA>";
			xmlMessage = xmlMessage + "<MessageSenderTelco>" + npo.getMessageSenderTelco() + "</MessageSenderTelco>";
			xmlMessage = xmlMessage + "<MessageReceiverTelco>" + npo.getMessageReceiverTelco()
					+ "</MessageReceiverTelco>";
			xmlMessage = xmlMessage + "<RequestId>" + requestId + "</RequestId>";
			xmlMessage = xmlMessage + "<Timestamp>" + getTimeStamp() + "</Timestamp>";
			xmlMessage = xmlMessage + "<BatchId>" + npo.getBatchId() + "</BatchId>";
			xmlMessage = xmlMessage + "<Success>" + success + "</Success>";
			xmlMessage = xmlMessage + "<Fail>" + fail + "</Fail>";
			xmlMessage = xmlMessage + "</SD-NOTICEA>";
			xmlMessage = xmlMessage + "</MNPFrame>";
			List results = GlobalXMLGenerator.transferToList(path, xmlMessage);
			if (results.size() > 1) {
				System.out.println("Result more than 1 items");
				for (int i = 1; i <= results.size(); i++) {
					System.out.println("Result " + i + ": XML[" + (String) ((Map) results.get(i - 1)).get("XML")
							+ "] Key[" + (String) ((Map) results.get(i - 1)).get("KEY") + "] MessageName["
							+ (String) ((Map) results.get(i - 1)).get("MESSAGE_NAME") + "]");
				}
			} else if (results.size() == 0) {
				System.out.println("No matched schema/selector found for the input XML");
			} else {
				System.out.println("Success");
				System.out.println((String) ((Map) results.get(0)).get("XML"));
				String xml = (String) ((Map) results.get(0)).get("XML");
				return xml;
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
			_logger.error("NPOUtils.generateSDNoticeAnswer() - unable to convert pojo into xml -" + e.getMessage());
		}
		return null;
	}

	public String convertZone1RecoveryFullReqIntoXML(RecoveryDBForm item, String messageSenderTelco,
			String messageReceiverTelco, String sessionId) {
		try {
			LocalDateTime localDateTime = java.time.LocalDateTime.now();
			DateTimeFormatter myFormatObjs = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
			String localTimestamp = localDateTime.format(myFormatObjs);
			String xmlMessage = "";
			xmlMessage = "<MNPFrame xmlns=\"MNPProtocol.xsd\">";
			xmlMessage = xmlMessage + "<RDBFD>";
			xmlMessage = xmlMessage + "<MessageSenderTelco>" + messageSenderTelco + "</MessageSenderTelco>";
			xmlMessage = xmlMessage + "<MessageReceiverTelco>" + messageReceiverTelco + "</MessageReceiverTelco>";
			xmlMessage = xmlMessage + "<RequestId>" + item.getRequestId() + "</RequestId>";
			xmlMessage = xmlMessage + "<Timestamp>" + localTimestamp + "</Timestamp>";
			xmlMessage = xmlMessage + "</RDBFD>";
			xmlMessage = xmlMessage + "</MNPFrame>";
			List results = GlobalXMLGenerator.transferToList(path, xmlMessage);
			if (results.size() > 1) {
				System.out.println("Result more than 1 items");
				for (int i = 1; i <= results.size(); i++) {
					System.out.println("Result " + i + ": XML[" + (String) ((Map) results.get(i - 1)).get("XML")
							+ "] Key[" + (String) ((Map) results.get(i - 1)).get("KEY") + "] MessageName["
							+ (String) ((Map) results.get(i - 1)).get("MESSAGE_NAME") + "]");
				}
			} else if (results.size() == 0) {
				System.out.println("No matched schema/selector found for the input XML");
			} else {
				System.out.println("Success");
				System.out.println((String) ((Map) results.get(0)).get("XML"));
				String xml = (String) ((Map) results.get(0)).get("XML");
				return xml;
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
			_logger.error("[sessionId=" + sessionId
					+ "]: NPOUtils.convertZone1RecoveryFullReqIntoXML().Exception occur while convert convert recovery db full msg into xml: with timestamp:["
					+ new Timestamp(System.currentTimeMillis()) + "]-" + e);
		}
		return null;
	}

	public String convertZone1RecoveryPartialReqIntoXML(RecoveryDBForm item, String messageSenderTelco,
			String messageReceiverTelco, String sessionId) {
		try {
			DateTimeFormatter myFormatObjs = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
			LocalDateTime localDateTime = java.time.LocalDateTime.now();
			String localTimestamp = localDateTime.format(myFormatObjs);
			String xmlMessage = "";
			xmlMessage = "<MNPFrame xmlns=\"MNPProtocol.xsd\">";
			xmlMessage = xmlMessage + "<RDBPD>";
			xmlMessage = xmlMessage + "<MessageSenderTelco>" + messageSenderTelco + "</MessageSenderTelco>";
			xmlMessage = xmlMessage + "<MessageReceiverTelco>" + messageReceiverTelco + "</MessageReceiverTelco>";
			xmlMessage = xmlMessage + "<RequestId>" + item.getRequestId() + "</RequestId>";
			xmlMessage = xmlMessage + "<Timestamp>" + localTimestamp + "</Timestamp>";
			if (item.getIsMSISDN()) {
				xmlMessage = xmlMessage + "<SubscriberSequence>";
				String[] split = item.getMsisdn().split("\\,");
				for (String msisdn : split) {
					xmlMessage = xmlMessage + "<SubscriberNumber>" + msisdn + "</SubscriberNumber>";
				}
				xmlMessage = xmlMessage + "</SubscriberSequence>";
			}
			if (item.getIsTimestamp()) {
				String[] split = item.getDateRange().split("\\-");
				if (split.length > 1) {
					String startDate = split[0];
					startDate = startDate.replace('/', '-');
					startDate = startDate + "T00:00:00.000";
					startDate = startDate.replace(" ", "");
					LocalDateTime localStartDateTime = LocalDateTime.parse(startDate);
					String startTimestamp = localStartDateTime.format(myFormatObjs);

					String endDate = split[1];
					endDate = endDate.replace('/', '-');
					endDate = endDate + "T23:59:59.000";
					endDate = endDate.replace(" ", "");
					LocalDateTime localEndDateTime = LocalDateTime.parse(endDate);
					String endTimestamp = localEndDateTime.format(myFormatObjs);
					xmlMessage = xmlMessage + "<StartDate>" + startTimestamp + "</StartDate>";
					xmlMessage = xmlMessage + "<EndDate>" + endTimestamp + "</EndDate>";
					xmlMessage = xmlMessage + "<TimeRange>";
					xmlMessage = xmlMessage + "<TimeRangeStart>" + startTimestamp + "</TimeRangeStart>";
					xmlMessage = xmlMessage + "<TimeRangeEnd>" + endTimestamp + "</TimeRangeEnd>";
					xmlMessage = xmlMessage + "</TimeRange>";
				}
			}
			if (item.getIsLSA()) {
				xmlMessage = xmlMessage + "<LSA>" + item.getLsa() + "</LSA>";
			}
			xmlMessage = xmlMessage + "</RDBPD>";
			xmlMessage = xmlMessage + "</MNPFrame>";
			List results = GlobalXMLGenerator.transferToList(path, xmlMessage);
			if (results.size() > 1) {
				System.out.println("Result more than 1 items");
				for (int i = 1; i <= results.size(); i++) {
					System.out.println("Result " + i + ": XML[" + (String) ((Map) results.get(i - 1)).get("XML")
							+ "] Key[" + (String) ((Map) results.get(i - 1)).get("KEY") + "] MessageName["
							+ (String) ((Map) results.get(i - 1)).get("MESSAGE_NAME") + "]");
				}
			} else if (results.size() == 0) {
				System.out.println("No matched schema/selector found for the input XML");
			} else {
				System.out.println("Success");
				System.out.println((String) ((Map) results.get(0)).get("XML"));
				String xml = (String) ((Map) results.get(0)).get("XML");
				return xml;
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
			_logger.error("[sessionId=" + sessionId
					+ "]: NPOUtils.convertZone1RecoveryPartialReqIntoXML().Exception occur while convert recovery db partial msg into xml: with timestamp:["
					+ new Timestamp(System.currentTimeMillis()) + "]-" + e);
		}
		return null;
	}

	public String convertZone2RecoveryFullReqIntoXML(RecoveryDBForm item, String transactionId, String lsaId,
			String sender, String sessionId) {
		try {
			LocalDateTime myDateObj = LocalDateTime.now();
			DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
			String msgCreateTimeStamp = myDateObj.format(myFormatObj);
			String xmlMessage = "";
			xmlMessage = "<NPCMessageData>";
			xmlMessage = xmlMessage + "<NPCData>";

			xmlMessage = xmlMessage + "<MessageHeader>";
			xmlMessage = xmlMessage + "<LSAID>" + lsaId + "</LSAID>";
			xmlMessage = xmlMessage + "<PortType>ANY</PortType>";
			xmlMessage = xmlMessage + "<TransactionID>" + transactionId + "</TransactionID>";
			xmlMessage = xmlMessage + "<MessageID>6001</MessageID>";
			xmlMessage = xmlMessage + "<MsgCreateTimeStamp>" + msgCreateTimeStamp + "</MsgCreateTimeStamp>";
			xmlMessage = xmlMessage + "<Sender>" + sender + "</Sender>";
			xmlMessage = xmlMessage + "</MessageHeader>";
			xmlMessage = xmlMessage + "<NPCMessage>";
			xmlMessage = xmlMessage + "<SynchronisationRequest>";
			xmlMessage = xmlMessage + "<DownloadType>Full</DownloadType>";
			xmlMessage = xmlMessage + "</SynchronisationRequest>";
			xmlMessage = xmlMessage + "</NPCMessage>";

			xmlMessage = xmlMessage + "</NPCData>";
			xmlMessage = xmlMessage + "</NPCMessageData>";
			List results = GlobalXMLGenerator.transferToList(path, xmlMessage);
			if (results.size() > 1) {
				System.out.println("Result more than 1 items");
				for (int i = 1; i <= results.size(); i++) {
					System.out.println("Result " + i + ": XML[" + (String) ((Map) results.get(i - 1)).get("XML")
							+ "] Key[" + (String) ((Map) results.get(i - 1)).get("KEY") + "] MessageName["
							+ (String) ((Map) results.get(i - 1)).get("MESSAGE_NAME") + "]");
				}
			} else if (results.size() == 0) {
				System.out.println("No matched schema/selector found for the input XML");
			} else {
				System.out.println("Success");
				System.out.println((String) ((Map) results.get(0)).get("XML"));
				String xml = (String) ((Map) results.get(0)).get("XML");
				return xml;
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
			_logger.error("[sessionId=" + sessionId
					+ "]: NPOUtils.convertZone2RecoveryFullReqIntoXML().Exception occur while convert RDBFRequest zone2 msg into xml: with timestamp:["
					+ new Timestamp(System.currentTimeMillis()) + "]-" + e);
		}
		return null;
	}

	public String convertZone2RecoveryPartialReqIntoXML(RecoveryDBForm item, String transactionId, String sender,
			String sessionId) {
		try {
			LocalDateTime myDateObj = LocalDateTime.now();
			DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
			String msgCreateTimeStamp = myDateObj.format(myFormatObj);
			String xmlMessage = "";
			xmlMessage = "<NPCMessageData>";
			xmlMessage = xmlMessage + "<NPCData>";

			xmlMessage = xmlMessage + "<MessageHeader>";
			xmlMessage = xmlMessage + "<LSAID>" + item.getLsa() + "</LSAID>";
			xmlMessage = xmlMessage + "<PortType>ANY</PortType>";
			xmlMessage = xmlMessage + "<TransactionID>" + transactionId + "</TransactionID>";
			xmlMessage = xmlMessage + "<MessageID>6001</MessageID>";
			xmlMessage = xmlMessage + "<MsgCreateTimeStamp>" + msgCreateTimeStamp + "</MsgCreateTimeStamp>";
			xmlMessage = xmlMessage + "<Sender>" + sender + "</Sender>";
			xmlMessage = xmlMessage + "</MessageHeader>";

			xmlMessage = xmlMessage + "<NPCMessage>";
			xmlMessage = xmlMessage + "<SynchronisationRequest>";
			if (item.getIsLSA()) {
				xmlMessage = xmlMessage + "<ReqSenderLSAID>" + item.getLsa() + "</ReqSenderLSAID>";
			}
			xmlMessage = xmlMessage + "<DownloadType>Delta</DownloadType>";
			if (item.getIsTimestamp()) {
				String[] split = item.getDateRange().split("\\-");
				if (split.length > 1) {
					DateTimeFormatter myFormatObjs = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
					String startDate = split[0];
					startDate = startDate.replace('/', '-');
					startDate = startDate + "T00:00:00.000";
					startDate = startDate.replace(" ", "");
					LocalDateTime localStartDateTime = LocalDateTime.parse(startDate);
					String startTimestamp = localStartDateTime.format(myFormatObjs);

					String endDate = split[1];
					endDate = endDate.replace('/', '-');
					endDate = endDate + "T23:59:59.000";
					endDate = endDate.replace(" ", "");
					LocalDateTime localEndDateTime = LocalDateTime.parse(endDate);
					String endTimestamp = localEndDateTime.format(myFormatObjs);
					xmlMessage = xmlMessage + "<StartDate>" + startTimestamp + "</StartDate>";
					xmlMessage = xmlMessage + "<EndDate>" + endTimestamp + "</EndDate>";
				}
			}
			xmlMessage = xmlMessage + "</SynchronisationRequest>";
			xmlMessage = xmlMessage + "</NPCMessage>";

			xmlMessage = xmlMessage + "</NPCData>";
			xmlMessage = xmlMessage + "</NPCMessageData>";
			List results = GlobalXMLGenerator.transferToList(path, xmlMessage);
			if (results.size() > 1) {
				System.out.println("Result more than 1 items");
				for (int i = 1; i <= results.size(); i++) {
					System.out.println("Result " + i + ": XML[" + (String) ((Map) results.get(i - 1)).get("XML")
							+ "] Key[" + (String) ((Map) results.get(i - 1)).get("KEY") + "] MessageName["
							+ (String) ((Map) results.get(i - 1)).get("MESSAGE_NAME") + "]");
				}
			} else if (results.size() == 0) {
				System.out.println("No matched schema/selector found for the input XML");
			} else {
				System.out.println("Success");
				System.out.println((String) ((Map) results.get(0)).get("XML"));
				String xml = (String) ((Map) results.get(0)).get("XML");
				return xml;
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
			_logger.error("[sessionId=" + sessionId
					+ "]: NPOUtils.convertZone2RecoveryPartialReqIntoXML().Exception occur while convert RDBPRequest zone2 msg into xml: with timestamp:["
					+ new Timestamp(System.currentTimeMillis()) + "]-" + e);
		}
		return null;
	}

	public static String getTimeStamp() {
		ZonedDateTime zonedDateTime = ZonedDateTime.now().withFixedOffsetZone()
				.withZoneSameInstant(java.time.ZoneOffset.ofHoursMinutes(5, 30));
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
		String formattedDateTime = zonedDateTime.format(formatter);
		return formattedDateTime;
	}

	/* start code for suspension */

	public String convertJsonIntoInitSuspensionRequest(BillingResolution item, String messageSenderTelco, int zone_type,
			String area, String requestId) {
		try {
			String inputBillDateTime = item.getBill_date() + " 00:00:00";
			String inputDueDateTime = item.getDue_date() + " 00:00:00";
			DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
			LocalDateTime billDateTime = LocalDateTime.parse(inputBillDateTime, inputFormatter);
			LocalDateTime dueDateTime = LocalDateTime.parse(inputDueDateTime, inputFormatter);
			DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
			String formattedBillDateTime = billDateTime.format(outputFormatter);
			String formattedDueDateTime = dueDateTime.format(outputFormatter);

			String xmlMessage = "";
			if (zone_type == 1) {
				String messageReceiverTelco = ReadConfigFile.getProperties().getProperty("MessageReceiverTelco-mch1");
				xmlMessage = "<MNPFrame xmlns=\"MNPProtocol.xsd\">";
				xmlMessage = xmlMessage + "<NPOS>";
				xmlMessage = xmlMessage + "<MessageSenderTelco>" + messageSenderTelco + "</MessageSenderTelco>";
				xmlMessage = xmlMessage + "<MessageReceiverTelco>" + messageReceiverTelco + "</MessageReceiverTelco>";
				xmlMessage = xmlMessage + "<RequestId>" + requestId + "</RequestId>";
				xmlMessage = xmlMessage + "<Timestamp>" + timestamp + "</Timestamp>";
				xmlMessage = xmlMessage + "<ReferenceId>" + item.getTransactionId() + "</ReferenceId>";
				xmlMessage = xmlMessage + "<SubscriberNumber>" + item.getMsisdn() + "</SubscriberNumber>";
				xmlMessage = xmlMessage + "<BillDate>" + formattedBillDateTime + "</BillDate>";
				xmlMessage = xmlMessage + "<DueDate>" + formattedDueDateTime + "</DueDate>";
				xmlMessage = xmlMessage + "<Amount>" + item.getAmount() + "</Amount>";
				xmlMessage = xmlMessage + "<Remark>1</Remark>";
				xmlMessage = xmlMessage + "</NPOS>";
				xmlMessage = xmlMessage + "</MNPFrame>";
			} else if (zone_type == 2) {
				xmlMessage = "<NPCMessageData>";
				xmlMessage = xmlMessage + "<NPCData>";
				xmlMessage = xmlMessage + "<MessageHeader>";
				xmlMessage = xmlMessage + "<LSAID>" + area + "</LSAID>";
				xmlMessage = xmlMessage + "<PortType>MOBILE</PortType>";
				xmlMessage = xmlMessage + "<TransactionID>" + item.getTransactionId() + "</TransactionID>";
				xmlMessage = xmlMessage + "<MessageID>7001</MessageID>";
				xmlMessage = xmlMessage + "<MsgCreateTimeStamp>" + getLocalDateTime() + "</MsgCreateTimeStamp>";
				xmlMessage = xmlMessage + "<Sender>" + messageSenderTelco + "</Sender>";
				xmlMessage = xmlMessage + "</MessageHeader>";
				xmlMessage = xmlMessage + "<NPCMessage>";
				xmlMessage = xmlMessage + "<NonpaymentDisconnReq>";
				xmlMessage = xmlMessage + "<NumberRange>" + item.getMsisdn() + "</NumberRange>";
				xmlMessage = xmlMessage + "<BillAmount>" + item.getAmount() + "</BillAmount>";
				xmlMessage = xmlMessage + "<BillDate>" + formattedBillDateTime + "</BillDate>";
				xmlMessage = xmlMessage + "<BillDueDate>" + formattedDueDateTime + "</BillDueDate>";
				xmlMessage = xmlMessage + "<Comments>1</Comments>";
				xmlMessage = xmlMessage + "</NonpaymentDisconnReq>";
				xmlMessage = xmlMessage + "</NPCMessage>";

				xmlMessage = xmlMessage + "</NPCData>";
				xmlMessage = xmlMessage + "</NPCMessageData>";
			}
			List results = GlobalXMLGenerator.transferToList(path, xmlMessage);
			if (results.size() > 1) {
				System.out.println("Result more than 1 items");
				for (int i = 1; i <= results.size(); i++) {
					System.out.println("Result " + i + ": XML[" + (String) ((Map) results.get(i - 1)).get("XML")
							+ "] Key[" + (String) ((Map) results.get(i - 1)).get("KEY") + "] MessageName["
							+ (String) ((Map) results.get(i - 1)).get("MESSAGE_NAME") + "]");
				}
				return "2";
			} else if (results.size() == 0) {
				System.out.println("No matched schema/selector found for the input XML");
				return "3";
			} else {
				System.out.println("Success");
				System.out.println((String) ((Map) results.get(0)).get("XML"));
				String xml = (String) ((Map) results.get(0)).get("XML");
				return xml;
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
			_logger.error("NPOUtils.convertJsonIntoInitSuspensionRequest() - unable to convert pojo into xml -"
					+ e.getMessage());
		}
		return null;
	}

	public String convertJsonIntoInitSuspensionCancel(BillingResolution item, String messageSenderTelco, int mch_type,
			String transactionId, String area) {
		try {
			String xmlMessage = "";
			if (mch_type == 1) {
				String messageReceiverTelco = ReadConfigFile.getProperties().getProperty("MessageReceiverTelco-mch1");
				xmlMessage = "<MNPFrame xmlns=\"MNPProtocol.xsd\">";
				xmlMessage = xmlMessage + "<NPOSPR>";
				xmlMessage = xmlMessage + "<MessageSenderTelco>" + messageSenderTelco + "</MessageSenderTelco>";
				xmlMessage = xmlMessage + "<MessageReceiverTelco>" + messageReceiverTelco + "</MessageReceiverTelco>";
				xmlMessage = xmlMessage + "<RequestId>" + item.getTransactionId() + "</RequestId>";
				xmlMessage = xmlMessage + "<Timestamp>" + timestamp + "</Timestamp>";
				xmlMessage = xmlMessage + "<ReferenceId>893-M-230-20220608-A001282</ReferenceId>";
				xmlMessage = xmlMessage + "<ResultCode>" + 200 + "</ResultCode>";
				xmlMessage = xmlMessage + "</NPOSPR>";
				xmlMessage = xmlMessage + "</MNPFrame>";
			} else if (mch_type == 2) {
				xmlMessage = "<NPCMessageData>";
				xmlMessage = xmlMessage + "<NPCData>";
				xmlMessage = xmlMessage + "<MessageHeader>";
				xmlMessage = xmlMessage + "<LSAID>" + area + "</LSAID>";
				xmlMessage = xmlMessage + "<PortType>MOBILE</PortType>";
				xmlMessage = xmlMessage + "<TransactionID>" + transactionId + "</TransactionID>";
				xmlMessage = xmlMessage + "<MessageID>7007</MessageID>";
				xmlMessage = xmlMessage + "<MsgCreateTimeStamp>" + getLocalDateTime() + "</MsgCreateTimeStamp>";
				xmlMessage = xmlMessage + "<Sender>" + messageSenderTelco + "</Sender>";
				xmlMessage = xmlMessage + "</MessageHeader>";
				xmlMessage = xmlMessage + "<NPCMessage>";
				xmlMessage = xmlMessage + "<NpdCancelRequest>";
				xmlMessage = xmlMessage + "<NumberRange>";
				xmlMessage = xmlMessage + "<NumberFrom>" + item.getMsisdn() + "</NumberFrom>";
				xmlMessage = xmlMessage + "<NumberTo>" + item.getMsisdn() + "</NumberTo>";
				xmlMessage = xmlMessage + "</NumberRange>";
				xmlMessage = xmlMessage + "</NpdCancelRequest>";
				xmlMessage = xmlMessage + "</NPCMessage>";
				xmlMessage = xmlMessage + "</NPCData>";
				xmlMessage = xmlMessage + "</NPCMessageData>";
			}
			List results = GlobalXMLGenerator.transferToList(path, xmlMessage);
			if (results.size() > 1) {
				System.out.println("Result more than 1 items");
				for (int i = 1; i <= results.size(); i++) {
					System.out.println("Result " + i + ": XML[" + (String) ((Map) results.get(i - 1)).get("XML")
							+ "] Key[" + (String) ((Map) results.get(i - 1)).get("KEY") + "] MessageName["
							+ (String) ((Map) results.get(i - 1)).get("MESSAGE_NAME") + "]");
				}
				return "2";
			} else if (results.size() == 0) {
				System.out.println("No matched schema/selector found for the input XML");
				return "3";
			} else {
				System.out.println("Success");
				System.out.println((String) ((Map) results.get(0)).get("XML"));
				String xml = (String) ((Map) results.get(0)).get("XML");
				return xml;
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
			_logger.error("NPOUtils.convertJsonIntoInitSuspensionRequest() - unable to convert pojo into xml -"
					+ e.getMessage());
		}
		return null;
	}

	public String convertJsonIntoInitSuspensionACK(BillingResolution item, String messageSenderTelco, int mch_type,
			String transactionId, String area) {
		try {
			String xmlMessage = "";
			if (mch_type == 1) {
				String messageReceiverTelco = ReadConfigFile.getProperties().getProperty("MessageReceiverTelco-mch1");
				xmlMessage = "<MNPFrame xmlns=\"MNPProtocol.xsd\">";
				xmlMessage = xmlMessage + "<NPOSAACK>";
				xmlMessage = xmlMessage + "<MessageSenderTelco>" + messageSenderTelco + "</MessageSenderTelco>";
				xmlMessage = xmlMessage + "<MessageReceiverTelco>" + messageReceiverTelco + "</MessageReceiverTelco>";
				xmlMessage = xmlMessage + "<RequestId>" + item.getTransactionId() + "</RequestId>";
				xmlMessage = xmlMessage + "<Timestamp>" + timestamp + "</Timestamp>";
				xmlMessage = xmlMessage + "<ReferenceId>" + item.getTransactionId() + "</ReferenceId>";
				xmlMessage = xmlMessage + "<ResultCode>" + 200 + "</ResultCode>";
				xmlMessage = xmlMessage + "</NPOSAACK>";
				xmlMessage = xmlMessage + "</MNPFrame>";
			} else if (mch_type == 2) {
				xmlMessage = "<NPCMessageData>";
				xmlMessage = xmlMessage + "<NPCData>";
				xmlMessage = xmlMessage + "<MessageHeader>";
				xmlMessage = xmlMessage + "<LSAID>" + area + "</LSAID>";
				xmlMessage = xmlMessage + "<PortType>MOBILE</PortType>";
				xmlMessage = xmlMessage + "<TransactionID>" + transactionId + "</TransactionID>";
				xmlMessage = xmlMessage + "<MessageID>7005</MessageID>";
				xmlMessage = xmlMessage + "<MsgCreateTimeStamp>" + getLocalDateTime() + "</MsgCreateTimeStamp>";
				xmlMessage = xmlMessage + "<Sender>" + messageSenderTelco + "</Sender>";
				xmlMessage = xmlMessage + "</MessageHeader>";
				xmlMessage = xmlMessage + "<NPCMessage>";
				xmlMessage = xmlMessage + "<NpdAckRequest>";
				xmlMessage = xmlMessage + "<NumberRange>";
				xmlMessage = xmlMessage + "<NumberFrom>" + item.getMsisdn() + "</NumberFrom>";
				xmlMessage = xmlMessage + "<NumberTo>" + item.getMsisdn() + "</NumberTo>";
				xmlMessage = xmlMessage + "</NumberRange>";
				xmlMessage = xmlMessage + "</NpdAckRequest>";
				xmlMessage = xmlMessage + "</NPCMessage>";
				xmlMessage = xmlMessage + "</NPCData>";
				xmlMessage = xmlMessage + "</NPCMessageData>";
			}
			List results = GlobalXMLGenerator.transferToList(path, xmlMessage);
			if (results.size() > 1) {
				System.out.println("Result more than 1 items");
				for (int i = 1; i <= results.size(); i++) {
					System.out.println("Result " + i + ": XML[" + (String) ((Map) results.get(i - 1)).get("XML")
							+ "] Key[" + (String) ((Map) results.get(i - 1)).get("KEY") + "] MessageName["
							+ (String) ((Map) results.get(i - 1)).get("MESSAGE_NAME") + "]");
				}
				return "2";
			} else if (results.size() == 0) {
				System.out.println("No matched schema/selector found for the input XML");
				return "3";
			} else {
				System.out.println("Success");
				System.out.println((String) ((Map) results.get(0)).get("XML"));
				String xml = (String) ((Map) results.get(0)).get("XML");
				return xml;
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
			_logger.error("NPOUtils.convertJsonIntoInitSuspensionRequest() - unable to convert pojo into xml -"
					+ e.getMessage());
		}
		return null;
	}

	public String convertJsonIntoISuspensionNPOSA(BillingResolution item, String messageSenderTelco, int mch_type,
			String transactionId, String area) {
		try {
			String xmlMessage = "";
			if (mch_type == 1) {
				String messageReceiverTelco = ReadConfigFile.getProperties().getProperty("MessageReceiverTelco-mch1");
				xmlMessage = "<MNPFrame xmlns=\"MNPProtocol.xsd\">";
				xmlMessage = xmlMessage + "<NPOSA>";
				xmlMessage = xmlMessage + "<MessageSenderTelco>" + messageSenderTelco + "</MessageSenderTelco>";
				xmlMessage = xmlMessage + "<MessageReceiverTelco>" + messageReceiverTelco + "</MessageReceiverTelco>";
				xmlMessage = xmlMessage + "<RequestId>" + item.getTransactionId() + "</RequestId>";
				xmlMessage = xmlMessage + "<Timestamp>" + timestamp + "</Timestamp>";
				xmlMessage = xmlMessage + "<ReferenceId>" + item.getTransactionId() + "</ReferenceId>";
				xmlMessage = xmlMessage + "<ResultCode>" + 200 + "</ResultCode>";
				xmlMessage = xmlMessage + "</NPOSA>";
				xmlMessage = xmlMessage + "</MNPFrame>";
			} else if (mch_type == 2) {
				xmlMessage = "<NPCMessageData>";
				xmlMessage = xmlMessage + "<NPCData>";
				xmlMessage = xmlMessage + "<MessageHeader>";
				xmlMessage = xmlMessage + "<LSAID>" + area + "</LSAID>";
				xmlMessage = xmlMessage + "<PortType>MOBILE</PortType>";
				xmlMessage = xmlMessage + "<TransactionID>" + transactionId + "</TransactionID>";
				xmlMessage = xmlMessage + "<MessageID>7005</MessageID>";
				xmlMessage = xmlMessage + "<MsgCreateTimeStamp>" + getLocalDateTime() + "</MsgCreateTimeStamp>";
				xmlMessage = xmlMessage + "<Sender>" + messageSenderTelco + "</Sender>";
				xmlMessage = xmlMessage + "</MessageHeader>";
				xmlMessage = xmlMessage + "<NPCMessage>";
				xmlMessage = xmlMessage + "<NpdAckRequest>";
				xmlMessage = xmlMessage + "<NumberRange>";
				xmlMessage = xmlMessage + "<NumberFrom>" + item.getMsisdn() + "</NumberFrom>";
				xmlMessage = xmlMessage + "<NumberTo>" + item.getMsisdn() + "</NumberTo>";
				xmlMessage = xmlMessage + "</NumberRange>";
				xmlMessage = xmlMessage + "</NpdAckRequest>";
				xmlMessage = xmlMessage + "</NPCMessage>";
				xmlMessage = xmlMessage + "</NPCData>";
				xmlMessage = xmlMessage + "</NPCMessageData>";
			}
			List results = GlobalXMLGenerator.transferToList(path, xmlMessage);
			if (results.size() > 1) {
				System.out.println("Result more than 1 items");
				for (int i = 1; i <= results.size(); i++) {
					System.out.println("Result " + i + ": XML[" + (String) ((Map) results.get(i - 1)).get("XML")
							+ "] Key[" + (String) ((Map) results.get(i - 1)).get("KEY") + "] MessageName["
							+ (String) ((Map) results.get(i - 1)).get("MESSAGE_NAME") + "]");
				}
				return "2";
			} else if (results.size() == 0) {
				System.out.println("No matched schema/selector found for the input XML");
				return "3";
			} else {
				System.out.println("Success");
				System.out.println((String) ((Map) results.get(0)).get("XML"));
				String xml = (String) ((Map) results.get(0)).get("XML");
				return xml;
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
			_logger.error("NPOUtils.convertJsonIntoInitSuspensionRequest() - unable to convert pojo into xml -"
					+ e.getMessage());
		}
		return null;
	}

	private static String getLocalDateTime() {
		LocalDateTime myDateObj = LocalDateTime.now();
		DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
		String msgCreateTimeStamp = myDateObj.format(myFormatObj);
		return msgCreateTimeStamp;
	}

	/* start code for MCH WEBSERVICE response */
	public String generatePortingOutXML(NPO npo, String binaryFile) {
		try {
			String xmlMessage = "";
			String messageSenderTelco = ReadConfigFile.getProperties().getProperty("MessageSenderTelco-ZOOM");
			String messageReceiverTelco = ReadConfigFile.getProperties().getProperty("MessageReceiverTelco-mch1");
			xmlMessage = "<MNPFrame xmlns=\"MNPProtocol.xsd\">";
			xmlMessage = xmlMessage + "<PORTING_OUT>";
			xmlMessage = xmlMessage + "<Source>" + messageSenderTelco + "</Source>";
			xmlMessage = xmlMessage + "<TimeStamp>" + timestamp + "</TimeStamp>";
			xmlMessage = xmlMessage + "<Service>" + npo.getServiceType() + "</Service>";
			xmlMessage = xmlMessage + "<RequestId>" + npo.getRequestId() + "</RequestId>";
			xmlMessage = xmlMessage + "<ReferenceId>" + npo.getReferenceId() + "</ReferenceId>";
			xmlMessage = xmlMessage + "<OrderedTransferTime>" + timestamp + "</OrderedTransferTime>";
			xmlMessage = xmlMessage + "<ResultCode>" + 200 + "</ResultCode>";
			if (npo.getSubscriberAuthSequence().getSubscriberAuthorization().size() > 0) {
				for (SubscriberAuthorization item : npo.getSubscriberAuthSequence().getSubscriberAuthorization()) {
					xmlMessage = xmlMessage + "<SubscriberArr>";
					xmlMessage = xmlMessage + "<MSISDN>" + item.getSubscriberNumber() + "</MSISDN>";
					xmlMessage = xmlMessage + "<DummyMSISDN>" + item.getSubscriberNumber() + "</DummyMSISDN>";
					xmlMessage = xmlMessage + "<SIM>" + item.getSubscriberNumber() + "</SIM>";
					xmlMessage = xmlMessage + "<IMSI>" + item.getSubscriberNumber() + "</IMSI>";
					xmlMessage = xmlMessage + "<HLR>" + item.getSubscriberNumber() + "</HLR>";
					xmlMessage = xmlMessage + "<PinCode>" + item.getSubscriberNumber() + "</PinCode>";
					xmlMessage = xmlMessage + "</SubscriberArr>";
				}
			} else {
				for (SubscriberSequence item : npo.getSubscriberSequence()) {
					xmlMessage = xmlMessage + "<SubscriberArr>";
					xmlMessage = xmlMessage + "<MSISDN>" + item.getSubscriberNumber() + "</MSISDN>";
					xmlMessage = xmlMessage + "<DummyMSISDN>" + item.getSubscriberNumber() + "</DummyMSISDN>";
					xmlMessage = xmlMessage + "<SIM>" + item.getSubscriberNumber() + "</SIM>";
					xmlMessage = xmlMessage + "<IMSI>" + item.getSubscriberNumber() + "</IMSI>";
					xmlMessage = xmlMessage + "<HLR>" + item.getSubscriberNumber() + "</HLR>";
					xmlMessage = xmlMessage + "<PinCode>" + item.getSubscriberNumber() + "</PinCode>";
					xmlMessage = xmlMessage + "</SubscriberArr>";
				}
			}
			if (npo.getCorporateCustomer() != null) {
				xmlMessage = xmlMessage + "<CustomerData>";
				xmlMessage = xmlMessage + "<SubcriberId>" + npo.getServiceType() + "</SubcriberId>";
				xmlMessage = xmlMessage + "<Remark1>" + npo.getServiceType() + "</Remark1>";
				xmlMessage = xmlMessage + "<Remark2>" + npo.getServiceType() + "</Remark2>";
				xmlMessage = xmlMessage + "<Remark3>" + npo.getServiceType() + "</Remark3>";
				xmlMessage = xmlMessage + "<Remark4>" + npo.getServiceType() + "</Remark4>";
				xmlMessage = xmlMessage + "<Remark5>" + npo.getServiceType() + "</Remark5>";
				xmlMessage = xmlMessage + "</CustomerData>";
			} else {
				if (npo.getPersonCustomer() != null) {
					xmlMessage = xmlMessage + "<CustomerData>";
					xmlMessage = xmlMessage + "<SubcriberId>" + npo.getServiceType() + "</SubcriberId>";
					xmlMessage = xmlMessage + "<Remark1>" + npo.getServiceType() + "</Remark1>";
					xmlMessage = xmlMessage + "<Remark2>" + npo.getServiceType() + "</Remark2>";
					xmlMessage = xmlMessage + "<Remark3>" + npo.getServiceType() + "</Remark3>";
					xmlMessage = xmlMessage + "<Remark4>" + npo.getServiceType() + "</Remark4>";
					xmlMessage = xmlMessage + "<Remark5>" + npo.getServiceType() + "</Remark5>";
					xmlMessage = xmlMessage + "</CustomerData>";
				}
			}
			xmlMessage = xmlMessage + "<RNO>" + 200 + "</RNO>";
			xmlMessage = xmlMessage + "<DNO>" + 200 + "</DNO>";
			xmlMessage = xmlMessage + "<Area>" + 200 + "</Area>";
			xmlMessage = xmlMessage + "<RN>" + 200 + "</RN>";
			xmlMessage = xmlMessage + "<CustomerRequestTime>" + 200 + "</CustomerRequestTime>";
			xmlMessage = xmlMessage + "<OrderedApprovalTime>" + 200 + "</OrderedApprovalTime>";
			xmlMessage = xmlMessage + "<Comment>" + 200 + "</Comment>";
			xmlMessage = xmlMessage + "</PORTING_OUT>";
			xmlMessage = xmlMessage + "</MNPFrame>";

			List results = GlobalXMLGenerator.transferToList(path, xmlMessage);
			if (results.size() > 1) {
				System.out.println("Result more than 1 items");
				for (int i = 1; i <= results.size(); i++) {
					System.out.println("Result " + i + ": XML[" + (String) ((Map) results.get(i - 1)).get("XML")
							+ "] Key[" + (String) ((Map) results.get(i - 1)).get("KEY") + "] MessageName["
							+ (String) ((Map) results.get(i - 1)).get("MESSAGE_NAME") + "]");
				}
				return "2";
			} else if (results.size() == 0) {
				System.out.println("No matched schema/selector found for the input XML");
				return "3";
			} else {
				System.out.println("Success");
				System.out.println((String) ((Map) results.get(0)).get("XML"));
				String xml = (String) ((Map) results.get(0)).get("XML");
				return xml;
			}
		} catch (

		Exception e) {
			System.out.println(e.getMessage());
			_logger.error(
					"NPOUtils.convertJsonIntoInitPortRequest() - unable to convert pojo into xml -" + e.getMessage());
		}
		return null;
	}

	public String generatePortingConfirmationXML(NPOA npo) {
		try {
			String xmlMessage = "";
			String messageSenderTelco = ReadConfigFile.getProperties().getProperty("MessageSenderTelco-ZOOM");
			String messageReceiverTelco = ReadConfigFile.getProperties().getProperty("MessageReceiverTelco-mch1");
			xmlMessage = "<MNPFrame xmlns=\"MNPProtocol.xsd\">";
			xmlMessage = xmlMessage + "<PORTING_CONFIRMATION>";
			xmlMessage = xmlMessage + "<Source>" + messageSenderTelco + "</Source>";
			xmlMessage = xmlMessage + "<TimeStamp>" + messageReceiverTelco + "</TimeStamp>";
			xmlMessage = xmlMessage + "<Service>" + npo.getRequestId() + "</Service>";
			xmlMessage = xmlMessage + "<BillingUID1>" + timestamp + "</BillingUID1>";
			xmlMessage = xmlMessage + "<RequestId>" + npo.getReferenceId() + "</RequestId>";
			xmlMessage = xmlMessage + "<PortingTime>" + timestamp + "</PortingTime>";
			xmlMessage = xmlMessage + "<APPROVAL>" + 200 + "</APPROVAL>";
			for (SubscriberResult item : npo.getSubscriberResult()) {
				xmlMessage = xmlMessage + "<MSISDNUIDResult>";
				xmlMessage = xmlMessage + "<MSISDN>" + item.getSubscriberNumber() + "</MSISDN>";
				xmlMessage = xmlMessage + "<RequestId>" + npo.getRequestId() + "</RequestId>";
				xmlMessage = xmlMessage + "<ResultCode>" + 200 + "</ResultCode>";
				xmlMessage = xmlMessage + "</MSISDNUIDResult>";
			}
			xmlMessage = xmlMessage + "<Comment>" + 200 + "</Comment>";
			xmlMessage = xmlMessage + "</PORTING_CONFIRMATION>";
			xmlMessage = xmlMessage + "</MNPFrame>";

			List results = GlobalXMLGenerator.transferToList(path, xmlMessage);
			if (results.size() > 1) {
				System.out.println("Result more than 1 items");
				for (int i = 1; i <= results.size(); i++) {
					System.out.println("Result " + i + ": XML[" + (String) ((Map) results.get(i - 1)).get("XML")
							+ "] Key[" + (String) ((Map) results.get(i - 1)).get("KEY") + "] MessageName["
							+ (String) ((Map) results.get(i - 1)).get("MESSAGE_NAME") + "]");
				}
				return "2";
			} else if (results.size() == 0) {
				System.out.println("No matched schema/selector found for the input XML");
				return "3";
			} else {
				System.out.println("Success");
				System.out.println((String) ((Map) results.get(0)).get("XML"));
				String xml = (String) ((Map) results.get(0)).get("XML");
				return xml;
			}
		} catch (

		Exception e) {
			System.out.println(e.getMessage());
			_logger.error(
					"NPOUtils.convertJsonIntoInitPortRequest() - unable to convert pojo into xml -" + e.getMessage());
		}
		return null;
	}

	public String generateNPOARspConfirmationXML(NPOT npo) {
		try {
			String xmlMessage = "";
			String messageSenderTelco = ReadConfigFile.getProperties().getProperty("MessageSenderTelco-ZOOM");
			String messageReceiverTelco = ReadConfigFile.getProperties().getProperty("MessageReceiverTelco-mch1");
			xmlMessage = "<MNPFrame xmlns=\"MNPProtocol.xsd\">";
			xmlMessage = xmlMessage + "<PORTING_OUT_CONFIRMATION>";
			xmlMessage = xmlMessage + "<Source>" + messageSenderTelco + "</Source>";
			xmlMessage = xmlMessage + "<TimeStamp>" + messageReceiverTelco + "</TimeStamp>";
			xmlMessage = xmlMessage + "<Service>" + npo.getRequestId() + "</Service>";
			xmlMessage = xmlMessage + "<BillingUID1>" + timestamp + "</BillingUID1>";
			xmlMessage = xmlMessage + "<RequestId>" + npo.getReferenceId() + "</RequestId>";
			xmlMessage = xmlMessage + "<PortingTime>" + timestamp + "</PortingTime>";
			xmlMessage = xmlMessage + "<APPROVAL>" + 200 + "</APPROVAL>";
			for (SubscriberSequence item : npo.getSubscriberSequence()) {
				xmlMessage = xmlMessage + "<MSISDNUIDResult>";
				xmlMessage = xmlMessage + "<MSISDN>" + item.getSubscriberNumber() + "</MSISDN>";
				xmlMessage = xmlMessage + "<RequestId>" + npo.getRequestId() + "</RequestId>";
				xmlMessage = xmlMessage + "<ResultCode>" + 200 + "</ResultCode>";
				xmlMessage = xmlMessage + "</MSISDNUIDResult>";
			}
			xmlMessage = xmlMessage + "<Comment>" + 200 + "</Comment>";
			xmlMessage = xmlMessage + "</PORTING_OUT_CONFIRMATION>";
			xmlMessage = xmlMessage + "</MNPFrame>";

			List results = GlobalXMLGenerator.transferToList(path, xmlMessage);
			if (results.size() > 1) {
				System.out.println("Result more than 1 items");
				for (int i = 1; i <= results.size(); i++) {
					System.out.println("Result " + i + ": XML[" + (String) ((Map) results.get(i - 1)).get("XML")
							+ "] Key[" + (String) ((Map) results.get(i - 1)).get("KEY") + "] MessageName["
							+ (String) ((Map) results.get(i - 1)).get("MESSAGE_NAME") + "]");
				}
				return "2";
			} else if (results.size() == 0) {
				System.out.println("No matched schema/selector found for the input XML");
				return "3";
			} else {
				System.out.println("Success");
				System.out.println((String) ((Map) results.get(0)).get("XML"));
				String xml = (String) ((Map) results.get(0)).get("XML");
				return xml;
			}
		} catch (

		Exception e) {
			System.out.println(e.getMessage());
			_logger.error(
					"NPOUtils.convertJsonIntoInitPortRequest() - unable to convert pojo into xml -" + e.getMessage());
		}
		return null;
	}

	public String generateDisconnectionOrderXML(SD sd) {
		try {
			String xmlMessage = "";
			String messageSenderTelco = ReadConfigFile.getProperties().getProperty("MessageSenderTelco-ZOOM");
			String messageReceiverTelco = ReadConfigFile.getProperties().getProperty("MessageReceiverTelco-mch1");
			xmlMessage = "<MNPFrame xmlns=\"MNPProtocol.xsd\">";
			xmlMessage = xmlMessage + "<DISCONNECT_ORDER>";
			xmlMessage = xmlMessage + "<Source>" + messageSenderTelco + "</Source>";
			xmlMessage = xmlMessage + "<TimeStamp>" + messageReceiverTelco + "</TimeStamp>";
			xmlMessage = xmlMessage + "<Service>" + sd.getRequestId() + "</Service>";
			for (SDInfo item : sd.getSDInfo()) {
				xmlMessage = xmlMessage + "<MSISDNUID>";
				xmlMessage = xmlMessage + "<MSISDN>" + item.getSubscriberNumber() + "</MSISDN>";
				xmlMessage = xmlMessage + "<RequestId>" + sd.getRequestId() + "</RequestId>";
				xmlMessage = xmlMessage + "</MSISDNUID>";
			}
			xmlMessage = xmlMessage + "</DISCONNECT_ORDER>";
			xmlMessage = xmlMessage + "</MNPFrame>";

			List results = GlobalXMLGenerator.transferToList(path, xmlMessage);
			if (results.size() > 1) {
				System.out.println("Result more than 1 items");
				for (int i = 1; i <= results.size(); i++) {
					System.out.println("Result " + i + ": XML[" + (String) ((Map) results.get(i - 1)).get("XML")
							+ "] Key[" + (String) ((Map) results.get(i - 1)).get("KEY") + "] MessageName["
							+ (String) ((Map) results.get(i - 1)).get("MESSAGE_NAME") + "]");
				}
				return "2";
			} else if (results.size() == 0) {
				System.out.println("No matched schema/selector found for the input XML");
				return "3";
			} else {
				System.out.println("Success");
				System.out.println((String) ((Map) results.get(0)).get("XML"));
				String xml = (String) ((Map) results.get(0)).get("XML");
				return xml;
			}
		} catch (

		Exception e) {
			System.out.println(e.getMessage());
			_logger.error(
					"NPOUtils.convertJsonIntoInitPortRequest() - unable to convert pojo into xml -" + e.getMessage());
		}
		return null;
	}

	public String generateConnectionOrderXML(SC sc, String binaryFile) {
		try {
			String xmlMessage = "";
			String messageSenderTelco = ReadConfigFile.getProperties().getProperty("MessageSenderTelco-ZOOM");
			String messageReceiverTelco = ReadConfigFile.getProperties().getProperty("MessageReceiverTelco-mch1");
			xmlMessage = "<MNPFrame xmlns=\"MNPProtocol.xsd\">";
			xmlMessage = xmlMessage + "<CONNECTION_ORDER>";
			xmlMessage = xmlMessage + "<Source>" + messageSenderTelco + "</Source>";
			xmlMessage = xmlMessage + "<TimeStamp>" + messageReceiverTelco + "</TimeStamp>";
			xmlMessage = xmlMessage + "<Service>" + sc.getRequestId() + "</Service>";
			for (SCInfo item : sc.getSCInfo()) {
				xmlMessage = xmlMessage + "<MSISDNUID>";
				xmlMessage = xmlMessage + "<MSISDN>" + item.getSubscriberNumber() + "</MSISDN>";
				xmlMessage = xmlMessage + "<RequestId>" + sc.getRequestId() + "</RequestId>";
				xmlMessage = xmlMessage + "</MSISDNUID>";
			}
			xmlMessage = xmlMessage + "</CONNECTION_ORDER>";
			xmlMessage = xmlMessage + "</MNPFrame>";

			List results = GlobalXMLGenerator.transferToList(path, xmlMessage);
			if (results.size() > 1) {
				System.out.println("Result more than 1 items");
				for (int i = 1; i <= results.size(); i++) {
					System.out.println("Result " + i + ": XML[" + (String) ((Map) results.get(i - 1)).get("XML")
							+ "] Key[" + (String) ((Map) results.get(i - 1)).get("KEY") + "] MessageName["
							+ (String) ((Map) results.get(i - 1)).get("MESSAGE_NAME") + "]");
				}
				return "2";
			} else if (results.size() == 0) {
				System.out.println("No matched schema/selector found for the input XML");
				return "3";
			} else {
				System.out.println("Success");
				System.out.println((String) ((Map) results.get(0)).get("XML"));
				String xml = (String) ((Map) results.get(0)).get("XML");
				return xml;
			}
		} catch (

		Exception e) {
			System.out.println(e.getMessage());
			_logger.error(
					"NPOUtils.convertJsonIntoInitPortRequest() - unable to convert pojo into xml -" + e.getMessage());
		}
		return null;
	}
}
