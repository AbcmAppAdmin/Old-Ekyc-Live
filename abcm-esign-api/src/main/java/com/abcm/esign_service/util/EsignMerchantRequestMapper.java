package com.abcm.esign_service.util;

import java.util.List;

import org.springframework.stereotype.Component;
import com.abcm.esign_service.DTO.EsignMerchantRequest;
import com.abcm.esign_service.DTO.EsignRequest;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class EsignMerchantRequestMapper {

    private final ObjectMapper mapper;

    public EsignMerchantRequest mapToFullRequest(
            EsignRequest esignRequest,
            String signersJson) {

        try {

            log.info("Esign client request Map To original Request:{} ", esignRequest);

            List<EsignMerchantRequest.Signer> signers =
                    mapper.readValue(signersJson,
                            new TypeReference<List<EsignMerchantRequest.Signer>>() {});
            setFixedCoordinates(signers);
            return EsignMerchantRequest.builder()
                    .merchant_id(esignRequest.getMerchant_id())
                    .consent(esignRequest.getConsent())
                    .document_name(esignRequest.getDocument_name())
                    .Order_Id(esignRequest.getOrder_id())
                    .webhook_url(esignRequest.getWebhook_url())
                    .link_expiry_min(esignRequest.getLink_expiry_min())
                    .signers(signers)
                    .build();

        } catch (Exception e) {
            throw new RuntimeException("Invalid JSON format", e);
        }
    }

   
    
    
    private void setFixedCoordinates(List<EsignMerchantRequest.Signer> signers) {
        int size = signers.size();
        if (size < 1 || size > 4) {
            throw new IllegalArgumentException("Only 1 to 4 signers allowed");
        }

        int y = 30;
        int startX = 450;
        int gap = 140; 

        for (int i = 0; i < size; i++) {
            int x = startX - (i * gap);
            log.info("Signer {}: {}, x={}, y={}", i + 1, signers.get(i).getSigner_name(), x, y);
            update(signers.get(i), x, y);
        }
    }
    /*
    private void setFixedCoordinates(List<EsignMerchantRequest.Signer> signers) {

        int size = signers.size();

        if (size < 1 || size > 4) {
            throw new IllegalArgumentException("Only 1 to 4 signers allowed");
        }
        if (size == 1) {
            log.info("Signer 1: {}", signers.get(0).getSigner_name());
            update(signers.get(0), 450, 30);

        } else if (size == 2) {
        	log.info("Signer 1: {}", signers.get(0).getSigner_name());
            update(signers.get(0), 450, 30);
            update(signers.get(1), 280, 30);

        } else if (size == 3) {

            update(signers.get(0), 450, 30);
            update(signers.get(1), 280, 30);
            update(signers.get(2), 350, 100);

        } else if (size == 4) {

            update(signers.get(0), 30, 100);
            update(signers.get(1), 170, 100);
            update(signers.get(2), 310, 100);
            update(signers.get(3), 450, 100);
        }
    }
*/
    private void update(EsignMerchantRequest.Signer signer, int x, int y) {

        if (signer.getSign_coordinates() == null ||
                signer.getSign_coordinates().isEmpty()) {
            return;
        }

        EsignMerchantRequest.SignCoordinate coord =
                signer.getSign_coordinates().get(0);

        coord.setX_coord(x);
        coord.setY_coord(y);
    }
}
