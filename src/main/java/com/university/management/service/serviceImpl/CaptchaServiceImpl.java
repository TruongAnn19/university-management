package com.university.management.service.serviceImpl;

import com.university.management.model.dto.response.RecaptchaResponse;
import com.university.management.service.CaptchaService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
public class CaptchaServiceImpl implements CaptchaService {

    @Value("${google.recaptcha.secret}")
    private String secretKey;

    @Value("${google.recaptcha.verify-url}")
    private String verifyUrl;

    @Value("${google.recaptcha.threshold}")
    private double threshold;

    private final RestTemplate restTemplate = new RestTemplate();

    public void verifyCaptcha(String token) {
        if (token == null || token.isBlank()) {
            throw new RuntimeException("Vui lòng xác thực Captcha");
        }

        MultiValueMap<String, String> requestMap = new LinkedMultiValueMap<>();
        requestMap.add("secret", secretKey);
        requestMap.add("response", token);

        try {
            RecaptchaResponse apiResponse = restTemplate.postForObject(verifyUrl, requestMap, RecaptchaResponse.class);

            if (apiResponse == null) {
                throw new RuntimeException("Lỗi kết nối Google Recaptcha");
            }

            if (!apiResponse.success()) {
                log.error("Captcha Error Codes: {}", apiResponse.errorCodes());
                throw new RuntimeException("Captcha không hợp lệ");
            }

            if (apiResponse.score() < threshold) {
                log.warn("Phát hiện nghi vấn Bot. Score: {}", apiResponse.score());
                throw new RuntimeException("Hệ thống phát hiện nghi vấn truy cập bất thường (Bot)");
            }

        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}
