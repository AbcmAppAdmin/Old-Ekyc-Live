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
    public static String downloadSignedDocument(String documentUrl, String trackId, String downloadDir) {

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
            // Create download directory if it doesn't exist
            Path dirPath = Paths.get(downloadDir);
            if (!Files.exists(dirPath)) {
                Files.createDirectories(dirPath);
                log.info("Created download directory: {}", dirPath);
            }

            // Generate unique filename: ESIGN_{trackId}_{yyyyMMdd_HHmmss}.pdf
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String fileName = "ESIGN_" + trackId + "_" + timestamp + ".pdf";
            Path filePath = dirPath.resolve(fileName);

            // Download file from URL
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

}