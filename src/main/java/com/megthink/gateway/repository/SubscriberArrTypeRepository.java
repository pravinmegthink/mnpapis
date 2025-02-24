package com.megthink.gateway.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.megthink.gateway.model.SubscriberArrType;

@Repository("subscriberArrTypeRepository")
public interface SubscriberArrTypeRepository extends JpaRepository<SubscriberArrType, Integer> {

	List<SubscriberArrType> findAllByPortIdAndResultCode(Integer portId, Integer resultCode);

}
