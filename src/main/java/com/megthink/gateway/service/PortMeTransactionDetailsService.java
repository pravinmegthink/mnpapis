package com.megthink.gateway.service;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.megthink.gateway.model.PortMeDetails;
import com.megthink.gateway.model.PortMeTransactionDetails;
import com.megthink.gateway.model.SubscriberArrType;
import com.megthink.gateway.model.SubscriberAuthorization;
import com.megthink.gateway.model.SubscriberResult;
import com.megthink.gateway.model.SubscriberResultType;
import com.megthink.gateway.repository.PortMeTransactionDetailsRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;

@Service("portMeTransactionDetailsService")
public class PortMeTransactionDetailsService {
	private static final Logger _logger = LoggerFactory.getLogger(PortMeTransactionDetailsService.class);
	@PersistenceContext
	private EntityManager entityManager;
	private PortMeTransactionDetailsRepository portMeTransactionRepository;

	@Autowired
	public PortMeTransactionDetailsService(PortMeTransactionDetailsRepository portMeTransactionRepository) {
		this.portMeTransactionRepository = portMeTransactionRepository;
	}

	@Transactional
	public PortMeTransactionDetails savePortMeTransactionDetails(PortMeTransactionDetails portMeTransaction) {
		PortMeTransactionDetails details = null;
		try {
			if (portMeTransaction != null) {
				details = portMeTransactionRepository.save(portMeTransaction);
			}
		} catch (Exception e) {
			_logger.error(
					"PortMeTransactionDetailsService.savePortMeTransactionDetails()-Exception occurs while save port me transaction details "
							+ e.getMessage());
		}
		return details;
	}

	public int savePortMeTransactionDetails(PortMeTransactionDetails transactionDetails, List<SubscriberArrType> list) {
		List<PortMeTransactionDetails> transactionList = new ArrayList<>();
		int totalRecord = 0;

		try {
			for (int i = 0; i < list.size(); i++) {
				if (list.get(i).getMsisdn() != null && transactionDetails.getRequestId() != null) {
					PortMeTransactionDetails txdetails = new PortMeTransactionDetails();
					txdetails.setRequestId(transactionDetails.getRequestId());
					txdetails.setStatus(transactionDetails.getStatus());
					txdetails.setRequestType(transactionDetails.getRequestType());
					txdetails.setMsisdn(list.get(i).getMsisdn());

					totalRecord++;
					transactionList.add(txdetails);
				}
			}

			if (transactionList.size() > 0) {
				portMeTransactionRepository.saveAll(transactionList);
			}
		} catch (Exception e) {
			_logger.error(
					"PortMeTransactionDetailsService.savePortMeTransactionDetails() - Exception occurs while saving port me transaction details: "
							+ e.getMessage(),
					e);
		}

		return totalRecord;
	}

//	public <T extends PortMeTransactionDetails> Collection<T> bulkSave(Collection<T> entities) {
//		final List<T> savedEntities = new ArrayList<T>(entities.size());
//		int i = 0;
//		for (T t : entities) {
//			savedEntities.add(persistOrMerge(t));
//			i++;
//			if (i % entities.size() == 0) {
//				// Flush a batch of inserts and release memory.
//				entityManager.flush();
//				entityManager.clear();
//			}
//		}
//		return savedEntities;
//	}

//	@Transactional
//	private <T extends PortMeTransactionDetails> T persistOrMerge(T t) {
//		if (t == null) {
//	        throw new IllegalArgumentException("Cannot persist or merge a null object.");
//	    }
//		if (t.getId() == null) {
//			entityManager.persist(t);
//			return t;
//		} else {
//			return entityManager.merge(t);
//		}
//	}

	// private void saveTransactionList(List<PortMeTransactionDetails>
	// transactionList) {
	// try {
	// portMeTransactionRepository.saveAll(transactionList);
	// portMeTransactionRepository.flush();
	// } catch (Exception e) {
	// _logger.error("Error while saving transaction list: " + e.getMessage(), e);
	// }
	// }

