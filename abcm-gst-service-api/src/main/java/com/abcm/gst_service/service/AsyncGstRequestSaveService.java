package com.abcm.gst_service.service;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Map;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.abcm.gst_service.dto.MerchantGstMasterRequest;
import com.abcm.gst_service.exception.CustomException;
import com.abcm.gst_service.repository.GstVerificationRepository;
import com.abcmkyc.entity.KycData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class AsyncGstRequestSaveService {
	private final Environment environment;
	private final GstVerificationRepository gstVerificationRepository;

	@Async
	@Transactional
	public KycData saveGstAsync(MerchantGstMasterRequest request, Map<String, Object> requestBody, String providerName,
			String productName, String merchantName, Long productRate) {
		try {
			/*
			log.debug("Saving GST Master async for merchant: {}, orderId: {}", request.getMerchantId(),
					request.getOrderId());
			if (gstVerificationRepository.existsByMerchantIdAndOrderId(request.getMerchantId(),request.getOrderId())) {
				log.info("Order Id Already exists: {}", request.getOrderId());
				throw new CustomException(environment.getProperty("custom.messages.order-id-duplicate"),
						Integer.parseInt(environment.getProperty("custom.codes.order-id-missing")));
			}
			*/
			LocalDateTime now = LocalDateTime.now();
			KycData kycData = KycData.builder().merchantId(request.getMerchantId()).merchantName(merchantName)
					.merchantRequestAt(now).merchantRequest(request.toString())
					.identificationNo(request.getBusinessGstinNumber()).clientProviderName(providerName)
					.consent(request.getConsent()).product(productName).productRate(productRate)
					.providerRequest(requestBody.toString()).createdAt(now).trackId(generateKycTransactionId())
					//.orderId(request.getOrderId())
					.build();
			return gstVerificationRepository.save(kycData);

		} catch (CustomException ce) {
   // let your custom error propagate to the controller advice
			throw ce;
		} catch (Exception e) {
			log.error("Failed to save GST data for merchant: {}", request.getMerchantId(), e);
			throw new CustomException(environment.getProperty("custom.messages.internal-server"),
					Integer.parseInt(environment.getProperty("custom.codes.internal-server")));
		}
	}

	private String generateKycTransactionId() {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmssSSS");
		return "KYC" + dateFormat.format(new Date());
	}

}
