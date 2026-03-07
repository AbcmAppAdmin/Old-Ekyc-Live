package com.abcm.addhar_service.service;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Map;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import com.abcm.addhar_service.controller.ZoopInitRequest;
import com.abcm.addhar_service.dto.AadhaarOtpRequest;
import com.abcm.addhar_service.dto.VerifyOtpRequest;
import com.abcm.addhar_service.exception.CustomException;
import com.abcm.addhar_service.repository.AadharRepository;
import com.abcmkyc.entity.KycData;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class AsyncAaddharRequestSaveService {

	private final AadharRepository aadharRepository;

    private final Environment environment;

	@Async
	public KycData saveAadharAsync(
	    AadhaarOtpRequest request,
	    String serviceProvider,
	    String productName,
	    Map<String, Object> requestBody,
	    String merchantName
	) throws JsonProcessingException {
		
		/*
		  if (aadharRepository.existsByMerchantIdAndOrderId(request.getMerchant_id(),
		  request.getOrderId())) {
			  
			  throw new CustomException(
		  environment.getProperty("custom.messages.order-id-duplicate"),
		  Integer.parseInt(environment.getProperty("custom.codes.order-id-missing")) );
		  } 
		  */
		  
	    LocalDateTime now = LocalDateTime.now();
	    KycData kycData = KycData.builder()
	        .identificationNo(request.getCustomer_aadhaar_number())
	        .merchantId(request.getMerchant_id())
	        .consent(request.getConsent())
	        .merchantRequest( new ObjectMapper().writeValueAsString(request))  
	        .merchantRequestAt(now)
	        .merchantResponseAt(now)
	        .product(productName)
	        .clientProviderName(serviceProvider)
	        .createdAt(now)
	        .trackId(generateKycTransactionId())
	        .providerRequest(new ObjectMapper().writeValueAsString(requestBody))
	        .merchantName(merchantName)
	       // .orderId(request.getOrderId())    
	        .build();
	    log.info("Aadhaar OTP Request saved for trackId: {}", kycData.getTrackId());
	    return aadharRepository.save(kycData);
	}











	
	@Async
	public KycData saveVerifyAadharAsync(
			VerifyOtpRequest verifyOtpRequest,
			String providerName,
			String productName,
			Map<String, Object> requestBody,
			String merchantName
			) throws JsonProcessingException {
		
		
		log.info("Saving Aadhaar Verify Request");
		LocalDateTime now = LocalDateTime.now();
		KycData kycData = KycData.builder()
				.merchantId(verifyOtpRequest.getMerchant_id())
				.consent(verifyOtpRequest.getConsent())
				.adharVerifyRequest(new ObjectMapper().writeValueAsString(verifyOtpRequest))
				.merchantRequestAt(now)
				.product(productName)
				.createdAt(now)
				.clientProviderName(providerName)
				.trackId(generateKycTransactionId())
				.merchantName(merchantName)
				.providerVerifyRequest(new ObjectMapper().writeValueAsString(requestBody))
				.build();
		return aadharRepository.save(kycData);
	}
	
	@Async
    public KycData saveAadharDigiAsync(ZoopInitRequest request, String providerName, String productName,
			Map<String, Object> requestBody, String merchantName,Long productrate) {
		log.info("saveAadharDigiAsync Request"+request.getMerchantResponseUrl());
		 if (aadharRepository.existsByMerchantIdAndOrderId(request.getMerchant_id(),
				  request.getOrderId())) {
					  
					  throw new CustomException(
				  environment.getProperty("custom.messages.order-id-duplicate"),
				  Integer.parseInt(environment.getProperty("custom.codes.order-id-missing")) );
				  } 
		LocalDateTime now = LocalDateTime.now();
		KycData kycData = KycData.builder()
				.merchantId(request.getMerchant_id())
				.consent(request.getConsent())
				.merchantRequest(request.toString())
				.merchantRequestAt(now)
				.product(productName)
				.productRate(productrate)
				.createdAt(now)
				.clientProviderName(providerName)
				.trackId(generateKycTransactionId())
				.merchantName(merchantName)
				.merchantWebhookUrl(request.getMerchantResponseUrl()) 
				.merchantRedirect(request.getMerchantRedirectUrl())
				.providerRequest(requestBody.toString())
				.orderId(request.getOrderId())
				.build();
		return aadharRepository.save(kycData);
	}

	/*
	 * private String maskAadhaarNumber(String aadhaarNumber) { return
	 * (aadhaarNumber != null && aadhaarNumber.length() == 12) ? "********" +
	 * aadhaarNumber.substring(8) : aadhaarNumber; }
	 */

	public  String generateKycTransactionId() {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmssSSS");
		Date now = new Date();
		return "KYC"+ dateFormat.format(now);
	}












	











}
