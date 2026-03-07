package com.abcm.dl_service.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.abcm.dl_service.dto.DlVerifyRequest;
import com.abcm.dl_service.exception.CustomException;
import com.abcm.dl_service.repository.DlVerificationRepository;
import com.abcmkyc.entity.KycData;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor

public class AsyncPanRequestSaveService {

    
    private final DlVerificationRepository dlVerificationRepository;
    
    private final Environment environment;

    /**
     * Saves PAN verification data asynchronously.
     * @param MerchantName 
     * @return Saved KycData entity.
     */
    @Async
    @Transactional
    public KycData saveDlAsync(DlVerifyRequest request, Map<String, Object> panProviderRequest, String providerName, String productName, String MerchantName) {
        log.info("Saving PAN data asynchronously in thread: {}", Thread.currentThread().getName());
        /*
        if (dlVerificationRepository.existsByMerchantIdAndOrderId(request.getMerchantId(),request.getOrderId())) {
        	 throw new CustomException(
                     environment.getProperty("custom.messages.order-id-duplicate"),
                     Integer.parseInt(environment.getProperty("custom.codes.order-id-missing"))
                 );
        }
        */
        LocalDateTime currentTime = LocalDateTime.now();
        KycData kycData = KycData.builder()
                .merchantId(request.getMerchantId())
                .identificationNo(request.getCustomer_dl_number())
                .consent(request.getConsent())
                .merchantRequestAt(currentTime)
                .clientProviderName(providerName)
                .merchantRequest(request.toString())
                .providerRequest(panProviderRequest.toString())
                .product(productName)
                .merchantName(MerchantName)
                .createdAt(currentTime)
                .trackId(generateKycTransactionId())
              //  .orderId(request.getOrderId())              
                .build();
        return dlVerificationRepository.save(kycData);
    }

    /**
     * Masks the PAN number by replacing the last 5 digits with asterisks.
     */
	/*
	 * public static String maskPan(String panNumber) { return (panNumber != null &&
	 * panNumber.length() == 10) ? panNumber.substring(0, 5) + "*****" : panNumber;
	 * }
	 */

    /**
     * Generates a unique KYC transaction ID based on the current timestamp.
     */
    public String generateKycTransactionId() {
        String timestamp = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS")
                                            .format(LocalDateTime.now());
        return "KYC" + timestamp;
    }
}
