package com.abcmkyc.entity;



import java.time.LocalDateTime;

import com.abcmkyc.util.FieldEncryptor;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "kyc_data")  
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor

public class KycData {



	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;


	@Column(name = "merchant_id", length = 100)
    private String merchantId;
	
	@Column(name = "merchant_name", length = 100)
    private String merchantName; 
	
	@Column(name = "merchant_request_at")
    private LocalDateTime merchantRequestAt;

    @Column(name = "merchant_response_at")
    private LocalDateTime merchantResponseAt;
    
    
    @Column(name = "created_at")
    private LocalDateTime created_at;
    
    
    
    @Column(name = "merchant_request",columnDefinition = "LONGTEXT")
    @Convert(converter = FieldEncryptor.class)
    private String merchantRequest;

    
    
    
    @Column(name = "merchant_response", columnDefinition = "LONGTEXT")
    @Convert(converter = FieldEncryptor.class)
    private String merchantResponse;
    
    
    @Column(name = "otp", length = 6)
    private String otp;
    
    
    @Column(name = "otp_verify", columnDefinition = "BOOLEAN DEFAULT FALSE")
    private boolean otpVerify;
    
    
    
    @Column(name = "identification_no", length = 200)
    @Convert(converter = FieldEncryptor.class)
    private String identificationNo;
    
    
	
    
	@Column(name = "group_id", length = 50)
	private String groupId;


	@Column(name = "task_id", length = 50)
	private String taskId;


	@Column(name = "request_id", length = 100)
	private String requestId;


	@Column(name = "product", length = 50)
	private String product;

	@Column(name = "client_provider_name", length = 100)
	private String clientProviderName;

	@Column(name = "consent", length = 100)
	private String consent;


	@Column(name = "reason_message", length = 1000)
	private String reasonMessage;


	@Column(name = "response_message", length = 500)
	private String responseMessage;


	@Column(name = "response_code", length = 50)
	private String responseCode;

	@Column(name = "billable", length = 20)
	private String billable;
	
	
	@Column(name = "response_result", length = 999)
    private String ResponseResult;

	@Column(name = "source", length = 50)
	private String source;
	
	
	@Column(name = "status", length = 50)
	private String status;

	
	
	@Column(name = "track_id", length = 150)
	private String trackId;
	
	@Column(name="provider_request",columnDefinition = "LONGTEXT")
	@Convert(converter = FieldEncryptor.class)
	private String providerRequest;
	
	@Column(name = "provider_response", columnDefinition = "LONGTEXT")
	@Convert(converter = FieldEncryptor.class)
    private String ProviderResponse;
	
	@Column(name="product_rate")
	private Long productRate;
	
	@Column(name="verification_id")
	private String verificationId;
	
	@Column(name="customer_name")
	private String customerName;
	
	@Column(name="legal_name")
	private String legalName;
	
	
	//New Column Added kyc data 
	
	@Column(name="webhook_security_key")
    private String webhookSecurityKey;
    
    
      
    @Column(name="merchant_redirect")
    private String merchantRedirect;
    
    @Column(name="merchant_webhook_url")
    private String merchantWebhookUrl;
    
    @Column(name = "webhook_status_merchant", columnDefinition = "BOOLEAN DEFAULT FALSE")
    private boolean webhookStatusMerchant;

    
    @Column(name="sdk_url")
    private String sdkUrl;
    
    @Column(name="short_url")
    private String shortUrl;
    
    @Column(name = "webhook_status", columnDefinition = "BOOLEAN DEFAULT FALSE")
    private boolean webhookStatus;
    
    @Column(name = "order_id", length = 500)
    private String orderId; 

    
    
    @Column(name = "Adhar_verify_request",columnDefinition = "LONGTEXT")
    @Convert(converter = FieldEncryptor.class)
    private String adharVerifyRequest;

    
    
    
    @Column(name = "Adhar_verify_response",columnDefinition = "LONGTEXT")
    @Convert(converter = FieldEncryptor.class)
    private String adharVerifyresponse;
    
    
    @Column(name = "provider_verify_request",columnDefinition = "LONGTEXT")
    @Convert(converter = FieldEncryptor.class)
    private String providerVerifyRequest;

    
    
    
    @Column(name = "provider_verify_response",columnDefinition = "LONGTEXT")
    @Convert(converter = FieldEncryptor.class)
    private String providerVerifyresponse;
    
    //new Column For Esigned
    
	@Column(name="signer_email")
	private String signerEmail;
	
	
	@Column(name="signer_document_name")
	private String signerDocumentName;
	
	
	@Column(name="fetch_name")
	private String fetchName;
	
	
	@Column(name="signed_document_path")
	private String signedDocumentPath;
	
	
	@Column(name="signer_purpose")
	private String signerPurpose;
	
	
	@Column(name="signer_status")
	private String signerStatus;
	
	
	@Column(name="document_path")
	private String documentPath;
	
	
	@Column(name = "signer_email_notification", length = 50)
	private String signerEmailNotification;
	
	 @Column(name = "signed_at")
	 private LocalDateTime signedAt;
	 
	 
	 @Column(name = "signer_url",columnDefinition = "LONGTEXT")
	   
	    private String signerUrl;
	
	 
	 @Column(name = "signer_sdk_url",columnDefinition = "LONGTEXT")
	   
	  private String signerSdkUrl;
	
	
	
	
	
	
	


}
