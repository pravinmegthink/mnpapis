package com.megthink.gateway.dao;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import com.megthink.gateway.dao.mapper.TerminateSIMMapper;
import com.megthink.gateway.model.TerminateSim;

@Repository
public class TerminateSimDao {

	private static final Logger _logger = LoggerFactory.getLogger(TerminateSimDao.class);

	@Autowired
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	public void updateTerminateSIM(int terminateId, int currstatus) {
		try {
			String sql = "update sim_terminate_tx set status=:status,updated_date_time = now() WHERE terminate_id=:terminate_id";
			MapSqlParameterSource namedParameters = new MapSqlParameterSource();
			namedParameters.addValue("status", currstatus);
			namedParameters.addValue("terminate_id", terminateId);
			int rowsUpdated = namedParameterJdbcTemplate.update(sql, namedParameters);
			if (rowsUpdated > 0) {
				_logger.info("Updated successfully");
			} else {
				_logger.info("Updated fail");
			}
		} catch (Exception e) {
			_logger.error("TerminateSimDao.current_status() - " + e.getMessage());
		}
	}

	private boolean isValidRequestId(String requestId) {
		return requestId != null && requestId.matches("^[A-Za-z0-9]+$");
	}

	public void updateTerminateSIM(String requestId, int currstatus, int userId) {
		if (requestId != null) {
			if (!isValidRequestId(requestId)) {
				throw new IllegalArgumentException("Invalid requestId");
			}
			try {
				String sql = "update sim_terminate_tx set status=:status,user_id=:user_id,updated_date_time = now() WHERE request_id=:request_id";

				MapSqlParameterSource namedParameters = new MapSqlParameterSource();
				namedParameters.addValue("status", currstatus);
				namedParameters.addValue("user_id", userId);
				namedParameters.addValue("request_id", requestId);
				int rowsUpdated = namedParameterJdbcTemplate.update(sql, namedParameters);
				if (rowsUpdated > 0) {
					_logger.info("Updated successfully");
				} else {
					_logger.info("Updated fail");
				}
			} catch (Exception e) {
				_logger.error("TerminateSimDao.current_status() - " + e.getMessage());
			}
		}
	}

	public void updateTerminateSIMbyRequestId(String requestId, int currstatus) {
		if (requestId != null) {
			if (!isValidRequestId(requestId)) {
				throw new IllegalArgumentException("Invalid requestId");
			}
			try {
				String sql = "update sim_terminate_tx set status=:status,updated_date_time = now() WHERE request_id=:request_id";
				MapSqlParameterSource namedParameters = new MapSqlParameterSource();
				namedParameters.addValue("status", currstatus);
				namedParameters.addValue("request_id", requestId);
				int rowsUpdated = namedParameterJdbcTemplate.update(sql, namedParameters);
				if (rowsUpdated > 0) {
					_logger.info("Updated successfully");
				} else {
					_logger.info("Updated fail");
				}
			} catch (Exception e) {
				_logger.error("TerminateSimDao.current_status() - " + e.getMessage());
			}
		}
	}

	public List<TerminateSim> getMSISDNDetails(String msisdn) {
		List<TerminateSim> list = null;
		try {
			String sql = "select null as request_id, msisdn,area,orginal_carrier,present_carrier as dno from tbl_master_np where msisdn=:msisdn";
			MapSqlParameterSource namedParameters = new MapSqlParameterSource();
			namedParameters.addValue("msisdn", msisdn);
			list = namedParameterJdbcTemplate.query(sql, namedParameters, new TerminateSIMMapper());
			_logger.debug(" PortMtDao.getMSISDNDetails() SQL = {} ; Returned list = {}", sql, list);
		} catch (Exception e) {
			list = new ArrayList<TerminateSim>();
			_logger.error("Exception occurs while getting TerminateSimDao.getMSISDNDetails()-" + e.getMessage());
		}
		return list;
	}

	public List<TerminateSim> getNRHDetails() {
		List<TerminateSim> list = null;
		try {
			String sql = "select tx.request_id ,mt.msisdn,tx.area,tx.original_carrier as orginal_carrier,tx.dno from sim_terminate_mt mt "
					+ "join sim_terminate_tx tx on tx.terminate_id=mt.terminate_id where mt.request_type=:request_type";
			MapSqlParameterSource namedParameters = new MapSqlParameterSource();
			namedParameters.addValue("request_type", "TERMINATE_OUT");
			list = namedParameterJdbcTemplate.query(sql, namedParameters, new TerminateSIMMapper());
			_logger.debug(" PortMtDao.getMSISDNDetails() SQL = {} ; Returned list = {}", sql, list);
		} catch (Exception e) {
			list = new ArrayList<TerminateSim>();
			_logger.error("Exception occurs while getting TerminateSimDao.getNRHDetails()-" + e.getMessage());
		}
		return list;
	}
}