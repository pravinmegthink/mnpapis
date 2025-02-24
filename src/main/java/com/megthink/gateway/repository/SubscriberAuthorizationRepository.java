package com.megthink.gateway.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.megthink.gateway.model.SubscriberAuthorization;

@Repository("subscriberAuthorizationRepository")
public interface SubscriberAuthorizationRepository extends JpaRepository<SubscriberAuthorization, Integer> {

}
