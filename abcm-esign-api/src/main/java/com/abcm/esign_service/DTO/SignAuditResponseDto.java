package com.abcm.esign_service.DTO;

import java.time.LocalDateTime;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Builder
@ToString
public class SignAuditResponseDto {

	    private String merchantId;
	    private String merchantName;
	    private String documentName;
	    private String orderId;
	    private String trackId;
	    private LocalDateTime requestAt;
	    private LocalDateTime signedAt;
	    private String status;
	    private String signerStatus;
	    private String customerName;
	    private String customerEmail;
}
