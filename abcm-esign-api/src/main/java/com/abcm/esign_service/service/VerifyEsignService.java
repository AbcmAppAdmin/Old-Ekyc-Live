package com.abcm.esign_service.service;

import org.springframework.web.multipart.MultipartFile;

import com.abcm.esign_service.DTO.EsignRequest;
import com.abcm.esign_service.DTO.ResponseModel;

public interface VerifyEsignService {
	
	
	public ResponseModel verifyEsign(EsignRequest basicRequest, String signersJson, MultipartFile file, String appId,
			String apiKey);
	
	public boolean existRequestId(String requestId);

	

}
