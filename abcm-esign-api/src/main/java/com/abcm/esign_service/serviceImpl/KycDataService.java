package com.abcm.esign_service.serviceImpl;

import org.springframework.stereotype.Service;

import com.abcm.esign_service.repo.EsignRepository;
import com.abcmkyc.entity.KycData;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class KycDataService {

    private final EsignRepository repository;

    public KycData getDocumentUrlByRequestId(String requestId) {
        return repository.findByRequestId(requestId)
                .orElseThrow(() -> new RuntimeException("url not found"));
    }
}
