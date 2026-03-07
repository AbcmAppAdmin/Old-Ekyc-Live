package com.abcm.kyc.service.ui.merchant;

import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.abcm.kyc.service.ui.config.SecurityUtil;
import com.abcm.kyc.service.ui.dto.AddBalanceRequest;
import com.abcm.kyc.service.ui.dto.MerchantDTO;
import com.abcm.kyc.service.ui.dto.PaymentResponseModel;
import com.abcm.kyc.service.ui.payment.PaymentService;
import com.abcm.kyc.service.ui.service.MerchantService;
import com.abcmkyc.entity.Merchant_Master;
import com.abcmkyc.entity.Wallet;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/app/merchant/")
public class MerchantController {
	
	@Autowired
	private MerchantService merchantService;

	@Autowired
	private PaymentService paymentService;
	
	

	
	@GetMapping("/dashboard")
	public String merchantDashboard(@ModelAttribute("authUser") SecurityUtil.AuthenticatedUser authUser, Model model,HttpSession httpSession) {
	    
		log.info("Merchant Dashboard Accessed at: {}", LocalDateTime.now());
		if (authUser == null) {
	        return "redirect:/login";
	    }
		log.info("Merchant Dashboard Accessed at: {}", LocalDateTime.now(),authUser.getUsername());

	    model.addAttribute("username", authUser.getUsername());
	    Merchant_Master merchant = merchantService.getMerchantByUsername(authUser.getUsername());
	    model.addAttribute("merchant", merchant);
	    // Handle wallet - get it if merchant exists, otherwise set to null
	    Wallet wallet = null;
	    if (merchant != null && merchant.getMid() != null) {
	        try {
	            model.addAttribute("merchantName", merchant.getName());
	            wallet = merchantService.getWalletByMerchantId(merchant.getMid());
	            log.info("Available Wallet Amount:{} "+wallet.getBalance()+"alert balance:{}"+wallet.getAlertBalance());
	            
	        } catch (NoSuchElementException e) {
	            log.warn("No wallet found for merchant ID: {}", merchant.getMid());
	            wallet = null; // Explicitly set to null if not found
	           
	        }
	    }
	    if(wallet.getBalance()<=wallet.getAlertBalance())
	    {
		    merchantService.sendMerchnatLowBalanceEmail(merchant.getName(),merchant.getEmail(),  wallet.getBalance());
	    }
	    model.addAttribute("wallet", wallet);
	    // Handle subscribed services count
	    int subscribeCount = 0;
	    if (merchant != null) {
	        subscribeCount = merchantService.getSubscribedServicesCount(merchant);
	    }
	    model.addAttribute("subscribeCount", subscribeCount);
	    
	    return "Merchant/MerchantDashboard";
	}
	
	
	
	
	
	@GetMapping("/Apps")
	public String AppsView(@ModelAttribute("authUser") SecurityUtil.AuthenticatedUser authUser, Model model) {
		return Optional.ofNullable(authUser).map(user -> {
		log.info("merchant Appview return: {}", user.getUsername());
			model.addAttribute("username", user.getUsername());
			Merchant_Master merchant = merchantService.getMerchantByUsername(user.getUsername());
			model.addAttribute("subscribeCount",merchantService.getSubscribedServicesCount(merchant));
			log.info("Merchant Master :: "+merchant);
			model.addAttribute("merchant",merchant );
			return "Merchant/Apps";
		}).orElse("redirect:/login");
	}
	
	@GetMapping("/products")
	public String productsView(@ModelAttribute("authUser") SecurityUtil.AuthenticatedUser authUser, Model model) {
		return Optional.ofNullable(authUser).map(user -> {
			
			log.info(" Merchant ProductView: {}", user.getUsername());
			model.addAttribute("username", user.getUsername());
			Merchant_Master merchant = merchantService.getMerchantByUsername(user.getUsername());
			log.info("Merchant Master :: "+merchant);
			 model.addAttribute("merchantName", merchant.getName());
			model.addAttribute("merchant",merchant );
			return "Merchant/Product";
		}).orElse("redirect:/login");
	}
	
	
	
	
	
	
	@GetMapping("/billing")
	public String billingView(Principal principal, Model model, HttpServletRequest request) {
	   log.info("merchnat billing view");    
		if (principal == null) {
	        return "redirect:/login";
	    }
		
		log.info("Billing page accessed at: {}",principal.getName());

	    String username = principal.getName();
	    log.info("Accessing billing page for user: {}", username);

	    Merchant_Master merchant = merchantService.getMerchantByUsername(username);
	    if (merchant == null || merchant.getMid() == null) {
	    	
	        log.error("Merchant not found or invalid for username: {}", username);
	        return "redirect:/login";
	    }
	    model.addAttribute("merchantName", merchant.getName());
	    model.addAttribute("merchant", merchant);

	    Wallet wallet = null;
	    try {
	        wallet = merchantService.getWalletByMerchantId(merchant.getMid());
	    } catch (NoSuchElementException e) {
	        log.warn("No wallet found for merchant ID: {}", merchant.getMid());
	        wallet = null;
	    }

	    if (wallet == null) {
	        wallet = new Wallet();
	        log.info("Created a new empty wallet for merchant ID: {}", merchant.getMid());
	    }

	    log.info("Wallet validity: {}", wallet.getValidity());

	    Long daysDifference = null;
	    if (wallet.getValidity() != null) {
	        try {
	            daysDifference = ChronoUnit.DAYS.between(LocalDate.now(), wallet.getValidity());
	            log.info("Validity in days: {}", daysDifference);
	        } catch (Exception e) {
	            log.error("Error calculating validity difference", e);
	            daysDifference = null;
	        }
	    } else {
	        log.warn("Wallet validity is null");
	    }

	    model.addAttribute("wallet", wallet);
	    model.addAttribute("validityDays", daysDifference);

	    HttpSession session = request.getSession(false);
	    if (session != null) {
	        PaymentResponseModel responseModel = (PaymentResponseModel) session.getAttribute("paymentResponse");
	        if (responseModel != null) {
	            model.addAttribute("paymentResponse", responseModel);
	            session.removeAttribute("paymentResponse");
	        }
	    }

	    return "Merchant/Billing";
	}

	
	
	
	
