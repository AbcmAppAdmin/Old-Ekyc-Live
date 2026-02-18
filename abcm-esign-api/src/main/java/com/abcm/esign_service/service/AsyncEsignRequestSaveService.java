package com.abcm.esign_service.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.abcm.esign_service.DTO.EsignMerchantRequest;
import com.abcm.esign_service.dyanamicRequestBody.ZoopEsignAdhaarRequest;
import com.abcm.esign_service.exception.CustomException;
import com.abcm.esign_service.repo.EsignRepository;
import com.abcmkyc.entity.KycData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class AsyncEsignRequestSaveService {

	private final EsignRepository repository;

	private final Environment environment;
	
	@Transactional
	public KycData saveEsignAsync(EsignMerchantRequest request,
	                              ZoopEsignAdhaarRequest zoopEsignAdhaarRequest,
	                              String providerName,
	                              String productName,
	                              String merchantName) {

	    log.info("Saved Request Esign Verification {}", request);
	    
	    if (repository.existsByMerchantIdAndOrderId(
	            request.getMerchant_id(),
	            request.getOrder_Id())) {
	        throw new CustomException(
	                environment.getProperty("custom.messages.order-id-duplicate"),
	                Integer.parseInt(environment.getProperty("custom.codes.order-id-missing")));
	    }
	    LocalDateTime currentTime = LocalDateTime.now();
	    KycData lastSaved = null;
	    for (EsignMerchantRequest.Signer signer : request.getSigners()) {
	        KycData kycData = KycData.builder()
	                .merchantId(request.getMerchant_id())
	                .merchantName(merchantName)
	                .consent(request.getConsent())
	                .merchantRequestAt(currentTime)
	                .clientProviderName(providerName)
	                .merchantRequest(request.toString())
	                .providerRequest(zoopEsignAdhaarRequest.toString())
	                .product(productName)
	                .created_at(currentTime)
	                .trackId(generateKycTransactionId())
	                .orderId(request.getOrder_Id())
	                .customerName(signer.getSigner_name())
	                .signerEmail(signer.getSigner_email())
	                .signerPurpose(signer.getSigner_purpose())
	                .signerDocumentName(request.getDocument_name())
	                .signerEmailNotification(signer.getEmail_notification())
	                .build();
	        lastSaved = repository.save(kycData);
	    }
	    return lastSaved;
	}



	/**
	 * Generates a unique KYC transaction ID based on the current timestamp.
	 */
	public String generateKycTransactionId() {
		String timestamp = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS").format(LocalDateTime.now());
		return "KYC" + timestamp;
	}
}
