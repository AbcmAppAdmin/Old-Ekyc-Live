package com.abcm.kyc.service.ui.admin;

import java.nio.charset.StandardCharsets;
import java.security.Principal;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.abcm.kyc.service.ui.config.SecurityUtil;
import com.abcm.kyc.service.ui.dto.AddBalanceRequest;
import com.abcm.kyc.service.ui.dto.ApiResponseModel;
import com.abcm.kyc.service.ui.dto.ProductDTO;
import com.abcm.kyc.service.ui.payment.PaymentService;
import com.abcm.kyc.service.ui.service.AdminService;
import com.abcm.kyc.service.ui.service.OnboardClientService;
import com.abcmkyc.entity.MerchantProductRoute;
import com.abcmkyc.entity.Merchant_Master;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping("/app/admin")
@Slf4j
@RequiredArgsConstructor
public class AdminController {

	

	@Autowired
	private PaymentService paymentService;


	private final  OnboardClientService onboardclient;


	private final AdminService adminService;
	
	 
	
	

	@GetMapping("/dashboard")
	public String showAdminDashboard(@ModelAttribute("authUser") SecurityUtil.AuthenticatedUser authUser, Model model) {
		try {
			
			log.info("Admin Dashboard loading...", authUser != null ? authUser.getUsername() : "No Authenticated User");
			return Optional.ofNullable(authUser).map(user -> {
				log.info("Welcome to Admin Dashboard kyc: {}", user.getUsername());
				model.addAttribute("merchantList",adminService.getAllMerchantIdMidNames());
				return "Admin/Dashboard";
			}).orElse("redirect:/login");
		}catch (Exception e) {
			return "redirect:/login";
		}
		
		
	}



	@GetMapping("/billing")
	public String billingView(Principal principal, Model model, HttpServletRequest request) {
		try {
			log.info("Billing loading...", principal != null ? principal.getName() : "No Principal");
			if (principal == null) {
				return "redirect:/login";
			}
			model.addAttribute("merchantList", adminService.findAll());
			return "Admin/Billing";
		}catch (Exception e) {
			return"redirect:/login";
		}
		
	}
	
	
	
	@GetMapping("/merchantRoutingView")
	public String merchantRoutingView(Principal principal, Model model, HttpServletRequest request,
		    @RequestParam(value = "mid", required = false) String mid,@RequestParam(value = "name", required = false) String name) {
		try {
			log.info("Merchant Routing loading ...", principal != null ? principal.getName() : "No Principal",mid,name);
			if (principal == null) {
				return "redirect:/login";
			}
		    String Mid = new String(Base64.getUrlDecoder().decode(mid), StandardCharsets.UTF_8);
		    String Name = new String(Base64.getUrlDecoder().decode(name), StandardCharsets.UTF_8);
			model.addAttribute("mid",Mid);
			model.addAttribute("Name",Name);
			model.addAttribute("username", principal.getName());
			return "Admin/MerchantRouting";
		}catch (Exception e) {
			return"redirect:/login";
		}
		
	}


	@GetMapping("/Apps")
	public String AppsView(@ModelAttribute("authUser") SecurityUtil.AuthenticatedUser authUser, Model model) {
		try {
			log.info("AppsView loading...", authUser != null ? authUser.getUsername() : "No Authenticated User");
			return Optional.ofNullable(authUser).map(user -> {
				// model.addAttribute("username", user.getUsername());
				return "Admin/Apps";
			}).orElse("redirect:/login");
		}catch (Exception e) {
			return"redirect:/login";
		}
		
	}

	@GetMapping("/products")
	public String productsView(@ModelAttribute("authUser") SecurityUtil.AuthenticatedUser authUser, Model model) {
		try {
			log.info("ProductsView loading...", authUser != null ? authUser.getUsername() : "No Authenticated User");
			return Optional.ofNullable(authUser).map(user -> {
				 List<ProductDTO> productList=adminService.productList();
				 log.info("Product MAster"+productList);
				 
				 model.addAttribute("productList", productList);
				return "Admin/Product";
			}).orElse("redirect:/login");
		}catch (Exception e) {
			return"redirect:/login";
		}
		
	}
	
	
	
