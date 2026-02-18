package com.abcm.esign_service.controller;

import java.io.IOException;

import com.abcm.esign_service.serviceImpl.KycDataService;
import com.abcmkyc.entity.KycData;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.abcm.esign_service.DTO.EsignRequest;
import com.abcm.esign_service.DTO.ResponseModel;
import com.abcm.esign_service.service.VerifyEsignService;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/v1/")
@RequiredArgsConstructor
@Slf4j
public class EsignVerificationController {

	private final VerifyEsignService service;
    private  final KycDataService kycDataService;

	@PostMapping(value = "eSignAadhaar/verify", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseModel> esignVerify(@ModelAttribute EsignRequest basicRequest,
			@RequestPart("signers") String signersJson, @RequestPart("document_recipients") String recipientsJson,
			@RequestPart("file") MultipartFile file, @RequestHeader("app-id") String appId,
			@RequestHeader("api-key") String apiKey) throws IOException {

		   log.info("Recevied eSignAdhaar Controller Request hit:{}");
		  ResponseModel responseModel = service.verifyEsign(basicRequest, signersJson, recipientsJson, file, appId,apiKey);
		  return ResponseEntity.status(responseModel.getStatusCode()).body(responseModel);
	}
	
	
	
	 @GetMapping("e-sign/{requestId}")
	    public void redirectToZoop(@PathVariable String requestId, HttpServletResponse response) throws IOException {
	        
	       
	        String zoopUrl = "https://esign.zoop.plus/v5/viewer/" + requestId + "?show_download_btn=Y&mode=REDIRECT&v=4.2.0";
	        
	     
	        response.setStatus(HttpServletResponse.SC_FOUND); // 302 Found
	        response.setHeader("Location", zoopUrl);
	        response.setHeader("Connection", "close");
	    }


    @GetMapping("view/{requestId}")
    public void viewSignedDocument(@PathVariable String requestId, HttpServletResponse response) throws IOException {

        KycData data = kycDataService.getDocumentUrlByRequestId(requestId);
        response.setStatus(HttpServletResponse.SC_FOUND); // 302 Found
        response.setHeader("Location", data.getSdkUrl());
        response.setHeader("Connection", "close");
    }


}

