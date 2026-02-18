package com.abcm.kyc.service.ui.restcall;







import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.abcm.kyc.service.ui.dto.ApiResponseModel;
import com.abcm.kyc.service.ui.dto.MerchantKycOnboardingRequest;
import com.abcm.kyc.service.ui.dto.MerchantRoutingUiRequest;
import com.abcm.kyc.service.ui.dto.OfflineWalletRequest;
import com.abcm.kyc.service.ui.dto.RoleAuthModelAdmin;
import com.abcm.kyc.service.ui.dto.TxnReportRequest;
import com.abcm.kyc.service.ui.dto.UpdatePasswordModel;
import com.abcm.kyc.service.ui.service.AdminService;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@RestController
@RequestMapping("/app/public/")
@RequiredArgsConstructor
@Slf4j
public class AdminRestController {
	
	private final AdminService adminService;
	
	@PostMapping("onboardMerchant")
	public ResponseEntity<ApiResponseModel> merchantOnboard(
			
			@RequestBody MerchantKycOnboardingRequest merchantKycOnboardingRequest) {
		
		log.info("Admin Rest Controller Inside Merchant Onboard {} ",merchantKycOnboardingRequest.toString());
		ApiResponseModel response = adminService.saveOrUpdateMerchant(merchantKycOnboardingRequest);
		return ResponseEntity.ok(response);
	}

	@GetMapping("allMerchnat")
	public ResponseEntity<ApiResponseModel> allMerchnat() {
		log.info("Admin  Rest Controller Inside All Merchant Details {} ");
		ApiResponseModel response = adminService.allMerchnatDetails();
		return ResponseEntity.ok(response);
	}

	@GetMapping("fetchById")
	public ResponseEntity<ApiResponseModel> fetchMerchnatById(@RequestParam(value = "mid") String mid) {
		log.info("Admin Rest Controller Inside Fetch Merchant By Id {} ", mid);
		ApiResponseModel response = adminService.fetchMerchantById(mid);
		return ResponseEntity.ok(response);
	}

	@DeleteMapping("DeleteById")
	public ResponseEntity<ApiResponseModel> deleteMerchantById(@RequestParam(value = "id") long id) {
		log.info("Admin Rest Controller Inside Delete Merchant By Id {} ", id);
		ApiResponseModel response = adminService.deleteMerchantById(id);
		return ResponseEntity.ok(response);
	}

	@PostMapping("update-authstatus")
	public ResponseEntity<String> updateAuthnticationstatus(@RequestBody RoleAuthModelAdmin request) {
		try {
			log.info("Admin Rest Controller Inside Update Authntication Status {} ", request.toString());
			adminService.updateColumnByDynamicJPQL(request);
			return ResponseEntity.ok("Authntication Update Successfully");
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to add balance: " + e.getMessage());
		}
	}




	@PostMapping("wallet-report")
	public ResponseEntity< ApiResponseModel>FetchWalletReport(@RequestBody TxnReportRequest reportRequest)
	{
		

		log.info("Admin controller inside wallter report{} ",reportRequest.toString());
		return new ResponseEntity<ApiResponseModel>(adminService.FetchWalletReport(reportRequest),HttpStatus.OK);

	}
	
	
	
	@GetMapping("wallet-dashboard-report")
	public ResponseEntity< ApiResponseModel>FetchWalletReport( @RequestParam(value = "merchant_id",required = false) String merchant_id)
	{
		log.info("Admin controller  wallet-dashboard-report inside wallter report{} ",merchant_id);
		return new ResponseEntity<ApiResponseModel>(adminService.FetchWalletReportDashboard(merchant_id),HttpStatus.OK);

	}




	@PostMapping("txn-report")
	public ResponseEntity< ApiResponseModel>FetchTxnReport(@RequestBody TxnReportRequest reportRequest)
	{
		log.info("Admin Controller Inside Txn Report{} ",reportRequest.toString());
		return new ResponseEntity<ApiResponseModel>(adminService.FetchTxnReport(reportRequest),HttpStatus.OK);

	}



	@GetMapping("wallet-balance-count")
	public ResponseEntity<ApiResponseModel>WalletBalanceCount(@RequestParam(value = "merchantId") String merchantId)
	{
		log.info("Admin  Rest Controller Inside Wallet Balance Count{} ",merchantId);
		return new  ResponseEntity<ApiResponseModel>(adminService.WalletBalanceCount(merchantId),HttpStatus.OK);


	}


