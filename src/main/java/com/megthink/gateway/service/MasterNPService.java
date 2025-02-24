package com.megthink.gateway.service;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.megthink.gateway.model.MasterNP;
import com.megthink.gateway.repository.MasterNPRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;

@Service("masterNPService")
public class MasterNPService {

	private static final Logger _logger = LoggerFactory.getLogger(MasterNPService.class);
	@PersistenceContext
	private EntityManager entityManager;
	private MasterNPRepository repository;

	Timestamp timestamp = new Timestamp(System.currentTimeMillis());

	@Autowired
	public MasterNPService(MasterNPRepository repository) {
		this.repository = repository;
	}

	@Transactional
	public MasterNP saveMasterNP(MasterNP data) {
		return repository.save(data);
	}

	public MasterNP findByMsisdn(String msisdn) {
		return repository.findByMsisdn(msisdn);

	}

	@SuppressWarnings("unchecked")
	@Transactional
	public List<MasterNP> getTransactionStatus(String stDate, String enDate, String msisdn, int userId) {
		List<MasterNP> list = new ArrayList<MasterNP>();
		try {
			if (stDate != null && msisdn.equals("")) {
				String dateFormatPattern = "yyyy-MM-dd";
				DateFormat dateFormat = new SimpleDateFormat(dateFormatPattern);
				Date startDate = dateFormat.parse(stDate);
				Date endDate = dateFormat.parse(enDate);
				String sql = "SELECT tbl_master_np.* FROM tbl_master_np WHERE Date(first_trans_date) between ? and ?";
				Query query = entityManager.createNativeQuery(sql, MasterNP.class).setParameter(1, startDate)
						.setParameter(2, endDate);
				list = (List<MasterNP>) query.getResultList();
			} else if (stDate == null && !msisdn.equals("")) {
				String sql = "SELECT tbl_master_np.* FROM tbl_master_np WHERE msisdn = ?";
				Query query = entityManager.createNativeQuery(sql, MasterNP.class).setParameter(1, msisdn);
				list = (List<MasterNP>) query.getResultList();
			} else if (stDate != null && !msisdn.equals("")) {
				String dateFormatPattern = "yyyy-MM-dd";
				DateFormat dateFormat = new SimpleDateFormat(dateFormatPattern);
				Date startDate = dateFormat.parse(stDate);
				Date endDate = dateFormat.parse(enDate);
				String sql = "SELECT tbl_master_np.* FROM tbl_master_np WHERE msisdn = ? and Date(first_trans_date) between ? and ?";
				Query query = entityManager.createNativeQuery(sql, MasterNP.class).setParameter(1, msisdn)
						.setParameter(2, startDate).setParameter(3, endDate);
				list = (List<MasterNP>) query.getResultList();
			}
		} catch (Exception e) {
			_logger.error("Exception occurs while getting MasterNPService.getTransactionStatus() - " + e.getMessage());
		}
		return list;
	}
}