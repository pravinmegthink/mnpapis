package com.megthink.gateway.api;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.megthink.gateway.dao.DashboardDao;
import com.megthink.gateway.dao.NumberPlanDao;
import com.megthink.gateway.model.DashboardChart;
import com.megthink.gateway.model.NumberPlan;

@RestController
public class DashboardApi {

	private static final Logger LOGGER = LoggerFactory.getLogger(DashboardApi.class);

	@Autowired
	DashboardDao dashboardDao;

	@Autowired
	private NumberPlanDao numberPlanDao;

	@RequestMapping(value = "/chartsData", method = RequestMethod.GET, produces = { "application/xml",
			"application/json" })
	@ResponseStatus(HttpStatus.OK)
	public String getChartData(@RequestParam("chartNumber") String chartNumber) throws JSONException {
		List<NumberPlan> list = numberPlanDao.getOperatorInformation();
		if (list.size() > 0) {
			JSONArray results = new JSONArray();
			int chartNum = Integer.parseInt(chartNumber);
			try {
				if (chartNum == 1) {
					for (NumberPlan item : list) {
						List<DashboardChart> dashbaordList = dashboardDao.getDashboardDataByOpId(item.getOp_id());
						JSONObject myDashboardObj = new JSONObject();
						myDashboardObj.put("name", item.getOp_name());
						myDashboardObj.put("data", dashbaordList);
						results.put(myDashboardObj);
					}
					return results.toString();
				}
				if (chartNum == 2) {
					int zone1Count = dashboardDao.getTotalCount(1);
					JSONObject myJioObj = new JSONObject();
					myJioObj.put("name", "Zone 1");
					myJioObj.put("count", zone1Count);
					results.put(myJioObj);
					int zone2Count = dashboardDao.getTotalCount(2);
					JSONObject myAirtelObj = new JSONObject();
					myAirtelObj.put("name", "Zone 2");
					myAirtelObj.put("count", zone2Count);
					results.put(myAirtelObj);
					return results.toString();
				}
			} catch (Exception e) {
				LOGGER.error("Getting when generating dashboard api - " + e.getMessage());
			}
		}
		return null;
	}
}