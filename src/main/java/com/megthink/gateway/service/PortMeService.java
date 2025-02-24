package com.megthink.gateway.service;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.megthink.gateway.model.PortMe;
import com.megthink.gateway.repository.PortMeRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;

@Service("portMeService")
public class PortMeService {

	private static final Logger _logger = LoggerFactory.getLogger(PortMeService.class);

	private PortMeRepository portMeRepository;

	@PersistenceContext
	private EntityManager entityManager;

	@Autowired
	public PortMeService(PortMeRepository portMeRepository) {
		this.portMeRepository = portMeRepository;
	}

	public PortMe savePortMe(PortMe portMe) {
		return portMeRepository.save(portMe);
	}

	public PortMe findByRequestId(String requestId) {
		return portMeRepository.findByRequestId(requestId);
	}

	@Transactional
	public String getTransactionId(String param1, String param2) {
		try {
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
		} catch (Exception e) {
			return null;
		}
	}

	@Transactional
	public String getReferenceId(String rno, String dno, String nrh, String portme) {
		try {
			String transactionId = null;
			String query = "SELECT * FROM generate_id1('" + rno + "','" + dno + "','" + nrh + "','" + portme + "')";
			@SuppressWarnings("unchecked")
			List<Object[]> columns = entityManager.createNativeQuery(query).getResultList();
			for (Object obj : columns) {
				transactionId = obj.toString();
			}
			return transactionId;
		} catch (Exception e) {
			return null;
		}
	}

	@Transactional
	public void updatePortMeStatus(int currstatus, String requestId) {
		try {
			entityManager.createNativeQuery("update port_tx set status=?,updated_date_time = now() WHERE request_id=?")
					.setParameter(1, currstatus).setParameter(2, requestId).executeUpdate();
		} catch (Exception e) {
			_logger.error("PortMtDao.updatePortMtStatusByMsisdn() - " + e.getMessage());
		}
	}

	@Transactional
	public void updatePortMeStatus(int currstatus, String requestId, int userId, int mch) {
		try {
			entityManager.createNativeQuery(
					"UPDATE port_tx SET status = ?, user_id = ?, mch=?, updated_date_time = NOW() WHERE request_id = ?")
					.setParameter(1, currstatus).setParameter(2, userId).setParameter(3, mch).setParameter(4, requestId)
					.executeUpdate();
		} catch (Exception e) {
			_logger.error("PortMeDao.updatePortMeStatus() - " + e.getMessage());
		}
	}

	@Transactional
	public void updatePortMeStatus(int currstatus, String requestId, int userId) {
		try {
			entityManager.createNativeQuery(
					"UPDATE port_tx SET status = ?, user_id = ?, updated_date_time = NOW() WHERE request_id = ?")
					.setParameter(1, currstatus).setParameter(2, userId).setParameter(3, requestId).executeUpdate();
		} catch (Exception e) {
			_logger.error("PortMeDao.updatePortMeStatus() - " + e.getMessage());
		}
	}

	@Transactional
	public void updatePortMeStats(int currstatus, String requestId, int userId, String reqType) {
		try {
			entityManager.createNativeQuery(
					"UPDATE port_tx SET status = ?, user_id = ?, updated_date_time = NOW() WHERE request_id = ? and request_type=?")
					.setParameter(1, currstatus).setParameter(2, userId).setParameter(3, requestId)
					.setParameter(4, reqType).executeUpdate();
		} catch (Exception e) {
			_logger.error("PortMeDao.updatePortMeStatus() - " + e.getMessage());
		}
	}

	@SuppressWarnings("unchecked")
	@Transactional
	public List<PortMe> getListPortMtByDateRange(String reqType, int status, String dateRange, String requestId,
			int userId) {
		List<PortMe> list = null;
		try {

			if (!requestId.equals("") && dateRange == null) {
				String sql = "SELECT port_tx.* FROM port_tx JOIN constants ON constkey = ? AND constcode = port_tx.status WHERE request_type = ? "
						+ "AND port_tx.status = ? AND request_id = ? ";
				Query query = entityManager.createNativeQuery(sql, PortMe.class).setParameter(1, reqType)
						.setParameter(2, reqType).setParameter(3, status).setParameter(4, requestId);
				list = (List<PortMe>) query.getResultList();
			} else if (!requestId.equals("") && dateRange != null) {
				String sql = "SELECT port_tx.* FROM port_tx JOIN constants ON constkey = ? AND constcode = port_tx.status WHERE request_type = ? "
						+ "AND port_tx.status = ? AND request_id = ? AND Date(port_tx.created_date_time) BETWEEN "
						+ dateRange;
				Query query = entityManager.createNativeQuery(sql, PortMe.class).setParameter(1, reqType)
						.setParameter(2, reqType).setParameter(3, status).setParameter(4, requestId);
				list = (List<PortMe>) query.getResultList();
			} else if (requestId.equals("") && dateRange != null) {
				String sql = "SELECT port_tx.* FROM port_tx JOIN constants ON constkey = ? AND constcode = port_tx.status WHERE request_type = ? "
						+ "AND port_tx.status = ? AND Date(port_tx.created_date_time) BETWEEN " + dateRange;
				Query query = entityManager.createNativeQuery(sql, PortMe.class).setParameter(1, reqType)
						.setParameter(2, reqType).setParameter(3, status);
				list = (List<PortMe>) query.getResultList();
			} else {
				String sql = "SELECT port_tx.* FROM port_tx JOIN constants ON constkey = ? AND constcode = port_tx.status WHERE request_type = ? "
						+ "AND port_tx.status = ?";
				Query query = entityManager.createNativeQuery(sql, PortMe.class).setParameter(1, reqType)
						.setParameter(2, reqType).setParameter(3, status);
				list = (List<PortMe>) query.getResultList();
			}
		} catch (Exception e) {
			list = new ArrayList<PortMe>();
			_logger.error(
					"Exception occurs while getting PortMeService.getListPortMtByDateRange() - " + e.getMessage());
		}
		return list;
	}

