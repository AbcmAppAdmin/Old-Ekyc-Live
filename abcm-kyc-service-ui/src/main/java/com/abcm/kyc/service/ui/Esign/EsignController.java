package com.abcm.kyc.service.ui.Esign;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.abcm.kyc.service.ui.dto.ApiResponseModel;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/app/public/")
@RequiredArgsConstructor
@Slf4j
public class EsignController {
	
	private final EsignService esignService;
	
	@PostMapping("submit/esign")
	public ResponseEntity<ApiResponseModel>EsignCall(@ModelAttribute EsignRequest esignRequest,@RequestPart("signers") String signersJson, @RequestPart("file") MultipartFile file)
	{
		ApiResponseModel apiResponseModel=esignService.esignRequest(esignRequest,signersJson,file);
		return ResponseEntity.status(apiResponseModel.getResponseCode()).body(apiResponseModel);

		
	}

}
