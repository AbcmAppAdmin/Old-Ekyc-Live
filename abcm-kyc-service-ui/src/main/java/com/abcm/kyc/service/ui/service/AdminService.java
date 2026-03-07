package com.abcm.kyc.service.ui.service;

import java.util.List;

import com.abcm.kyc.service.ui.dto.ApiResponseModel;
import com.abcm.kyc.service.ui.dto.MerchantDTO;
import com.abcm.kyc.service.ui.dto.MerchantIdMidNameProjection;
import com.abcm.kyc.service.ui.dto.MerchantKycOnboardingRequest;
import com.abcm.kyc.service.ui.dto.MerchantRoutingUiRequest;
import com.abcm.kyc.service.ui.dto.OfflineWalletRequest;
import com.abcm.kyc.service.ui.dto.ProductDTO;
import com.abcm.kyc.service.ui.dto.RoleAuthModelAdmin;
import com.abcm.kyc.service.ui.dto.TxnReportRequest;
import com.abcm.kyc.service.ui.dto.UpdatePasswordModel;
import com.abcmkyc.entity.Merchant_Master;

import jakarta.servlet.http.HttpSession;



public interface AdminService {
	/*
	 * Here All Service Define For Admin Pannel
	 * */
	
	public ApiResponseModel saveOrUpdateMerchant(MerchantKycOnboardingRequest merchantKycOnboardingRequest);

	public ApiResponseModel allMerchnatDetails();

	public ApiResponseModel fetchMerchantById(String id);

	public ApiResponseModel deleteMerchantById(long id);
	
	public void updateColumnByDynamicJPQL(RoleAuthModelAdmin authModelAdmin);
	
	
	public ApiResponseModel fetchMerchantById(long id);
	
	public List<Merchant_Master>findAll();
	
	public ApiResponseModel FetchWalletReport(TxnReportRequest txnReportRequest);
	
	public ApiResponseModel FetchWalletReportDashboard(String merchnatId);
	
	
	public ApiResponseModel FetchTxnReport(TxnReportRequest txnReportRequest);
	
	
	public ApiResponseModel WalletBalanceCount(String merchantId);

	public ApiResponseModel ProductSubcribeCount(String merchantId);

	public ApiResponseModel kycCountReport(String fromDate, String toDate, String merchantId, String product);
	
	public List<ProductDTO>productList();

	public ApiResponseModel fetchProviderProductList(String merchant_id);

	public ApiResponseModel saveMerchantRouteDetails(MerchantRoutingUiRequest request,HttpSession httpSession);

	public ApiResponseModel routingUpdateDetails(String merchant_id);

	public List<MerchantIdMidNameProjection> getAllMerchantIdMidNames();

	public ApiResponseModel fetchFixedProviderProductRate(String clientId, String productId, String productRate);

	public ApiResponseModel offlineBalanceAdd(OfflineWalletRequest offlineWalletRequest);

	public ApiResponseModel getWalletBalance(String merchantId);

	public boolean updateUserPassword(UpdatePasswordModel updatePasswordModel);
	
	
	public List<MerchantDTO> getEsignMerchants();

	
	
	

	
	
	

}
