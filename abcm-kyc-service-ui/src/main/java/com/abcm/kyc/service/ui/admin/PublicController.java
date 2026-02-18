package com.abcm.kyc.service.ui.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.abcm.kyc.service.ui.dto.PaymentResponseModel;
import com.abcm.kyc.service.ui.payment.PaymentService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
public class PublicController {
	
	@Autowired
	private PaymentService paymentService;
	
	@PostMapping("/payment-response")
	public String handlePaymentResponse(HttpServletRequest request,
	                                    RedirectAttributes redirectAttributes,
	                                    HttpSession session) {
	    PaymentResponseModel paymentResponse = paymentService.handleWalletBalanceUpdate(request);
	    session.setAttribute("paymentResponse", paymentResponse);
	    return "redirect:/app/merchant/billing";
	}
	
	@GetMapping("/invalidError")
	public String invalidRequestId(
	        @RequestParam(required = false) String requestId,
	        Model model) {

	    if (requestId == null || requestId.isEmpty()) {
	        model.addAttribute("exceptionError", "Something went wrong. Please try again later.");
	        model.addAttribute("showRequestId", false); // BOOLEAN, NOT STRING
	    } else {
	        model.addAttribute("errorMessage", "The requested Request ID " + requestId + " was not found or is invalid.");
	        model.addAttribute("showRequestId", true);  // BOOLEAN, NOT STRING
	        model.addAttribute("requestId", requestId);
	    }

	    return "Esign/ErrorResponse"; // /WEB-INF/views/Error/ekyDemo.jsp
	}
	
	
	@RequestMapping("/response")
	public String returnRespopnseEsign(
	        @RequestParam(required = false) String status,
	        Model model) {
	    log.info("Return Response From Esign Service to Display: {}", status);

	    if (status == null || status.isEmpty()) {
	        model.addAttribute("exceptionError", "Something went wrong. Please try again later.");
	        model.addAttribute("showRequestId", false); 
	    } 
	    else if ("success".equalsIgnoreCase(status)) {  // Fixed string comparison
	        log.info("Esign Return Response Status: {}", status);
	        model.addAttribute("status", status);
	    } 
	    else {
	        // For any other status (optional)
	        model.addAttribute("exceptionError", "Something went wrong. Please try again later.");
	    }

	    return "Esign/EsignRetrunResponse";  // maps to your JSP
	}
	
	
	
	
	@PostMapping("/esign/webhook")
	public String receviedEsignWebhook(@RequestBody Object esignWebhookResponse)
	{
		log.info("Esign Recevied Webhook");
		return null;
		
	}




	


}
