package com.abcm.kyc.service.ui.service.impl;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.abcm.kyc.service.ui.ApiCall.KycReportApiClient;
import com.abcm.kyc.service.ui.dto.ApiResponseModel;
import com.abcm.kyc.service.ui.dto.MerchantDTO;
import com.abcm.kyc.service.ui.dto.MerchantIdMidNameProjection;
import com.abcm.kyc.service.ui.dto.MerchantKycOnboardingRequest;
import com.abcm.kyc.service.ui.dto.MerchantRoutingUiRequest;
import com.abcm.kyc.service.ui.dto.OfflineWalletRequest;
import com.abcm.kyc.service.ui.dto.ProductDTO;
import com.abcm.kyc.service.ui.dto.RoleAuthModelAdmin;
import com.abcm.kyc.service.ui.dto.RoutingResponseContainer;
import com.abcm.kyc.service.ui.dto.TxnReportDto;
import com.abcm.kyc.service.ui.dto.TxnReportRequest;
import com.abcm.kyc.service.ui.dto.UpdatePasswordModel;
import com.abcm.kyc.service.ui.payment.PaymentService;
import com.abcm.kyc.service.ui.repository.AppUserRepository;
import com.abcm.kyc.service.ui.repository.MerchantRepository;
import com.abcm.kyc.service.ui.repository.RoleRepository;
import com.abcm.kyc.service.ui.repository.TransactionHistoryRepo;
import com.abcm.kyc.service.ui.repository.WalletRepository;
import com.abcm.kyc.service.ui.repository.WalletRepository.WalletMerchantView;
import com.abcm.kyc.service.ui.service.AdminService;
import com.abcm.kyc.service.ui.util.ResponseUtil;
import com.abcmkyc.entity.AppUser;
import com.abcmkyc.entity.Credentials;
import com.abcmkyc.entity.MerchantProductRoute;
import com.abcmkyc.entity.Merchant_Master;
import com.abcmkyc.entity.Role;
import com.abcmkyc.entity.Wallet;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminServiceImpl implements AdminService {
	private final MerchantRepository merchantRepository;
	private final ResponseUtil responseUtil;
	private final PasswordEncoder passwordEncoder;
	private final RoleRepository repository;
	private static final Long ID = 2L;
	@PersistenceContext
	private EntityManager entityManager;
	private final TransactionHistoryRepo transactionHistoryRepo;
	private final WalletRepository walletRepository;
	private final KycReportApiClient apiClient;
	private final PaymentService paymentService; 
	
	private final AppUserRepository appUserRepository;
	
	

	@Override
	@Transactional
	public ApiResponseModel saveOrUpdateMerchant(MerchantKycOnboardingRequest dto) {
		try {
			log.info("Onboard merchant Dto Model: {}", dto.getProductRoutes());
			boolean isNewMerchant = dto.getId() <= 0;
			Merchant_Master merchant = isNewMerchant
					? new Merchant_Master()
							: merchantRepository.findById(dto.getId()).orElse(new Merchant_Master());
			if (isNewMerchant) {
				log.info("New Merchnat Onboard");
				try {
					merchant.setMid(generateNextMerchantMid());
				} catch (Exception e) {
					log.error("Error generating Merchant MID: {}");
					return new ApiResponseModel(500, "Failed to generate Merchant MID", null);
				}
			}
			// Set merchant basic fields
			merchant.setName(dto.getName());
			merchant.setEmail(dto.getEmail());
			merchant.setPhoneOne(dto.getPhone());
			merchant.setPhoneTwo(dto.getAlterNativePhone());
			merchant.setBusinessType(dto.getBusinessType());
			merchant.setWebsiteUrl(dto.getWebsiteUrl());
			merchant.setBuissnessAaddress(dto.getBusinessAddress());
			merchant.setCountry(dto.getCountry());
			merchant.setState(dto.getState());
			merchant.setCity(dto.getCity());
			merchant.setPincode(dto.getPincode());
			merchant.setIpAllowed(dto.getIpAllowed());
			if (merchant.getCredentials() == null) {
			    merchant.setCredentials(new Credentials());
			}
			if (isNewMerchant) {
			    merchant.getCredentials().setCreatedBy(dto.getCreatedBy());
			} else {
			    merchant.getCredentials().setModifiedBy(dto.getCreatedBy());
			}
			;
			// Handle credentials
			if (merchant.getCredentials() == null) {
				merchant.setCredentials(new Credentials());
			}
			if (dto.getUsername() == null || dto.getUsername().isBlank()) {
			    // Reuse existing username (no change)
			    dto.setUsername(merchant.getCredentials().getUsername());
			    
			} else {
			    // Use new username
			    merchant.getCredentials().setUsername(dto.getUsername());
			  
			}
			
			if (dto.getPassword() == null || dto.getPassword().isBlank()) {
			    // Reuse existing password (no change)
			    // Do not re-encode or change DB value
			    merchant.getCredentials().setPassword(merchant.getCredentials().getPassword());
			} else {
			    // Use new password (encode it)
			    merchant.getCredentials().setPassword(passwordEncoder.encode(dto.getPassword()));
			}

			merchant.getCredentials().setModifiedAt(LocalDateTime.now());
		
			// Set role
			Role role = repository.findById(ID).orElse(null);
			if (role == null) {
				return new ApiResponseModel(404, "Role not defined", null);
			}
			merchant.setRole(role);
			if (isNewMerchant) {
				merchant.setApiKey(generateApiKey());
				merchant.setAppId(generateAppId());
				
			}
			List<MerchantProductRoute> updatedRoutes = new ArrayList<>();
			
			Map<Long, MerchantProductRoute> existingRouteMap = merchant.getProductRoutes() != null
					? merchant.getProductRoutes().stream()
							.filter(route -> route.getProduct_id() != null)
							.collect(Collectors.toMap(MerchantProductRoute::getProduct_id, route -> route))
							: new HashMap<>();
			if (dto.getProductRoutes() != null) {
				for (MerchantKycOnboardingRequest.MerchantProductRoute routeDto : dto.getProductRoutes()) {
					MerchantProductRoute route;
					if (routeDto.getProductId() != 0 && existingRouteMap.containsKey(routeDto.getProductId())) {
						// Update existing route
						route = existingRouteMap.get(routeDto.getProductId());
						route.setModifiedAt(LocalDateTime.now());
				        route.setModifiedBy(dto.getCreatedBy()); // use createdBy as modifiedBy input
					} else {
						// New route
						route = new MerchantProductRoute();
						route.setCreatedAt(LocalDateTime.now());
						route.setCreatedBy(dto.getCreatedBy());
					}
					route.setProductName(routeDto.getProductName());
					route.setProduct_id(routeDto.getProductId());
					
					route.setMid(merchant.getMid());
					updatedRoutes.add(route);
				}
			}

			// 🟢 Fix: instead of setProductRoutes(), clear and add
			if (merchant.getProductRoutes() == null) {
				merchant.setProductRoutes(new ArrayList<>());
			} else {
				merchant.getProductRoutes().clear();
			}
			// maintain bi-directional mapping
			for (MerchantProductRoute route : updatedRoutes) {
				route.setMerchant(merchant);
			}
			merchant.getProductRoutes().addAll(updatedRoutes);
			merchant = merchantRepository.save(merchant);
			String message = isNewMerchant ? "MERCHANT_ONBOARD_SUCCESS" : "MERCHANT_UPDATE_SUCCESS";
			return responseUtil.build(message, merchant);

		}catch (Exception e) {
			//e.getMessage();
			log.info("The Onboard exception");
		}
		return null;
	}



	private String generateApiKey() {
		SecureRandom random = new SecureRandom();
		StringBuilder apiKey = new StringBuilder();
		for (int i = 0; i < 4; i++) {
			apiKey.append(
					random.ints(6, 0, 36)
					.mapToObj(i1 -> String.valueOf("ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".charAt(i1)))
					.collect(Collectors.joining())
					);
			if (i < 3)
				apiKey.append("-");
		}
		return apiKey.toString();
	}

	private String generateAppId() {
		return UUID.randomUUID().toString().replace("-", "").substring(0, 24);
	}

	@Override
	public ApiResponseModel allMerchnatDetails() {
		
	    try {
	    	log.info("Admin serviceimpl Inside All Merchant Details {} ");
	        List<Merchant_Master> allMerchants = merchantRepository.findAllByOrderByCredentialsCreatedAtDesc();
	        if (allMerchants.isEmpty()) {
	            return responseUtil.build("NOT_FOUND", null);
	        }

	        allMerchants.forEach(merchant -> {
	            if (merchant.getCredentials() != null) {
	                Credentials onlyCreatedAt = new Credentials();
	                onlyCreatedAt.setCreatedAt(merchant.getCredentials().getCreatedAt());                     onlyCreatedAt.setUsername(merchant.getCredentials().getUsername());	               
                     merchant.setCredentials(onlyCreatedAt);
	            }
	            merchant.setRole(null);
	            merchant.setProductRoutes(null);
	        });
	        return responseUtil.build("FETCH_SUCCESS", allMerchants);
	    } catch (Exception e) {
	        e.printStackTrace();
	        log.error("Error occurred while fetching all merchant details: ", e.getMessage());
	        return responseUtil.build("INTERNAL_ERROR");
	    }
	}



	@Override
	public ApiResponseModel fetchMerchantById(String id) {
		try {
			 log.info("Fetch Merchnat Data Get Admin Ui Service Method",id);
			Merchant_Master master = merchantRepository.getMerchantByMid(id);
			if (master != null) {
				return responseUtil.build("FETCH_SUCCESS", master);
			} else {
				return responseUtil.build("NOT_FOUND", null);
			}
		} catch (Exception e) {
			return responseUtil.build("INTERNAL_ERROR");
		}
	}

	@Override
	public ApiResponseModel deleteMerchantById(long id) {
		try {
			// Check if the merchant exists by ID
			Optional<Merchant_Master> merchantOptional = merchantRepository.findById(id);
			if (merchantOptional.isPresent()) {
				// Delete the merchant from the repository
				merchantRepository.deleteById(id);
				// Return success response
				return responseUtil.build("DELETE_MSG", null);
			} else {
				// Return not found response if merchant doesn't exist
				return responseUtil.build("NOT_FOUND", null);
			}
		} catch (Exception e) {
			// Handle any errors that occur during deletion
			return responseUtil.build("INTERNAL_ERROR", null);
		}
	}

	@Override
	@Transactional
	public void updateColumnByDynamicJPQL(RoleAuthModelAdmin authModelAdmin) {
		log.info("Inside Admin updateColumnByDynamicJPQL  method Invoke{}:"+authModelAdmin);
		// Entity name and column names
		String entityName = "Merchant_Master";
		String colmid = "mid";
		String colid = "id";
		String colusername = "credentials.username";
		String jpqlUpdate = "";
		Long id = Long.parseLong(authModelAdmin.getId());  // Convert String to Long
		if (authModelAdmin.getDataType().equalsIgnoreCase("Boolean")) {
			log.info("Inside Boolean Status Update");
			jpqlUpdate = "UPDATE " + entityName + " e SET e." + authModelAdmin.getColumnName() + " = " + authModelAdmin.getValue()
			+ " WHERE e." + colusername + " = '" + authModelAdmin.getUsername() + "'"
			+ " AND e." + colid + " = :id"
			+ " AND e." + colmid + " = '" + authModelAdmin.getMid() + "'";
		}
		else {
			jpqlUpdate = "UPDATE " + entityName + " e SET e." + authModelAdmin.getColumnName() + " = " + authModelAdmin.getValue()
			+ " WHERE e." + colusername + " = '" + authModelAdmin.getUsername() + "'"
			+ " AND e." + colid + " = :id"  // Use parameterized query for Long value
			+ " AND e." + colmid + " = '" + authModelAdmin.getMid() + "'";
		}

		//log.info("Jpa Query For Update"+jpqlUpdate);
		// Create and execute the query
		Query query = entityManager.createQuery(jpqlUpdate);  // Remove wildcard here
		query.setParameter("id", id);  // Set the Long type value for id

		//log.info("success:" + query);
		query.executeUpdate();  // Execute the update query
	}

	@Override
	public ApiResponseModel fetchMerchantById(long id) {
		try {
			log.info("Update Merchnat Data Get Admin Ui Service Method",id);
			Merchant_Master master = merchantRepository.findById(id).orElse(null);
			if (master != null) {
				return responseUtil.build("FETCH_SUCCESS", master);
			} else {
				return responseUtil.build("NOT_FOUND", null);
			}
		} catch (Exception e) {
			return responseUtil.build("INTERNAL_ERROR");
		}
	}




	private String generateNextMerchantMid() {
		String prefix = "K";
		int startingNumber = 10000;
		try {
			Integer maxMid = merchantRepository.findMaxMid(); // returns Integer
			log.info("Max MID fetched: " + maxMid);
			int numericPart = (maxMid == null || maxMid == 0) ? (startingNumber + 1) : (startingNumber + maxMid + 1);
			return prefix + numericPart;
		} catch (Exception e) {
			log.error("Error generating next MID");
			return null;
		}
	}




	@Override
	public List<Merchant_Master> findAll() {
		try
		{
			return merchantRepository.findAll();
		}catch (Exception e) {
		}
		return null;
	}




	@Override
	@Transactional(readOnly = true)
	public ApiResponseModel FetchWalletReport(TxnReportRequest txnReportRequest) {
		try {
			log.info("admin seviceimpl Wallet  Report{}",txnReportRequest);
			final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy");
			LocalDate fromDate = LocalDate.parse(txnReportRequest.getFromDate(), DATE_FORMATTER);
			LocalDate toDate = LocalDate.parse(txnReportRequest.getToDate(), DATE_FORMATTER);
			List<WalletRepository.WalletMerchantView> walletList;
			if (txnReportRequest.getMerchantId().equalsIgnoreCase("all")) {
				walletList = walletRepository.findByTxnDateBetween(
						fromDate.atStartOfDay(),
						toDate.atTime(23, 59, 59)
						);
			} 
			
			
			else {
				walletList = walletRepository.findByMerchantIdAndTxnDateBetween(
						txnReportRequest.getMerchantId(),
						fromDate.atStartOfDay(),
						toDate.atTime(23, 59, 59)
						);
			}
			
			return walletList.isEmpty()
					? responseUtil.build("NOT_FOUND", null)
							: responseUtil.build("FETCH_SUCCESS", walletList);

		} catch (DateTimeParseException e) {
			return responseUtil.build("INVALID_DATE_FORMAT", null);
		}
	}

	

	@Override
	@Transactional(readOnly = true)
	public ApiResponseModel FetchTxnReport(TxnReportRequest req) {
		
	    try {
	    	log.info("Admin Service Impl Inside Txn Report Method{}",req);
	        DateTimeFormatter DF = DateTimeFormatter.ofPattern("dd-MM-yyyy");
	        LocalDate from = LocalDate.parse(req.getFromDate(), DF);
	        LocalDate to   = LocalDate.parse(req.getToDate(),   DF);
	        LocalDateTime start = from.atStartOfDay();
	        LocalDateTime end   = to.atTime(23, 59, 59);
	        // 1) Determine wallet IDs to scan
	        List<WalletRepository.WalletMerchantView> wallets;
	        String mid = req.getMerchantId();
	        if (mid == null || mid.isBlank() || mid.equalsIgnoreCase("all")) {
	            wallets = walletRepository.findAllWalletData();
	        } else {
	            wallets = walletRepository.findAllWalletDataByMerchant(mid);
	        }

	        if (wallets.isEmpty()) {
	            return responseUtil.build("NOT_FOUND", null);
	        }

	        List<Long> walletIds = wallets.stream()
	                                      .map(WalletMerchantView::getWalletId)
	                                      .toList();
	        // 2) One-shot fetch of all txns
	        List<TxnReportDto> report = 
	            transactionHistoryRepo.findReportForWallets(walletIds, start, end);

	        if (report.isEmpty()) {
	            return responseUtil.build("NOT_FOUND", null);
	        }
	        return responseUtil.build("FETCH_SUCCESS", report);

	    } catch (DateTimeParseException ex) {
	        return responseUtil.build("INVALID_DATE_FORMAT", null);
	    }
	}


	@Override
	@Transactional(readOnly = true)
	public ApiResponseModel WalletBalanceCount(String merchantId) {
		try {
			log.info("Admin  Service Impl Inside Wallet Balance Count{} ",merchantId);
			
			BigDecimal walletCount = walletRepository.getKycWalletSummary(merchantId);
			if (walletCount == null) {

				return createResponse(HttpStatus.NOT_FOUND, "No wallet data found for the specified merchant", null);
			}
			return responseUtil.build("FETCH_SUCCESS", walletCount);

		} catch (DataAccessException dae) {

			return createResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Error accessing wallet data", null);
		} catch (Exception e) {
			return createResponse(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred", null);
		}
	}
	private ApiResponseModel createResponse(HttpStatus status, String message, Object data) {
		return new ApiResponseModel(status.value(), message, data);
	}

	@Override
	@Transactional(readOnly = true)
	public ApiResponseModel ProductSubcribeCount(String merchantId) {
	    try {
	    	log.info("Admin  Service Impl Inside Product Subscribe Count{} ",merchantId);
	        if (merchantId == null || merchantId.trim().isEmpty()) {
	            merchantId = "";
	        }
	        int productSubscribeCount = merchantRepository.GetServiceCount(merchantId);
	        return responseUtil.build("FETCH_SUCCESS", productSubscribeCount);
	    } catch (DataAccessException dae) {
	        return responseUtil.build("INTERNAL_ERROR");
	    } catch (Exception e) {
	        return responseUtil.build("INTERNAL_ERROR");
	    }
	}





	@Override
	public ApiResponseModel kycCountReport(String fromDate, String toDate, String merchantId, String product) {
		try {
			String apiCountResponseModel = apiClient.kycCountApiCall(fromDate, toDate, merchantId, product);
			// Convert JSON string to a Map
			ObjectMapper mapper = new ObjectMapper();
			@SuppressWarnings("unchecked")
			Map<String, Object> parsedData = mapper.readValue(apiCountResponseModel, Map.class);
			return responseUtil.build("FETCH_SUCCESS", parsedData);
		} catch (Exception e) {
			System.out.println("Exception in kyc Count report before send: " + e.getMessage());
			return responseUtil.build("INTERNAL_ERROR");
		}
	}

	@Override
	@Transactional(readOnly = true)
	public List<ProductDTO> productList() {
		return apiClient.getProducts();
	}




	@Override
	public ApiResponseModel fetchProviderProductList(String merchantId) {
		ObjectMapper mapper = new ObjectMapper();
		try {
			String jsonResponse = apiClient.getProductRoutesByMerchantId(merchantId);
			Map<String, Object> responseMap = mapper.readValue(jsonResponse, new TypeReference<Map<String, Object>>() {});
			return new ApiResponseModel(200, "success", responseMap);
		} catch (JsonProcessingException e) {
			// Log the JSON parsing error clearly
			log.error("Failed to parse JSON response for merchantId {}: {}");
			return new ApiResponseModel(500, "JSON parse error: " + e.getMessage(), null);
		} catch (Exception e) {
			// Log unexpected exceptions
			log.error("Unexpected error fetching provider product list for merchantId {}: {}",e.getMessage());
			return new ApiResponseModel(500, "Internal error: " + e.getMessage(), null);
		}
	}





	@Override
	public ApiResponseModel saveMerchantRouteDetails(MerchantRoutingUiRequest request,HttpSession httpSession) {
	    try {
	    	
	        log.info("Merchant route save admin ui service method",request);
	        String username = (String) httpSession.getAttribute("username");
	        if (request.getRoutingDetails() != null) {
	            for (MerchantRoutingUiRequest.RoutingDetail detail : request.getRoutingDetails()) {
	                detail.setCreatedBy(request.getUsername());
	                detail.setUpdatedby(request.getUsername());
	                
	            }
	        }
	        String routeApiResponse = apiClient.saveMerchantRouting(request);
	        ObjectMapper mapper = new ObjectMapper();
	        Map<String, Object> dataMap = mapper.readValue(routeApiResponse, new TypeReference<Map<String, Object>>() {});
	        return new ApiResponseModel(200, "success", dataMap);
	    } catch (Exception e) {
	        log.error("exception in merchant route admin ui service: {}", e.getMessage(), e);
	        return new ApiResponseModel(500, "Internal server error", null);
	    }
	}





	@Override
	public ApiResponseModel routingUpdateDetails(String merchantId) {
		log.info("Admin Service Impl Inside Fetch Update Routing Details{} ",merchantId);
	    long startTime = System.currentTimeMillis();
	    RoutingResponseContainer.RoutingResponse responseDto = apiClient.fetchRoutingUpdateDetailsApiAsDto(merchantId);

	    if (responseDto == null || responseDto.data() == null) {
	        log.warn("No data found for merchantId: {}", merchantId);
	        return new ApiResponseModel(404, "No data found", null);
	    }

	    long endTime = System.currentTimeMillis();
	    log.info("routingUpdateDetails execution time: {} ms", (endTime - startTime));

	    return new ApiResponseModel(200, "success", responseDto.data());
	}





	@Override
	public List<MerchantIdMidNameProjection> getAllMerchantIdMidNames() {
		log.info("Admin Service Impl Inside All Merchant Id Mid Name Projection{} ");
		
		return merchantRepository.findAllMerchantsNative();
	}




	@Override
	public ApiResponseModel fetchFixedProviderProductRate(String clientId, String productId, String productRate) {
		log.info("Admin Service Impl Inside Fetch Fixed Provider Product Rate{} ",clientId,productId,productRate);
	    long convertedRate = 0L;
	    try {
	        if (productRate != null && !productRate.isBlank()) {
	            double rateDouble = Double.parseDouble(productRate);
	            convertedRate = Math.round(rateDouble * 100);
	        }
	    } catch (NumberFormatException e) {
	        // If parsing fails, treat as invalid input
	        return new ApiResponseModel(400, "Invalid product rate format", null);
	    }
	    String convertedRateStr = String.valueOf(convertedRate);

	    return Optional.ofNullable(apiClient.checkProductRate(clientId, productId, convertedRateStr))
	        .filter(response -> !response.isBlank())
	        .map(response -> new ApiResponseModel(200, "Success", response))
	        .orElseGet(() -> new ApiResponseModel(400, "Failed to fetch fixed provider product rate", null));
	}





	@Override
	public ApiResponseModel offlineBalanceAdd(OfflineWalletRequest offlineWalletRequest) {
		log.info("Merchnat Offline Balance mode Service Method Hit{}",offlineWalletRequest);
		 
		ApiResponseModel apiResponseModel=paymentService.offlineBalanceAdd(offlineWalletRequest);
		return apiResponseModel;
	}




	@Override
	public ApiResponseModel getWalletBalance(String merchantId) {
		log.info("Retrieving wallet balance for merchant ID: {}", merchantId);
	    long balance = walletRepository.findByMerchantId(merchantId)
	            .map(Wallet::getBalance)
	            .orElse(0L);
	    
	    log.info("Merchnat Balance is"+balance);
	    return new ApiResponseModel(200, "Wallet balance fetched successfully", balance);
	}



	
	@Override
	@Transactional(readOnly = true)
	public ApiResponseModel FetchWalletReportDashboard(String merchantId) {
	    log.info("Wallet  Dasboard Report{}",merchantId);

	    List<WalletRepository.WalletMerchantView> walletList;
	    if (merchantId != null && !merchantId.isBlank()) {
	        walletList = walletRepository.findAllWalletDataByMerchant(merchantId);
	    } else {
	        // when merchantId is null or blank
	        walletList = walletRepository.findAllWalletData();
	    }

	    return walletList.isEmpty()
	        ? responseUtil.build("NOT_FOUND", null)
	        : responseUtil.build("FETCH_SUCCESS", walletList);
	}



	@Override
	public boolean updateUserPassword(UpdatePasswordModel updatePasswordModel) {
		log.info("Update Password method"+updatePasswordModel);
		Optional<AppUser> appUser = appUserRepository.findByCredentialsUsername(updatePasswordModel.getUsername());
		if(!appUser.isPresent())
		{
			return false;
		}
		if(!updatePasswordModel.getNewPassword().equalsIgnoreCase(updatePasswordModel.getConfirmNewPassword()))
		{
			return false;
		}
		AppUser user=appUser.get();
		String password=passwordEncoder.encode(updatePasswordModel.getConfirmNewPassword());
		user.getCredentials().setPassword(password);
		appUserRepository.save(user);
		return true;
		
		
	}



	@Override
	public List<MerchantDTO> getEsignMerchants() {
	    try {
	        return merchantRepository.fetchEsignMerchants().stream()
	                .map(row -> new MerchantDTO(
	                        (String) row[0], // merchantId as String
	                        (String) row[1]  // merchantName as String
	                ))
	                .collect(Collectors.toList());
	    } catch (Exception e) {
	        log.error("Exception in Esign Report: {}", e.getMessage(), e);
	    }
	    return List.of(); // return empty list instead of null
	}




	
	
	
	






}
