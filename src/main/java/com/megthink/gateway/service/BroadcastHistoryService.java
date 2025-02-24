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

import com.megthink.gateway.model.BroadcastHistory;
import com.megthink.gateway.repository.BroadcastHistoryRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;

@Service("broadcastHistoryService")
public class BroadcastHistoryService {
	private static final Logger _logger = LoggerFactory.getLogger(BroadcastHistoryService.class);

	@PersistenceContext
	private EntityManager entityManager;

	private BroadcastHistoryRepository repository;

	Timestamp timestamp = new Timestamp(System.currentTimeMillis());

	@Autowired
	public BroadcastHistoryService(BroadcastHistoryRepository repository) {
		this.repository = repository;
	}

	public BroadcastHistory saveBroadcastHistory(BroadcastHistory data) {
		return repository.save(data);
	}

	@SuppressWarnings("unchecked")
	@Transactional
	public List<BroadcastHistory> getBroadcastHistory(String msisdn, int userId, String stDate, String enDate) {
		List<BroadcastHistory> list = null;
		try {
			String dateFormatPattern = "yyyy-MM-dd";
			DateFormat dateFormat = new SimpleDateFormat(dateFormatPattern);
			Date startDate = null;
			Date endDate = null;
			if (stDate != null) {
				startDate = dateFormat.parse(stDate);
				endDate = dateFormat.parse(enDate);
			}
			String sql = null;
			if (startDate != null && msisdn.equals("")) {
				sql = "select tbl_broadcast.* from tbl_broadcast where Date(transaction_date) between ? and ? ";
				Query query = entityManager.createNativeQuery(sql, BroadcastHistory.class).setParameter(1, startDate)
						.setParameter(2, endDate);
				list = (List<BroadcastHistory>) query.getResultList();
			} else if (startDate == null && !msisdn.equals("")) {
				sql = "select tbl_broadcast.* from tbl_broadcast where msisdn =? ";
				Query query = entityManager.createNativeQuery(sql, BroadcastHistory.class).setParameter(1, msisdn);
				list = (List<BroadcastHistory>) query.getResultList();
			} else if (startDate != null && !msisdn.equals("")) {
				sql = "select tbl_broadcast.* from tbl_broadcast where msisdn =? and Date(transaction_date) between ? and ? ";
				Query query = entityManager.createNativeQuery(sql, BroadcastHistory.class).setParameter(1, msisdn)
						.setParameter(2, startDate).setParameter(3, endDate);
				list = (List<BroadcastHistory>) query.getResultList();
			}
		} catch (Exception e) {
			list = new ArrayList<BroadcastHistory>();
			_logger.error(
					"Exception occurs while getting BroadcastHistoryService.getBroadcastHistory()-" + e.getMessage());
		}
		return list;
	}

	@Transactional
	public int updateBroadcastHistory(String transactionId, int resultCode) {
		int success = 0;
		try {
			if (transactionId != null) {
				String sql = "update tbl_broadcast set remark=?,re_trans_date=now() WHERE transaction_id=?";
				entityManager.createNativeQuery(sql).setParameter(1, Integer.toString(resultCode))
						.setParameter(2, transactionId).executeUpdate();
				success = 1;
			}
		} catch (Exception e) {
			success = 0;
			_logger.error("BroadcastHistoryService.updateBroadcastHistory() with error - " + e.getMessage());
		}
		return success;
	}
}