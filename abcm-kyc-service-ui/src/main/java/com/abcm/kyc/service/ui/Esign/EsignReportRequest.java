package com.abcm.kyc.service.ui.Esign;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class EsignReportRequest {
	private String merchantId;
    private String fromDate;
    private String toDate;
    private String status;
    private int page;
    private int size;
}
