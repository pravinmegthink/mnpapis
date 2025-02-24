package com.megthink.gateway.dao;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import com.megthink.gateway.dao.common.CommonDao;
import com.megthink.gateway.dao.mapper.PortMeMapper;
import com.megthink.gateway.model.PortMe;

@Repository
public class PortMeDao extends CommonDao {

	private static final Logger _logger = LoggerFactory.getLogger(PortMeDao.class);

	@Autowired
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	public List<PortMe> getListPortMeDetails(String reqType, int userId) {
		List<PortMe> list = null;
		try {
			String sql = "select port_id,request_id,area,dno,service,constants.description as status,Date(created_date_time) as created_date_time from port_tx "
					+ "join constants on constkey=:constkey and constcode=port_tx.status where request_type=:request_type and port_tx.user_id=:user_id order by created_date_time desc";
			MapSqlParameterSource namedParameters = new MapSqlParameterSource();
			namedParameters.addValue("constkey", reqType);
			namedParameters.addValue("request_type", reqType);
			namedParameters.addValue("user_id", userId);
			list = namedParameterJdbcTemplate.query(sql, namedParameters, new PortMeMapper());
			_logger.debug(" PortMeDao.getListPortMeDetails() SQL = {} ; Returned list = {}", sql, list);
		} catch (Exception e) {
			list = new ArrayList<PortMe>();
			_logger.error("Exception occurs while getting PortmeDao.getListPortMeDetails-" + e.getMessage());
		}
		return list;
	}

	public List<PortMe> getListPortMeDetails(String reqType, int userId, String reqId, String dateRange) {
		List<PortMe> list = null;
		try {
			if (reqId != "" && dateRange == null) {
				String sql = "select port_id,request_id,area,dno,service,constants.description as status,Date(created_date_time) as created_date_time from port_tx "
						+ "join constants on constkey=:constkey and constcode=port_tx.status where request_type=:request_type and port_tx.user_id=:user_id and port_tx.request_id=:reqId order by created_date_time desc";
				MapSqlParameterSource namedParameters = new MapSqlParameterSource();
				namedParameters.addValue("constkey", reqType);
				namedParameters.addValue("request_type", reqType);
				namedParameters.addValue("user_id", userId);
				namedParameters.addValue("reqId", reqId);
				list = namedParameterJdbcTemplate.query(sql, namedParameters, new PortMeMapper());
			} else if (reqId == "" && dateRange != null) {
				String sql = "select port_id,request_id,area,dno,service,constants.description as status,Date(created_date_time) as created_date_time from port_tx "
						+ "join constants on constkey=:constkey and constcode=port_tx.status where request_type=:request_type and port_tx.user_id=:user_id AND Date(port_tx.created_date_time) BETWEEN "
						+ dateRange + " order by created_date_time desc";
				MapSqlParameterSource namedParameters = new MapSqlParameterSource();
				namedParameters.addValue("constkey", reqType);
				namedParameters.addValue("request_type", reqType);
				namedParameters.addValue("user_id", userId);
				list = namedParameterJdbcTemplate.query(sql, namedParameters, new PortMeMapper());
			} else if (reqId != "" && dateRange != null) {
				String sql = "select port_id,request_id,area,dno,service,constants.description as status,Date(created_date_time) as created_date_time from port_tx "
						+ "join constants on constkey=:constkey and constcode=port_tx.status where request_type=:request_type and port_tx.user_id=:user_id and port_tx.request_id=:reqId AND Date(port_tx.created_date_time) BETWEEN "
						+ dateRange + " order by created_date_time desc";
				MapSqlParameterSource namedParameters = new MapSqlParameterSource();
				namedParameters.addValue("constkey", reqType);
				namedParameters.addValue("request_type", reqType);
				namedParameters.addValue("user_id", userId);
				namedParameters.addValue("reqId", reqId);
				list = namedParameterJdbcTemplate.query(sql, namedParameters, new PortMeMapper());
			}
		} catch (Exception e) {
			list = new ArrayList<PortMe>();
			_logger.error("Exception occurs while getting PortmeDao.getListPortMeDetails-" + e.getMessage());
		}
		return list;
	}

	public List<PortMe> getListPortMT(String reqType, int status, int userId) {
		List<PortMe> list = null;
		try {
			String sql = "select port_id,request_id,area,dno,service,constants.description as status,Date(created_date_time) as created_date_time from port_tx "
					+ "join constants on constkey=:constkey and constcode=port_tx.status where request_type=:request_type and port_tx.status =:status order by created_date_time desc";
			MapSqlParameterSource namedParameters = new MapSqlParameterSource();
			namedParameters.addValue("constkey", reqType);
			namedParameters.addValue("request_type", reqType);
			namedParameters.addValue("status", status);
			// namedParameters.addValue("user_id", userId);
			list = namedParameterJdbcTemplate.query(sql, namedParameters, new PortMeMapper());
			_logger.info("getting port activation results...", sql);
			_logger.debug(" PortMeDao.getListPortMeDetails() SQL = {} ; Returned list = {}", sql, list);
		} catch (Exception e) {
			list = new ArrayList<PortMe>();
		}
		return list;
	}

	/* shobhit */
	public List<String> getListOfAreaByOpId(String opId) {
		String sql = "select area from msisdn_range where op_id=:opId";
		MapSqlParameterSource namedParameters = new MapSqlParameterSource();
		namedParameters.addValue("opId", opId);
		List<String> resultList = (List<String>) getNamedParameterJdbcOperations().queryForList(sql, namedParameters,
				String.class);
		_logger.debug(" UserDao.getUserHierarchy() SQL = {} ; Returned List = {}", sql, resultList);
		return resultList;
	}

	public String getRnbyOpIdandarea(String opId, String area) {
		String sql = "select routing_info from msisdn_range where op_id=:opId and area=:area";
		MapSqlParameterSource namedParameters = new MapSqlParameterSource();
		namedParameters.addValue("opId", opId);
		namedParameters.addValue("area", area);
		String resultList = getNamedParameterJdbcOperations().queryForObject(sql, namedParameters, String.class);
		_logger.debug(" UserDao.getUserHierarchy() SQL = {} ; Returned List = {}", sql, resultList);
		return resultList;
	}

	public String getdonorbymsisdn(String msisdn) {
		String sql = "select op_id from msisdn_range where :msisdn between start_range and end_range";
		MapSqlParameterSource namedParameters = new MapSqlParameterSource();
		namedParameters.addValue("msisdn", msisdn);
		String resultList = getNamedParameterJdbcOperations().queryForObject(sql, namedParameters, String.class);
		_logger.debug("UserDao.getUserHierary() SQL = {}; Returned List = {}", sql, resultList);
		return resultList;
	}
	/* end shobhit */
}