	@GetMapping("/ekycDemo")
	public String ekycDemo(@ModelAttribute("authUser") SecurityUtil.AuthenticatedUser authUser, Model model) {
	    try {
	        log.info("Loading ekycDemo page for user: {}", authUser != null ? authUser.getUsername() : "No Authenticated User");
	        return (authUser != null) ? "Admin/ekyDemo" : "redirect:/login";
	    } catch (Exception e) {
	        log.error("Error loading ekycDemo page", e);
	        return "redirect:/login";
	    }
	}

	@GetMapping("/merchantOnboard")
	public String merchantOnboardView(@ModelAttribute("authUser") SecurityUtil.AuthenticatedUser authUser,
			Model model) {
		try {
			log.info("merchantOnboardView loading...", authUser != null ? authUser.getUsername() : "No Authenticated User");
			return Optional.ofNullable(authUser).map(user -> {
				log.info("merchantOnboardView: {}", user.getUsername());
				log.info("Product List {}"+adminService.productList());
				model.addAttribute("productList", adminService.productList());
				model.addAttribute("username", user.getUsername());
				return "Admin/MerchantOnboard";
			}).orElse("redirect:/login");
		}catch (Exception e) {
			return"redirect:/login";
		}
		
	}

	@GetMapping("/clientOnboard")
	public String clientOnboardView(@ModelAttribute("authUser") SecurityUtil.AuthenticatedUser authUser, Model model) {
		try {
			return Optional.ofNullable(authUser).map(user -> {
				log.info("Welcome to Admin Dashboard: {}", user.getUsername());
				return "Admin/ClientOnboard";
			}).orElse("redirect:/login");
		}catch (Exception e) {
			return"redirect:/login";
		}
	
	}

	@GetMapping("/allReport")
	public String allReportView(@ModelAttribute("authUser") SecurityUtil.AuthenticatedUser authUser, Model model) {
		try {
			log.info("allReportView loading...", authUser != null ? authUser.getUsername() : "No Authenticated User");
			return Optional.ofNullable(authUser).map(user -> {
				model.addAttribute("username", authUser.getUsername());
				return "Admin/Report";
			}).orElse("redirect:/login");
		}catch (Exception e) {
			return "redirect:/login";
		}
		
	}

	@GetMapping("/Authntication")
	public String AuthnticationView(@ModelAttribute("authUser") SecurityUtil.AuthenticatedUser authUser, Model model) {
		try {
			log.info("AuthnticationView loading...", authUser != null ? authUser.getUsername() : "No Authenticated User");
			return Optional.ofNullable(authUser).map(user -> {
				return "Admin/Authntication";
			}).orElse("redirect:/login");
		}catch (Exception e) {
			log.error("Error during authentication view loading");
	        return "redirect:/login";
		}
		
	}

	@PostMapping("processToPayRequest")
	public String processPayment(@ModelAttribute AddBalanceRequest addBalanceRequest, HttpServletRequest request) {
		try {
			
			//log.info("FeeCollectionRequest: " + addBalanceRequest);
			return paymentService.processPayment(addBalanceRequest, request);
		} catch (Exception e) {
			log.error("Error processing payment: " + e.getMessage());
			e.printStackTrace(); 
			return "errorpage"; 
		}
	}

	@PostMapping("/respurl")
	public String paymentResponsePage(HttpServletRequest request) {
		try {
			return "paymentresponse";
		} catch (Exception e) {
			log.error("Error processing payment response: ");
			return "errorpage";
		}
	}






	@GetMapping("/updateMerchant/{id}")
	public String AuthnticationView(@ModelAttribute("authUser") SecurityUtil.AuthenticatedUser authUser,
	                                @PathVariable("id") String id,
	                                Model model) {
	    try {
	    	log.info("Update Merchant loading...",id, authUser != null ? authUser.getUsername() : "No Authenticated User");
	        return Optional.ofNullable(authUser)
	            .map(user -> {
	                log.info("Welcome to Admin Dashboard: {}", user.getUsername());
	                ApiResponseModel response = adminService.fetchMerchantById(Long.parseLong(id));
	                log.info("Fetched merchant: {}", response);
	                Merchant_Master merchant = (Merchant_Master) response.getData();
	                List<MerchantProductRoute> productRoutes = merchant.getProductRoutes();
	                List<ProductDTO> productList=adminService.productList();
	                model.addAttribute("merchant", merchant);
	            	model.addAttribute("productList", productList);
	                System.out.println("Product rout");
	                model.addAttribute("productRoutes", productRoutes);
	                model.addAttribute("username", user.getUsername());
	                return "Admin/UpdateMerchant";
	            })
	            .orElse("redirect:/login");
	    } catch (Exception e) {
	        return "redirect:/login";
	    }
	}




