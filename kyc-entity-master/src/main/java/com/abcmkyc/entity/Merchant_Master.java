 package com.abcmkyc.entity;


import java.util.ArrayList;
import java.util.List;

import com.abcmkyc.Enum.AadharOkyc;
import com.abcmkyc.Enum.Apps;
import com.abcmkyc.Enum.Billing;
import com.abcmkyc.Enum.DRIVING_LICENSE;
import com.abcmkyc.Enum.Dashboard;
import com.abcmkyc.Enum.Esign;
import com.abcmkyc.Enum.GstLit;
import com.abcmkyc.Enum.KycReport;
import com.abcmkyc.Enum.PanPro;
import com.abcmkyc.Enum.Product;
import com.abcmkyc.Enum.VoterId;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Table(name = "merchant_master")
@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Merchant_Master {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private long id;

	@Column(name = "mid", length = 100, unique = true, nullable = false)
	private String mid;

	@Column(name = "name", length = 999)
	private String name;

	/* new Column */
	@Column(name = "source", length = 50)
	private String source;

	@Column(name = "status", columnDefinition = "BOOLEAN DEFAULT FALSE")
	private boolean status;

	@Column(name = "mode", length = 50)
	private String mode;

	@Column(name = "rate", length = 50)
	private long rate;

	@Column(name = "email", length = 100)
	private String email;

	@Column(name = "service_providername", length = 100)
	private String serviceProviderName;

	@Column(name = "website_url", length = 500)
	private String websiteUrl;

	@Column(name = "buissness_aaddress", length = 1000)
	private String buissnessAaddress;

	@Column(name = "country", length = 100)
	private String country;

	@Column(name = "state", length = 100)
	private String state;

	@Column(name = "city", length = 100)
	private String city;

	@Column(name = "pincode", length = 100)
	private String pincode;

	@Column(name = "phone_one", length = 100)
	private String phoneOne;

	@Column(name = "phone_two", length = 100)
	private String phoneTwo;

	@Column(name = "business_type", length = 100)
	private String businessType;






	@Embedded
	private Credentials credentials;

	@ManyToOne
	private Role role;

	@Column(length = 8, columnDefinition = "varchar(10) default 'DESABLE'")
	@Enumerated(EnumType.STRING)
	private Dashboard dashboard = Dashboard.DESABLE;

	@Column(length = 8, columnDefinition = "varchar(10) default 'DESABLE'")
	@Enumerated(EnumType.STRING)
	private Billing billing = Billing.DESABLE;

	@Column(length = 8, columnDefinition = "varchar(10) default 'DESABLE'")
	@Enumerated(EnumType.STRING)
	private Product product = Product.DESABLE;

	@Column(length = 8, columnDefinition = "varchar(10) default 'DESABLE'")
	@Enumerated(EnumType.STRING)
	private Apps apps = Apps.DESABLE;

	@Column(length = 8, columnDefinition = "varchar(10) default 'DESABLE'")
	@Enumerated(EnumType.STRING)
	private AadharOkyc aadharOkyc = AadharOkyc.DESABLE;

	@Column(length = 8, columnDefinition = "varchar(10) default 'DESABLE'")
	@Enumerated(EnumType.STRING)
	private PanPro panPro = PanPro.DESABLE;

	@Column(length = 8, columnDefinition = "varchar(10) default 'DESABLE'")
	@Enumerated(EnumType.STRING)
	private GstLit gstLit = GstLit.DESABLE;

	@Column(length = 8, columnDefinition = "varchar(10) default 'DESABLE'")
	@Enumerated(EnumType.STRING)
	private KycReport kycReport = KycReport.DESABLE;
	
	
	@Column(length = 8, columnDefinition = "varchar(10) default 'DESABLE'")
	@Enumerated(EnumType.STRING)
	private DRIVING_LICENSE drivingLicense = DRIVING_LICENSE.DESABLE;

	
	
	@Column(length = 8, columnDefinition = "varchar(10) default 'DESABLE'")
	@Enumerated(EnumType.STRING)
	private VoterId voterId  = VoterId.DESABLE;
	

	@Column(length = 8, columnDefinition = "varchar(10) default 'DESABLE'")
	@Enumerated(EnumType.STRING)
	private Esign  esign  = Esign.DESABLE;


	@Column(name = "api_key", length = 100)
	private String apiKey;

	@Column(name = "app_id", length = 100)
	private String appId;
	
	
	@Column(name = "ip_allowed", length = 1000, nullable = true)
	private String ipAllowed;


	@OneToMany(mappedBy = "merchant", cascade = CascadeType.ALL, fetch =
			FetchType.EAGER)
	@JsonIgnore
	
	private List<MerchantRouting> providerRoutings;

	@OneToMany(mappedBy = "merchant", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<MerchantProductRoute> productRoutes = new ArrayList<>();







}
