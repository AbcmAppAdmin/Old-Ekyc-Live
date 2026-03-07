package com.abcm.kyc.service.ui.Esign;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
	
	
	@GetMapping("/report")
    public ResponseEntity<ApiResponseModel>fetchEsignReport( @RequestParam(required = false) String merchantId,
            @RequestParam(required = false) String fromDate,
            @RequestParam(required = false) String toDate,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size)
    {
		
		
		
		
		ApiResponseModel responseModel=esignService.esignFetchReport(merchantId,fromDate,toDate,status,page,size);
		
		return ResponseEntity.status(responseModel.getResponseCode()).body(responseModel);


    	
    }

}