	@GetMapping("/updateClientMaster/{id}")
	public String updateClientMaster(@ModelAttribute("authUser") SecurityUtil.AuthenticatedUser authUser, 
			@PathVariable("id") String id, 
			Model model) {
		return Optional.ofNullable(authUser)
				.map(user -> {
					log.info("Welcome to Admin Dashboard: {}", user.getUsername());
					ApiResponseModel response = onboardclient.findbyId(Long.parseLong(id));
					//log.info("Fetched merchant: {}", response);
					
					model.addAttribute("ClientMaster", response.getData());
					return "Admin/UpdateClientOnboard";
				})
				.orElse("redirect:/login");
	}



	@GetMapping("/kycReport")
	public String KycReportView(@ModelAttribute("authUser") SecurityUtil.AuthenticatedUser authUser, Model model) {
		try {
			return Optional.ofNullable(authUser).map(user -> {
				log.info("Welcome to Admin kycReport Controller: {}", user.getAuthorities());
				model.addAttribute("merchantList", adminService.findAll());
				return "Admin/AllReport";
			}).orElse("redirect:/login");
		}catch (Exception e) {
			return "redirect:/login";
		}
		
	}
	
	
	@GetMapping("/kycCountReport")
	public String kycCountReportView(@ModelAttribute("authUser") SecurityUtil.AuthenticatedUser authUser, Model model) {
		try {
			log.info("kycCountReportView loading...", authUser != null ? authUser.getUsername() : "No Authenticated User");
			return Optional.ofNullable(authUser).map(user -> {
				//log.info("Welcome to Admin kycReport Controller: {}", user.getAuthorities());
				//log.info("Welcome to Admin kycReport Controller: {}", user.getUsername());
				model.addAttribute("merchantList", adminService.getAllMerchantIdMidNames());
				return "Admin/TxnCountReport";
			}).orElse("redirect:/login");
		}catch (Exception e) {
			return "redirect:/login";
		}
		
	}
	
	
	@GetMapping("/merchantRoutingViewUpdate")
	public String merchantRoutingView(Principal principal, Model model, HttpServletRequest request) {
		try {
			
			log.info("Merchant Routing loading ...", principal != null ? principal.getName() : "No Principal");
			if (principal == null) {
				return "redirect:/login";
			}
			//log.info("The merchnat List is"+adminService.getAllMerchantIdMidNames());
			log.info("Username-----------------"+principal.getName());
			model.addAttribute("midList", adminService.getAllMerchantIdMidNames());
			model.addAttribute("username", principal.getName());
			return "Admin/MerchantRouteUpdate";
		}catch (Exception e) {
			return"redirect:/login";
		}
		
	}
	
	
	@GetMapping("/addMerchantBalance")
	public String addMerchantBalanceView(Principal principal, Model model, HttpServletRequest request) {
		try {
			log.info("Add Balance loading ...", principal != null ? principal.getName() : "No user found");
			if (principal == null) {
				return "redirect:/login";
			}
			
			//log.info("The merchnat List is"+adminService.getAllMerchantIdMidNames()+"principal"+principal.getName());
			model.addAttribute("midList", adminService.getAllMerchantIdMidNames());
			model.addAttribute("username", principal.getName());
			return "Admin/AddBalance";
		}catch (Exception e) {
			return"redirect:/login";
		}
		
	}
	
	
	
	@GetMapping("/signer")
	public String signerView(Principal principal, Model model, HttpServletRequest request) {
		try {
			log.info("Add Balance loading ...", principal != null ? principal.getName() : "No user found");
			if (principal == null) {
				return "redirect:/login";
			}
			
			//log.info("The merchnat List is"+adminService.getAllMerchantIdMidNames()+"principal"+principal.getName());
			model.addAttribute("midList", adminService.getAllMerchantIdMidNames());
			model.addAttribute("username", principal.getName());
			return "Admin/signer";
		}catch (Exception e) {
			return"redirect:/login";
		}
		
	}



}