	// @SuppressWarnings("unchecked")
	// @Transactional
	// public List<PortMeDetails> getListPortMtDetails(String reqType, String
	// requestId, int status, int userId) {
	// try {
	// String sql = "SELECT null as billinguid1, port_mt.id AS port_id,
	// port_tx.request_id, port_tx.area, port_tx.dno, port_mt.msisdn,
	// port_tx.service, constants.description AS status, "
	// + "port_mt.imsi, port_mt.hlr, port_mt.sim, TO_CHAR(port_mt.created_date_time,
	// 'dd-mm-yyyy HH24:MI:SS') created_date_time,
	// TO_CHAR(port_mt.updated_date_time, 'dd-mm-yyyy HH24:MI:SS') updated_date_time
	// "
	// + "FROM port_mt JOIN port_tx ON port_mt.port_id = port_tx.port_id "
	// + "JOIN constants ON constants.constcode = port_mt.status "
	// + "WHERE constants.constkey = ? AND port_tx.request_id = ? AND
	// port_tx.user_id = ? AND port_mt.status = ?";
	//
	// Query query = entityManager.createNativeQuery(sql,
	// PortMeDetails.class).setParameter(1, reqType)
	// .setParameter(2, requestId).setParameter(3, userId).setParameter(4, status);
	// List<PortMeDetails> list = (List<PortMeDetails>) query.getResultList();
	// return list;
	// } catch (Exception e) {
	// _logger.error("Exception occurs while getting
	// PortMeService.getListPortMtDetails() - " + e.getMessage());
	// return new ArrayList<PortMeDetails>();
	// }
	// }

	@Transactional
	public void updatePortMtResultCodeByMsisdn(int currstatus, String msisdn, String req_type, int resultCode) {
		try {
			entityManager.createNativeQuery(
					"UPDATE port_mt SET status = ?, result_code = ?, updated_date_time = NOW() WHERE msisdn = ? AND request_type = ?")
					.setParameter(1, currstatus).setParameter(2, resultCode).setParameter(3, msisdn)
					.setParameter(4, req_type).executeUpdate();
		} catch (Exception e) {
			_logger.error("PortMeService.updatePortMtResultCodeByMsisdn() - " + e.getMessage());
		}
	}

	@Transactional
	public void updateNPOARsp(int currstatus, String msisdn, String req_type, int resultCode) {
		try {
			entityManager.createNativeQuery(
					"UPDATE port_mt SET status = ?, result_code = ?, assigned_disc_time=now(), updated_date_time = NOW() WHERE msisdn = ? AND request_type = ?")
					.setParameter(1, currstatus).setParameter(2, resultCode).setParameter(3, msisdn)
					.setParameter(4, req_type).executeUpdate();
		} catch (Exception e) {
			_logger.error("PortMeService.updatePortMtResultCodeByMsisdn() - " + e.getMessage());
		}
	}

	@Transactional
	public void updateNPOARequest(int currstatus, String msisdn, String req_type, int resultCode) {
		try {
			entityManager.createNativeQuery(
					"UPDATE port_mt SET status = ?, result_code = ?,assigned_disc_time=now(), updated_date_time = NOW() WHERE msisdn = ? AND request_type = ?")
					.setParameter(1, currstatus).setParameter(2, resultCode).setParameter(3, msisdn)
					.setParameter(4, req_type).executeUpdate();
		} catch (Exception e) {
			_logger.error("PortMeService.updatePortMtResultCodeByMsisdn() - " + e.getMessage());
		}
	}

	@Transactional
	public void updatePortAprovalReqeust(int currstatus, String msisdn, String req_type, int resultCode) {
		try {
			entityManager.createNativeQuery(
					"UPDATE port_mt SET status = ?, result_code = ?, updated_date_time = NOW(), aproval_time=now() WHERE msisdn = ? AND request_type = ?")
					.setParameter(1, currstatus).setParameter(2, resultCode).setParameter(3, msisdn)
					.setParameter(4, req_type).executeUpdate();
		} catch (Exception e) {
			_logger.error("PortMeService.updatePortMtResultCodeByMsisdn() - " + e.getMessage());
		}
	}

