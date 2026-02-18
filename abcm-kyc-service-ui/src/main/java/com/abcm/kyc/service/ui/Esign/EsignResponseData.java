package com.abcm.kyc.service.ui.Esign;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EsignResponseData {

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
    @AllArgsConstructor
    @NoArgsConstructor
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

