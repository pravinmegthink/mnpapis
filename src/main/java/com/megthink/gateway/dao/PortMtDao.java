package com.megthink.gateway.dao;

import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import com.megthink.gateway.dao.common.CommonDao;
import com.megthink.gateway.dao.mapper.PortMeDetailsMapper;
import com.megthink.gateway.model.PortMeDetails;

import jakarta.transaction.Transactional;

@Repository
public class PortMtDao extends CommonDao {

	private static final Logger _logger = LoggerFactory.getLogger(PortMtDao.class);

	@Autowired
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	public String sanitizeMsisdn(String input) {
		return input.replaceAll("[^0-9]", "");
	}

	public void updatePortMtStatus(int currstatus, int portId) {
		try {
			String sql = "update port_mt set status=:status,updated_date_time = now() WHERE port_id=:port_id";
			MapSqlParameterSource namedParameters = new MapSqlParameterSource();
			namedParameters.addValue("status", currstatus);
			namedParameters.addValue("port_id", portId);
			int rowsUpdated = namedParameterJdbcTemplate.update(sql, namedParameters);
			if (rowsUpdated > 0) {
				_logger.info("Updated successfully");
			} else {
				_logger.info("Updated fail");
			}
		} catch (Exception e) {
			_logger.error("PortMtDao.updatePortMtStatus() - " + e.getMessage());
		}
	}

	public String getOriginalCarrier(String msisdn) {
		msisdn = sanitizeMsisdn(msisdn);
		String sql = "SELECT original_carrier FROM tbl_master_np WHERE msisdn = :msisdn";

		MapSqlParameterSource namedParameters = new MapSqlParameterSource().addValue("msisdn", msisdn);
		try {
			return namedParameterJdbcTemplate.queryForObject(sql, namedParameters, String.class);
		} catch (EmptyResultDataAccessException ex) {
			return null;
		} catch (Exception e) {
			_logger.error("CarrierRepository.getOriginalCarrier() - " + e.getMessage(), e);
			return null;
		}
	}

	public List<PortMeDetails> getListPortMtDetails(String reqType, int portId) {
		List<PortMeDetails> list = null;
		try {
			String sql = "select port_mt.id as port_id,port_tx.request_id,port_tx.area,port_tx.dno,port_mt.msisdn,port_tx.service,constants.description as status,"
					+ "port_mt.imsi,port_mt.hlr,port_mt.sim,TO_CHAR(port_mt.created_date_time,'dd-mm-yyyy HH24:MI:SS') created_date_time,TO_CHAR(port_mt.updated_date_time,'dd-mm-yyyy HH24:MI:SS') updated_date_time from port_mt "
					+ "join port_tx on port_mt.port_id=port_tx.port_id "
					+ "join constants on constants.constcode=port_mt.status where constants.constkey=:reqType and port_mt.port_id=:port_id";
			MapSqlParameterSource namedParameters = new MapSqlParameterSource();
			namedParameters.addValue("reqType", reqType);
			namedParameters.addValue("port_id", portId);
			list = namedParameterJdbcTemplate.query(sql, namedParameters, new PortMeDetailsMapper());
			_logger.debug(" PortMtDao.getListPortMtDetails() SQL = {} ; Returned list = {}", sql, list);
		} catch (Exception e) {
			list = new ArrayList<PortMeDetails>();
			_logger.error("Exception occurs while getting PortMtDao.getListPortMtDetails()-" + e.getMessage());
		}
		return list;
	}

	public List<PortMeDetails> getListPortMEDetails(String reqType, int userId) {
		List<PortMeDetails> list = null;
		try {
			String sql = "select port_mt.id as port_id,port_tx.request_id,port_tx.area,port_tx.dno,port_mt.msisdn,port_tx.service,constants.description as status,"
					+ "port_mt.imsi,port_mt.hlr,port_mt.sim,TO_CHAR(port_mt.created_date_time,'dd-mm-yyyy HH24:MI:SS') created_date_time,TO_CHAR(port_mt.updated_date_time,'dd-mm-yyyy HH24:MI:SS') updated_date_time from port_mt "
					+ "join port_tx on port_mt.port_id=port_tx.port_id and port_mt.status not in (20,21,22)"
					+ "join constants on constants.constcode=port_mt.status where constants.constkey=:reqType and port_mt.request_type=:request_type and port_tx.user_id=:user_id order by port_mt.created_date_time desc";
			MapSqlParameterSource namedParameters = new MapSqlParameterSource();
			namedParameters.addValue("reqType", reqType);
			namedParameters.addValue("request_type", reqType);
			namedParameters.addValue("user_id", userId);
			list = namedParameterJdbcTemplate.query(sql, namedParameters, new PortMeDetailsMapper());
			_logger.debug(" PortMtDao.getListPortMtDetails() SQL = {} ; Returned list = {}", sql, list);
		} catch (Exception e) {
			list = new ArrayList<PortMeDetails>();
			_logger.error("Exception occurs while getting PortMtDao.getListPortMEDetails()-" + e.getMessage());
		}
		return list;
	}

