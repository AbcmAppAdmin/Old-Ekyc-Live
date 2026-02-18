package com.abcm.esign_service.apiCall;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import com.abcm.esign_service.DTO.ProductDetailsDto;
import com.abcm.esign_service.dyanamicRequestBody.ZoopEsignAdhaarRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ServiceProviderApiCall {

    private final RestTemplate restTemplate;

    public String providerApiCall(ZoopEsignAdhaarRequest zoopEsignAdhaarRequest, ProductDetailsDto productDetailsDto) {

        log.info("Zoop Esign Provider API Call - AppID: " + productDetailsDto.getProviderAppId() +
                ", AppKey: " + productDetailsDto.getProviderAppkey());

        HttpHeaders httpHeaders = new HttpHeaders();

        httpHeaders.setContentType(MediaType.APPLICATION_JSON);

        setProviderHeaders(httpHeaders, productDetailsDto.getProviderName(), productDetailsDto.getProviderAppId(),
                productDetailsDto.getProviderAppkey());

        HttpEntity<ZoopEsignAdhaarRequest> entity = new HttpEntity<>(zoopEsignAdhaarRequest, httpHeaders);

        try {
            ResponseEntity<String> response = restTemplate.exchange(productDetailsDto.getAadhaarOtpSendUrl().trim(),
                    HttpMethod.POST, entity, String.class);

            return response.getBody();

        } catch (HttpClientErrorException | HttpServerErrorException e) {
            log.error("Error response from API: {}", e.getResponseBodyAsString());
            e.printStackTrace();
            return "Error: " + e.getResponseBodyAsString();

        } catch (RuntimeException e) {
            log.error("Exception occurred: {}", e.getMessage());
            e.printStackTrace();
            return "Exception occurred Runtime: " + e.getMessage();

        } catch (Exception e) {
            log.error("Exception occurred: {}", e.getMessage());
            e.printStackTrace();
            return "Exception occurred Main: " + e.getMessage();
        }
    }

    private void setProviderHeaders(HttpHeaders headers, String provider, String appId, String apiKey) {
        log.info("App id: {}, appKey: {}", appId, apiKey);
        switch (provider.toLowerCase()) {
            case "zoop-esign" -> {
                headers.set("app-id", appId);
                headers.set("api-key", apiKey);
            }
            default -> throw new IllegalArgumentException("Unsupported provider: " + provider);
        }
    }
}
