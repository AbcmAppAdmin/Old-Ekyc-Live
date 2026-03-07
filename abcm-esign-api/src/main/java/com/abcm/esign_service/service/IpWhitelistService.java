package com.abcm.esign_service.service;

import java.util.Arrays;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.abcm.esign_service.DTO.MerchantIpProjection;
import com.abcm.esign_service.repo.MerchnatMasterRepo;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class IpWhitelistService {
	
	private final  MerchnatMasterRepo masterRepo;
	
	 public void assertIpAllowed(String merchantId, HttpServletRequest request)
	 {
		 
		    String clientIp = extractClientIp(request);
		    
	        log.info("IP check started for userId={}, requestedIp={}", merchantId, clientIp);
	        
	        if (!StringUtils.hasText(clientIp)) {
	            log.warn("IP check failed for userId={}: cannot determine client IP", merchantId);
	            throw new IllegalStateException("Forbidden: cannot determine client IP");
	        }
	        
	        MerchantIpProjection merchant = masterRepo.findMerchantByMid(merchantId);
	        if (merchant == null) {
	        	throw new IllegalStateException("Forbidden: merchant not found");
	        }
	        
	        if (!StringUtils.hasText(merchant.getIpAllowed())) {
	            log.warn("IP check failed for merchantId={}, requestedIp={}: IP whitelist not configured", merchantId, clientIp);
	            throw new IllegalStateException("Forbidden: IP not allowed or IP Empty");
	        }
	        boolean allowed = Arrays.stream(merchant.getIpAllowed().split(","))
	                .map(String::trim)
	                .filter(StringUtils::hasText)
	                .anyMatch(ip -> ip.equals(clientIp));
	        if (!allowed) {
	            log.warn("IP check failed for merchantId={}, requestedIp={}, allowedIps={}", merchantId, clientIp, merchant.getIpAllowed());
	            throw new IllegalStateException("Forbidden: IP not allowed");
	        }
	        
	 }

	 private String extractClientIp(HttpServletRequest request) {
		 log.info("extractClientIp start: {}");
	        String xff = request.getHeader("X-Forwarded-For");
	        if (StringUtils.hasText(xff)) {
	            int comma = xff.indexOf(',');
	            String first = comma >= 0 ? xff.substring(0, comma) : xff;
	            return first.trim();
	        }
	        String realIp = request.getHeader("X-Real-IP");
	        if (StringUtils.hasText(realIp)) return realIp.trim();
	        return request.getRemoteAddr();
	    }
}