	@Transactional
	public List<PortMeDetails> getListPortMeDetailsByDateRange(String reqType, String dateRange, String reqId,
			String msisdn, int userId) {
		List<PortMeDetails> list = null;
		try {
			if (reqId != "" && msisdn == "" && dateRange == null) {
				String sql = "SELECT port_mt.id AS port_id, port_tx.request_id, port_tx.area, port_tx.dno, port_mt.msisdn, "
						+ "port_tx.service, constants.description AS status, port_mt.imsi, port_mt.hlr, port_mt.sim, "
						+ "TO_CHAR(port_mt.created_date_time, 'dd-mm-yyyy HH24:MI:SS') AS created_date_time, "
						+ "TO_CHAR(port_mt.updated_date_time, 'dd-mm-yyyy HH24:MI:SS') AS updated_date_time "
						+ "FROM port_mt " + "JOIN port_tx ON port_mt.port_id = port_tx.port_id "
						+ "JOIN constants ON constants.constcode = port_mt.status "
						+ "WHERE constants.constkey = :reqType AND port_tx.user_id = :userId AND port_tx.request_id = :reqId";

				MapSqlParameterSource namedParameters = new MapSqlParameterSource();
				namedParameters.addValue("reqType", reqType);
				namedParameters.addValue("userId", userId);
				namedParameters.addValue("reqId", reqId);
				list = namedParameterJdbcTemplate.query(sql, namedParameters, new PortMeDetailsMapper());

			} else if (reqId == "" && msisdn != "" && dateRange == null) {
				String sql = "SELECT port_mt.id AS port_id, port_tx.request_id, port_tx.area, port_tx.dno, port_mt.msisdn, "
						+ " port_tx.service, constants.description AS status, port_mt.imsi, port_mt.hlr, port_mt.sim, "
						+ " TO_CHAR(port_mt.created_date_time, 'dd-mm-yyyy HH24:MI:SS') AS created_date_time, "
						+ " TO_CHAR(port_mt.updated_date_time, 'dd-mm-yyyy HH24:MI:SS') AS updated_date_time "
						+ " FROM port_mt JOIN port_tx ON port_mt.port_id = port_tx.port_id "
						+ " JOIN constants ON constants.constcode = port_mt.status "
						+ " WHERE constants.constkey = :reqType AND port_tx.user_id = :user_id AND port_mt.msisdn = :msisdn";
				MapSqlParameterSource namedParameters = new MapSqlParameterSource();
				namedParameters.addValue("reqType", reqType);
				namedParameters.addValue("user_id", userId);
				namedParameters.addValue("msisdn", msisdn);
				list = namedParameterJdbcTemplate.query(sql, namedParameters, new PortMeDetailsMapper());
			} else if (reqId == "" && msisdn == "" && dateRange != null) {
				String sql = "SELECT port_mt.id AS port_id, port_tx.request_id, port_tx.area, port_tx.dno, port_mt.msisdn, "
						+ " port_tx.service, constants.description AS status, port_mt.imsi, port_mt.hlr, port_mt.sim, "
						+ " TO_CHAR(port_mt.created_date_time, 'dd-mm-yyyy HH24:MI:SS') AS created_date_time, "
						+ " TO_CHAR(port_mt.updated_date_time, 'dd-mm-yyyy HH24:MI:SS') AS updated_date_time "
						+ " FROM port_mt JOIN port_tx ON port_mt.port_id = port_tx.port_id "
						+ " JOIN constants ON constants.constcode = port_mt.status "
						+ " WHERE constants.constkey = :reqType AND port_tx.user_id = :user_id AND Date(port_tx.created_date_time) BETWEEN "
						+ dateRange;

				MapSqlParameterSource namedParameters = new MapSqlParameterSource();
				namedParameters.addValue("reqType", reqType);
				namedParameters.addValue("user_id", userId);
				list = namedParameterJdbcTemplate.query(sql, namedParameters, new PortMeDetailsMapper());
			} else if (reqId != "" && msisdn != "" && dateRange != null) {
				String sql = "SELECT port_mt.id AS port_id, port_tx.request_id, port_tx.area, port_tx.dno, port_mt.msisdn, "
						+ " port_tx.service, constants.description AS status, port_mt.imsi, port_mt.hlr, port_mt.sim, "
						+ " TO_CHAR(port_mt.created_date_time, 'dd-mm-yyyy HH24:MI:SS') AS created_date_time, "
						+ " TO_CHAR(port_mt.updated_date_time, 'dd-mm-yyyy HH24:MI:SS') AS updated_date_time "
						+ " FROM port_mt JOIN port_tx ON port_mt.port_id = port_tx.port_id "
						+ " JOIN constants ON constants.constcode = port_mt.status "
						+ " WHERE constants.constkey = :reqType AND port_tx.user_id = :user_id AND port_tx.request_id = :reqId AND port_mt.msisdn = :msisdn AND Date(port_tx.created_date_time) BETWEEN "
						+ dateRange;
				MapSqlParameterSource namedParameters = new MapSqlParameterSource();
				namedParameters.addValue("reqType", reqType);
				namedParameters.addValue("user_id", userId);
				namedParameters.addValue("reqId", reqId);
				namedParameters.addValue("msisdn", msisdn);
				list = namedParameterJdbcTemplate.query(sql, namedParameters, new PortMeDetailsMapper());
			} else if (reqId != "" && msisdn != "" && dateRange == null) {
				String sql = "SELECT port_mt.id AS port_id, port_tx.request_id, port_tx.area, port_tx.dno, port_mt.msisdn, "
						+ " port_tx.service, constants.description AS status, port_mt.imsi, port_mt.hlr, port_mt.sim, "
						+ " TO_CHAR(port_mt.created_date_time, 'dd-mm-yyyy HH24:MI:SS') AS created_date_time, "
						+ " TO_CHAR(port_mt.updated_date_time, 'dd-mm-yyyy HH24:MI:SS') AS updated_date_time "
						+ " FROM port_mt JOIN port_tx ON port_mt.port_id = port_tx.port_id "
						+ " JOIN constants ON constants.constcode = port_mt.status "
						+ " WHERE constants.constkey = :reqType AND port_tx.user_id = :user_id AND port_mt.msisdn = :msisdn AND port_tx.request_id = :reqId";
				MapSqlParameterSource namedParameters = new MapSqlParameterSource();
				namedParameters.addValue("reqType", reqType);
				namedParameters.addValue("user_id", userId);
				namedParameters.addValue("msisdn", msisdn);
				namedParameters.addValue("reqId", reqId);
				list = namedParameterJdbcTemplate.query(sql, namedParameters, new PortMeDetailsMapper());
			} else if (reqId == "" && msisdn != "" && dateRange != null) {
				String sql = "SELECT port_mt.id AS port_id, port_tx.request_id, port_tx.area, port_tx.dno, port_mt.msisdn, "
						+ " port_tx.service, constants.description AS status, port_mt.imsi, port_mt.hlr, port_mt.sim, "
						+ " TO_CHAR(port_mt.created_date_time, 'dd-mm-yyyy HH24:MI:SS') AS created_date_time, "
						+ " TO_CHAR(port_mt.updated_date_time, 'dd-mm-yyyy HH24:MI:SS') AS updated_date_time "
						+ " FROM port_mt JOIN port_tx ON port_mt.port_id = port_tx.port_id "
						+ " JOIN constants ON constants.constcode = port_mt.status "
						+ " WHERE constants.constkey = :reqType AND port_tx.user_id = :user_id AND port_mt.msisdn = :msisdn  AND Date(port_tx.created_date_time) BETWEEN "+dateRange;
				MapSqlParameterSource namedParameters = new MapSqlParameterSource();
				namedParameters.addValue("reqType", reqType);
				namedParameters.addValue("user_id", userId);
				namedParameters.addValue("msisdn", msisdn);
				list = namedParameterJdbcTemplate.query(sql, namedParameters, new PortMeDetailsMapper());
			} else if (reqId != "" && msisdn == "" && dateRange != null) {
				String sql = "SELECT port_mt.id AS port_id, port_tx.request_id, port_tx.area, port_tx.dno, port_mt.msisdn, "
						+ " port_tx.service, constants.description AS status, port_mt.imsi, port_mt.hlr, port_mt.sim, "
						+ " TO_CHAR(port_mt.created_date_time, 'dd-mm-yyyy HH24:MI:SS') AS created_date_time, "
						+ " TO_CHAR(port_mt.updated_date_time, 'dd-mm-yyyy HH24:MI:SS') AS updated_date_time "
						+ " FROM port_mt JOIN port_tx ON port_mt.port_id = port_tx.port_id "
						+ " JOIN constants ON constants.constcode = port_mt.status "
						+ " WHERE constants.constkey = :reqType AND port_tx.user_id = :user_id AND port_tx.request_id = :reqId  AND Date(port_tx.created_date_time) BETWEEN "+dateRange;
				MapSqlParameterSource namedParameters = new MapSqlParameterSource();
				namedParameters.addValue("reqType", reqType);
				namedParameters.addValue("user_id", userId);
				namedParameters.addValue("reqId", reqId);
				list = namedParameterJdbcTemplate.query(sql, namedParameters, new PortMeDetailsMapper());
			}

		} catch (Exception e) {
			list = new ArrayList<PortMeDetails>();
			_logger.error(
					"Exception occurs while getting PortMtDao.getListPortMeDetailsByDateRange() - " + e.getMessage());
		}
		return list;
	}


