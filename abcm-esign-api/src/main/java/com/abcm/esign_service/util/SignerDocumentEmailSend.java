package com.abcm.esign_service.util;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.abcm.esign_service.repo.MerchnatMasterRepo;
import com.abcmkyc.entity.KycData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class SignerDocumentEmailSend {

	private final SendFailureEmail sendFailureEmail;
	private final MerchnatMasterRepo merchnatMasterRepo;

	@Value("${email.esign.success.subject}")
	private String esignedSuccess;

	@Value("${email.esign.finalDownload.path}")
	private String EsignFinalDocumentPath;

	public void downloadDocsEmailSend(String documentUrl, String documentName, List<KycData> kycList)
			throws IOException {

		log.info("From Abcm to merchant downloadDocsEmailSend: {}, merchnat Id : {}",documentUrl);

		String mailstring = CommonUtils.readUsingFileInputStream(EsignFinalDocumentPath);
		mailstring = mailstring.replace("{{signerShortUrl}}", documentUrl);
		String sendTo = merchnatMasterRepo.findEmailByMid(kycList.get(0).getMerchantId());
		log.info("Merchant mail id:{}", sendTo);
		sendFailureEmail.sendEkycFailureEmail(mailstring, esignedSuccess, sendTo,kycList);
	}
}
