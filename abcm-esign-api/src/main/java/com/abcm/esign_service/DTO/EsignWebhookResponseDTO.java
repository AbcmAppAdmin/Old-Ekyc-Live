package com.abcm.esign_service.DTO;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EsignWebhookResponseDTO {

    @JsonProperty("response_code")
    private String responseCode;

    @JsonProperty("response_message")
    private String responseMessage;

    @JsonProperty("merchant_id")
    private String merchantId;

    private String billable;

    private Boolean success;

    @JsonProperty("signing_status")
    private String singingStatus;

    @JsonProperty("order_id")
    private String orderId;

    private SignerDTO signer;

    @JsonProperty("other_signer")
    private List<SignerDTO> otherSigner;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class SignerDTO {

        @JsonProperty("request_id")
        private String requestId;

        @JsonProperty("fetched_name")
        private String fetchedName;

        @JsonProperty("given_name")
        private String givenName;

        @JsonProperty("name_match_score")
        private double nameMatchScore;

        private String status;

        @JsonProperty("track_id")
        private String trackId;

        @JsonProperty("signed_url")
        private String signedUrl;

        private String email;

    }
}
