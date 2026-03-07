package com.abcm.kyc.service.ui.util;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;


@WebServlet("/getImage")
public class ImageProcess extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final Logger log = LoggerFactory.getLogger(ImageProcess.class);

    private static final String SENT_DIR = "/opt/E-KycDocument/SignerDocument/SentDocuments/";
    private static final String RECEIVED_DIR = "/opt/E-KycDocument/SignerDocument/ReceivedDocuments/";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String imageName = request.getParameter("path");
        String sender = request.getParameter("sender");

        if (imageName == null || imageName.isEmpty()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "File path is required");
            return;
        }

        if (sender == null || (!sender.equalsIgnoreCase("send") && !sender.equalsIgnoreCase("receiver"))) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid sender type");
            return;
        }

        // Map to safe directory
        Path baseDir = sender.equalsIgnoreCase("send") ? Paths.get(SENT_DIR) : Paths.get(RECEIVED_DIR);

        // Prevent directory traversal attacks
        Path filePath = baseDir.resolve(imageName).normalize();
        if (!filePath.startsWith(baseDir)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access denied");
            return;
        }

        if (!Files.exists(filePath) || Files.isDirectory(filePath)) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "File not found");
            return;
        }

        // Serve the file
        String contentType = Files.probeContentType(filePath);
        if (contentType == null) {
            contentType = "application/octet-stream";
        }

        response.setContentType(contentType);
        try (InputStream in = Files.newInputStream(filePath)) {
            in.transferTo(response.getOutputStream());
        }
    }
}
