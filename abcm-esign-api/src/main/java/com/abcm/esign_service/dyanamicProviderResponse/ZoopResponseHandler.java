package com.abcm.esign_service.dyanamicProviderResponse;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import com.abcm.esign_service.DTO.EsignResponseToMerchant;
import com.abcm.esign_service.DTO.EsignResponseToMerchant.EsignResponseToMerchantBuilder;
import com.abcm.esign_service.DTO.ResponseModel;
import com.abcm.esign_service.exception.CustomException;
import com.abcm.esign_service.repo.EsignRepository;
import com.abcm.esign_service.util.EsignResponseUpdate;
import com.abcm.esign_service.util.UrlHelper;
import com.abcmkyc.entity.KycData;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class ZoopResponseHandler implements ProviderResponseHandler<ResponseModel> {

	private static final DateTimeFormatter ISO_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
			.withZone(ZoneOffset.UTC);

	private final Environment environment;
	private final EsignResponseUpdate esignResponseUpdate;
	private final EsignRepository repository;
	private final UrlHelper urlHelper;
	

	@Override
	public ResponseModel esignVerifyResponseToMerchant(JSONObject input, String trackId, String merchantId,
	        Long productRate, String orderId, String whiteLabel) {
	    try {
	       log.info("Esign Map The Response start=>: {}");
	        boolean isSuccess = input.optBoolean("success");
	        
	        String timestamp = ISO_FORMATTER.format(Instant.now());
	        EsignResponseToMerchantBuilder responseBuilder = EsignResponseToMerchant
	                .builder()
	                .merchantId(merchantId)
	                .orderId(orderId)
	                .responseTime(timestamp)
	             //   .whiteLabel(whiteLabel)
	                .success(isSuccess);
	        List<KycData> kycList = repository.findByOrderId(orderId);
	        Map<String, String> trackIdToRequestId = new ConcurrentHashMap<>();
	        Map<String, String> trackIdToShortUrl = new ConcurrentHashMap<>();
	        Map<String, String> trackIdToSigningUrl = new ConcurrentHashMap<>();
	        List<EsignResponseToMerchant.SignerRequest> signerRequests = new ArrayList<>();
	        JSONArray signersArray = input.optJSONArray("requests");
	        log.info("Signer JSON Array :{}",signersArray);
	        if (signersArray != null && signersArray.length() > 0) {
	        	
	            for (int i = 0; i < signersArray.length(); i++) {
	                JSONObject s = signersArray.getJSONObject(i);
	                String signerName = s.optString("signer_name");
	                String requestId = s.optString("request_id");
	                String signingUrl = s.optString("signing_url");
	                String shortUrl = requestId != null ? urlHelper.generateLongUrl(requestId) : null;

	      
	                KycData matched = kycList.stream()
	                	    .filter(k -> signerName.trim().equalsIgnoreCase(k.getCustomerName().trim()))
	                	    .findFirst()
	                	    .orElse(null);

	                if (matched != null) {
	                	log.info("customer Name match start: {}");
	                    trackIdToRequestId.put(matched.getTrackId(), requestId);
	                    trackIdToShortUrl.put(matched.getTrackId(), shortUrl);
	                    trackIdToSigningUrl.put(matched.getTrackId(), signingUrl);
	                    EsignResponseToMerchant.SignerRequest signer = EsignResponseToMerchant.SignerRequest
	                            .builder()
	                            .requestId(requestId)
	                            .trackId(matched.getTrackId())
	                            .signerName(signerName)
	                            .signerEmail(matched.getSignerEmail())
	                            .signingUrl(shortUrl) // Can also use actual
	                            .emailNotification(matched.getSignerEmailNotification())
	                             
	                            .build();
	                    signerRequests.add(signer);
	                }
	            }
	        }
	        if (isSuccess) {
	            responseBuilder.responseCode("200");
	            responseBuilder.responseMessage("E-Sign link generated successfully");
	        } else {
	            JSONObject metadata = input.optJSONObject("metadata");
	            String reasonMessage = metadata != null ? metadata.optString("reason_message") : "Failed";
	            log.info("Reason message is: {}", reasonMessage);
	            List<String> signerErrors = Arrays.asList(
	                    "signers[0].signer_name",
	                    "signer_name must be a string",
	                    "signer_name is required"
	            );

	            List<String> documentErrors = Arrays.asList(
	                    "document.data\" is not allowed to be empty",
	                    "document data field is missing"
	            );
	            if (signerErrors.stream().anyMatch(reasonMessage::contains)) {
	                responseBuilder.responseCode(input.optString("response_code", "400"));
	                responseBuilder.responseMessage("Signer name is invalid or missing");
	                responseBuilder.billable("N");
	            } else if (documentErrors.stream().anyMatch(reasonMessage::contains)) {
	                responseBuilder.responseCode(input.optString("response_code", "400"));
	                responseBuilder.responseMessage("Document data is missing or empty");
	                responseBuilder.billable("N");

	            // Fallback for all other errors
	            } else {
	                responseBuilder.responseCode("400");
	                responseBuilder.responseMessage("eSign initiation failed");
	                responseBuilder.billable("N");
	            }
	        }
	        
	        responseBuilder.signerRequests(signerRequests);
	        EsignResponseToMerchant response = responseBuilder.build();
	        log.info("Final Merchant Response: {}", response);
	        esignResponseUpdate.updateVoterIdResponseBatch(kycList, response, input, isSuccess, trackIdToRequestId,
	                productRate, trackIdToShortUrl, trackIdToSigningUrl);
	        if(isSuccess)
	        {
	        esignResponseUpdate.sendEsignSdkUlLink(kycList, trackIdToShortUrl);
	        }
	       
	        return new ResponseModel(isSuccess ? "success" : "failed",
	                isSuccess ? HttpStatus.OK.value() : HttpStatus.BAD_REQUEST.value(), response);

	    } catch (Exception e) {
	        log.error("Exception in mapping Zoop response", e);
	        throw new CustomException(environment.getProperty("custom.messages.internal-server"),
	                Integer.parseInt(environment.getProperty("custom.codes.internal-server")));
	    }
	}

}
