package com.abcm.esign_service.esignReport;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Builder
@ToString
public class EsignReportResponseDTO {
	 private String merchantId;
	    private String merchantName;
	    private String provider;
	    private String orderId;
	    private String trackId;
	    private LocalDateTime requestAt;
	    private LocalDateTime signedAt;
	    private String documentPath;
	    private String signerDocumentPath;
	    private String status;
	    private String signerStatus;
	    private String customerName;
	    private String billable;
	    private String productName;
	    private boolean finalsignStatus;
	    private String customerEmail;
	    private String whiteLabel;
}
