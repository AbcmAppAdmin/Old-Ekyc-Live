package com.abcm.kyc.service.ui.Esign;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EsignMerchantRequest {

    private String merchant_id;
    private String consent;
    private String document_name;
    private String Order_Id;
    private String webhook_url;
    private String link_expiry_min;
    private List<Signer> signers;               
   

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Signer {
        private String signer_name;
        private String signer_email;
        private String email_notification;
        private String signer_purpose;
        private List<SignCoordinate> sign_coordinates;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SignCoordinate {
        private int page_num;
        private int x_coord;
        private int y_coord;
    }
}
