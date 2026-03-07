package com.abcm.esign_service.serviceImpl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;

import com.abcm.esign_service.DTO.EsignMerchantRequest;
import com.abcm.esign_service.DTO.EsignRequest;
import com.abcm.esign_service.DTO.MerchantResponse;
import com.abcm.esign_service.DTO.ProductDetailsDto;
import com.abcm.esign_service.DTO.ResponseModel;
import com.abcm.esign_service.apiCall.ServiceProviderApiCall;
import com.abcm.esign_service.dyanamicProviderResponse.ResponseDispatcher;
import com.abcm.esign_service.dyanamicRequestBody.EsignRequestDispatcher;
import com.abcm.esign_service.dyanamicRequestBody.ZoopEsignAdhaarRequest;
import com.abcm.esign_service.esignReport.EsignReportResponseDTO;
import com.abcm.esign_service.exception.CustomException;
import com.abcm.esign_service.repo.EsignRepository;
import com.abcm.esign_service.service.AsyncEsignRequestSaveService;
import com.abcm.esign_service.service.VerifyEsignService;
import com.abcm.esign_service.util.CommonUtils;
import com.abcm.esign_service.util.EsignMerchantRequestMapper;
import com.abcm.esign_service.util.SendFailureEmail;
import com.abcm.esign_service.util.ValidiateEsignRequest;
import com.abcmkyc.entity.KycData;
import com.abcmkyc.entity.Merchant_Master_Details;

