package com.megthink.gateway.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.megthink.gateway.model.PortMe;

@Repository("portMeRepository")
public interface PortMeRepository extends JpaRepository<PortMe, Integer> {

	@Query(nativeQuery = true, value = "call package_master$check_msisdns(:i_msisdn, null, null, null, null, null)")
	public String validateMSISDN(@Param("i_msisdn") String i_msisdn);
	
	@Query(nativeQuery = true, value = "select getsynrequestid(:op_id)")
	public String getSynReqeustId(@Param("op_id") String op_id);

	@Query(nativeQuery = true, value = "call package_master$check_rno_area(:op_id,:area,null,null)")
	public String validateRNO(@Param("op_id") String op_id, @Param("area") String area);

	@Query(nativeQuery = true, value = "call package_master$check_no_of_days(:i_msisdn, null)")
	public int getNoOfDaysByMSISDN(@Param("i_msisdn") String i_msisdn);

	@Query(nativeQuery = true, value = "call package_master$check_no_of_days(:i_msisdn, null)")
	public int getOrderReversalDays(@Param("i_msisdn") String i_msisdn);

	PortMe findByRequestId(String requestId);

}
