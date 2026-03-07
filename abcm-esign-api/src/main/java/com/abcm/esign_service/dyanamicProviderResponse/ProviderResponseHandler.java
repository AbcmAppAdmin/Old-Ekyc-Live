package com.abcm.esign_service.dyanamicProviderResponse;



import org.json.JSONObject;

public interface ProviderResponseHandler<T> {
    T esignVerifyResponseToMerchant(JSONObject responseObj, String trackId, String merchantId,Long productRate, String orderId,String whiteLabel);
}
