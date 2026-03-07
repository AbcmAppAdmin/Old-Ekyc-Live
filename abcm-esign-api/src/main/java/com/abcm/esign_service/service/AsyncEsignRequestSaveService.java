package com.abcm.esign_service.service;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.abcm.esign_service.DTO.EsignMerchantRequest;
import com.abcm.esign_service.dyanamicRequestBody.ZoopEsignAdhaarRequest;
import com.abcm.esign_service.exception.CustomException;
import com.abcm.esign_service.repo.EsignRepository;
import com.abcmkyc.entity.KycData;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class AsyncEsignRequestSaveService {

	private final EsignRepository repository;

	private final Environment environment;
	
	@Value("${signerDocumentPath}")
	private String signerDocumentPath;
	
	@Value("${SendDocumentName}")
	private String SendDocumentName;
	
	
	
	@Transactional
	public KycData saveEsignAsync(EsignMerchantRequest request,
	                              ZoopEsignAdhaarRequest zoopEsignAdhaarRequest,
	                              String providerName,
	                              String productName,
	                              String merchantName, MultipartFile file,String allowDwonload, HttpServletRequest httpServletRequest, String signersJson) {

	    log.info("Saved Request Esign Verification {}, allow download: {}", request, allowDwonload);
	    if (repository.existsByMerchantIdAndOrderId(
	            request.getMerchant_id(),
	            request.getOrder_Id())) {
	        throw new CustomException(
	                environment.getProperty("custom.messages.order-id-duplicate"),
	                Integer.parseInt(environment.getProperty("custom.codes.order-id-missing")));
	    }
	    LocalDateTime currentTime = LocalDateTime.now();
	    Path filePath = null;   
	    String documentPath = null; 
	    String ipAddress = httpServletRequest.getHeader("X-Forwarded-For");
	 if (ipAddress == null || ipAddress.isEmpty()) {
	     ipAddress = httpServletRequest.getRemoteAddr();
	 } else {
	     ipAddress = ipAddress.split(",")[0].trim();
	 }
	    log.info("IP Address of the requestor: {}", ipAddress);
	    if ("Y".equalsIgnoreCase(allowDwonload) && file != null && !file.isEmpty()) {
            log.info("download allow for this merchant:{}", merchantName);
	        String baseFolder = signerDocumentPath+SendDocumentName;
	        String merchantFolder = baseFolder + java.io.File.separator + request.getMerchant_id();
	        String orderFolder = merchantFolder + java.io.File.separator + request.getOrder_Id();

	        createFolderIfNotExists(orderFolder);

	        String fileName = file.getOriginalFilename();
	        filePath = Paths.get(orderFolder, fileName);

	        try (FileOutputStream fos = new FileOutputStream(filePath.toFile())) {
	            fos.write(file.getBytes());
	            documentPath = filePath.toString(); // only set if saved
	            log.info("File saved for order {}: {}", request.getOrder_Id(), documentPath);
	        } catch (IOException e) {
	            log.error("Failed to save file: {}", e.getMessage(), e);
	        }
	    }
	    
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
	                .createdAt(currentTime)
	                .trackId(generateKycTransactionId())
	                .orderId(request.getOrder_Id())
	                .customerName(signer.getSigner_name())
	                .signerEmail(signer.getSigner_email())
	                .signerPurpose(signer.getSigner_purpose())
	                .signerDocumentName(request.getDocument_name())
	                .signerEmailNotification(signer.getEmail_notification())
	                .merchantWebhookUrl(request.getWebhook_url())
                    .documentPath(documentPath != null ? documentPath : null)
                    .allowDocument(allowDwonload)
                    .ipAddress(ipAddress)
                    .whiteLabel(zoopEsignAdhaarRequest.getWhite_label())                    
	                .build();
	        lastSaved = repository.save(kycData);
	    }
	    return lastSaved;
	}

	 private void createFolderIfNotExists(String folderPath) {
	        Path path = Paths.get(folderPath);
	        try {
	            if (!Files.exists(path)) {
	                Files.createDirectories(path);
	                log.info("Order folder created: {}", folderPath);
	            }
	        } catch (IOException e) {
	            log.error("Failed to create folder: {}", folderPath, e);
	        }
	    }

	
	public String generateKycTransactionId() {
		String timestamp = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS").format(LocalDateTime.now());
		return "KYC" + timestamp;
	}
}
