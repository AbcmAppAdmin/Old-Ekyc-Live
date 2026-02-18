package com.abcm.kyc.service.ui.Esign;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.abcm.kyc.service.ui.dto.ApiResponseModel;

@Service
public interface EsignService {

	ApiResponseModel esignRequest(EsignRequest esignRequest, String signersJson, MultipartFile file);

}
