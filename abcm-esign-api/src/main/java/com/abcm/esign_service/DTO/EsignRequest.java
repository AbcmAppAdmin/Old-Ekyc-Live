package com.abcm.esign_service.DTO;

import lombok.Data;

@Data
public class EsignRequest {

	private String merchant_id;
	private String consent;
	private String document_name;
	private String link_expiry;
	private String order_id;
	private String webhook_url;
	private String allow_download;

}