	@PostMapping("processToPayRequest")
	public String processPayment(@ModelAttribute AddBalanceRequest addBalanceRequest, HttpServletRequest request) {
		try {
			log.info("process to payment merchant balance{}: " + addBalanceRequest);
			return paymentService.processPayment(addBalanceRequest, request);
		} catch (Exception e) {
			log.error("Error processing payment: " + e.getMessage());
			e.printStackTrace(); // Log stack trace for debugging purposes
			return "errorpage"; // Return an appropriate error page or error response
		}
	}
	
	
	
	@GetMapping("/kycReport")
	public String showkycReport(@ModelAttribute("authUser") SecurityUtil.AuthenticatedUser authUser, Model model) {
	    return Optional.ofNullable(authUser).map(user -> {
	        log.info("Welcome to merchant kyc Report : {}", user.getUsername());
	        model.addAttribute("username", user.getUsername());
	        // Fetch merchant details
	        Merchant_Master merchantOptional = merchantService.getMerchantByUsername(user.getUsername());
	        if (merchantOptional==null) {
	            log.error("Merchant details not found for username: {}", user.getUsername());
	            // Handle this case, maybe redirect or show a relevant error message
	            return "redirect:/errorPage";  // Redirect to an error page or show a custom error message
	        }
	        log.info("Merchant Master :: {}", merchantOptional);
	        // Add merchant details to the model
	        model.addAttribute("merchantName", merchantOptional.getName());
	        model.addAttribute("merchant", merchantOptional);
	        // Return the view name for AllReport
	        return "Merchant/AllReport";
	    }).orElse("redirect:/login");  // Redirect to login if user is not authenticated
	}
	
	
	@GetMapping("/txnCountReport")
	public String txnCountReportView(@ModelAttribute("authUser") SecurityUtil.AuthenticatedUser authUser, Model model) {
		return Optional.ofNullable(authUser).map(user -> {
			log.info("txnCountReport View Report Page: {}",user.getUsername());
			model.addAttribute("username", user.getUsername());
			Merchant_Master merchant = merchantService.getMerchantByUsername(user.getUsername());
			log.info("Merchnat Info Table"+merchant);
			model.addAttribute("merchantName", merchant.getName());
			model.addAttribute("merchant",merchant);
			return "Merchant/TxnCountReport";
		}).orElse("redirect:/login");
	}
	
	@GetMapping("/signer")
	public String signerView(@ModelAttribute("authUser") SecurityUtil.AuthenticatedUser authUser, Model model) {
	    return Optional.ofNullable(authUser).map(user -> {
	        log.info("Signer Page Accessed by: {}", user.getUsername());
	        model.addAttribute("username", user.getUsername());
	        Merchant_Master merchant = merchantService.getMerchantByUsername(user.getUsername());
	        if (merchant == null) {
	            log.error("Merchant not found for username: {}", user.getUsername());
	            return "redirect:/login";
	        }
	        model.addAttribute("merchantName", merchant.getName());
	        model.addAttribute("merchant", merchant);
	        model.addAttribute("merchantId", merchant.getMid());

	        return "Merchant/signer";
	    }).orElse("redirect:/login");
	}
	
	@GetMapping("/esignReport")
	public String esignReportView(@ModelAttribute("authUser") SecurityUtil.AuthenticatedUser authUser, Model model) {
	    return Optional.ofNullable(authUser).map(user -> {
	        log.info("E-Sign Report Page Accessed by: {}", user.getUsername());
	        model.addAttribute("username", user.getUsername());
	        Merchant_Master merchant = merchantService.getMerchantByUsername(user.getUsername());
	        log.info("Merchant Details :{}",merchant);
	        if (merchant == null) {
	            log.error("Merchant not found for username: {}", user.getUsername());
	            return "redirect:/login";
	        }
	        model.addAttribute("merchantName", merchant.getName());
	        model.addAttribute("merchant", merchant);

	        return "Merchant/signerReport";
	    }).orElse("redirect:/login");
	}



}
