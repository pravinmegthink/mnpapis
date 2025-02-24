package com.megthink.gateway.service;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.megthink.gateway.model.SubscriberAuthorization;
import com.megthink.gateway.repository.SubscriberAuthorizationRepository;

@Service
public class SubscriberAuthorizationService {

	private static final Logger _logger = LoggerFactory.getLogger(SubscriberAuthorizationService.class);

	private final SubscriberAuthorizationRepository subscriberAuthorizationRepository;

	@Autowired
	public SubscriberAuthorizationService(SubscriberAuthorizationRepository subscriberAuthorizationRepository) {
		this.subscriberAuthorizationRepository = subscriberAuthorizationRepository;
	}

	public int saveMt(int portId, List<SubscriberAuthorization> list, String requestType, int status,
			String activationDateTime, String disconnectionDateTime) {
		List<SubscriberAuthorization> authList = new ArrayList<SubscriberAuthorization>();
		int resetCnt = 0;
		int totalRecord = 0;
		try {
			for (int i = 0; i < list.size(); i++) {
				try {
					SubscriberAuthorization subAuth = new SubscriberAuthorization();
					subAuth.setSubscriberNumber(list.get(i).getSubscriberNumber());
					subAuth.setOwnerId(list.get(i).getOwnerId());
					subAuth.setTypeOfId(list.get(i).getTypeOfId());
					subAuth.setPortId(portId);
					subAuth.setRequest_type(requestType);
					subAuth.setStatus(status);
					subAuth.setActivationDateTime(activationDateTime);
					subAuth.setDisconnectionDateTime(disconnectionDateTime);
					resetCnt++;
					totalRecord++;
					authList.add(subAuth);
					if (resetCnt == 500) {
						subscriberAuthorizationRepository.saveAll(authList);
						subscriberAuthorizationRepository.flush();
						authList.clear();
						resetCnt = 0;
					}
				} catch (Exception e) {
					_logger.error("SubscriberAuthorizationService.saveMt()-Exception occurs while save portmt details "
							+ e.getMessage());
				}
			}

			if (resetCnt < 500) {
				subscriberAuthorizationRepository.saveAll(authList);
				authList.clear();
			}
		} catch (Exception e) {
			_logger.error("SubscriberAuthorizationService.saveMt()-Exception occurs while save portmt details "
					+ e.getMessage());
		}
		return totalRecord;
	}
}