	public int savePortMeTransactionDetail(PortMeTransactionDetails transactionDetails,
			List<SubscriberResult> list) {
		List<PortMeTransactionDetails> transactionList = new ArrayList<PortMeTransactionDetails>();
		int resetCnt = 0;
		int totalRecord = 0;
		try {
			for (int i = 0; i < list.size(); i++) {
				try {
					if (list.get(i).getSubscriberNumber() != null && transactionDetails.getRequestId() != null) {
						PortMeTransactionDetails txdetails = new PortMeTransactionDetails();
						txdetails.setRequestId(transactionDetails.getRequestId());
						txdetails.setStatus(transactionDetails.getStatus());
						txdetails.setRequestType(transactionDetails.getRequestType());
						txdetails.setMsisdn(list.get(i).getSubscriberNumber());
						resetCnt++;
						totalRecord++;
						transactionList.add(txdetails);
					}
				} catch (Exception e) {
					_logger.error(
							"PortMeTransactionDetailsService.savePortMeTransactionDetail()-Exception occurs while save port me transaction details "
									+ e.getMessage());
				}
			}

			if (transactionList.size() > 0) {
//				bulkSave(transactionList);
				portMeTransactionRepository.saveAll(transactionList);
			}
		} catch (Exception e) {
			_logger.error(
					"PortMeTransactionDetailsService.savePortMeTransactionDetail()-Exception occurs while save port me transaction details "
							+ e.getMessage());
		}
		return totalRecord;
	}

	public int savePortMeTransactionDetailStat(PortMeTransactionDetails transactionDetails,
			List<SubscriberAuthorization> list) {
		List<PortMeTransactionDetails> transactionList = new ArrayList<PortMeTransactionDetails>();
		int resetCnt = 0;
		int totalRecord = 0;
		try {
			for (int i = 0; i < list.size(); i++) {
				try {
					if (list.get(i).getSubscriberNumber() != null && transactionDetails.getRequestId() != null) {
						PortMeTransactionDetails txdetails = new PortMeTransactionDetails();
						txdetails.setRequestId(transactionDetails.getRequestId());
						txdetails.setStatus(transactionDetails.getStatus());
						txdetails.setRequestType(transactionDetails.getRequestType());
						txdetails.setMsisdn(list.get(i).getSubscriberNumber());
						resetCnt++;
						totalRecord++;
						transactionList.add(txdetails);
						// if (resetCnt == 500) {
						// portMeTransactionRepository.saveAll(transactionList);
						// portMeTransactionRepository.flush();
						// transactionList.clear();
						// resetCnt = 0;
						// }
					}
				} catch (Exception e) {
					_logger.error(
							"PortMeTransactionDetailsService.savePortMeTransactionDetailStat()-Exception occurs while save port me transaction details "
									+ e.getMessage());
				}
			}
			if (transactionList.size() > 0) {
//				bulkSave(transactionList);
				portMeTransactionRepository.saveAll(transactionList);
			}
		} catch (Exception e) {
			_logger.error(
					"PortMeTransactionDetailsService.savePortMeTransactionDetail()-Exception occurs while save port me transaction details "
							+ e.getMessage());
		}
		return totalRecord;
	}

	@SuppressWarnings("unchecked")
	@Transactional
	public List<PortMeDetails> getListOrderReversalDetails(int days, String msisdn) {
		List<PortMeDetails> list = null;
		try {
			if (msisdn != null) {
				String sql = "SELECT id AS port_id, request_id, area, dno, msisdn, service, NULL AS status, NULL AS created_date_time, NULL AS updated_date_time, imsi, hlr, sim FROM port_history WHERE msisdn = ? AND (Current_Date - Date(created_date_time)) <= ?";
				Query query = entityManager.createNativeQuery(sql, PortMeDetails.class).setParameter(1, msisdn).setParameter(2,
						days);
				list = (List<PortMeDetails>) query.getResultList();
			}
		} catch (Exception e) {
			_logger.error("Exception occurs while getting OrderReversalDao.getListOrderReversalDetails() - "
					+ e.getMessage());
		}
		return list;
	}
}