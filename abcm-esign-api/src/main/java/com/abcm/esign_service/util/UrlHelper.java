package com.abcm.esign_service.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class UrlHelper {

  
    @Value("${ContextPath}")
    private String domain;

    public String generateLongUrl(String requestId) {
        if (requestId == null || requestId.isBlank()) {
            throw new IllegalArgumentException("request_id cannot be null, empty, or blank");
        }
        return domain+"/api/v1/e-sign/"+ requestId;
    }


    public String getDocumentUrl(String requestId) {
        if (requestId == null || requestId.isBlank()) {
            throw new IllegalArgumentException("request_id cannot be null, empty, or blank");
        }
        return domain+"/view/"+ requestId;
    }
}
