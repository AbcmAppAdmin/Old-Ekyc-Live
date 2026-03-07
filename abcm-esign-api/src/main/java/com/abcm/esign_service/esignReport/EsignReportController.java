package com.abcm.esign_service.esignReport;

import java.security.MessageDigest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.abcm.esign_service.DTO.ResponseModel;
import com.abcm.esign_service.service.VerifyEsignService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("api/esign")
@RequiredArgsConstructor
@Slf4j
public class EsignReportController {

	private final VerifyEsignService service;

	@GetMapping("/report")
	public ResponseEntity<ResponseModel> fetchEsignReport(@RequestParam(required = false) String merchantId,
			@RequestParam(required = false) String fromDate, @RequestParam(required = false) String toDate,
			@RequestParam(required = false) String status, @RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "20") int size) {

		ResponseModel responseModel = service.esignFetchReport(merchantId, fromDate, toDate, status, page, size);
		return ResponseEntity.status(responseModel.getStatusCode()).body(responseModel);

	}

	@GetMapping("/signerAuditReport")
	public ResponseEntity<ResponseModel> fetchEsignReport(@RequestParam(required = false) String merchantId,
			@RequestParam(required = false) String fromDate, @RequestParam(required = false) String toDate,
			@RequestParam(required = false) String status) {

		ResponseModel responseModel = service.esignAuditReport(merchantId, fromDate, toDate);
		return ResponseEntity.status(responseModel.getStatusCode()).body(responseModel);

	}

	@GetMapping("/verifyHash")
	public ResponseEntity<ResponseModel> fetchEsignReport(@RequestParam("hash") String hash,
			@RequestParam("file") MultipartFile file) {

		if (file == null || file.isEmpty() || hash == null || hash.isBlank()) {
			ResponseModel response = new ResponseModel("File or hash is missing", HttpStatus.BAD_REQUEST.value(), null);
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
		}
		try {
			byte[] fileBytes = file.getBytes();
			String generatedHash = generateSHA256(fileBytes);
			String providedHash = hash.split(",")[0].trim();

			log.info("Generated hash: {}", generatedHash);
			log.info("Provided hash: {}", providedHash);

			boolean isValid = generatedHash.equalsIgnoreCase(providedHash);
			String message = isValid ? "Document is valid ✅" : "Document hash mismatch ❌";

			ResponseModel response = new ResponseModel(message, HttpStatus.OK.value(), isValid);
			return ResponseEntity.ok(response);

		} catch (Exception e) {
			e.printStackTrace();
			ResponseModel response = new ResponseModel("Error processing file: " + e.getMessage(),
					HttpStatus.INTERNAL_SERVER_ERROR.value(), null);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
		}
	}

	private String generateSHA256(byte[] input) throws Exception {
		MessageDigest digest = MessageDigest.getInstance("SHA-256");
		byte[] hashBytes = digest.digest(input);

		StringBuilder hexString = new StringBuilder();
		for (byte b : hashBytes) {
			hexString.append(String.format("%02x", b));
		}
		return hexString.toString();
	}
}