	public List<PortMeDetails> getListPortMtDetails(String requestId) {
		List<PortMeDetails> list = null;
		try {
			String sql = "select port_mt.id as port_id,port_tx.request_id,port_tx.area,port_tx.dno,port_mt.msisdn,port_tx.service,constants.description as status,"
					+ "port_mt.imsi,port_mt.hlr,port_mt.sim,TO_CHAR(port_mt.created_date_time,'dd-mm-yyyy HH24:MI:SS') created_date_time,TO_CHAR(port_mt.updated_date_time,'dd-mm-yyyy HH24:MI:SS') updated_date_time from port_mt "
					+ "join port_tx on port_mt.port_id=port_tx.port_id "
					+ "join constants on constants.constcode=port_mt.status where port_tx.request_id=:request_id";
			MapSqlParameterSource namedParameters = new MapSqlParameterSource();
			namedParameters.addValue("request_id", requestId);
			list = namedParameterJdbcTemplate.query(sql, namedParameters, new PortMeDetailsMapper());
			_logger.debug(" PortMtDao.getListPortActivationDetails() SQL = {} ; Returned list = {}", sql, list);
		} catch (Exception e) {
			list = new ArrayList<PortMeDetails>();
			_logger.error("Exception occurs while getting PortMtDao.getListPortMtDetails()-" + e.getMessage());
		}
		return list;
	}

