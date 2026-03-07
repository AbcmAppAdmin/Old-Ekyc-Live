package com.abcm.esign_service.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.Date;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class CommonUtils {
	private static final String receviedFolderName="ReceivedDocuments";
    public static long generateUniqueId() {

        Date currentDate = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");

        String formattedDate = dateFormat.format(currentDate);

        long uniqueId = Long.parseLong(formattedDate);

        uniqueId %= 100000000;

        return uniqueId;
    }

    public boolean isNullOrEmpty(String value) {
        return value == null || value.trim().isEmpty();
    }

    public boolean isInvalidUserId(long userId) {
        return userId <= 0;
    }

    public static String readUsingFileInputStream(String fileName) throws IOException {
        FileInputStream fis = null;
        byte[] buffer = new byte[10];
        StringBuilder sb = new StringBuilder();
        try {
            fis = new FileInputStream(fileName);

            while (fis.read(buffer) != -1) {
                sb.append(new String(buffer));
                buffer = new byte[10];
            }
            fis.close();

        } catch (FileNotFoundException e) {
            e.toString();
        } finally {
            if (fis != null)
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
        return sb.toString();
    }

    // Used For Esign - Convert document to Base64
    public static String convertDocumentToBase64(MultipartFile file) {
        try {
            byte[] fileData = file.getBytes();
            return Base64.getEncoder().encodeToString(fileData);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Downloads a signed document from the given URL and saves it to the specified
     * directory.
     * File is named uniquely using trackId: ESIGN_{trackId}_{timestamp}.pdf
     *
     * @param documentUrl URL of the signed document to download
     * @param trackId     unique tracker ID used in the filename
     * @param downloadDir directory path where the file will be saved
     * @return absolute path of the downloaded file, or null if download failed
     */
    public static String downloadSignedDocument(String documentUrl, String merchantId,String trackId, String orderId, String downloadDir) {
    	log.info("Download allow Y Files inside:{}, trackId: {}",orderId, trackId);
        if (documentUrl == null || documentUrl.isBlank()) {
            log.error("Document URL is null or blank, cannot download");
            return null;
        }
        if (trackId == null || trackId.isBlank()) {
            log.error("TrackId is null or blank, cannot generate unique filename");
            return null;
        }

        HttpURLConnection connection = null;
        try {
            // Create the base directory path if it doesn't exist
            Path baseDirPath = Paths.get(downloadDir, receviedFolderName);
            if (!Files.exists(baseDirPath)) {
                Files.createDirectories(baseDirPath);
                log.info("Created base directory: {}", baseDirPath);
            }

            // Create merchant directory
            Path merchantDir = baseDirPath.resolve(merchantId);
            if (!Files.exists(merchantDir)) {
                Files.createDirectories(merchantDir);
                log.info("Created merchant directory: {}", merchantDir);
            }

            // Create order directory
            Path orderDir = merchantDir.resolve(orderId);
            if (!Files.exists(orderDir)) {
                Files.createDirectories(orderDir);
                log.info("Created order directory: {}", orderDir);
            }

            // Generate unique filename for the signed document
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String fileName = "ESIGN_" + trackId + "_" + timestamp + ".pdf";
            Path filePath = orderDir.resolve(fileName);

            // Download the document from the provided URL
            URI uri = URI.create(documentUrl);
            connection = (HttpURLConnection) uri.toURL().openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(15000);
            connection.setReadTimeout(30000);

            int responseCode = connection.getResponseCode();
            if (responseCode != HttpURLConnection.HTTP_OK) {
                log.error("Failed to download document. HTTP response code: {}, URL: {}", responseCode, documentUrl);
                return null;
            }

            // Save the downloaded document
            try (InputStream inputStream = connection.getInputStream()) {
                Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
            }

            String absolutePath = filePath.toAbsolutePath().toString();
            log.info("Signed document downloaded successfully: {}, trackId: {}", absolutePath, trackId);
            return absolutePath;

        } catch (Exception e) {
            log.error("Failed to download signed document for trackId: {}, URL: {}, error: {}",
                    trackId, documentUrl, e.getMessage(), e);
            return null;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }
    
    
 
    public static String downloadSignedDocumentAndGenerateHash(String documentUrl) {
        log.info("Downloading signed document from URL: {}", documentUrl);

        if (documentUrl == null || documentUrl.isBlank()) {
            log.error("Document URL is null or blank");
            return null;
        }

        HttpURLConnection connection = null;

        try {
            URI uri = URI.create(documentUrl);
            connection = (HttpURLConnection) uri.toURL().openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(15000);
            connection.setReadTimeout(30000);
            connection.setInstanceFollowRedirects(true); // automatically follow redirects

            int responseCode = connection.getResponseCode();

            // If redirect still occurs manually follow
            if (responseCode == HttpURLConnection.HTTP_MOVED_TEMP ||
                responseCode == HttpURLConnection.HTTP_MOVED_PERM ||
                responseCode == HttpURLConnection.HTTP_SEE_OTHER) {

                String newUrl = connection.getHeaderField("Location");
                log.info("Redirected to URL: {}", newUrl);
                connection.disconnect();

                // recursive call to follow the redirect
                return downloadSignedDocumentAndGenerateHash(newUrl);
            }

            if (responseCode != HttpURLConnection.HTTP_OK) {
                log.error("Failed to download document. HTTP code: {}", responseCode);
                return null;
            }

            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            try (InputStream inputStream = connection.getInputStream()) {
                byte[] buffer = new byte[8192];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    digest.update(buffer, 0, bytesRead);
                }
            }

            // Convert hash bytes to hex string
            byte[] hashBytes = digest.digest();
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                hexString.append(String.format("%02x", b));
            }

            String hash = hexString.toString();
            log.info("Successfully generated SHA-256 hash for document URL: {}", hash);

            return hash;

        } catch (Exception e) {
            log.error("Error downloading or hashing document from URL: {}", documentUrl, e);
            return null;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }
}