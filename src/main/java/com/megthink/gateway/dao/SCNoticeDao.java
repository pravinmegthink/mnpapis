package com.megthink.gateway.dao;

import java.sql.Timestamp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import com.megthink.gateway.dao.common.CommonDao;
import com.megthink.gateway.model.MasterNP;

@Repository
public class SCNoticeDao extends CommonDao {

	private static final Logger _logger = LoggerFactory.getLogger(SCNoticeDao.class);

	public int updateMasterNP(MasterNP item, String sessionId) {
		String sql = null;
		int success = 0;
		try {
			sql = "update tbl_master_np set remark=:remark,area=:area,nqms_flag=0 ,rn=:rn,present_carrier=:present_carrier,re_trans_date=now(),action=:action,carrier_history=:carrier_history,orginal_carrier=:orginal_carrier,active=:active,transaction_date=:transaction_date,history_area=:history_area WHERE msisdn=:msisdn";
			SqlParameterSource namedParameters = new MapSqlParameterSource().addValue("remark", item.getRemark())
					.addValue("area", item.getArea()).addValue("rn", item.getRn())
					.addValue("present_carrier", item.getPresent_carrier()).addValue("action", item.getAction())
					.addValue("carrier_history", item.getCarrier_history())
					.addValue("orginal_carrier", item.getOrginal_carrier()).addValue("active", item.getActive())
					.addValue("transaction_date", item.getTransaction_date())
					.addValue("history_area", item.getHistory_area()).addValue("msisdn", item.getMsisdn());
			executeUpdate(sql, namedParameters);
			success = 1;
		} catch (Exception e) {
			success = 0;
			_logger.error("[sessionId=" + sessionId
					+ "]: Exception occurs during update SCNoticeDao.updateMasterNP(), sql : [" + sql
					+ "]  with timestamp:[" + new Timestamp(System.currentTimeMillis()) + "]-" + e.getMessage());
		}
		return success;
	}

	public int updateMasterNPFor1010Zone2(MasterNP item, String sessionId) {
		String sql = null;
		int flag = 0;
		try {
			sql = "update tbl_master_np set remark=:remark,area=:area,nqms_flag=0 ,present_carrier=:present_carrier,re_trans_date=now(),action=:action,carrier_history=:carrier_history,orginal_carrier=:orginal_carrier,active=:active,transaction_date=:transaction_date,history_area=:history_area,original_area=:original_area, rn=:rn WHERE msisdn=:msisdn";
			SqlParameterSource namedParameters = new MapSqlParameterSource().addValue("remark", item.getRemark())
					.addValue("area", item.getArea()).addValue("present_carrier", item.getPresent_carrier())
					.addValue("action", item.getAction()).addValue("carrier_history", item.getCarrier_history())
					.addValue("orginal_carrier", item.getOrginal_carrier()).addValue("active", item.getActive())
					.addValue("transaction_date", item.getTransaction_date())
					.addValue("history_area", item.getHistory_area()).addValue("original_area", item.getOriginal_area())
					.addValue("rn", item.getRn()).addValue("msisdn", item.getMsisdn());
			executeUpdate(sql, namedParameters);
			flag = 1;
		} catch (Exception e) {
			flag = 0;
			_logger.error("[sessionId=" + sessionId
					+ "]: Exception occurs during update SCNoticeDao.updateMasterNPFor1010Zone2(), sql : [" + sql
					+ "]  with timestamp:[" + new Timestamp(System.currentTimeMillis()) + "]-" + e.getMessage());
		}
		return flag;
	}

	public int updateSDMasterNP(MasterNP item, String sessionId) {
		int flag = 0;
		String sql = null;
		try {
			sql = "UPDATE tbl_master_np SET area=:area, nqms_flag=0, rn=:rn, present_carrier=:present_carrier, carrier_history=:carrier_history, active=:active, disconnection_date=:disconnection_date, re_trans_date=:re_trans_date, remark=:remark, action=:action,transaction_date=:transaction_date,history_area=:history_area,original_area=:original_area WHERE msisdn= :msisdn";
			SqlParameterSource namedParameters = new MapSqlParameterSource().addValue("area", item.getArea())
					.addValue("rn", item.getRn()).addValue("present_carrier", item.getPresent_carrier())
					.addValue("carrier_history", item.getCarrier_history()).addValue("active", item.getActive())
					.addValue("disconnection_date", item.getDisconnection_date())
					.addValue("re_trans_date", item.getRe_trans_date()).addValue("remark", item.getRemark())
					.addValue("action", item.getAction()).addValue("transaction_date", item.getTransaction_date())
					.addValue("history_area", item.getHistory_area()).addValue("original_area", item.getOriginal_area())
					.addValue("msisdn", item.getMsisdn());
			executeUpdate(sql, namedParameters);
			flag = 1;
		} catch (Exception e) {
			flag = 0;
			_logger.error("[sessionId=" + sessionId
					+ "]: Exception occurs during update SCNoticeDao.updateSDMasterNP(), sql : [" + sql
					+ "]  with timestamp:[" + new Timestamp(System.currentTimeMillis()) + "]-" + e.getMessage());
		}
		return flag;
	}
	
