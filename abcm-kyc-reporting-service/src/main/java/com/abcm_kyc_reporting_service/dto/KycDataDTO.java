package com.abcm_kyc_reporting_service.dto;

import java.time.LocalDateTime;

public class KycDataDTO {

    private String merchantId;
    private String merchantName;
    private String providerName;
    private String product;
    private String billable;
    private String identificationNo;
    private String customerName;
    private String legalName;
    private LocalDateTime createdAt;
    private LocalDateTime merchantRequestAt;
    private LocalDateTime merchantResponseAt;
    private String trackId;
    private String requestId;
    private String responseMessage;
    private String status;
    private String reasonMessage;

    private Boolean webhookStatusMerchant;
    private Boolean webhookStatus;
    private String orderId;
    private String signerStatus;

    public KycDataDTO() {}

    public KycDataDTO(
        String merchantId,
        String merchantName,
        String providerName,
        String product,
        String billable,
        String identificationNo,
        String customerName,
        String legalName,
        LocalDateTime createdAt,
        LocalDateTime merchantRequestAt,
        LocalDateTime merchantResponseAt,
        String trackId,
        String requestId,
        String responseMessage,
        String status,
        String reasonMessage,
        Boolean webhookStatusMerchant,
        Boolean webhookStatus,
        String orderId,
        String signerStatus
    ) {
        this.merchantId = merchantId;
        this.merchantName = merchantName;
        this.providerName = providerName;
        this.product = product;
        this.billable = billable;
        this.identificationNo = identificationNo;
        this.customerName = customerName;
        this.legalName = legalName;
        this.createdAt = createdAt;
        this.merchantRequestAt = merchantRequestAt;
        this.merchantResponseAt = merchantResponseAt;
        this.trackId = trackId;
        this.requestId = requestId;
        this.responseMessage = responseMessage;
        this.status = status;
        this.reasonMessage = reasonMessage;
        this.webhookStatusMerchant = webhookStatusMerchant;
        this.webhookStatus = webhookStatus;
        this.orderId = orderId;
        this.signerStatus = signerStatus; // Setting signerStatus
    }

    // Getters
    public String getMerchantId() { return merchantId; }
    public String getMerchantName() { return merchantName; }
    public String getProviderName() { return providerName; }
    public String getProduct() { return product; }
    public String getBillable() { return billable; }
    public String getIdentificationNo() { return identificationNo; }
    public String getCustomerName() { return customerName; }
    public String getLegalName() { return legalName; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getMerchantRequestAt() { return merchantRequestAt; }
    public LocalDateTime getMerchantResponseAt() { return merchantResponseAt; }
    public String getTrackId() { return trackId; }
    public String getRequestId() { return requestId; }
    public String getResponseMessage() { return responseMessage; }
    public String getStatus() { return status; }
    public String getReasonMessage() { return reasonMessage; }
    public Boolean isWebhookStatusMerchant() { return webhookStatusMerchant; }
    public Boolean isWebhookStatus() { return webhookStatus; }
    public String getOrderId() { return orderId; }
    public String getSignerStatus() { return signerStatus; }

    // Setters
    public void setMerchantId(String merchantId) { this.merchantId = merchantId; }
    public void setMerchantName(String merchantName) { this.merchantName = merchantName; }
    public void setProviderName(String providerName) { this.providerName = providerName; }
    public void setProduct(String product) { this.product = product; }
    public void setBillable(String billable) { this.billable = billable; }
    public void setIdentificationNo(String identificationNo) { this.identificationNo = identificationNo; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
    public void setLegalName(String legalName) { this.legalName = legalName; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public void setMerchantRequestAt(LocalDateTime merchantRequestAt) { this.merchantRequestAt = merchantRequestAt; }
    public void setMerchantResponseAt(LocalDateTime merchantResponseAt) { this.merchantResponseAt = merchantResponseAt; }
    public void setTrackId(String trackId) { this.trackId = trackId; }
    public void setRequestId(String requestId) { this.requestId = requestId; }
    public void setResponseMessage(String responseMessage) { this.responseMessage = responseMessage; }
    public void setStatus(String status) { this.status = status; }
    public void setReasonMessage(String reasonMessage) { this.reasonMessage = reasonMessage; }
    public void setWebhookStatusMerchant(Boolean webhookStatusMerchant) { this.webhookStatusMerchant = webhookStatusMerchant; }
    public void setWebhookStatus(Boolean webhookStatus) { this.webhookStatus = webhookStatus; }
    public void setOrderId(String orderId) { this.orderId = orderId; } 
    public void setSignerStatus(String signerStatus) { this.signerStatus = signerStatus; }

}
