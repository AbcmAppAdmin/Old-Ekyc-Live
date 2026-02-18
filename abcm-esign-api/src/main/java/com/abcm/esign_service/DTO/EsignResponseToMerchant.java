package com.abcm.esign_service.DTO;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder(toBuilder = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EsignResponseToMerchant {

    @JsonProperty("response_code")
    public String responseCode;

    @JsonProperty("response_message")
    public String responseMessage;

    @JsonProperty("merchant_id")
    public String merchantId;

    @JsonProperty("billable")
    public String billable;

    @JsonProperty("success")
    public Boolean success;

    @JsonProperty("order_id")
    public String orderId;
    
    @JsonProperty("response_time")
    public String responseTime;

    
    @JsonProperty("signer_requests")
    public List<SignerRequest> signerRequests;

    @Data
    @Builder(toBuilder = true)
    public static class SignerRequest {
        
        @JsonProperty("request_id")
        public String requestId;

        @JsonProperty("track_id")
        public String trackId;

        @JsonProperty("signer_name")
        public String signerName;
        
        @JsonProperty("email_notification")
        public String emailNotification;

        @JsonProperty("signer_email")
        public String signerEmail;

        @JsonProperty("signing_url")
        public String signingUrl;
    }
}