import jakarta.persistence.criteria.Predicate;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class EsignServiceImpl implements VerifyEsignService {

	private final ValidiateEsignRequest validiateEsignRequest;

	private final ServiceProviderApiCall apiCall;

	private final AsyncEsignRequestSaveService asyncEsignRequestSaveService;

	@Value("${merchantDetails}")
	private String merchantDetails;

	private final Environment environment;

	private final ValidiateEsignRequest voterIdRequestvalidator;

	private final EsignRequestDispatcher dispatcher;

	private final ResponseDispatcher responseDispatcher;

	private final EsignMerchantRequestMapper esignMerchantRequestMapper;

	/* This Properties to Email Trigger When Service is Down */

	@Value("${email.serviceDown.subject}")
	private String serviceDownSubject;

	@Value("${email.serviceDown.template.path}")
	private String serviceDownTemplatePath;

	@Value("${Env}")
	private String env;

	private final SendFailureEmail sendFailureEmail;

	@Value("${email.send.to}")
	private String to;

	private final EsignRepository repository;

	@Override
	public ResponseModel verifyEsign(EsignRequest request, String signersJson, MultipartFile file, String appId,
			String apiKey,HttpServletRequest httpServletRequest) {

		log.info("verify Esigner api service method :{}, signer data:{}", request, signersJson);
		
		validiateEsignRequest.validateEsignRequest(request, file,signersJson);

		ProductDetailsDto productDetailsDto = getProdcutDetails(request.getMerchant_id());
		
		voterIdRequestvalidator.checkBalance(request.getMerchant_id(), productDetailsDto.getMerchantName());
		log.info("after wallet balance check: {}");

		voterIdRequestvalidator.validateApiCredentials(productDetailsDto, appId, apiKey);

		log.info("after valideate credential check");

		EsignMerchantRequest esignMerchantRequest = esignMerchantRequestMapper.mapToFullRequest(request, signersJson);
		
		log.info("Esign Request before send provider:{}", esignMerchantRequest);
		
		voterIdRequestvalidator.checksignersize(esignMerchantRequest.getSigners());

		ZoopEsignAdhaarRequest zoopEsignAdhaarRequest = dispatcher
				.EsignProviderRequestBody(productDetailsDto.getProviderName(), esignMerchantRequest, file);

		 log.info("Requesy Body Dyanamic{}", zoopEsignAdhaarRequest);

		KycData kycData = asyncEsignRequestSaveService.saveEsignAsync(esignMerchantRequest, zoopEsignAdhaarRequest,
				productDetailsDto.getProviderName(), productDetailsDto.getProductName(),
				productDetailsDto.getMerchantName(), file, request.getAllow_download(),httpServletRequest,signersJson);

		log.info("after saving the voter-id request{}", kycData.getOrderId(), kycData.getMerchantId());

		return handleEsignAadhaarVerificationAsync(zoopEsignAdhaarRequest, productDetailsDto, kycData.getTrackId(),
				kycData.getOrderId(),zoopEsignAdhaarRequest.getWhite_label());
	}

	private ResponseModel handleEsignAadhaarVerificationAsync(ZoopEsignAdhaarRequest zoopEsignAdhaarRequest,
			ProductDetailsDto productDetailsDto, String trackId, String orderId,String whiteLabel) {
		log.info("Api Call Esign Response:{}");
		
		String mainResponse = apiCall.providerApiCall(zoopEsignAdhaarRequest, productDetailsDto);
		log.info("Esign Response API response : {}", mainResponse);
		if (mainResponse == null || mainResponse.isBlank() || "fail:false".equalsIgnoreCase(mainResponse)
				) {
			log.info("Failed Response:{}", mainResponse);
			CompletableFuture.runAsync(() -> {
				try {
					String mailstring1 = CommonUtils.readUsingFileInputStream(serviceDownTemplatePath);
					mailstring1 = mailstring1.replace("{{Service_Name}}", productDetailsDto.getProductName());
					mailstring1 = mailstring1.replace("{{providerName}}", productDetailsDto.getProviderName());
					mailstring1 = mailstring1.replace("{{Environment}}", env);
					mailstring1 = mailstring1.replace("{{Reason}}",
							"The Provider service " + productDetailsDto.getProviderName()
									+ " is currently unavailable. Please try again later");
					DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
					// Get current date-time
					String timestamp = LocalDateTime.now().format(formatter);
					mailstring1 = mailstring1.replace("{{Timestamp}}", timestamp);
					sendFailureEmail.sendEkycFailureEmail(mailstring1, "", serviceDownSubject, to);

				} catch (Exception e) {
					e.printStackTrace();
				}
			});
			//log.error("API response is null, empty, 'fail:false', or not valid JSON: " + mainResponse);
			throw new CustomException(environment.getProperty("custom.messages.provider-invalid-res"),
					Integer.parseInt(environment.getProperty("custom.codes.provider-invalid-res")));
		}
		JSONObject responseObj = new JSONObject(mainResponse);
		return responseDispatcher.getVoterIdResponse(productDetailsDto.getProviderName(), responseObj, trackId,
				productDetailsDto.getMerchantId(), productDetailsDto.getProductRate(), orderId,whiteLabel);
	}

	public ProductDetailsDto getProdcutDetails(String mid) {
		Merchant_Master_Details merchantMaster = getMerchantByMid(mid); // no caching
		return extractOkycProductDetails(merchantMaster);
	}

	public Merchant_Master_Details getMerchantByMid(String mid) {
		log.info("Fetching Merchant Details for MID: {}", mid);
		WebClient webClient = WebClient.builder().baseUrl(merchantDetails).build();
		MerchantResponse response = webClient.get().uri(uriBuilder -> uriBuilder
				.path("/api/merchant-kyc-routing/merchant-details").queryParam("merchantId", mid).build()).retrieve()
				.bodyToMono(MerchantResponse.class).block();
		if (response == null || response.getData() == null) {
			log.error("No merchant data found for MID: {}", mid);
			throw new CustomException(environment.getProperty("custom.messages.Not-Found"),
					Integer.parseInt(environment.getProperty("custom.codes.Not-Found")));
		}
		Merchant_Master_Details details = response.getData();
		if (details.getProductDetails() == null || details.getProductDetails().isEmpty()) {
			log.error("No product details found for merchant MID: {}", mid);
			throw new CustomException(environment.getProperty("custom.messages.product-not-found"),
					Integer.parseInt(environment.getProperty("custom.codes.product-not-found")));
		}
		log.info("Successfully fetched Merchant Details for MID: {}", mid);
		return details;
	}

	public ProductDetailsDto extractOkycProductDetails(Merchant_Master_Details merchantMaster) {
		log.info("The merchant Active status is " + merchantMaster.isActive());
		if (merchantMaster == null || merchantMaster.getProductDetails() == null) {
			throw new RuntimeException("Merchant or Product details missing");
		}

		//log.info("merchant master details : {} ", merchantMaster);
		return merchantMaster.getProductDetails().stream()
				.filter(product -> "E-SIGN".equalsIgnoreCase(product.getProductName()))
				.flatMap(product -> product.getProviders().stream()
						.flatMap(provider -> provider.getMerchantCharges().stream().map(charges -> {
							ProductDetailsDto d = new ProductDetailsDto();
							d.setId(merchantMaster.getId());
							d.setMerchantId(merchantMaster.getMerchantId());
							d.setMerchantName(merchantMaster.getMerchantName());
							d.setAppId(merchantMaster.getAppId());
							d.setApiKey(merchantMaster.getApiKey());
							d.setActive(merchantMaster.isActive());
							d.setOKYC(merchantMaster.getOKYC());
							d.setProductId(product.getProductId());
							d.setProductName(product.getProductName());
							d.setProviderId(provider.getProviderId());
							d.setProviderName(provider.getProviderName());
							d.setProviderAppId(provider.getProviderAppId());
							d.setProviderAppkey(provider.getProviderAppkey());
							d.setAadhaarOtpSendUrl(provider.getAadhaarOtpSendUrl());
							d.setRouteId(charges.getRouteId());
							d.setProductRate(charges.getProductRate());
							return d;
						})))
				.findFirst().orElseThrow(() -> new CustomException(environment.getProperty("custom.messages.Not-Found"),
						Integer.parseInt(environment.getProperty("custom.codes.Not-Found"))));
	}

	@Override
	public boolean existRequestId(String requestId) {
		return repository.existsByRequestId(requestId);
	}

	@Override
	public ResponseModel esignFetchReport(String merchantId, String fromDate, String toDate, String status, int page, int size) {
	    try {
	    	log.info("Esign Report Method Inside this: {}", merchantId);
	        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
	        Specification<KycData> specification = (root, query, cb) -> {
	            List<Predicate> predicates = new ArrayList<>();
	            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
	            if (fromDate != null && !fromDate.isEmpty() && toDate != null && !toDate.isEmpty()) {
	                LocalDate from = LocalDate.parse(fromDate, formatter);
	                LocalDate to = LocalDate.parse(toDate, formatter);
	                predicates.add(cb.between(root.get("createdAt"), from.atStartOfDay(), to.atTime(23, 59, 59)));
	            } else {
	                throw new CustomException("fromDate and toDate are required",400);
	            }
	            if (merchantId != null && !merchantId.equalsIgnoreCase("ALL") && !merchantId.isEmpty()) {
	                predicates.add(cb.equal(root.get("merchantId"), merchantId));
	            }
	            if (status != null && !status.equalsIgnoreCase("ALL") && !status.isEmpty()) {
	                predicates.add(cb.equal(root.get("signerStatus"), status));
	            }
	            predicates.add(cb.equal(root.get("product"), "E-SIGN"));

	            return cb.and(predicates.toArray(new Predicate[0]));
	        };
	        Page<KycData> pageResult = repository.findAll(specification, pageable);
	        List<EsignReportResponseDTO> items = pageResult.getContent().stream()
	                .map(data -> EsignReportResponseDTO.builder()
	                        .merchantId(data.getMerchantId())
	                        .merchantName(data.getMerchantName())
	                        .provider(data.getClientProviderName())
	                        .orderId(data.getOrderId())
	                        .trackId(data.getTrackId())
	                        .requestAt(data.getMerchantRequestAt())
	                        .documentPath(data.getDocumentPath())
	                        .signerDocumentPath(data.getSignedDocumentPath())
	                        .signedAt(data.getSignedAt())
	                        .status(data.getStatus())
	                        .signerStatus(data.getSignerStatus())
	                        .customerName(data.getCustomerName())
	                        .billable(data.getBillable())
	                        .productName(data.getProduct())
	                        .finalsignStatus(data.isFinalSignStatus())
	                        .customerEmail(data.getSignerEmail())
	                        .whiteLabel(data.getWhiteLabel())
	                        .build())
	                .toList();
	        Map<String, Object> meta = new HashMap<>();
	        meta.put("page", pageResult.getNumber());
	        meta.put("size", pageResult.getSize());
	        meta.put("totalElements", pageResult.getTotalElements());
	        meta.put("totalPages", pageResult.getTotalPages());
	        meta.put("hasNext", pageResult.hasNext());
	        meta.put("hasPrevious", pageResult.hasPrevious());
	        Map<String, Object> responseData = new HashMap<>();
	        responseData.put("items", items);
	        responseData.put("meta", meta);
	        if(items.isEmpty() || items.size()==0  || items==null)
	        {
	        	return new ResponseModel("empty", HttpStatus.NOT_FOUND.value(), "Data Not Found");
	        }
	        return new ResponseModel("success", HttpStatus.OK.value(), responseData);

	    } catch (Exception e) {
	        log.error("Error fetching esign report", e);
	        return new ResponseModel("failed", HttpStatus.BAD_REQUEST.value(), null);
	    }
	}

	
	@Override
	public ResponseModel esignAuditReport(String merchantId, String fromDate, String toDate) {
	    try {
	        log.info("Esign Audit Report Method: {}", merchantId);

	        Specification<KycData> specification = (root, query, cb) -> {
	            List<Predicate> predicates = new ArrayList<>();
	            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
	            if (fromDate != null && !fromDate.isEmpty() && toDate != null && !toDate.isEmpty()) {
	                LocalDate from = LocalDate.parse(fromDate, formatter);
	                LocalDate to = LocalDate.parse(toDate, formatter);
	                predicates.add(cb.between(root.get("createdAt"), from.atStartOfDay(), to.atTime(23, 59, 59)));
	            } else {
	                throw new CustomException("fromDate and toDate are required", 400);
	            }
	            if (merchantId != null && !merchantId.equalsIgnoreCase("ALL") && !merchantId.isEmpty()) {
	                predicates.add(cb.equal(root.get("merchantId"), merchantId));
	            }
	            predicates.add(cb.equal(root.get("product"), "E-SIGN"));

	            return cb.and(predicates.toArray(new Predicate[0]));
	        };

	        List<KycData> auditList = repository.findAll(specification, Sort.by("createdAt").descending());
	        log.info("Size: {}", auditList.size());

	        if (auditList.isEmpty()) {
	            return new ResponseModel("empty", HttpStatus.NOT_FOUND.value(), "Data Not Found");
	        }
	        // Group by orderId
	        Map<String, List<KycData>> groupedByOrder = auditList.stream()
	                .collect(Collectors.groupingBy(KycData::getOrderId));
	        List<Map<String, Object>> groupedResponse = new ArrayList<>();
	        for (Map.Entry<String, List<KycData>> entry : groupedByOrder.entrySet()) {
	            List<KycData> orderList = entry.getValue();
	            KycData first = orderList.get(0); 
	            Map<String, Object> orderMap = new LinkedHashMap<>();
	            orderMap.put("documentName", first.getSignerDocumentName());
	            orderMap.put("documentId", first.getOrderId());

	            orderList.stream()
	                     .filter(KycData::isFinalSignStatus)
	                     .findFirst()
	                     .ifPresent(data -> orderMap.put("documentHash", data.getDocumentHash()));

	            boolean allSigned = orderList.stream()
	                    .allMatch(data -> "SIGNED".equalsIgnoreCase(data.getStatus()));

	            orderMap.put("status", allSigned ? "Complete" : "Inprogress");

	            // Add signer details as array
	            List<Map<String, Object>> signers = orderList.stream().map(data -> {
	                Map<String, Object> signerMap = new LinkedHashMap<>();
	                signerMap.put("signerName", data.getCustomerName());
	                signerMap.put("signerEmail", data.getSignerEmail());
	                signerMap.put("signerStatus", data.getSignerStatus());
	                signerMap.put("signedAt", data.getSignedAt());
	                signerMap.put("role", "signer");
	                signerMap.put("state", data.getState());
	                signerMap.put("notification", data.getSignerEmailNotification());
	                signerMap.put("ipAddress", data.getIpAddress());
	                return signerMap;
	            }).toList();

	            orderMap.put("signers", signers);
	            groupedResponse.add(orderMap);
	        }

	        // Wrap in final response
	        Map<String, Object> responseData = new HashMap<>();
	        responseData.put("items", groupedResponse);

	        return new ResponseModel("success", HttpStatus.OK.value(), responseData);

	    } catch (Exception e) {
	        log.error("Error fetching esign audit report", e);
	        return new ResponseModel("failed", HttpStatus.BAD_REQUEST.value(), null);
	    }
	}

}