	public int updateMasterNPFor5004Zone2(MasterNP item, String sessionId) {
		String sql = null;
		int flag = 0;
		try {
			sql = "UPDATE tbl_master_np SET area=:area, nqms_flag=0, rn=:rn, present_carrier=:present_carrier, carrier_history=:carrier_history, active=:active, disconnection_date=:disconnection_date, re_trans_date=:re_trans_date, remark=:remark, action=:action,transaction_date=:transaction_date,history_area=:history_area,original_area=:original_area WHERE msisdn= :msisdn";

			SqlParameterSource namedParameters = new MapSqlParameterSource().addValue("area", item.getArea())
					.addValue("rn", item.getRn()).addValue("present_carrier", item.getPresent_carrier())
					.addValue("carrier_history", item.getCarrier_history()).addValue("active", item.getActive())
					.addValue("disconnection_date", item.getDisconnection_date())
					.addValue("re_trans_date", item.getRe_trans_date()).addValue("remark", item.getRemark())
					.addValue("action", item.getAction()).addValue("transaction_date", item.getTransaction_date())
					.addValue("history_area", item.getHistory_area()).addValue("original_area", item.getOriginal_area())
					.addValue("msisdn", item.getMsisdn());
			executeUpdate(sql, namedParameters);
			flag = 1;
		} catch (Exception e) {
			flag = 0;
			_logger.error("[sessionId=" + sessionId
					+ "]: Exception occurs during update SCNoticeDao.updateMasterNPFor5004Zone2(), sql : [" + sql
					+ "]  with timestamp:[" + new Timestamp(System.currentTimeMillis()) + "]-" + e.getMessage());
		}
		return flag;
	}

	public String getArea(String msisdn, String sessionId) {
		String area = null;
		String sql = null;
		try {
			_logger.info("[sessionId=" + sessionId + "]: SCNoticeDao.getArea()- trying to get area   with timestamp:["
					+ new Timestamp(System.currentTimeMillis()) + "]-");
			sql = "select area from msisdn_range where :msisdn BETWEEN msisdn_range.start_range AND msisdn_range.end_range limit 1";
			SqlParameterSource namedParameters = new MapSqlParameterSource().addValue("msisdn", msisdn);
			area = executeSingleStringColumnQuery(sql, namedParameters);
			_logger.info(
					"[sessionId=" + sessionId + "]: SCNoticeDao.getArea()- successfully to get area   with timestamp:["
							+ new Timestamp(System.currentTimeMillis()) + "]-SQL - " + sql);
		} catch (Exception e) {
			area = null;
		}
		if (area == null) {
			area = "NA";
		}
		return area;
	}

	public String getOperatorIdByMsisdn(String msisdn) {
		String opId = null;
		String sql = null;
		try {
			sql = "select op_id from msisdn_range where :msisdn BETWEEN msisdn_range.start_range AND msisdn_range.end_range limit 1;";
			SqlParameterSource namedParameters = new MapSqlParameterSource().addValue("msisdn", msisdn);
			opId = executeSingleStringColumnQuery(sql, namedParameters);

		} catch (CannotGetJdbcConnectionException jdbcConnectionException) {
			opId = "DBDown";
		} catch (Exception e) {
//			_logger.error(
//					"Exception occurs during SCNoticeDao.getOperatorIdByMsisdn() - " + e.getMessage() + " - " + sql);
		}
		if (opId == null) {
			opId = "NA";
		}
		return opId;
	}

	public String getMasterNPArea(String msisdn, String sessionId) {
		String area = null;
		try {
			String sql = "select  area from tbl_master_np where msisdn=:msisdn limit 1";
			SqlParameterSource namedParameters = new MapSqlParameterSource().addValue("msisdn", msisdn);
			area = executeSingleStringColumnQuery(sql, namedParameters);
			_logger.info("[sessionId=" + sessionId
					+ "]: SCNoticeDao.getMasterNPArea()- successfully to get masterNPArea   with timestamp:["
					+ new Timestamp(System.currentTimeMillis()) + "]-SQL - " + sql);
		} catch (CannotGetJdbcConnectionException jdbcConnectionException) {
			area = "DBDown";
		} catch (Exception e) {
			area = null;
		}
		return area;
	}

	public String getMasterNPOriginalCarrier(String msisdn, String sessionId) {
		String carrier = null;
		String sql = null;
		try {
			sql = "select  orginal_carrier from tbl_master_np where msisdn=:msisdn";
			SqlParameterSource namedParameters = new MapSqlParameterSource().addValue("msisdn", msisdn);
			carrier = executeSingleStringColumnQuery(sql, namedParameters);
		} catch (CannotGetJdbcConnectionException jdbcConnectionException) {
			carrier = "DBDown";
		} catch (Exception e) {
			carrier = null;
		}
		return carrier;
	}

	public boolean isDBConnectionLive(String sessionId) {
		try {
			_logger.info("[sessionId=" + sessionId + "]: Checking database connection health");
			String sql = "SELECT 1";
			SqlParameterSource namedParameters = new MapSqlParameterSource();
			Integer result = executeSingleIntColumnQuery(sql, namedParameters);
			_logger.info("[sessionId=" + sessionId + "]: Database connection is healthy");
			return result != null && result == 1;
		} catch (Exception e) {
			// Log or handle the exception if needed
			return false;
		}
	}

}