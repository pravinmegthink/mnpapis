package com.megthink.gateway.dao;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import com.megthink.gateway.dao.mapper.SubscriberMapper;
import com.megthink.gateway.model.SubscriberArrType;

@Repository
public class SubscriberDao {

    private static final Logger _logger = LoggerFactory.getLogger(SubscriberDao.class);

    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public List<SubscriberArrType> getSubscriberDetailsByMsisdn(String msisdn, String reqType) {
        try {
            if (msisdn != null) {
                msisdn = sanitizeMsisdn(msisdn);
                String sql = "SELECT * FROM port_mt WHERE msisdn = :msisdn AND request_type = :request_type";

                MapSqlParameterSource namedParameters = new MapSqlParameterSource()
                        .addValue("msisdn", sanitizeMsisdn(msisdn))
                        .addValue("request_type", reqType);

                return namedParameterJdbcTemplate.query(sql, namedParameters, new SubscriberMapper());
            }
        } catch (Exception e) {
            _logger.error("Exception occurs while getting SubscriberDao.getSubscriberDetailsByMsisdn() - " + e.getMessage(), e);
        }
        return new ArrayList<>();
    }

    public String sanitizeMsisdn(String input) {
        return input.replaceAll("[^0-9]", "");
    }
}
