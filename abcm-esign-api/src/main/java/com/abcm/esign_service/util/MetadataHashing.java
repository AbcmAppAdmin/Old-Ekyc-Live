package com.abcm.esign_service.util;

import java.security.MessageDigest;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;

public class MetadataHashing {

    public static String generateMetadataHash(String merchantId, String orderId, String documentName, String signersJson) throws NoSuchAlgorithmException {
        String metadata = "merchant_id=" + merchantId +
                          "&order_id=" + orderId +
                          "&document_name=" + documentName +
                          "&signers=" + signersJson;

        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        digest.update(metadata.getBytes(StandardCharsets.UTF_8));
        byte[] hashBytes = digest.digest();
        StringBuilder hexString = new StringBuilder();
        for (byte b : hashBytes) {
            hexString.append(String.format("%02x", b));
        }

        return hexString.toString();
    }

    public static boolean verifyMetadataHash(String originalHash, String merchantId, String orderId, String documentName, String signersJson) {
        try {
            String generatedHash = generateMetadataHash(merchantId, orderId, documentName, signersJson);
            System.out.println("2Original Metadata Hash: " + originalHash);
            return generatedHash.equals(originalHash);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void main(String[] args) {
        try {
            String merchantId = "K10001";
            String orderId = "ABELPAY2026022212246";
            String documentName = "Test_Agreement_Document";
            String signersJson = "[{\"signer_name\": \"Krushna Kacharu Dakale\", \"signer_email\": \"krushna.dakale@abcmapp.dev\", \"email_notification\": \"SEND\", \"signer_purpose\": \"Test Document\", \"sign_coordinates\": [{\"page_num\": 1}]}]";
            String signersJson2 = "[{\"signer_name\": \"Krushna Kacharu Dakale\", \"signer_email\": \"krushna.dakale@abcmapp.dev\", \"email_notification\": \"SEND\", \"signer_purpose\": \"Test Document\", \"sign_coordinates\": [{\"page_num\": 1}]}]";

            String originalHash = generateMetadataHash(merchantId, orderId, documentName, signersJson);
            System.out.println("1Original Metadata Hash: " + originalHash);

            boolean isValid = verifyMetadataHash(originalHash, merchantId, orderId, documentName, signersJson2);
            System.out.println("Hash verification result: " + (isValid ? "Valid" : "Invalid"));

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }
}