package com.abcm.kyc.service.ui.dto;


import java.util.List;

import lombok.Data;

@Data
public class MerchantKycOnboardingRequest {
    private long id;
	private String name;
	private String email;
	private String phone;
	private String alterNativePhone;
	private String businessType;
	private String websiteUrl;
	private String businessAddress;
	private String country;
	private String state;
	private String city;
	private String pincode;
	public String aaddharRate;
	public String panRate;
	public String gstRate;
	public String GstadvanceRate;
	private String username;
	private String password;
	private String createdBy;
	private String ipAllowed;
	
	 private List<MerchantProductRoute> productRoutes;
	
	 @Data
	public static class MerchantProductRoute
	{
		
		private long productId;
		private String productName;
		private String createdBy;
		private String modifiedBy;
		
	}

}
