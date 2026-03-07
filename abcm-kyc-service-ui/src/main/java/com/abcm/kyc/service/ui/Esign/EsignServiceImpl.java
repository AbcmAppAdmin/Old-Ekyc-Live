package com.abcm.kyc.service.ui.Esign;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.util.UriComponentsBuilder;

import com.abcm.kyc.service.ui.dto.ApiResponseModel;
import com.abcm.kyc.service.ui.repository.MerchantRepository;
import com.abcm.kyc.service.ui.repository.MerchantRepository.MerchantApiCredentialsProjection;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class EsignServiceImpl implements EsignService {

	private final WebClient.Builder webClientBuilder;

	private final MerchantRepository merchantRepository;

	
	@Value("${EsignApi}")
	private String EsignUrl;

	@Value("${webhookUrl}")
	private String webhookUrl;

	@Value("${EsignReport}")
	private String EsignReport;

	@Override
	public ApiResponseModel esignRequest(EsignRequest esignRequest, String signersJson, MultipartFile file) {
		log.info("Starting eSign request for merchant ID: {}", esignRequest.toString());
		try {
			MerchantApiCredentialsProjection credentialsProjection = merchantRepository
					.findApiCredentialsByMid(esignRequest.getMerchantId());
			String response = makeWebClientCall(esignRequest, signersJson, file,credentialsProjection);
			log.info("eSign Response: {}", response);
			return parseResponse(response);
		} catch (Exception e) {
			log.error("Error while processing eSign request: ", e);
			return new ApiResponseModel(500, "Error during eSign request", null);
		}
	}

	private ApiResponseModel parseResponse(String response) {
		try {
			JSONObject jsonObject = new JSONObject(response);
			int statusCode = jsonObject.optInt("statusCode", -1);
			if (statusCode == -1) {
				return handleErrorResponse(jsonObject);
			}
			if (statusCode == 200) {
				return handleSuccessResponse(jsonObject);
			}
			return new ApiResponseModel(500, "Unexpected response", response);
		} catch (Exception jsonParseException) {
			log.error("Error while parsing JSON response: ", jsonParseException);
			return new ApiResponseModel(500, "Error during JSON parsing", null);
		}
	}

	private ApiResponseModel handleErrorResponse(JSONObject jsonObject) {
		int responseCode = jsonObject.optInt("response_code", 400);
		if (responseCode == 400) {

			return new ApiResponseModel(400, "failed", jsonObject.optString("response_message"));
		} else {
			return new ApiResponseModel(400, "failed", jsonObject.optString("response_message"));
		}

	}

	private ApiResponseModel handleSuccessResponse(JSONObject jsonObject) throws IOException {
		JSONObject data = jsonObject.optJSONObject("data");
		if (data != null) {
			ObjectMapper objectMapper = new ObjectMapper();
			EsignResponseData esignData = objectMapper.readValue(data.toString(), EsignResponseData.class);
			return new ApiResponseModel(200, "Success", esignData);
		}
		return new ApiResponseModel(500, "Missing data", null);
	}

	private String makeWebClientCall(EsignRequest esignRequest, String signersJson, MultipartFile file, MerchantApiCredentialsProjection credentials)
			throws IOException {
		try {
			log.info("Esign Url:{}", EsignUrl);
			MultiValueMap<String, Object> body = buildRequestBody(esignRequest, signersJson, file);
			WebClient webClient = webClientBuilder.baseUrl(EsignUrl).build();
			return webClient.post().header(HttpHeaders.CONTENT_TYPE, MediaType.MULTIPART_FORM_DATA_VALUE)
					.header("app-id", credentials.getAppId()).header("api-key", credentials.getApiKey()).body(BodyInserters.fromMultipartData(body))
					.retrieve().bodyToMono(String.class).block();
		} catch (WebClientResponseException e) {
			log.error("Error response: {} with body: {}", e.getStatusCode(), e.getResponseBodyAsString());
			return e.getResponseBodyAsString();
		} catch (Exception e) {
			return "failed";
		}
	}

	private MultiValueMap<String, Object> buildRequestBody(EsignRequest esignRequest, String signersJson,
			MultipartFile file) throws IOException {
		MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
		body.add("merchant_id", esignRequest.getMerchantId());
		body.add("consent", "Y");
		body.add("document_name", esignRequest.getDocument_name());
		body.add("link_expiry", esignRequest.getLink_expiry_min());
		body.add("order_id", generateOrderId());
		body.add("webhook_url", webhookUrl);
		body.add("signers", signersJson);
		body.add("file", new MultipartInputStreamFileResource(file.getInputStream(), file.getOriginalFilename()));
		body.add("allow_download", esignRequest.getAllowDownload());
		return body;
	}

	static class MultipartInputStreamFileResource extends InputStreamResource {

		private final String filename;

		MultipartInputStreamFileResource(InputStream inputStream, String filename) {
			super(inputStream);
			this.filename = filename;
		}

		@Override
		public String getFilename() {
			return this.filename;
		}

		@Override
		public long contentLength() throws IOException {
			return -1;
		}
	}

	public static String generateOrderId() {

		LocalDateTime now = LocalDateTime.now();

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSSSSS");

		return "ABELPAY" + now.format(formatter);
	}

	public String mockResponse() {
		return " {\"message\":\"success\",\"statusCode\":200,\"data\":{\"response_code\":\"200\",\"response_message\":\"Success\",\"merchant_id\":\"K10001\",\"success\":true,\"order_id\":\"ABELPAY0001\",\"signer_requests\":[{\"request_id\":\"698f5385fe44cfeca9b3f730\",\"track_id\":\"KYC20260217113437129\",\"signer_name\":\"krushna kacharu dakale\",\"email_notification\":\"SEND\",\"signer_email\":\"dakalekrush546@gmail.com\",\"signing_url\":\"http://localhost:8049/api/v1/e-sign/698f5385fe44cfeca9b3f730\"},{\"request_id\":\"698f5385fe44cfeca9b3f732\",\"track_id\":\"KYC20260217113437198\",\"signer_name\":\"Nikita Bharat Landge\",\"email_notification\":\"SEND\",\"signer_email\":\"krushna.dakale@abcmapp.dev\",\"signing_url\":\"http://localhost:8049/api/v1/e-sign/698f5385fe44cfeca9b3f732\"}]}}\r\n";
	}

	@Override
	public ApiResponseModel esignFetchReport(String merchantId, String fromDate, String toDate, String status, int page,
			int size) {
		try {
			// Base URL for the endpoint
			// String baseUrl = "http://localhost:8049/api/esign/report";

			UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(EsignReport)
					.queryParamIfPresent("merchantId", Optional.ofNullable(merchantId).filter(s -> !s.isEmpty()))
					.queryParamIfPresent("fromDate", Optional.ofNullable(fromDate).filter(s -> !s.isEmpty()))
					.queryParamIfPresent("toDate", Optional.ofNullable(toDate).filter(s -> !s.isEmpty()))
					.queryParamIfPresent("status", Optional.ofNullable(status).filter(s -> !s.isEmpty()))
					.queryParam("page", page).queryParam("size", size);

			// Log the final URL for debugging purposes
			String url = uriBuilder.toUriString();
			log.info("Esign API Report URL: {}", url);
			String response = webClientBuilder.baseUrl(EsignReport).build().get().uri(url) // Use the built URL directly
					.retrieve().bodyToMono(String.class).block(); // Blocking call to wait for the response
			if (response.contains("Data Not Found")) {
				return new ApiResponseModel(404, "Data Not Found", null);
			}

			return new ApiResponseModel(200, "Success", response);

		} catch (WebClientResponseException e) {
			log.info("Esign 400 Response :{}", e.getResponseBodyAsString());
			log.error("Error response from API: {}. Status Code: {}. Response Body: {}", e.getClass().getSimpleName(),
					e.getStatusCode(), e.getResponseBodyAsString());
			if (e.getStatusCode().value() == 404) {
				return new ApiResponseModel(404, "data not fount", e.getResponseBodyAsString());

			}

		} catch (Exception e) {
			// General exception handling
			e.printStackTrace();
			return new ApiResponseModel(500, "Failed to fetch report: " + e.getMessage(), null);
		}
		return null;
	}

}
