package com.abcm.esign_service.service;

import org.springframework.web.multipart.MultipartFile;

import com.abcm.esign_service.DTO.EsignRequest;
import com.abcm.esign_service.DTO.ResponseModel;

import jakarta.servlet.http.HttpServletRequest;

public interface VerifyEsignService {
	
	
	public ResponseModel verifyEsign(EsignRequest basicRequest, String signersJson, MultipartFile file, String appId,
			String apiKey, HttpServletRequest request);
	
	public boolean existRequestId(String requestId);

	public ResponseModel esignFetchReport(String merchantId, String fromDate, String toDate, String status, int page,
			int size);

	public ResponseModel esignAuditReport(String merchantId, String fromDate, String toDate);

	

}
