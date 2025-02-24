package com.megthink.gateway.service;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.megthink.gateway.model.MSISDNUIDType;
import com.megthink.gateway.model.TerminateSimTransactionDetails;
import com.megthink.gateway.repository.TerminateSimTransactionDetailsRepository;

@Service("terminateSimTransactionDetailsService")
public class TerminateSimTransactionDetailsService {

	private static final Logger _logger = LoggerFactory.getLogger(PortMeTransactionDetailsService.class);

	private TerminateSimTransactionDetailsRepository terminateTransactionRepository;

	@Autowired
	public TerminateSimTransactionDetailsService(
			TerminateSimTransactionDetailsRepository terminateTransactionRepository) {
		this.terminateTransactionRepository = terminateTransactionRepository;
	}

	public TerminateSimTransactionDetails saveTerminateSimTransactionDetails(
			TerminateSimTransactionDetails transaction) {
		return terminateTransactionRepository.save(transaction);
	}

	public int saveTerminateSimTransactionDetails(TerminateSimTransactionDetails transaction,
			List<MSISDNUIDType> list) {
		List<TerminateSimTransactionDetails> transactionList = new ArrayList<TerminateSimTransactionDetails>();
		int resetCnt = 0;
		int totalRecord = 0;
		try {
			for (int i = 0; i < list.size(); i++) {
				try {
					TerminateSimTransactionDetails txdetails = new TerminateSimTransactionDetails();
					txdetails.setRequestId(transaction.getRequestId());
					txdetails.setStatus(transaction.getStatus());
					txdetails.setRequestType(transaction.getRequestType());
					txdetails.setMsisdn(list.get(i).getMsisdn());
					resetCnt++;
					totalRecord++;
					transactionList.add(txdetails);
					if (resetCnt == 500) {
						terminateTransactionRepository.saveAll(transactionList);
						terminateTransactionRepository.flush();
						transactionList.clear();
						resetCnt = 0;
					}
				} catch (Exception e) {
					_logger.error(
							"PortMeTransactionDetailsService.saveTerminateSimTransactionDetails()-Exception occurs while saving termintesim transaction details "
									+ e.getMessage());
				}
			}

			if (resetCnt < 500) {
				terminateTransactionRepository.saveAll(transactionList);
				transactionList.clear();
			}
		} catch (Exception e) {
			_logger.error(
					"PortMeTransactionDetailsService.saveTerminateSimTransactionDetails()-Exception occurs while saving termintesim transaction details "
							+ e.getMessage());
		}
		return totalRecord;
	}
}