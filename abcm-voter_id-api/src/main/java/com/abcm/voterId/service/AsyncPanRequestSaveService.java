package com.abcm.voterId.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.abcm.voterId.DTO.VoterIdRequestModel;
import com.abcm.voterId.VoterIdRepo.VoterIdRepository;
import com.abcm.voterId.exception.CustomException;
import com.abcmkyc.entity.KycData;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class AsyncPanRequestSaveService {

	private final VoterIdRepository repository;

	private final Environment environment;

	@Async
	@Transactional
	public KycData savePanAsync(VoterIdRequestModel request, Map<String, Object> panProviderRequest,
			String providerName, String productName, String MerchantName) {
		log.info("Saving VoterId  data asynchronously in thread: {}", Thread.currentThread().getName());
		/*
		if (repository.existsByMerchantIdAndOrderId(request.getMerchantId(),request.getOrderId())) {
			log.info("Order Id Already exists: {}", request.getOrderId());
			throw new CustomException(environment.getProperty("custom.messages.order-id-duplicate"),
					Integer.parseInt(environment.getProperty("custom.codes.order-id-missing")));
		}
		*/
		LocalDateTime currentTime = LocalDateTime.now();
		KycData kycData = KycData.builder()
				.merchantId(request.getMerchantId())
				.identificationNo(request.getEpicNumber())
				.consent(request.getConsent())
				.merchantRequestAt(currentTime)
				.clientProviderName(providerName)
				.merchantRequest(request.toString())
				.providerRequest(panProviderRequest.toString())
				.product(productName).merchantName(MerchantName)
				.createdAt(currentTime)
				.trackId(generateKycTransactionId())
				//.orderId(request.getOrderId())
				.build();
		return repository.save(kycData);
	}

	/**
	 * Masks the PAN number by replacing the last 5 digits with asterisks.
	 */
	public static String maskId(String panNumber) {
		return (panNumber != null && panNumber.length() == 10) ? panNumber.substring(0, 5) + "*****" : panNumber;
	}

	/**
	 * Generates a unique KYC transaction ID based on the current timestamp.
	 */
	public String generateKycTransactionId() {
		String timestamp = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS").format(LocalDateTime.now());
		return "KYC" + timestamp;
	}
}
