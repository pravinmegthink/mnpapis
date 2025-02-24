package com.megthink.gateway.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.megthink.gateway.model.SubscriberArrType;
import com.megthink.gateway.repository.SubscriberArrTypeRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;

@Service("subscriberArrTypeService")
public class SubscriberArrTypeService {

	@PersistenceContext
	private EntityManager entityManager;

	private static final Logger _logger = LoggerFactory.getLogger(SubscriberArrTypeService.class);

	private SubscriberArrTypeRepository subscriberArrTypeRepository;

	@Autowired
	public SubscriberArrTypeService(SubscriberArrTypeRepository subscriberArrTypeRepository) {
		this.subscriberArrTypeRepository = subscriberArrTypeRepository;
	}

	public List<SubscriberArrType> findSubArrByPortIdAndResultCode(Integer portId, Integer resultCode) {
		return subscriberArrTypeRepository.findAllByPortIdAndResultCode(portId, resultCode);
	}

	@Transactional
	public int saveMt(int portId, List<SubscriberArrType> list, String requestType, int status) {
		List<SubscriberArrType> subArrList = new ArrayList<SubscriberArrType>();
		int resetCnt = 0;
		int totalRecord = 0;
		if (portId != 0) {
			for (int i = 0; i < list.size(); i++) {
				try {
					if (list.get(i).getMsisdn() != null) {
						SubscriberArrType subscriber = new SubscriberArrType();
						subscriber.setMsisdn(list.get(i).getMsisdn());
						subscriber.setDummyMSISDN(list.get(i).getDummyMSISDN());
						subscriber.setSim(list.get(i).getSim());
						subscriber.setImsi(list.get(i).getImsi());
						subscriber.setHlr(list.get(i).getHlr());
						subscriber.setPinCode(list.get(i).getPinCode());
						subscriber.setPortId(portId);
						subscriber.setRequest_type(requestType);
						subscriber.setStatus(status);
						subscriber.setResultCode(200);
						resetCnt++;
						totalRecord++;
						subArrList.add(subscriber);
						if (resetCnt == 500) {
							subscriberArrTypeRepository.saveAll(subArrList);
							subscriberArrTypeRepository.flush();
							subArrList.clear();
							resetCnt = 0;
						}
					}
				} catch (Exception e) {
					_logger.error("SubscriberArrTypeService.saveMt()-Exception occurs while save portmt details "
							+ e.getMessage());
				}
			}
		}

		if (resetCnt < 500) {
			subscriberArrTypeRepository.saveAll(subArrList);
			subArrList.clear();
		}

		return totalRecord;
	}

	public List<SubscriberArrType> processBulkFile(MultipartFile listMPfile, String hlr, String alternumber)
			throws IOException {
		BufferedReader in = new BufferedReader(new InputStreamReader(listMPfile.getInputStream()));
		String line = "";
		String splitBy = ",";
		List<SubscriberArrType> preferenceList = new ArrayList<SubscriberArrType>();
		int totalRecord = 0;
		try {
			while ((line = in.readLine()) != null) {
				if (line.equals(",,,,,")) {

				} else {
					if (totalRecord == 0) {
						totalRecord++;
					} else {
						String[] row = line.split(splitBy);

						SubscriberArrType item = new SubscriberArrType();
						item.setMsisdn(row[0]);
						item.setPinCode(row[1]);
						item.setSim(row[2]);
						item.setImsi(row[3]);
						item.setHlr(hlr);
						item.setDummyMSISDN(alternumber);
						totalRecord++;
						preferenceList.add(item);
					}
				}
			}
		} catch (Exception e) {
			totalRecord = 0;
			_logger.error(
					"SubscriberArrTypeService.processBulkFile()-Exception occurs while bulk file " + e.getMessage());
		}
		return preferenceList;
	}

	@SuppressWarnings("unchecked")
	@Transactional
	public List<SubscriberArrType> getSubscriberDatailByMsisdnAndRequestType(String msisdn, String reqType) {
		try {
			if (msisdn != null) {
				String sql = "SELECT * FROM port_mt WHERE msisdn = ? AND request_type = ?";
				Query query = entityManager.createNativeQuery(sql, SubscriberArrType.class).setParameter(1, msisdn)
						.setParameter(2, reqType);
				List<SubscriberArrType> items = (List<SubscriberArrType>) query.getResultList();
				return items;
			}
		} catch (Exception e) {
			_logger.error("Exception occurs while getting SubscriberArrTypeService.getSubscriberDetailsByMsisdn() - "
					+ e.getMessage(), e);
		}
		return new ArrayList<>();
	}

	@Transactional
	public void insertWithQuery(int portId, String msisdn, String requestType, int status) {
		entityManager.createNativeQuery(
				"INSERT INTO port_mt (msisdn,request_type,port_id, status, created_date_time, updated_date_time,result_code) VALUES (?,?,?,?,now(),now(),200)")
				.setParameter(1, msisdn).setParameter(2, requestType).setParameter(3, portId).setParameter(4, status)
				.executeUpdate();
	}

	@Transactional
	public void savePortMT(int portId, String msisdn, String requestType, int status, String hlr, String dummyMSISDN) {
		try {
		entityManager.createNativeQuery(
				"INSERT INTO port_mt (msisdn,request_type,port_id, status, hlr,dummymsisdn, created_date_time, updated_date_time,result_code) VALUES (?,?,?,?,?,?,now(),now(),200)")
				.setParameter(1, msisdn).setParameter(2, requestType).setParameter(3, portId).setParameter(4, status)
				.setParameter(5, hlr).setParameter(6, dummyMSISDN).executeUpdate();
		}catch(Exception e) {
			System.out.println(e.getMessage());
		}
	}

	@Transactional
	public void createPortMTNPO(int portId, String msisdn, String requestType, int status, String activationDateTime,
			String disconnectionDateTime, String hlr) {
		entityManager.createNativeQuery(
				"INSERT INTO port_mt (msisdn,request_type,port_id, status, activation_date_time, disconnection_date_time,hlr, created_date_time, updated_date_time,result_code) VALUES (?,?,?,?,?,?,?,now(),now(),200)")
				.setParameter(1, msisdn).setParameter(2, requestType).setParameter(3, portId).setParameter(4, status)
				.setParameter(5, activationDateTime).setParameter(6, disconnectionDateTime).setParameter(7, hlr)
				.executeUpdate();
	}

	@Transactional
	public void updatePortMtStatusByMsisdn(int currstatus, String msisdn, String req_type, int resultCode) {
		try {
			entityManager.createNativeQuery(
					"UPDATE port_mt SET status = ?, result_code = ?, updated_date_time = NOW() WHERE msisdn = ? AND request_type = ?")
					.setParameter(1, currstatus).setParameter(2, resultCode).setParameter(3, msisdn)
					.setParameter(4, req_type).executeUpdate();

		} catch (Exception e) {
			_logger.error("PortMtDao.updatePortMtStatusByMsisdn() - " + e.getMessage());
		}
	}
}