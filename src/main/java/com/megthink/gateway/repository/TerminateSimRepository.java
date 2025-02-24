package com.megthink.gateway.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.megthink.gateway.model.TerminateSim;

@Repository("terminateSimRepository")
public interface TerminateSimRepository extends JpaRepository<TerminateSim, Integer> {

	@Query(nativeQuery = true, value = "call package_master$check_rno_area(:op_id,:area,null,null)")
	public String validateRNO(@Param("op_id") String op_id, @Param("area") String area);

	public TerminateSim findByRequestId(String requestId);
}
