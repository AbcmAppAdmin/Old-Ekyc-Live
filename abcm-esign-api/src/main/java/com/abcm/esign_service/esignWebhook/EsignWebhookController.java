package com.abcm.esign_service.esignWebhook;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("api/esign")
public class EsignWebhookController {
	

	@Autowired
	private EsignWebhookService webhookService;

	@PostMapping("/webhook")
	public void webhook(@RequestBody String response) {
		webhookService.updateWebhookResponse(response);	
	}

}
