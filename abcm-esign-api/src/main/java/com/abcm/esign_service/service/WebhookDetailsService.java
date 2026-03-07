package com.abcm.esign_service.service;

import com.abcm.esign_service.exception.CustomException;
import com.abcm.esign_service.repo.EsignRepository;
import com.abcmkyc.entity.KycData;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class WebhookDetailsService {

    @Autowired
    private EsignRepository esignRepository;

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * Fetches stored merchant webhook response by trackerId (single) or orderId
     * (list).
     * trackerId takes priority if both are provided.
     */
    public Object getWebhookDetails(String trackerId, String orderId) {

        // --- trackerId based lookup (single row) ---
        if (trackerId != null && !trackerId.isBlank()) {
            log.info("Fetching merchant webhook response for trackerId: {}", trackerId);

            KycData data = esignRepository.findByTrackId(trackerId);
            if (data == null) {
                throw new CustomException("No data found for trackerId: " + trackerId, 404);
            }
            if (data.getMerchantResponse() == null || data.getMerchantResponse().isBlank()) {
                throw new CustomException("Webhook response not yet available for trackerId: " + trackerId, 404);
            }
            return parseJsonSafe(data.getMerchantWebhookPayload());
        }

        // --- orderId based lookup (list) ---
        if (orderId != null && !orderId.isBlank()) {
            log.info("Fetching merchant webhook response for orderId: {}", orderId);

            List<KycData> dataList = esignRepository.findByOrderId(orderId);
            if (dataList == null || dataList.isEmpty()) {
                throw new CustomException("No data found for orderId: " + orderId, 404);
            }

            List<Object> responses = dataList.stream()
                    .filter(d -> d.getMerchantWebhookPayload() != null && !d.getMerchantWebhookPayload().isBlank())
                    .map(d -> parseJsonSafe(d.getMerchantWebhookPayload()))
                    .toList();

            if (responses.isEmpty()) {
                throw new CustomException("Webhook response not yet available for orderId: " + orderId, 404);
            }
            return responses;
        }

        // --- neither param provided ---
        throw new CustomException("trackerId or orderId is required", 400);
    }

    /**
     * Parses stored JSON string to Object so it returns as proper JSON, not escaped
     * string.
     */
    private Object parseJsonSafe(String json) {
        try {
            return objectMapper.readValue(json, Object.class);
        } catch (Exception e) {
            log.warn("Failed to parse merchantResponse JSON, returning raw string: {}", e.getMessage());
            return json;
        }
    }
}
