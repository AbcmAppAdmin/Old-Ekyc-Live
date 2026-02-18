package com.abcm.esign_service.controller;

import java.io.IOException;
import java.net.URI;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.abcm.esign_service.DTO.EsignRequest;
import com.abcm.esign_service.DTO.ResponseModel;
import com.abcm.esign_service.service.VerifyEsignService;
import com.abcm.esign_service.serviceImpl.KycDataService;
import com.abcmkyc.entity.KycData;

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
	
	@Value("${clientUrl}")
	private String clientUrl;


	@PostMapping(value = "eSignAadhaar/verify", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseModel> esignVerify(@ModelAttribute EsignRequest basicRequest,
			@RequestPart("signers") String signersJson, @RequestPart("file") MultipartFile file,
			@RequestHeader("app-id") String appId, @RequestHeader("api-key") String apiKey) throws IOException {

		log.info("Recevied eSignAdhaar Controller Request hit:{}");
		ResponseModel responseModel = service.verifyEsign(basicRequest, signersJson, file, appId, apiKey);
		return ResponseEntity.status(responseModel.getStatusCode()).body(responseModel);
	}

	
	
	
	
	@GetMapping("/e-sign/{requestId}")
	public ResponseEntity<Void> redirectToZoop(@PathVariable String requestId) {
	    try {
	        boolean exists = service.existRequestId(requestId);
	        if (!exists) {
	            URI errorPage = URI.create(clientUrl+"/invalidError?requestId=" + requestId);
	            return ResponseEntity.status(HttpStatus.FOUND)
	                                 .location(errorPage)
	                                 .build();
	        }
	        String zoopUrl = "https://esign.zoop.plus/v5/viewer/"+ requestId +"?show_download_btn=Y&mode=REDIRECT&v=4.2.0";
	        return ResponseEntity.status(HttpStatus.FOUND)
	                             .location(URI.create(zoopUrl))
	                             .build();
	    } catch (Exception e) {
	        URI errorPage = URI.create(clientUrl+"/invalidError?requestId=" + requestId);
	        return ResponseEntity.status(HttpStatus.FOUND)
	                             .location(errorPage)
	                             .build();
	    }
	}
	
	
	
	@RequestMapping(value = "/esignReturnResponse", method = {RequestMethod.GET, RequestMethod.POST})
	public ResponseEntity<Void> returnEsignResponse(@RequestParam(value = "action") String action) {
	    try {
	        log.info("client url is: {}", clientUrl + "/response?status=" + action);
	        URI responsePage = URI.create(clientUrl + "/response?status=" + action);
	        return ResponseEntity.status(HttpStatus.FOUND).location(responsePage).build();
	    } catch (Exception e) {
	        URI responsePage = URI.create(clientUrl + "/response?status=" + action);
	        return ResponseEntity.status(HttpStatus.FOUND).location(responsePage).build();
	    }
	}





    @GetMapping("view/{requestId}")
    public void viewSignedDocument(@PathVariable String requestId, HttpServletResponse response) throws IOException {

        KycData data = kycDataService.getDocumentUrlByRequestId(requestId);
        response.setStatus(HttpServletResponse.SC_FOUND);
        response.setHeader("Location", data.getSignerSdkUrl());
        response.setHeader("Connection", "close");
    }


}

