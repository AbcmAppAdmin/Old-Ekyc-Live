package com.abcm.esign_service.esignWebhook;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.abcm.esign_service.DTO.ResponseModel;
import com.abcm.esign_service.service.WebhookDetailsService;


@RestController
@RequestMapping("api/esign")
public class EsignWebhookController {
	

	@Autowired
	private EsignWebhookService webhookService;
	
	@Autowired
	private WebhookDetailsService  webhookDetailsService;

	@PostMapping("/webhook")
	public void webhook(@RequestBody String response) {
		webhookService.updateWebhookResponse(response);	
	}
	
	@GetMapping("/webhook/details")
	public ResponseEntity<ResponseModel> getWebhookDetails(
	       @RequestParam(value = "trackerId", required = false) String trackerId,
	       @RequestParam(value = "orderId", required = false) String orderId) {

	    Object data = webhookDetailsService.getWebhookDetails(trackerId, orderId);
	    ResponseModel responseModel = new ResponseModel("SUCCESS", 200, data);
	    return ResponseEntity.ok(responseModel);
	}

}
