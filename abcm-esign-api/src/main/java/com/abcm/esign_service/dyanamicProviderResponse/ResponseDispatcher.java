package com.abcm.esign_service.dyanamicProviderResponse;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ResponseDispatcher {

    private final Map<String, ProviderResponseHandler<?>> handlers = new HashMap<>();

    @Autowired
    public ResponseDispatcher(
        @Lazy ZoopResponseHandler zoopResponseHandler
        //@Lazy CashfreeResponseHandler cashfreeResponseHandler
    ) {
        handlers.put("zoop-esign", zoopResponseHandler);
        //handlers.put("cashfree", cashfreeResponseHandler);
    }

    @SuppressWarnings("unchecked")
    public <T> T getVoterIdResponse(String providerName, JSONObject input, String trackId, String merchanId, Long productRate,String orderId,String whiteLabel) {
       log.info("ResponseDispatcher Enter: " + providerName);
       //log.info("Provider API response : {} " ,  input);
        

        ProviderResponseHandler<T> handler = (ProviderResponseHandler<T>) handlers.get(providerName.toLowerCase());
        if (handler == null) {
            throw new RuntimeException("No response handler found for provider: " + providerName);
        }

        return handler.esignVerifyResponseToMerchant(input, trackId, merchanId, productRate,orderId, whiteLabel);
    }
}
