package com.abcm.esign_service.serviceImpl;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.CompletableFuture;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.gson.GsonAutoConfiguration;
import org.springframework.core.env.Environment;
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

	/* This Propeties to Email Trigger When Service is Down */

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
	public ResponseModel verifyEsign(EsignRequest request, String signersJson,
			MultipartFile file, String appId, String apiKey) {

		log.info("verify voter id service method{}", request);
		validiateEsignRequest.validateEsignRequest(request,file);

		ProductDetailsDto productDetailsDto = getProdcutDetails(request.getMerchant_id());
		log.info("Product Details Dto: {}", productDetailsDto);
		voterIdRequestvalidator.checkBalance(request.getMerchant_id(), productDetailsDto.getMerchantName());
		log.info("after wallet balance check: {}");

		voterIdRequestvalidator.validateApiCredentials(productDetailsDto, appId, apiKey);

		//log.info("after valideate credential check");

		EsignMerchantRequest esignMerchantRequest = esignMerchantRequestMapper.mapToFullRequest(request, signersJson);
		log.info("Gsom Reeust body:{}", esignMerchantRequest);
            
		voterIdRequestvalidator.checksignersize(esignMerchantRequest.getSigners());
		//log.info("after EsignMerchantRequest ReuestBuild :{}", productDetailsDto.getProviderName());

		ZoopEsignAdhaarRequest zoopEsignAdhaarRequest = dispatcher
				.EsignProviderRequestBody(productDetailsDto.getProviderName(), esignMerchantRequest, file);

		//log.info("Requesy Body Dyanamic{}", zoopEsignAdhaarRequest);

		KycData kycData = asyncEsignRequestSaveService.saveEsignAsync(esignMerchantRequest, zoopEsignAdhaarRequest,
				productDetailsDto.getProviderName(), productDetailsDto.getProductName(),
				productDetailsDto.getMerchantName());

		log.info("after saving the voter-id request{}", kycData.getOrderId(), kycData.getMerchantId());

		return handleEsignAadhaarVerificationAsync(zoopEsignAdhaarRequest, productDetailsDto, kycData.getTrackId(),
				kycData.getOrderId());
	}

	private ResponseModel handleEsignAadhaarVerificationAsync(ZoopEsignAdhaarRequest zoopEsignAdhaarRequest,
			ProductDetailsDto productDetailsDto, String trackId, String orderId) {
		log.info("Api Call handleVoterIdVerificationAsync");
		//Link Not Expired
		String errorResponse="{\r\n"
				+ "    \"success\": false,\r\n"
				+ "    \"response_code\": \"106\",\r\n"
				+ "    \"response_message\": \"Invalid inputs. Check error and retry\",\r\n"
				+ "    \"metadata\": {\r\n"
				+ "        \"billable\": \"N\",\r\n"
				+ "        \"reason_message\": \"\\\"signers[0].signer_name\\\" is required\"\r\n"
				+ "    }\r\n"
				+ "}";
		String apiResponse = "{\r\n"
				+ "    \"expires_at\": \"2026-02-21T17:17:50.052+00:00\",\r\n"
				+ "    \"request_timestamp\": \"2026-02-14T17:17:50.052+00:00\",\r\n"
				+ "    \"test\": false,\r\n"
				+ "    \"group_id\": \"6990ae3efe44cfeca9b72a56\",\r\n"
				+ "    \"success\": true,\r\n"
				+ "    \"requests\": [\r\n"
				+ "        {\r\n"
				+ "            \"signer_name\": \"Krushna Kacharu Dakale\",\r\n"
				+ "            \"signing_url\": \"https://esign.zoop.plus/v5/viewer/6990ae3efe44cfeca9b72a58?show_download_btn=Y&mode=REDIRECT&v=4.2.0\",\r\n"
				+ "            \"request_id\": \"6990ae3efe44 cfeca9b72a58\"\r\n"
				+ "        },\r\n"
				+ "        {\r\n"
				+ "            \"signer_name\": \"Nikita Bharat Landge\",\r\n"
				+ "            \"signing_url\": \"https://esign.zoop.plus/v5/viewer/6990ae3efe44cfeca9b72a5a?show_download_btn=Y&mode=REDIRECT&v=4.2.0\",\r\n"
				+ "            \"request_id\": \"6990ae3efe44cfeca9b72a5a\"\r\n"
				+ "        }\r\n"
				+ "    ],\r\n"
				+ "    \"webhook_security_key\": \"b9a966a9-77e0-47a1-9284-7d4b09a4546b\"\r\n"
				+ "}";
		
		String apiResponse2=" {\r\n"
				+ "    \"requests\": [\r\n"
				+ "        {\r\n"
				+ "            \"request_id\": \"698f5385fe44cfeca9b3f730\",\r\n"
				+ "            \"signer_name\": \"krushna kacharu dakale\",\r\n"
				+ "            \"signer_email\": \"krushnadakale@abcmapp.dev\",\r\n"
				+ "            \"signing_url\": \"https://esign.zoop.plus/v5/viewer/698f5385fe44cfeca9b3f730?show_download_btn=Y&mode=REDIRECT&v=4.2.0\"\r\n"
				+ "        },\r\n"
				+ "        {\r\n"
				+ "            \"request_id\": \"698f5385fe44cfeca9b3f732\",\r\n"
				+ "            \"signer_name\": \"Nikita Bharat Landge\",\r\n"
				+ "            \"signer_email\": \"dakalekrush546@gmail.com\",\r\n"
				+ "            \"signing_url\": \"https://esign.zoop.plus/v5/viewer/698f5385fe44cfeca9b3f732?show_download_btn=Y&mode=REDIRECT&v=4.2.0\"\r\n"
				+ "        }\r\n"
				+ "    ],\r\n"
				+ "    \"group_id\": \"698f5385fe44cfeca9b3f72e\",\r\n"
				+ "    \"success\": true,\r\n"
				+ "    \"webhook_security_key\": \"66ec7ea1-7d86-47bc-bec1-7116994672c3\",\r\n"
				+ "    \"request_timestamp\": \"2026-02-13T16:38:29.595+00:00\",\r\n"
				+ "    \"expires_at\": \"2026-02-20T16:38:29.595+00:00\",\r\n"
				+ "    \"test\": false\r\n"
				+ "}\r\n"
				+ "";
		String mainResponse= apiCall.providerApiCall(zoopEsignAdhaarRequest, productDetailsDto);
		log.info("voteri API response : {}", apiResponse2);
		if (apiResponse2 == null || apiResponse2.isBlank() || "fail:false".equalsIgnoreCase(apiResponse2)
				|| !apiResponse2.trim().startsWith("{")) {
			CompletableFuture.runAsync(() -> {
				try {
					log.info("Email Template Path: {}", serviceDownTemplatePath);
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
					 sendFailureEmail.sendEkycFailureEmail(mailstring1, "", serviceDownSubject,to);

				} catch (Exception e) {
					e.printStackTrace(); 
				}
			});
			log.error("API response is null, empty, 'fail:false', or not valid JSON: " + apiResponse2);
			throw new CustomException(environment.getProperty("custom.messages.provider-invalid-res"),
					Integer.parseInt(environment.getProperty("custom.codes.provider-invalid-res")));
		}
		JSONObject responseObj = new JSONObject(mainResponse);
		return responseDispatcher.getVoterIdResponse(productDetailsDto.getProviderName(), responseObj, trackId,
				productDetailsDto.getMerchantId(), productDetailsDto.getProductRate(), orderId);
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

		log.info("merchant master details : {} ", merchantMaster);
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
							d.setVoter_id(merchantMaster.getVoter_id());
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

}
