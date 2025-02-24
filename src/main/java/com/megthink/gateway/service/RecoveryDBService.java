package com.megthink.gateway.service;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import jakarta.persistence.*;
import jakarta.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.megthink.gateway.model.RecoveryDB;
import com.megthink.gateway.repository.RecoveryDBRepository;

@Service("recoveryDBService")
public class RecoveryDBService {

	private static final Logger _logger = LoggerFactory.getLogger(RecoveryDBService.class);

	private RecoveryDBRepository repository;

	@PersistenceContext
	EntityManager entityManager;

	Timestamp timestamp = new Timestamp(System.currentTimeMillis());

	@Autowired
	public RecoveryDBService(RecoveryDBRepository repository) {
		this.repository = repository;
	}

	public RecoveryDB saveRecoveryDB(RecoveryDB data) {
		return repository.save(data);
	}

	@Transactional
	public String getRequestId(String param) {
		int row = 0;
		String requestId = null;
		String query = "SELECT * FROM retriverid('" + param + "')";
		@SuppressWarnings("unchecked")
		List<Object[]> columns = entityManager.createNativeQuery(query).getResultList();
		for (Object obj : columns.get(0)) {
			// we set row == 0 so object have 2 data
			if (row == 0) {
				row++;
				requestId = obj.toString();
			}
		}
		return requestId;
	}

	@Transactional
	public String getTransactionId(String param1, String param2) {
		int row = 0;
		String transactionId = null;
		String query = "SELECT * FROM generate_id2('" + param1 + "','" + param2 + "')";
		@SuppressWarnings("unchecked")
		List<Object[]> columns = entityManager.createNativeQuery(query).getResultList();
		for (Object obj : columns.get(0)) {
			// we set row == 0 so object have 2 data
			if (row == 0) {
				row++;
				transactionId = obj.toString();
			}
		}
		return transactionId;
	}

	@Transactional
	public int updateRecoveryDB(RecoveryDB item) {
		int success = 0;
		try {
			if (item != null) {
				String requestId = item.getRequest_id();
				if (requestId != null) {
					String sql = "UPDATE recovery_info SET result_code=?, file_name=?, update_date=now() WHERE request_id=?";
					entityManager.createNativeQuery(sql).setParameter(1, item.getResult_code())
							.setParameter(2, item.getFile_name()).setParameter(3, item.getRequest_id()).executeUpdate();
				} else {
					_logger.error("RecoveryDBDao.updateRecoveryDB() - Invalid requestId");
				}
			}
		} catch (Exception e) {
			success = 0;
			_logger.error("RecoveryDBDao.updateRecoveryDB() with error - " + e.getMessage());
		}
		return success;
	}

	@SuppressWarnings("unchecked")
	@Transactional
	public List<RecoveryDB> getRecoveryStatusByRequestId(String requestId, String startDate, String endDate) {
		List<RecoveryDB> list = null;
		String sql = null;
		try {
			if (requestId.equals("") && startDate != null && !startDate.equals("")) {
				String dateFormatPattern = "yyyy-MM-dd";
				DateFormat dateFormat = new SimpleDateFormat(dateFormatPattern);
				Date stDate = dateFormat.parse(startDate);
				Date enDate = dateFormat.parse(endDate);
				sql = "SELECT * FROM recovery_info WHERE Date(submit_date) BETWEEN ? AND ?";
				Query query = entityManager.createNativeQuery(sql, RecoveryDB.class).setParameter(1, stDate)
						.setParameter(2, enDate);
				list = (List<RecoveryDB>) query.getResultList();
			} else if (!requestId.equals("") && startDate == null) {
				sql = "SELECT * FROM recovery_info WHERE request_id = ?";
				Query query = entityManager.createNativeQuery(sql, RecoveryDB.class).setParameter(1, requestId);
				list = (List<RecoveryDB>) query.getResultList();
			} else if (!requestId.equals("") && startDate != null) {
				String dateFormatPattern = "yyyy-MM-dd";
				DateFormat dateFormat = new SimpleDateFormat(dateFormatPattern);
				Date stDate = dateFormat.parse(startDate);
				Date enDate = dateFormat.parse(endDate);
				sql = "SELECT * FROM recovery_info WHERE request_id=? AND Date(submit_date) BETWEEN ? AND ?";
				Query query = entityManager.createNativeQuery(sql, RecoveryDB.class).setParameter(1, requestId)
						.setParameter(2, stDate).setParameter(3, enDate);
				list = (List<RecoveryDB>) query.getResultList();
			}

		} catch (Exception e) {
			_logger.info(
					"Exception occurs while getting RecoveryDBDao.getRecoveryStatusByRequestId() - " + e.getMessage());
		}
		return list;
	}

	@Transactional
	public int updateAckOfRecoveryDB(String requestId, int result_code) {
		int success = 0;
		if (requestId != null) {
			try {
				String sql = "UPDATE recovery_info SET result_code = ?, update_date = NOW() WHERE request_id = ?";
				entityManager.createNativeQuery(sql).setParameter(1, result_code).setParameter(2, requestId)
						.executeUpdate();
				success = 1;
			} catch (Exception e) {
				success = 0;
				_logger.error("RecoveryDBService.updateAckOfRecoveryDB() with error - " + e.getMessage());
			}
		} else {
			_logger.error("RecoveryDBService.updateAckOfRecoveryDB() - Invalid requestId");
		}
		return success;
	}

	@Transactional
	public int updateSynchronisationResponse(String path, String fileName, String requestId, String sessionId) {
		int flag = 0;
		String sql = null;
		try {
			sql = "UPDATE recovery_info SET path=?, file_name=?, update_date=now() WHERE request_id=?";
			entityManager.createNativeQuery(sql).setParameter(1, path).setParameter(2, fileName)
					.setParameter(3, requestId).executeUpdate();
			flag = 1;
		} catch (Exception e) {
			_logger.error("[sessionId=" + sessionId
					+ "]: Exception occurs during update RecoveryDBService.updateSynchronisationResponse(), sql : ["
					+ sql + "]  with timestamp:[" + new Timestamp(System.currentTimeMillis()) + "]-" + e.getMessage());
		}
		return flag;
	}
}