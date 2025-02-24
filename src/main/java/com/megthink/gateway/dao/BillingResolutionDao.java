package com.megthink.gateway.dao;

import java.sql.Timestamp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import com.megthink.gateway.dao.common.CommonDao;
import com.megthink.gateway.model.BillingResolution;

@Repository
public class BillingResolutionDao extends CommonDao {

	private static final Logger _logger = LoggerFactory.getLogger(BillingResolutionDao.class);

	public int updateBillingResolution(BillingResolution item, String sessionId) {
		String sql = null;
		int success = 0;
		try {
			sql = "update tbl_billing_resolution set reason=:reason,status=:status,user_id=user_id,updated_date=now(),canceled_date=now() WHERE msisdn=:msisdn and request_type=:request_type";
			SqlParameterSource namedParameters = new MapSqlParameterSource().addValue("reason", item.getReason())
					.addValue("status", item.getStatus()).addValue("user_id", item.getUser_id())
					.addValue("msisdn", item.getMsisdn()).addValue("request_type", item.getRequest_type());
			executeUpdate(sql, namedParameters);
			success = 1;
		} catch (Exception e) {
			success = 0;
			_logger.error("[sessionId=" + sessionId
					+ "]: Exception occurs during update BillingResolutionDao.updateBillingResolution(), sql : [" + sql
					+ "]  with timestamp:[" + new Timestamp(System.currentTimeMillis()) + "]-" + e.getMessage());
		}
		return success;
	}

	public int updateBillingResolutionACK(BillingResolution item, String sessionId) {
		String sql = null;
		int success = 0;
		try {
			sql = "update tbl_billing_resolution set bill_no=:bill_no,acc_no=:acc_no, status=:status,user_id=user_id,updated_date=now(),ack_date=now() WHERE msisdn=:msisdn and request_type=:request_type";
			SqlParameterSource namedParameters = new MapSqlParameterSource().addValue("bill_no", item.getBill_no())
					.addValue("acc_no", item.getAcc_no()).addValue("status", item.getStatus())
					.addValue("user_id", item.getUser_id()).addValue("msisdn", item.getMsisdn())
					.addValue("request_type", item.getRequest_type());
			executeUpdate(sql, namedParameters);
			success = 1;
		} catch (Exception e) {
			success = 0;
			_logger.error("[sessionId=" + sessionId
					+ "]: Exception occurs during update BillingResolutionDao.updateBillingResolutionACK(), sql : ["
					+ sql + "]  with timestamp:[" + new Timestamp(System.currentTimeMillis()) + "]-" + e.getMessage());
		}
		return success;
	}

	public String getRequestId(String msisdn) {
		String sql = null;
		try {
			sql = "select transaction_id from public.tbl_billing_resolution where msisdn= :msisdn";
			SqlParameterSource namedParameters = new MapSqlParameterSource().addValue("msisdn", msisdn);
			String reqId = executeSingleStringColumnQuery(sql, namedParameters);
			return reqId;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public int updateNPOSPR(BillingResolution item, String sessionId) {
		String sql = null;
		int success = 0;
		try {
			sql = "update tbl_billing_resolution set reason=:reason,status=:status,updated_date=now(),canceled_date=now() WHERE transaction_id=:transaction_id and request_type=:request_type";
			SqlParameterSource namedParameters = new MapSqlParameterSource().addValue("reason", item.getReason())
					.addValue("status", item.getStatus()).addValue("transaction_id", item.getTransactionId())
					.addValue("request_type", item.getRequest_type());
			executeUpdate(sql, namedParameters);
			success = 1;
		} catch (Exception e) {
			success = 0;
			_logger.error("[sessionId=" + sessionId
					+ "]: Exception occurs during update BillingResolutionDao.updateNPOSPR(), sql : [" + sql
					+ "]  with timestamp:[" + new Timestamp(System.currentTimeMillis()) + "]-" + e.getMessage());
		}
		return success;
	}

	public int updateNPOSAACK(BillingResolution item, String sessionId) {
		String sql = null;
		int success = 0;
		try {
			sql = "update tbl_billing_resolution set reason=:reason,status=:status,updated_date=now(),ack_date=now() WHERE transaction_id=:transaction_id and request_type=:request_type";
			SqlParameterSource namedParameters = new MapSqlParameterSource().addValue("reason", item.getReason())
					.addValue("status", item.getStatus()).addValue("transaction_id", item.getTransactionId())
					.addValue("request_type", item.getRequest_type());
			executeUpdate(sql, namedParameters);
			success = 1;
		} catch (Exception e) {
			success = 0;
			_logger.error("[sessionId=" + sessionId
					+ "]: Exception occurs during update BillingResolutionDao.updateNPOSAACK(), sql : [" + sql
					+ "]  with timestamp:[" + new Timestamp(System.currentTimeMillis()) + "]-" + e.getMessage());
		}
		return success;
	}

	public int updateNPOSA(BillingResolution item, String sessionId) {
		String sql = null;
		int success = 0;
		try {
			sql = "update tbl_billing_resolution set reason=:reason,status=:status,updated_date=now() WHERE transaction_id=:transaction_id and request_type=:request_type";
			SqlParameterSource namedParameters = new MapSqlParameterSource().addValue("reason", item.getReason())
					.addValue("status", item.getStatus()).addValue("transaction_id", item.getTransactionId())
					.addValue("request_type", item.getRequest_type());
			executeUpdate(sql, namedParameters);
			success = 1;
		} catch (Exception e) {
			success = 0;
			_logger.error("[sessionId=" + sessionId
					+ "]: Exception occurs during update BillingResolutionDao.updateNPOSAACK(), sql : [" + sql
					+ "]  with timestamp:[" + new Timestamp(System.currentTimeMillis()) + "]-" + e.getMessage());
		}
		return success;
	}
}