	@Transactional
	public List<String> getListOfMSISDN(String requestId) {
		String sql = "SELECT port_mt.msisdn FROM port_mt JOIN port_tx ON port_mt.port_id = port_tx.port_id WHERE port_tx.request_id = ?";
		Query query = entityManager.createNativeQuery(sql, String.class).setParameter(1, requestId);
		@SuppressWarnings("unchecked")
		List<String> list = (List<String>) query.getResultList();
		return list;
	}

	@Transactional
	public void cancelOrderByMsisdn(int currstatus, String msisdn, String requestType, String curReqType) {
		try {

			String sql = "UPDATE port_mt SET status = ?, request_type = ?, updated_date_time = NOW() WHERE msisdn = ? AND request_type = ?";
			entityManager.createNativeQuery(sql).setParameter(1, currstatus).setParameter(2, curReqType)
					.setParameter(3, msisdn).setParameter(4, requestType).executeUpdate();
		} catch (Exception e) {
			_logger.error("PortMeService.cancelOrderByMsisdn() - " + e.getMessage());
		}
	}

	@SuppressWarnings({ "unchecked" })
	@Transactional
	public List<PortMe> getListPortMeDetailsByDateRange(String reqType, String dateRange, String reqId, int userId) {
		List<PortMe> list = null;
		try {

			if ((!reqId.equals("")) && (dateRange == null)) {
				String sql = "select port_tx.*,constants.description as statusDesc from port_tx "
						+ "join constants on constkey=? and constcode=port_tx.status where request_type=? and port_tx.user_id=? and request_id=? order by created_date_time desc";
				Query query = entityManager.createNativeQuery(sql, PortMe.class).setParameter(1, reqType)
						.setParameter(2, reqType).setParameter(3, userId).setParameter(4, reqId.trim());
				list = (List<PortMe>) query.getResultList();
			} else if (dateRange != null && reqId.equals("")) {
				String sql = "select port_tx.*,constants.description as statusDesc from port_tx "
						+ " join constants on constkey=? and constcode=port_tx.status "
						+ " where request_type=? and port_tx.user_id=? and Date(created_date_time) between " + dateRange
						+ " order by created_date_time desc";
				Query query = entityManager.createNativeQuery(sql, PortMe.class).setParameter(1, reqType)
						.setParameter(2, reqType).setParameter(3, userId);
				list = (List<PortMe>) query.getResultList();
			} else if ((!reqId.equals("")) && (dateRange != null)) {
				String sql = "select port_tx.*,constants.description as statusDesc from port_tx "
						+ " join constants on constkey=? and constcode=port_tx.status "
						+ " where request_type=? and port_tx.user_id=? and request_id=? and Date(created_date_time) between "
						+ dateRange + " order by created_date_time desc";
				Query query = entityManager.createNativeQuery(sql, PortMe.class).setParameter(1, reqType)
						.setParameter(2, reqType).setParameter(3, userId).setParameter(4, reqId.trim());
				list = (List<PortMe>) query.getResultList();
			}
		} catch (Exception e) {
			list = new ArrayList<PortMe>();
			_logger.error(
					"Exception occurs while getting PortMeDao.getListPortMeDetailsByDateRange()-" + e.getMessage());
		}
		return list;
	}

	@SuppressWarnings("unchecked")
	@Transactional
	public List<PortMe> getReversalDetails(int id, String msisdn) {
		List<PortMe> details = null;
		try {
			if (msisdn != null) {
				String sql = "select * from port_history where msisdn=? and id =?";
				Query query = entityManager.createNativeQuery(sql, PortMe.class).setParameter(1, msisdn).setParameter(2,
						id);
				details = (List<PortMe>) query.getResultList();
			}
		} catch (Exception e) {
			details = new ArrayList<PortMe>();
			_logger.error("Exception occurs while getting PortMeService.getReversalDetails()-" + e.getMessage());
		}
		return details;
	}

	@Transactional
	public int isExistReqeust(String msisdn, String reqType) {
		int count = 0;
		try {
			if (msisdn != null) {
				String sql = "select id from port_mt where msisdn=? and request_type=?";
				Query query = entityManager.createNativeQuery(sql, PortMe.class).setParameter(1, msisdn).setParameter(2,
						reqType);
				count = query.getFirstResult();
			}
		} catch (Exception e) {
			count = 0;
			_logger.error("PortMtDao.isExist() - " + e.getMessage());
		}
		return count;
	}

	@Transactional
	public int getPortIdByRequestId(String requestId, String reqType) {
		int port_id = 0;
		try {
			String sql = "select port_id from port_tx where request_id=? and request_type=?";
			Query query = entityManager.createNativeQuery(sql);
			query.setParameter(1, requestId);
			query.setParameter(2, reqType);

			// Use getSingleResult() to fetch the single result
			Object result = query.getSingleResult();
			if (result != null) {
				port_id = ((Number) result).intValue();
			}
		} catch (Exception e) {
			port_id = 0;
			_logger.error("PortMtDao.isExist() - " + e.getMessage(), e);
		}
		return port_id;
	}
}