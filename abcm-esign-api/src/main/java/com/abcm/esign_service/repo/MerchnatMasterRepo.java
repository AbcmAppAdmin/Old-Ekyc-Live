package com.abcm.esign_service.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.abcm.esign_service.DTO.MerchantIpProjection;
import com.abcmkyc.entity.Merchant_Master;

@Repository
public interface MerchnatMasterRepo extends JpaRepository<Merchant_Master, Long> {

	@Query(value = "SELECT email FROM merchant_master WHERE mid = :mid", nativeQuery = true)
	String findEmailByMid(@Param("mid") String mid);

	@Query(value = """
			SELECT
			    mid as mid,
			    ip_allowed as ipAllowed,
			    status as status
			FROM merchant_master
			WHERE mid = :mid
			""", nativeQuery = true)
	MerchantIpProjection findMerchantByMid(@Param("mid") String mid);
}