	@Transactional
	public List<PortMeDetails> getListPortMtDetails(String reqType, String requestId, int status, int userId) {
		List<PortMeDetails> list = null;
		try {
			String sql = "SELECT port_mt.id AS port_id, port_tx.request_id, port_tx.area, port_tx.dno, port_mt.msisdn, port_tx.service, constants.description AS status, "
					+ "port_mt.imsi, port_mt.hlr, port_mt.sim, TO_CHAR(port_mt.created_date_time, 'dd-mm-yyyy HH24:MI:SS') created_date_time, TO_CHAR(port_mt.updated_date_time, 'dd-mm-yyyy HH24:MI:SS') updated_date_time "
					+ "FROM port_mt JOIN port_tx ON port_mt.port_id = port_tx.port_id "
					+ "JOIN constants ON constants.constcode = port_mt.status "
					+ "WHERE constants.constkey = :reqType AND port_tx.request_id = :requestId AND port_tx.user_id = :userId AND port_mt.status = :status";

			MapSqlParameterSource namedParameters = new MapSqlParameterSource();
			namedParameters.addValue("reqType", reqType);
			namedParameters.addValue("requestId", requestId);
			namedParameters.addValue("userId", userId);
			namedParameters.addValue("status", status);
			list = namedParameterJdbcTemplate.query(sql, namedParameters, new PortMeDetailsMapper());
			return list;
		} catch (Exception e) {
			_logger.error("Exception occurs while getting PortMtDao.getListPortMtDetails() - " + e.getMessage());
			return new ArrayList<PortMeDetails>();
		}
	}
}