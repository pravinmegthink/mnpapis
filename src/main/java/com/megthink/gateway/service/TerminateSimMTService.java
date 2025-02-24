package com.megthink.gateway.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.megthink.gateway.model.TerminateSimMT;
import com.megthink.gateway.repository.TerminateSimMTRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

@Service("terminateSimMTService")
public class TerminateSimMTService {
	private static final Logger _logger = LoggerFactory.getLogger(TerminateSimMTService.class);
	@PersistenceContext
	private EntityManager entityManager;
	private TerminateSimMTRepository terminateSimMTRepository;

	@Autowired
	public TerminateSimMTService(TerminateSimMTRepository terminateSimMTRepository) {
		this.terminateSimMTRepository = terminateSimMTRepository;
	}

	public TerminateSimMT saveTerminateSimMT(TerminateSimMT terminateSimMT) {
		return terminateSimMTRepository.save(terminateSimMT);
	}

	@Transactional
	public void updateTerminateSIMMT(int currstatus, String msisdn, String req_type, int resultCode) {
		if (msisdn != null) {
			try {
				String sql = "update sim_terminate_mt set status=?, result_code =?, updated_date_time = now() WHERE msisdn=? and request_type=?";
				entityManager.createNativeQuery(sql).setParameter(1, currstatus).setParameter(2, resultCode)
						.setParameter(3, msisdn).setParameter(4, req_type).executeUpdate();
			} catch (Exception e) {
				_logger.error("TerminateSimMTService.updateTerminateSIMMT() - " + e.getMessage());
			}
		}
	}
}