package com.abcm.esign_service.util;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;


import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import io.netty.resolver.DefaultAddressResolverGroup;
import lombok.extern.slf4j.Slf4j;
import reactor.netty.http.client.HttpClient;

@Service
@Slf4j
public class SendFailureEmail {

    @Value("${email.send.from}")
    private String from;

  
    @Value("${email.send.bcc}")
    private String bcc;

    @Value("${email.send.api.url}")
    private String emailApiUrl;

    // WebClient configured with DNS + timeouts
    private final WebClient webClient;

    public SendFailureEmail() {
        HttpClient httpClient = HttpClient.create()
                .resolver(DefaultAddressResolverGroup.INSTANCE) // Fix DNS resolution
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 30_000)
                .responseTimeout(Duration.ofSeconds(30))
                .doOnConnected(conn -> conn
                        .addHandlerLast(new ReadTimeoutHandler(30, TimeUnit.SECONDS))
                        .addHandlerLast(new WriteTimeoutHandler(30, TimeUnit.SECONDS)));

        this.webClient = WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build();
    }

    public void sendEkycFailureEmail(String mailString, String tracktId, String subject,String SendTo) {
        try {
            log.info("Email Voter_Id Failed/Low Balance Trigger: {}", subject);

            String finalSubject = (tracktId == null || tracktId.trim().isEmpty())
                    ? subject
                    : subject + " - " + tracktId;
            
            JSONArray bccArray = new JSONArray();
            if (bcc != null && !bcc.isBlank()) {
                for (String email : bcc.split(",")) {
                    bccArray.put(email.trim());
                }
            }

            JSONObject requestBody = new JSONObject();
            requestBody.put("from", from);
            requestBody.put("to", SendTo);
         //   requestBody.put("bcc", "");
            requestBody.put("subject", finalSubject);
            requestBody.put("contentType", "text/html");
            requestBody.put("body", mailString);

            String emailResponse = webClient.post()
                    .uri(emailApiUrl)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(BodyInserters.fromValue(requestBody.toString()))
                    .retrieve()
                    .bodyToMono(String.class)
                    .doOnError(ex -> log.error("WebClient Email Error: {}", ex.getMessage()))
                    .onErrorReturn("fail:false")
                    .block();

            log.info("Aadhaar Email Sent: TrackID={}, Response={}", tracktId, emailResponse);

        } catch (Exception e) {
            log.error("Exception in sendAadhaarFailureEmail for TrackID={}: {}", tracktId, e.getMessage(), e);
        }
    }
}