	@GetMapping("product-subscribe-count")
	public ResponseEntity<ApiResponseModel>ProductSubcribeCount(@RequestParam(value = "merchantId") String merchantId)
	{
		log.info("Admin  Rest Controller Inside Product Subscribe Count{} ",merchantId);
		return new  ResponseEntity<ApiResponseModel>(adminService.ProductSubcribeCount(merchantId),HttpStatus.OK);


	}


	
	@GetMapping("kyc-count-report")
	public ResponseEntity<ApiResponseModel> kycCountReport(
	        @RequestParam String fromDate,
	        @RequestParam String toDate,
	        @RequestParam String merchantId,
	        @RequestParam String product) {
		
		log.info("KYC Count Report fromDate: {}, toDate: {}, merchantId: {}, product: {}", fromDate, toDate, merchantId, product);

	    return new ResponseEntity<>(adminService.kycCountReport(fromDate, toDate, merchantId, product), HttpStatus.OK);
	}
	@GetMapping("provider-product-list/{merchant_id}")
	public ResponseEntity<ApiResponseModel>fetchProviderProductList(@PathVariable(value = "merchant_id") String merchant_id)
	{
	
		log.info("Provider Product List"+merchant_id);
		return new ResponseEntity<>(adminService.fetchProviderProductList(merchant_id), HttpStatus.OK);
 
		
	}
	
	
	@PostMapping("save-merchant-route")
	public ResponseEntity<ApiResponseModel>saveMerchantRoute(@RequestBody MerchantRoutingUiRequest request,HttpSession httpSession)
	{
		log.info("Admin Rt  Controller Inside Save Merchant Route Details{} ",request.toString());
		
		return new  ResponseEntity<ApiResponseModel>(adminService.saveMerchantRouteDetails(request,httpSession),HttpStatus.OK);
	}
	
	
	
	@GetMapping("fetchUpdateRoutingDetails")
	public ResponseEntity<ApiResponseModel>fetchUpdateRoutDetails(@RequestParam String merchant_id)
	{
		log.info("Admin Rest  Controller Inside Fetch Update Routing Details{} ",merchant_id);
		return new  ResponseEntity<ApiResponseModel>(adminService.routingUpdateDetails(merchant_id),HttpStatus.OK);

	}
	
	@GetMapping("fetch-product-rate")
	public ResponseEntity<ApiResponseModel>fetchFixedProductProviderRate( @RequestParam String clientId,
	        @RequestParam String productId,
	        @RequestParam String productRate)
	{
		log.info("Admin Rest  Controller Inside Fetch Fixed Product Provider Rate{} ",clientId,productId,productRate);
	    ApiResponseModel response = adminService.fetchFixedProviderProductRate(clientId, productId, productRate);

		return new ResponseEntity<ApiResponseModel>(response,HttpStatus.OK);
	}
	
	
	
	@PostMapping("add-merchant-balance")
	public ResponseEntity<ApiResponseModel>addMerchantBalance(@RequestBody OfflineWalletRequest offlineWalletRequest)
	{
		log.info("Merchnat Offline Balance mode Controller Method Hit{}");
		ApiResponseModel apiResponseModel=adminService.offlineBalanceAdd(offlineWalletRequest);
		return new ResponseEntity<ApiResponseModel>(apiResponseModel,HttpStatus.OK);
		
	}
	
	
	@GetMapping("merchant-existing-balance")
	public ResponseEntity<ApiResponseModel> getMerchantExistingBalance(@RequestParam("merchantId") String merchantId) {
	    try {
	    	log.info("Retrieving existing balance for merchant ID: {}", merchantId);
	        return new ResponseEntity<ApiResponseModel>(adminService.getWalletBalance(merchantId), HttpStatus.OK);  
	    } catch (Exception e) {
	        log.error("Error retrieving existing balance for merchant ID: {}", merchantId);
	        return new ResponseEntity<ApiResponseModel>(adminService.getWalletBalance(merchantId), HttpStatus.BAD_REQUEST);
	    }
		
	}
	
	
	@PostMapping("user-change-password")
	public ResponseEntity<?>changeAdminPassword(@RequestBody UpdatePasswordModel updatePasswordModel)
	{
		try
		{ 
			 log.info("Admin Rest Controller Inside Change User Password",updatePasswordModel.toString());
			boolean updateUserResponse=adminService.updateUserPassword(updatePasswordModel);
			 if (updateUserResponse) {
			        return ResponseEntity.ok(
			            new ApiResponseModel(200, " User Password Update successfully.", null)
			        );
			    } else {
			        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
			            new ApiResponseModel(400, "User Not Found or password mismatch.", null)
			        );
			    }
		}catch (Exception e) {
			 return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
			            new ApiResponseModel(500, "internal server error", null));
			 //return new ResponseEntity<ApiResponseModel>(adminService.updateUserPassword(updatePasswordModel), HttpStatus.OK);
		}
		
		
		
		
         

		
	}
	
	
	
	
	




}
