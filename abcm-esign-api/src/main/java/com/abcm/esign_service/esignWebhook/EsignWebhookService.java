package com.abcm.esign_service.esignWebhook;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.abcm.esign_service.DTO.EsignWebhookResponseDTO;
import com.abcm.esign_service.repo.EsignRepository;
import com.abcm.esign_service.util.UpdateBalance;
import com.abcm.esign_service.util.UrlHelper;
import com.abcmkyc.entity.KycData;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class EsignWebhookService {

    private final EsignRepository esignRepository;
    private final UrlHelper urlHelper;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    private static final String STATUS_SUCCESS = "SUCCESS";
    private static final String STATUS_SIGNED = "SIGNED";
    private static final String STATUS_IN_PROGRESS = "IN-PROGRESS";
    private static final String STATUS_COMPLETED = "COMPLETED";
    private  final UpdateBalance updatebalance;

    @Transactional
    public void updateWebhookResponse(String response) {
  
    	log.info("Provider Webhook Response:{}", response);
        JSONObject json = new JSONObject(response);
        JSONObject result = json.getJSONObject("result");
        JSONObject document = result.getJSONObject("document");
        String documentStatus = document.getString("status");
        String mainRequestId = json.getString("request_id");

        //update balance
        JSONObject metadata = json.optJSONObject("metadata");
       
        
        log.info("Webhook received for requestId: {}, documentStatus: {}", mainRequestId, documentStatus);

        // Collect all request IDs (main + other signers)
        List<String> requestIds = collectAllRequestIds(mainRequestId, result);

        // Single Database Fetch
        List<KycData> kycList = esignRepository.findByRequestIdIn(requestIds);
        if (kycList.isEmpty()) {
            log.warn("No KycData records found for requestIds: {}", requestIds);
            return;
        }

   
        //fetch map request id get kyc Data
        KycData mainKycData = kycList.stream()
                .filter(kyc -> mainRequestId.equals(kyc.getRequestId()))
                .findFirst()  // Should return one record
                .orElseThrow(() -> new RuntimeException("No KycData found for mainRequestId: " + mainRequestId));
        Map<String, KycData> kycMap = kycList.stream()
                .collect(Collectors.toMap(KycData::getRequestId, Function.identity()));
        
        //webhook response get meta data and check y balance deduct
        if (metadata != null) {
            String billable = metadata.optString("billable");
            if ("Y".equalsIgnoreCase(billable)) {
                // Proper logging with placeholder for dynamic values
                log.info("Balance update for Esign product: Product Rate: {} for MerchantId: {}",
                        mainKycData.getProductRate(), mainKycData.getMerchantId());
                // Update wallet balance
                updatebalance.updateWalletBalance(mainKycData.getMerchantId(), mainKycData.getProductRate());
            }
        }

        // Update main signer status
        JSONObject signer = result.getJSONObject("signer");
        updateSignerStatus(kycMap.get(mainRequestId), signer.optString("status"), documentStatus,document,mainRequestId);
        
      
        
        // Update other signers status
        if (result.has("other_signers")) {
            for (Object obj : result.getJSONArray("other_signers")) {
                JSONObject other = (JSONObject) obj;
                updateSignerStatus(kycMap.get(other.getString("request_id")),
                        other.optString("status"), documentStatus,null,null);
            }
        }

        // Mark webhook received for main signer
        KycData mainData = kycMap.get(mainRequestId);
        if (mainData != null) {
            mainData.setWebhookStatus(true);
        }

        esignRepository.saveAll(kycList);
        log.info("Webhook status updated for {} signer(s), orderId: {}",
                kycList.size(), kycList.isEmpty() ? "N/A" : kycList.get(0).getOrderId());

        // Post response to merchant webhook (async — don't block Zoop)
        postResponseToMerchant(json, kycList);
    }

    /**
     * Builds the merchant webhook response DTO and POSTs it to the merchant's
     * webhook URL.
     * Runs asynchronously so we don't block Zoop's webhook response.
     */
    public void postResponseToMerchant(JSONObject response, List<KycData> signersList) {

        if (signersList == null || signersList.isEmpty()) {
            log.warn("signersList is empty, skipping merchant webhook POST");
            return;
        }

        // Get merchant webhook URL from KycData
        String merchantWebhookUrl = signersList.get(0).getMerchantWebhookUrl();
        if (merchantWebhookUrl == null || merchantWebhookUrl.isBlank()) {
            log.info("No merchant webhook URL configured, skipping POST for orderId: {}",
                    signersList.get(0).getOrderId());
            return;
        }

        CompletableFuture.runAsync(() -> {
            try {
                EsignWebhookResponseDTO responseDTO = buildWebhookResponseDTO(response, signersList);
                String jsonBody = objectMapper.writeValueAsString(responseDTO);

                log.info("Posting webhook to merchant URL: {}, orderId: {}",
                        merchantWebhookUrl, responseDTO.getOrderId());

                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                HttpEntity<String> entity = new HttpEntity<>(jsonBody, headers);

                String merchantResponse = restTemplate.postForObject(
                        merchantWebhookUrl, entity, String.class);

                log.info("Merchant webhook response for orderId: {} => {}", responseDTO.getOrderId(), merchantResponse);

                // Update merchant webhook status in DB
                updateMerchantWebhookStatus(signersList, true);

            } catch (Exception e) {
                log.error("Merchant webhook POST failed for orderId: {}: {}",
                        signersList.get(0).getOrderId(), e.getMessage(), e);
                updateMerchantWebhookStatus(signersList, false);
            }
        });
    }

    // ──────────────────── Private Helper Methods ────────────────────

    /**
     * Collects the main requestId + all other signer requestIds into a single list.
     */
    private List<String> collectAllRequestIds(String mainRequestId, JSONObject result) {
        List<String> requestIds = new ArrayList<>();
        requestIds.add(mainRequestId);
        if (result.has("other_signers")) {
            for (Object obj : result.getJSONArray("other_signers")) {
                JSONObject other = (JSONObject) obj;
                requestIds.add(other.getString("request_id"));
            }
        }
        return requestIds;
    }

    /**
     * Updates a single signer's status. Only upgrades — never downgrades from
     * SUCCESS.
     * @param mainRequestId 
     * @param document 
     */
    private void updateSignerStatus(KycData kycData, String newSignerStatus, String documentStatus, JSONObject document, String mainRequestId) {
        if (kycData == null) {
            return;
        }
        // Only upgrade status if not already SUCCESS
        if (STATUS_SUCCESS.equalsIgnoreCase(kycData.getSignerStatus())) {
            return;
        }
        kycData.setSignerStatus(newSignerStatus);
        kycData.setStatus(STATUS_SUCCESS.equalsIgnoreCase(newSignerStatus)
                ? STATUS_SIGNED
                : documentStatus);
        kycData.setSignerSdkUrl(document.getString("signed_url"));
        ZonedDateTime utcDateTime = ZonedDateTime.parse(document.getString("signed_at"), DateTimeFormatter.ISO_DATE_TIME);
        ZonedDateTime indiaDateTime = utcDateTime.withZoneSameInstant(ZoneId.of("Asia/Kolkata"));
        LocalDateTime signerDate = indiaDateTime.toLocalDateTime();
        kycData.setSignedAt(signerDate);
        kycData.setSignerUrl(urlHelper.getDocumentUrl(mainRequestId));
    }

    /**
     * Builds the EsignWebhookResponseDTO from Zoop webhook response + saved
     * KycData.
     */
    private EsignWebhookResponseDTO buildWebhookResponseDTO(JSONObject response, List<KycData> signersList) {

        EsignWebhookResponseDTO responseDTO = new EsignWebhookResponseDTO();

        String mainRequestId = response.getString("request_id");
        JSONObject result = response.getJSONObject("result");
        JSONObject signer = result.getJSONObject("signer");

        Map<String, KycData> signerMap = signersList.stream()
                .collect(Collectors.toMap(KycData::getRequestId, Function.identity()));

        // Set top-level fields
        KycData firstSigner = signersList.get(0);
        responseDTO.setMerchantId(firstSigner.getMerchantId());
        responseDTO.setOrderId(firstSigner.getOrderId());
        responseDTO.setSuccess(response.optBoolean("success"));
        responseDTO.setResponseCode(response.optString("response_code", "200"));
        responseDTO.setResponseMessage(response.optString("response_message", ""));
        responseDTO.setBillable(response.has("metadata")
                ? response.getJSONObject("metadata").optString("billable", "N")
                : "N");

        // Build main signer DTO
        EsignWebhookResponseDTO.SignerDTO mainSignerDTO = new EsignWebhookResponseDTO.SignerDTO();
        mainSignerDTO.setRequestId(mainRequestId);
        mainSignerDTO.setGivenName(signer.optString("given_name"));
        mainSignerDTO.setFetchedName(signer.optString("fetched_name"));
        mainSignerDTO.setNameMatchScore(signer.optDouble("name_match_score", 0.0));
        mainSignerDTO.setStatus(signer.optString("status"));
        mainSignerDTO.setSignedUrl(urlHelper.getDocumentUrl(mainRequestId));

        // Set trackId from our DB record
        KycData mainKycData = signerMap.get(mainRequestId);
        if (mainKycData != null) {
            mainSignerDTO.setTrackId(mainKycData.getTrackId());
            mainSignerDTO.setEmail(mainKycData.getSignerEmail());
        }
        responseDTO.setSigner(mainSignerDTO);

        // Build other signers DTO list + determine overall signing status
        List<EsignWebhookResponseDTO.SignerDTO> otherSignerDTOs = new ArrayList<>();
        boolean allSigned = STATUS_SUCCESS.equalsIgnoreCase(signer.optString("status"));

        for (KycData data : signersList) {
            if (mainRequestId.equals(data.getRequestId())) {
                continue;
            }
            EsignWebhookResponseDTO.SignerDTO otherDto = new EsignWebhookResponseDTO.SignerDTO();
            otherDto.setRequestId(data.getRequestId());
            otherDto.setStatus(data.getSignerStatus());
            otherDto.setFetchedName(data.getFetchName());
            otherDto.setTrackId(data.getTrackId());
            otherDto.setEmail(data.getSignerEmail());
            otherDto.setSignedUrl(urlHelper.getDocumentUrl(data.getRequestId()));
            otherSignerDTOs.add(otherDto);

            if (!STATUS_SUCCESS.equalsIgnoreCase(data.getSignerStatus())) {
                allSigned = false;
            }
        }

        responseDTO.setOtherSigner(otherSignerDTOs);
        responseDTO.setSingingStatus(allSigned ? STATUS_COMPLETED : STATUS_IN_PROGRESS);

        return responseDTO;
    }

    /**
     * Updates webhookStatusMerchant flag for all signers in the order.
     */
    private void updateMerchantWebhookStatus(List<KycData> signersList, boolean success) {
        try {
            for (KycData data : signersList) {
                data.setWebhookStatusMerchant(success);
            }
            esignRepository.saveAll(signersList);
            log.info("Merchant webhook status updated to {} for orderId: {}",
                    success, signersList.get(0).getOrderId());
        } catch (Exception e) {
            log.error("Failed to update merchant webhook status: {}", e.getMessage());
        }
    }
}
