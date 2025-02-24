package com.megthink.gateway.service;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import com.megthink.gateway.model.NumberPlan;
import com.megthink.gateway.repository.NumberPlanRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;

@Service("numberPlanService")
public class NumberPlanService {
	private static final Logger _logger = LoggerFactory.getLogger(NumberPlanService.class);

	@PersistenceContext
	private EntityManager entityManager;

	private final NumberPlanRepository repository;

	@Autowired
	public NumberPlanService(NumberPlanRepository repository) {
		this.repository = repository;
	}

	public NumberPlan save(NumberPlan numberPlan) {
		try {
			return repository.save(numberPlan);
		} catch (DataAccessException ex) {
			_logger.error("NumberPlanService.save()-Error occurs while saving NumberPlan: " + ex.getMessage(), ex);
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	@Transactional
	public List<NumberPlan> getOperatorInformationByOpId(String op_id) {
		try {
			Query query = entityManager
					.createNativeQuery("SELECT * FROM msisdn_range WHERE msisdn_range.op_id = ?", NumberPlan.class)
					.setParameter(1, op_id);
			List<NumberPlan> items = (List<NumberPlan>) query.getResultList();
			return items;
		} catch (Exception e) {
			e.getStackTrace();
		}
		return null;
	}

	@Transactional
	public int isExist(String startRange, String endRange) {
		Integer count = null;
		try {
			String sql = "SELECT count(*) FROM msisdn_range WHERE msisdn_range.start_range = ? and msisdn_range.end_range = ?";
			Query query = entityManager.createNativeQuery(sql, Integer.class).setParameter(1, startRange)
					.setParameter(2, endRange);
			count = (Integer) query.getSingleResult();
			return count;
		} catch (Exception e) {
			e.getStackTrace();
		}
		return count;
	}

	@Transactional
	public void updateNumberPlan(NumberPlan item) {
		try {
			entityManager.createNativeQuery(
					"UPDATE msisdn_range SET area = ?, start_range = ?, end_range = ?, technology = ?, type = ?, routing_info = ?, changed_by = ?, update_date = now() WHERE op_id = ?")
					.setParameter(1, item.getArea()).setParameter(2, item.getStart_range())
					.setParameter(3, item.getEnd_range()).setParameter(4, item.getTechnology())
					.setParameter(5, item.getType()).setParameter(6, item.getRouting_info())
					.setParameter(7, item.getChanged_by()).setParameter(8, item.getOp_id()).executeUpdate();
		} catch (Exception e) {
			_logger.error("NumberPlanService.updateNumberPlan() - " + e.getMessage());
		}
	}
}