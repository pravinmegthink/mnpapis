package com.megthink.gateway.dao;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import com.megthink.gateway.dao.common.CommonDao;
import com.megthink.gateway.dao.mapper.TerminateSimMTMapper;
import com.megthink.gateway.model.TerminateSimMT;

@Repository
public class TerminateSimMTDao extends CommonDao {

	private static final Logger _logger = LoggerFactory.getLogger(TerminateSimMTDao.class);

	@Autowired
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	public String sanitizeMsisdn(String input) {
		return input.replaceAll("[^0-9]", "");
	}
	
	public List<TerminateSimMT> getListOfTerminateSimMT(int terminateId, int resultCode) {
		List<TerminateSimMT> list = null;
		try {
			String sql = "select msisdn, status, result_code from sim_terminate_mt where terminate_id=:terminate_id and result_code=:result_code";

			MapSqlParameterSource namedParameters = new MapSqlParameterSource();
			namedParameters.addValue("terminate_id", terminateId);
			namedParameters.addValue("result_code", resultCode);
			list = namedParameterJdbcTemplate.query(sql, namedParameters, new TerminateSimMTMapper());
		} catch (Exception e) {
			_logger.error(
					"Exception occurs while getting TerminateSimMTDao.getListOfTerminateSimMT()-" + e.getMessage());
		}
		return list;
	}

	public String getOriginalCarrier(String msisdn) {
		String carrier = null;
		try {
			String sql = "select orginal_carrier from tbl_master_np where msisdn=:msisdn";
			MapSqlParameterSource namedParameters = new MapSqlParameterSource().addValue("msisdn", msisdn);
			carrier = executeSingleStringColumnQuery(sql, namedParameters);
		} catch (Exception e) {
			carrier = null;
			_logger.error("TerminateSimMTDao.getOriginalCarrier() - " + e.getMessage());
		}
		return carrier;
	}
}