package com.abcm_kyc_reporting_service.dto;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class KycCountResultDto {

    private String merchantId;
    private String merchantName;
    private int okycCount;
    private int gstinCount;
    private int panCount;
    private int driving_license;
    private int voter_id;
    private int esign;
    private int totalCount;

    public KycCountResultDto(String merchantId, String merchantName, int okycCount, int gstinCount, int panCount,
                             int driving_license, int voter_id,int esign, int totalCount) {
        this.merchantId = merchantId;
        this.merchantName = merchantName;
        this.okycCount = okycCount;
        this.gstinCount = gstinCount;
        this.panCount = panCount;
        this.driving_license = driving_license;
        this.voter_id = voter_id;
        this.esign=esign;
        this.totalCount = totalCount;
    }
}
