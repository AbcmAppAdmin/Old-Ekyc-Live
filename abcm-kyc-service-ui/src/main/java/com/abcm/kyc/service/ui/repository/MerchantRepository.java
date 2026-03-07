package com.abcm.kyc.service.ui.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.abcm.kyc.service.ui.dto.MerchantIdMidNameProjection;
import com.abcmkyc.entity.Merchant_Master;




@Repository
public interface MerchantRepository extends JpaRepository<Merchant_Master, Long> {


	@Query("SELECT m FROM Merchant_Master m WHERE m.mid = :merchantId")
	Merchant_Master getMerchantByMid(@Param("merchantId") String merchantId);





	// This method returns an Optional to handle cases where the username might not exist
	Optional<Merchant_Master> findByCredentialsUsername(String username);

	@Query("SELECT MAX(m.id) FROM Merchant_Master m")
	Integer findMaxMid();  // Return Integer to allow null values


	
	
	@Query(value = "CALL GetKycDataCount(:merchantId, :startDate, :endDate)", nativeQuery = true)
	int getKycDataCount(
			@Param("merchantId") String merchantId,
			@Param("startDate") java.sql.Date startDate,
			@Param("endDate") java.sql.Date endDate
			);

	

	

	@Query(value = "CALL GetKycDataMonthwise(:merchantId, :year)", nativeQuery = true)
	List<Object[]> getKycDataMonthwise(
			@Param("merchantId") String merchantId,
			@Param("year") int year
			);

	List<Merchant_Master> findAllByOrderByCredentialsCreatedAtDesc();
	
	
	 @Query(value = "CALL getSubscribedServicesCount(?1)", nativeQuery = true)
	    int GetServiceCount(String merchantId);
	 
	 
	 
	 @Query(value = "SELECT id, mid, name FROM abcm_kyc_ms_db.merchant_master", nativeQuery = true)
	    List<MerchantIdMidNameProjection> findAllMerchantsNative();





	Optional<Merchant_Master> findByMid(String mid);
	
	
	@Query(
	        value = "SELECT mm.mid AS merchantId, mm.name AS merchantName " +
	                "FROM abcm_kyc_ms_db.merchant_master mm " +
	                "INNER JOIN abcm_kyc_ms_db.merchant_product_route mp ON mm.mid = mp.mid " +
	                "WHERE mp.product_name = 'E-SIGN'",
	        nativeQuery = true
	    )
	    List<Object[]> fetchEsignMerchants();
	    
	    public interface MerchantApiCredentialsProjection {
	        String getApiKey();
	        String getAppId();
	    }

	    @Query("SELECT m.apiKey AS apiKey, m.appId AS appId FROM Merchant_Master m WHERE m.mid = :mid")
	    MerchantApiCredentialsProjection findApiCredentialsByMid(@Param("mid") String mid);
	
	
	


}
