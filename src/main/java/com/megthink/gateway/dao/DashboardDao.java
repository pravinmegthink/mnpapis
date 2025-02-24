package com.megthink.gateway.dao;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import com.megthink.gateway.dao.common.CommonDao;
import com.megthink.gateway.dao.mapper.DashboardMapper;
import com.megthink.gateway.model.DashboardChart;

@Repository
public class DashboardDao extends CommonDao {

	@Autowired
	UserDao userDao;

	private static final Logger _logger = LoggerFactory.getLogger(DashboardDao.class);

	@Autowired
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	public List<DashboardChart> getDashboardDataByOpId(String opId) {
		List<DashboardChart> list = null;
		try {
			String sql = "SELECT DATE(date_range.date) AS date, "
					+ " COALESCE(Count(tbl_broadcast.msisdn), 0) AS count FROM ("
					+ "  SELECT generate_series(CURRENT_DATE - INTERVAL '7 days', CURRENT_DATE, '1 day'::interval) AS date "
					+ ") AS date_range "
					+ "LEFT JOIN tbl_broadcast ON date_range.date = DATE(tbl_broadcast.transaction_date) "
					+ " AND tbl_broadcast.orginal_carrier='" + opId + "' GROUP BY date_range.date;";
			MapSqlParameterSource namedParameters = new MapSqlParameterSource();
			list = namedParameterJdbcTemplate.query(sql, namedParameters, new DashboardMapper());
			_logger.debug("DashboardDao.getDashboardDataByOpId() SQL = {} ; namedParameters = status : {} ; Returned list = {}",
					sql, 2, list);
		} catch (Exception e) {
			_logger.error("Exception occurs when getting chart data DashboardDao.getDashboardDataByOpId()->" + e.getMessage());
		}
		return list;
	}

	public List<DashboardChart> getAirtelCount(int userId) {
		List<DashboardChart> list = null;
		try {
			String sql = "select extract('MONTH' from first_trans_date) as monthNumber, "
					+ "	TO_CHAR(DATE (Date(first_trans_date)), 'Month') AS xAxis, "
					+ " extract('MONTH' from current_date) AS currentMonth, COUNT(*) AS count from "
					+ "  tbl_broadcast_audit group by 2, extract('MONTH' from first_trans_date);";
			MapSqlParameterSource namedParameters = new MapSqlParameterSource();
			list = namedParameterJdbcTemplate.query(sql, namedParameters, new DashboardMapper());
			_logger.debug("DashboardDao.getAirtelCount() SQL = {} ; namedParameters = status : {} ; Returned list = {}",
					sql, 2, list);
		} catch (Exception e) {
			_logger.error("Getting erroro when getting chart data DashboardDao.getAirtelCount()->" + e.getMessage());
		}
		return list;
	}

	public int getTotalCount(int mchType) {
		String sql = "SELECT COUNT(*) FROM tbl_broadcast_audit WHERE mch = :mch";
		MapSqlParameterSource namedParameters = new MapSqlParameterSource().addValue("mch", mchType);
		try {
			return namedParameterJdbcTemplate.queryForObject(sql, namedParameters, Integer.class);
		} catch (Exception e) {
			_logger.error("DashboardRepository.getTotalCount() - " + e.getMessage(), e);
			return 0;
		}
	}
}