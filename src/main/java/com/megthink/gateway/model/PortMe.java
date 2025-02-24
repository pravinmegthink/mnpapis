package com.megthink.gateway.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import jakarta.persistence.*;

@Entity
@Table(name = "port_tx")
public class PortMe {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "port_id")
	private Integer portId;
	@Column(name = "source")
	private Integer source;
	@Column(name = "time_stamp")
	private String timeStamp;
	@Column(name = "billinguid1")
	private String billingUID1;
	@Column(name = "instanceid")
	private String instanceID;
	@Column(name = "request_id")
	private String requestId;
	@Column(name = "rno")
	private String rno;
	@Column(name = "dno")
	private String dno;
	@Column(name = "area")
	private String area;
	@Column(name = "rn")
	private String rn;
	@Column(name = "company_code")
	private String companyCode;
	@Column(name = "service")
	private String service;
	@Column(name = "customer_request_time")
	private String customerRequestTime;
	@Column(name = "data_type")
	private Integer dataType;
	@Column(name = "order_type")
	private Integer orderType;
	@Column(name = "order_date")
	private String orderDate;
	@Column(name = "partnerid")
	private String partnerID;
	@Column(name = "status")
	private Integer status;
	@Column(name = "last_area")
	private String last_area;
	@CreationTimestamp
	@Column(name = "created_date_time")
	private Date createdDate;
	@CreationTimestamp
	@Column(name = "updated_date_time")
	private Date updatedDate;
	@Column(name = "remark")
	private String remark;
	@Column(name = "original_carrier")
	private String originalCarrier;
	@Column(name = "request_type")
	private String request_type;
	@Column(name = "user_id")
	private int userId;
	@Column(name = "mch")
	private int mch;

	@Transient
	private List<SubscriberArrType> subscriberArrType;
	@Transient
	private SubscriberSequence subscriberSequence;
	@Transient
	private SubscriberResultType subscriberResult;
	@Transient
	private CustomerData customerData;
	@Transient
	private PersonCustomer personCustomer;
	@Transient
	private List<MSISDNUIDType> msisdnUID;
	@Transient
	private String approval;
	@Transient
	private String comment;
	@Transient
	private String statusDesc;
	@Transient
	private String hlr;
	@Transient
	private String dummyMSISDN;

	public Integer getPortId() {
		return portId;
	}

	public void setPortId(Integer portId) {
		this.portId = portId;
	}

	public Integer getSource() {
		return source;
	}

	public void setSource(Integer source) {
		this.source = source;
	}

	public String getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(String timeStamp) {
		this.timeStamp = timeStamp;
	}

	public String getBillingUID1() {
		return billingUID1;
	}

	public void setBillingUID1(String billingUID1) {
		this.billingUID1 = billingUID1;
	}

	public String getInstanceID() {
		return instanceID;
	}

	public void setInstanceID(String instanceID) {
		this.instanceID = instanceID;
	}

	public String getRequestId() {
		return requestId;
	}

	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}

	public String getRno() {
		return rno;
	}

	public void setRno(String rno) {
		this.rno = rno;
	}

	public String getDno() {
		return dno;
	}

	public void setDno(String dno) {
		this.dno = dno;
	}

	public String getArea() {
		return area;
	}

	public void setArea(String area) {
		this.area = area;
	}

	public String getRn() {
		return rn;
	}

	public void setRn(String rn) {
		this.rn = rn;
	}

	public String getCompanyCode() {
		return companyCode;
	}

	public void setCompanyCode(String companyCode) {
		this.companyCode = companyCode;
	}

	public String getService() {
		return service;
	}

	public void setService(String service) {
		this.service = service;
	}

	public String getCustomerRequestTime() {
		return customerRequestTime;
	}

	public void setCustomerRequestTime(String customerRequestTime) {
		this.customerRequestTime = customerRequestTime;
	}

	public Integer getDataType() {
		return dataType;
	}

	public void setDataType(Integer dataType) {
		this.dataType = dataType;
	}

	public Integer getOrderType() {
		return orderType;
	}

	public void setOrderType(Integer orderType) {
		this.orderType = orderType;
	}

	public String getOrderDate() {
		return orderDate;
	}

	public void setOrderDate(String orderDate) {
		this.orderDate = orderDate;
	}

	public String getPartnerID() {
		return partnerID;
	}

	public void setPartnerID(String partnerID) {
		this.partnerID = partnerID;
	}

	public List<SubscriberArrType> getSubscriberArrType() {
		if (subscriberArrType == null) {
			subscriberArrType = new ArrayList<SubscriberArrType>();
		}
		return subscriberArrType;
	}

	public void setSubscriberArrType(List<SubscriberArrType> subscriberArrType) {
		this.subscriberArrType = subscriberArrType;
	}

	public SubscriberSequence getSubscriberSequence() {
		if (subscriberSequence == null) {
			subscriberSequence = new SubscriberSequence();
		}
		return subscriberSequence;
	}

	public void setSubscriberSequence(SubscriberSequence subscriberSequence) {
		this.subscriberSequence = subscriberSequence;
	}

	public CustomerData getCustomerData() {
		// if (customerData == null) {
		// customerData = new CustomerData();
		// }
		return customerData;
	}

	public void setCustomerData(CustomerData customerData) {
		this.customerData = customerData;
	}

	public PersonCustomer getPersonCustomer() {
		if (personCustomer == null) {
			personCustomer = new PersonCustomer();
		}
		return personCustomer;
	}

	public void setPersonCustomer(PersonCustomer personCustomer) {
		this.personCustomer = personCustomer;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getApproval() {
		return approval;
	}

	public void setApproval(String approval) {
		this.approval = approval;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public SubscriberResultType getSubscriberResult() {
		if (subscriberResult == null) {
			subscriberResult = new SubscriberResultType();
		}
		return subscriberResult;
	}

	public void setSubscriberResult(SubscriberResultType subscriberResult) {
		this.subscriberResult = subscriberResult;
	}

	public List<MSISDNUIDType> getMsisdnUID() {
		return msisdnUID;
	}

	public void setMsisdnUID(List<MSISDNUIDType> msisdnUID) {
		this.msisdnUID = msisdnUID;
	}

	public String getLast_area() {
		return last_area;
	}

	public void setLast_area(String last_area) {
		this.last_area = last_area;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getOriginalCarrier() {
		return originalCarrier;
	}

	public void setOriginalCarrier(String originalCarrier) {
		this.originalCarrier = originalCarrier;
	}

	public String getRequest_type() {
		return request_type;
	}

	public void setRequest_type(String request_type) {
		this.request_type = request_type;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public int getMch() {
		return mch;
	}

	public void setMch(int mch) {
		this.mch = mch;
	}

	public String getStatusDesc() {
		return statusDesc;
	}

	public void setStatusDesc(String statusDesc) {
		this.statusDesc = statusDesc;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public Date getUpdatedDate() {
		return updatedDate;
	}

	public void setUpdatedDate(Date updatedDate) {
		this.updatedDate = updatedDate;
	}

	public String getHlr() {
		return hlr;
	}

	public void setHlr(String hlr) {
		this.hlr = hlr;
	}

	public String getDummyMSISDN() {
		return dummyMSISDN;
	}

	public void setDummyMSISDN(String dummyMSISDN) {
		this.dummyMSISDN = dummyMSISDN;
	}

}
