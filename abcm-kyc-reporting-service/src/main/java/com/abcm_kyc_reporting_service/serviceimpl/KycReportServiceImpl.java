package com.abcm_kyc_reporting_service.serviceimpl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.abcm_kyc_reporting_service.dto.KycApiCountResponseModel;
import com.abcm_kyc_reporting_service.dto.KycCountResultDto;
import com.abcm_kyc_reporting_service.dto.KycDataDTO;
import com.abcm_kyc_reporting_service.dto.KycReportRequestModel;
import com.abcm_kyc_reporting_service.dto.PaginationResponse;
import com.abcm_kyc_reporting_service.dto.ResponseModel;
import com.abcm_kyc_reporting_service.dto.StatusCheckRequest;
import com.abcm_kyc_reporting_service.exception.CustomException;
import com.abcm_kyc_reporting_service.repository.KycReportRepository;
import com.abcm_kyc_reporting_service.service.KycReportService;
import com.abcmkyc.entity.KycData;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.metamodel.SingularAttribute;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class KycReportServiceImpl implements KycReportService {

	private final String merchantDetails;
	private final Environment environment;
	private final KycReportRepository kycReportRepository;
	private final EntityManager entityManager;
	
	@Value("${kyc.recent.minutes}")
	private long recentMinutes;
	
  

	public KycReportServiceImpl(@Value("${merchantDetails}") String merchantDetails, Environment environment,
			KycReportRepository kycReportRepository, EntityManager entityManager
) {
		this.merchantDetails = merchantDetails;
		this.environment = environment;
		this.kycReportRepository = kycReportRepository;
		this.entityManager = entityManager;
		
	}

	@Override
	@Transactional(readOnly = true)
	public ResponseModel fetchReportOkyc(KycReportRequestModel request) {
	    LocalDateTime fromDate = parseDate(request.getFormDate()).atStartOfDay();
	    LocalDateTime toDate = parseDate(request.getToDate()).atTime(LocalTime.MAX);

	    Specification<KycData> spec = buildSpecification(
	        request.getMid(), fromDate, toDate, request.getProduct(), request.getSearch()
	    );

	    Page<KycData> panDataPage = request.getSize() == 0
	        ? kycReportRepository.findAll(spec, Pageable.unpaged())
	        : kycReportRepository.findAll(spec, PageRequest.of(Math.max(0, request.getPage()), request.getSize()));

	    Page<KycDataDTO> dtoPage = panDataPage.map(data -> new KycDataDTO(
	        data.getMerchantId(),
	        data.getMerchantName(),
	        data.getClientProviderName(),
	        data.getProduct(),
	        data.getBillable(),
	        data.getIdentificationNo(),
	        data.getCustomerName(),
	        data.getLegalName(),
	        data.getCreatedAt(),
	        data.getMerchantRequestAt(),
	        data.getMerchantResponseAt(),
	        data.getTrackId(),
	        data.getRequestId(),
	        data.getResponseMessage(),
	        data.getStatus(),
	        data.getReasonMessage(),
	        data.isWebhookStatusMerchant(),
	        data.isWebhookStatus(),
	        data.getOrderId(),
	        data.getSignerStatus()
	    ));

	    boolean noData = dtoPage.isEmpty();
	    String messageKey = noData ? "custom.messages.data-not-found" : "custom.messages.success";
	    String codeKey = noData ? "custom.codes.data-not-found" : "custom.codes.success";

	    return buildResponseWithPagination(messageKey, codeKey, noData ? null : dtoPage.getContent(), dtoPage);
	}

	/* Merchnat Report This */
	@Override
	@Transactional(readOnly = true)
	public ResponseModel fetchReportOkycMerchant(KycReportRequestModel request) {
		log.info("Invoked Kyc Report Service Inside Service Method Request:{} ", request);
		LocalDateTime fromDate = parseDate(request.getFormDate()).atStartOfDay();
		LocalDateTime toDate = parseDate(request.getToDate()).atTime(LocalTime.MAX);
		Specification<KycData> spec = buildSpecificationMerchant(request.getMid(), fromDate, toDate,
				request.getProduct(), request.getSearch());
		Page<KycData> panDataPage = request.getSize() == 0 ? kycReportRepository.findAll(spec, Pageable.unpaged())
				: kycReportRepository.findAll(spec, PageRequest.of(Math.max(0, request.getPage()), request.getSize()));
		Page<KycDataDTO> dtoPage = panDataPage.map(data -> new KycDataDTO(data.getMerchantId(), data.getMerchantName(),
				data.getClientProviderName(), data.getProduct(), data.getBillable(), data.getIdentificationNo(),
				data.getCustomerName(), data.getLegalName(), data.getCreatedAt(), data.getMerchantRequestAt(), // ✅																						// Correctl																							// mapped
				data.getMerchantResponseAt(), // ✅ Correctly mapped
				data.getTrackId(), data.getRequestId(), data.getResponseMessage(), data.getStatus(),
				data.getReasonMessage(),  data.isWebhookStatusMerchant(), // ✅ NEW FIELD
			    data.isWebhookStatus(),data.getOrderId(),data.getSignerStatus()));
		boolean noData = dtoPage.isEmpty();
		String messageKey = noData ? "custom.messages.data-not-found" : "custom.messages.success";
		String codeKey = noData ? "custom.codes.data-not-found" : "custom.codes.success";
		return buildResponseWithPagination(messageKey, codeKey, noData ? null : dtoPage.getContent(), dtoPage);
	}

	private LocalDate parseDate(String dateStr) {
		if (dateStr == null || dateStr.isBlank()) {
			throw new CustomException("Invalid date format", 500);
		}
		return LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
	}

	private Specification<KycData> buildSpecificationMerchant(String mid, LocalDateTime fromDate, LocalDateTime toDate,
			String product, String searchText) {
		return (root, query, cb) -> {
			Predicate datePredicate = cb.between(root.get("createdAt"), fromDate, toDate);
			Predicate midPredicate = (mid != null && !mid.isBlank()) ? cb.equal(root.get("merchantId"), mid)
					: cb.conjunction();
			Predicate productPredicate = switch (product.toLowerCase()) {
			case "pan" -> cb.equal(root.get("product"), "PAN");
			case "gstin" -> cb.equal(root.get("product"), "GSTIN");
			case "okyc" -> cb.equal(root.get("product"), "OKYC");
			case "driving_license" -> cb.equal(root.get("product"), "DRIVING_LICENSE");
			case "e-sign" -> cb.equal(root.get("product"), "E-SIGN");
			default -> cb.and(root.get("product").in("PAN", "OKYC", "GSTIN", "DRIVING_LICENSE", "VOTER-ID","E-SIGN"));
			};
			// Always filter only records with status = 'SUCCESS'
			Predicate statusPredicate = cb.equal(root.get("status"), "SUCCESS");

			Predicate searchPredicate = cb.conjunction();
			if (searchText != null && !searchText.isBlank()) {
				List<Predicate> fieldPredicates = new ArrayList<>();
				for (SingularAttribute<? super KycData, ?> attribute : root.getModel().getSingularAttributes()) {
					if (attribute.getType().getJavaType().equals(String.class)) {
						fieldPredicates.add(
								cb.like(cb.lower(root.get(attribute.getName())), "%" + searchText.toLowerCase() + "%"));
					}
				}
				if (!fieldPredicates.isEmpty()) {
					searchPredicate = cb.or(fieldPredicates.toArray(new Predicate[0]));
				}
			}

			query.orderBy(cb.desc(root.get("createdAt")));

			// Combine all predicates including status = 'SUCCESS'
			return cb.and(datePredicate, midPredicate, productPredicate, searchPredicate, statusPredicate);
		};
	}

	private Specification<KycData> buildSpecification(String mid, LocalDateTime fromDate, LocalDateTime toDate,
			String product, String searchText) {
		return (root, query, cb) -> {
			Predicate datePredicate = cb.between(root.get("createdAt"), fromDate, toDate);
			Predicate midPredicate = (mid != null && !mid.isBlank()) ? cb.equal(root.get("merchantId"), mid)
					: cb.conjunction();
			Predicate productPredicate = switch (product.toLowerCase()) {
			case "pan" -> cb.equal(root.get("product"), "PAN");
			case "gstin" -> cb.equal(root.get("product"), "GSTIN");
			case "okyc" -> cb.and(cb.equal(root.get("product"), "OKYC"));
			case "driving_license" -> cb.equal(root.get("product"), "DRIVING_LICENSE");
			case "voter-id" -> cb.equal(root.get("product"), "VOTER-ID");
			case "e-sign" -> cb.equal(root.get("product"), "E-SIGN");
			default -> cb.and(root.get("product").in("PAN", "OKYC", "GSTIN", "DRIVING_LICENSE", "VOTER-ID","E-SIGN"));
			};

			Predicate searchPredicate = cb.conjunction();
			if (searchText != null && !searchText.isBlank()) {
				List<Predicate> fieldPredicates = new ArrayList<>();
				for (SingularAttribute<? super KycData, ?> attribute : root.getModel().getSingularAttributes()) {
					if (attribute.getType().getJavaType().equals(String.class)) {
						fieldPredicates.add(cb.like(cb.lower(root.get(attribute.getName())), "%" + searchText + "%"));
					}
				}
				if (!fieldPredicates.isEmpty()) {
					searchPredicate = cb.or(fieldPredicates.toArray(new Predicate[0]));
				}
			}

			query.orderBy(cb.desc(root.get("createdAt")));
			return cb.and(datePredicate, midPredicate, productPredicate, searchPredicate);
		};
	}

	private ResponseModel buildResponseWithPagination(String messageKey, String codeKey, Object data, Page<?> page) {
		String message = environment.getProperty(messageKey);
		int code = Integer.parseInt(environment.getProperty(codeKey));
		PaginationResponse pagination = new PaginationResponse(page.getTotalPages(), page.getTotalElements(),
				page.getNumber(), page.getSize());
		return new ResponseModel(message, code, data, pagination);
	}

	@Override
	@Transactional(readOnly = true)
	public KycApiCountResponseModel fetchReportOkyc(Date startDate, Date endDate, String merchantId, String product) {
		try {
			@SuppressWarnings("unchecked")
			List<Object[]> rawResults = entityManager
					.createNativeQuery(
							"CALL abcm_kyc_ms_db.GetKycSummaryCounts(:merchantId, :startDate, :endDate, :product)")
					.setParameter("merchantId", merchantId).setParameter("startDate", startDate)
					.setParameter("endDate", endDate).setParameter("product", product).getResultList();

			List<KycCountResultDto> results = rawResults.stream()
					.map(row -> new KycCountResultDto(row[0] != null ? row[0].toString() : null,
							row[1] != null ? row[1].toString() : null,
							row[2] != null ? ((Number) row[2]).intValue() : 0,
							row[3] != null ? ((Number) row[3]).intValue() : 0,
							row[4] != null ? ((Number) row[4]).intValue() : 0,
							row[5] != null ? ((Number) row[5]).intValue() : 0,
							row[6] != null ? ((Number) row[6]).intValue() : 0,
							row[7] != null ? ((Number) row[7]).intValue() : 0,
							row[8] != null ? ((Number) row[8]).intValue() : 0	))
					.collect(Collectors.toList());

			if (results.isEmpty()) {
				return new KycApiCountResponseModel("No Data Found", 204, results);
			}
			return new KycApiCountResponseModel("success", HttpStatus.OK.value(), results);

		} catch (Exception e) {
			log.error("Error in fetchReportOkyc: {}", e.getMessage(), e);
			return new KycApiCountResponseModel("failed", 500, null);
		}
	}

	
	
	@Override
	public ResponseModel checkStatus(String appId, String apiKey, StatusCheckRequest statusCheckRequest) {
	    log.info("statusCheckRequest {}", statusCheckRequest);
	    if (appId == null || appId.isBlank() || apiKey == null || apiKey.isBlank()) {
	        return new ResponseModel("Unauthorized: Invalid API key or App ID", 403, null);
	    }
	    String merchantId = statusCheckRequest.getMerchantId();
	    String orderId    = statusCheckRequest.getOrderId();
	    if (merchantId == null || merchantId.isBlank()) {
	        return new ResponseModel("Missing required field: merchantId", 400, null);
	    }
	    try {
	        if (orderId != null) {
	            orderId = orderId.trim();
	            if (orderId.isEmpty()) orderId = null;
	        }
	        //LocalDateTime since = LocalDateTime.now().minusMinutes(recentMinutes);
	        List<KycData> recentData = kycReportRepository
	                .findByMerchantAndOptionalOrderIdNative(merchantId, orderId);
	        if (recentData.isEmpty()) {
	            return new ResponseModel("No records found", 404, null);
	        }
	        ObjectMapper mapper = new ObjectMapper();
	        List<Map<String, Object>> validItems = new ArrayList<>();
	        for (KycData dto : recentData) {
	            log.info("Processing record ID={}, Billable={}", dto.getId(), dto.getBillable());

	            Map<String, Object> parsedMap = new LinkedHashMap<>();

	            String json = dto.getMerchantResponse();
	            if (json != null && json.trim().startsWith("{")) {
	                try {
	                    parsedMap = mapper.readValue(json, new TypeReference<Map<String, Object>>() {});
	                    // Normalize timestamp
	                    if (parsedMap.containsKey("response_timestamp")) {
	                        normalizeTimestamp(parsedMap, dto.getId());
	                    }
	                } catch (Exception e) {
	                    log.warn("Invalid JSON for record ID {}: {}", dto.getId(), e.getMessage());
	                    parsedMap.put("raw_merchant_response", json); // store raw string if invalid JSON
	                }
	            } else {
	                // No valid JSON → include minimal info
	                parsedMap.put("raw_merchant_response", json);
	            }

	            Map<String, Object> item = new LinkedHashMap<>();
	            item.put("product", dto.getProduct());
	            item.put("status", dto.getStatus());
	            item.put("data", parsedMap);

	            validItems.add(item);
	        }

	        if (validItems.isEmpty()) {
	            return new ResponseModel("No valid data found", 404, null);
	        }

	        return new ResponseModel("status records fetched successfully", 200, validItems);

	    } catch (Exception e) {
	        log.error("Internal error in checkStatus", e);
	        return new ResponseModel("Internal Server Error", 500, null);
	    }
	}

	private void normalizeTimestamp(Map<String, Object> parsedMap, Long recordId) {
	    try {
	        Object tsObj = parsedMap.get("response_timestamp");
	        if (tsObj instanceof String timestampStr && !timestampStr.isBlank()) {
	            LocalDateTime dateTime = null;

	            List<DateTimeFormatter> formatters = List.of(
	                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS"),
	                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
	            );
	            for (DateTimeFormatter fmt : formatters) {
	                try { dateTime = LocalDateTime.parse(timestampStr, fmt); break; }
	                catch (Exception ignored) {}
	            }
	            if (dateTime == null) {
	                try {
	                    ZonedDateTime zdt = ZonedDateTime.parse(timestampStr);
	                    dateTime = zdt.withZoneSameInstant(ZoneId.of("Asia/Kolkata")).toLocalDateTime();
	                } catch (Exception ignored) {}
	            }
	            if (dateTime != null) {
	                parsedMap.put("response_timestamp",
	                    dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
	            } else {
	                log.warn("❗ Unrecognized timestamp format for record {}: {}", recordId, timestampStr);
	            }
	        }
	    } catch (Exception ex) {
	        log.warn("❌ Timestamp format fail for record {}: {}", recordId, ex.getMessage());
	    }
	}


	
	

	




	

}





