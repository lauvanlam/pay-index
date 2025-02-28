package com.vanlam.server;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import java.util.*;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/api")
public class PaymentController {

    String url = "https://mpay.skpayapp.com/api/pay/create";
    String privateKey = "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCjlX4lUscf82Bp45Do5W/OPMooTvZAoWj1WCHTEFepbUaIhA/qHUG3IEk8cxaZtgckVbh97Nux36t2+/WwNm+gCo41hKoS3EAGeW/g5YpbgJX3wcOSaZ9jhP17vb21CcZfQUNCWcjDMnBoEctqU2CyqZWdrnzA7D/96JlbPxqYdHiTIPz0NHqwOromamhKDxpg+A09MiByqlraDOKd9qNeX1o/5fMmnX9aBvAXZmMqX5mZdQaMuzZLAQPeT0n4HLFjHb/HTTl4lx1Ug/uoanFAywjtL63/6OXiYqm8GjionJdq7FY5CfeEh75I3UdEpKg/ZWKV4YP+YCV4Y1evHKJvAgMBAAECggEAXzCGj0F0DeuZlwSNNnkMbn6BRKNuOH20jdATHrbLzBOCj74JZLpRmzZ2Z26xIBEEZuhayywhS4hURpCnjzqeCgsaZZolPYRc3Wec6smnkUdp/RoLrA86aLbiqjbnRYnCnXtkoB+O68dWEbkJHX4XLt/v6Cm4/qp7Mk0/sBEwcLWnqi28vffVlYiPigGd5p8sbgcosVEQCN2t0+soKiGxt8zex+zyYv8PqHrpZkmitj2J38ll8tv7tQFWHCTGclhOagm1KE1N64cuKPhHXBNSxX/Py3saJYwfnCaegvPrLWjhe8yX4VAP9JK2WKliPDUDR9e4mM22KJl1CDyCRd7WAQKBgQDND6HaYLVQCdWE7aG5FEdLm0q/xfN+Rpig2KyYGMwHU+EVnAPR2BXo51k2tDplrygk7HdrYc/muqcq00alX95T/3azmEi9XkMvDnWea8/msqgTfmWv//CqmlzWnoKwTgN08HmaLPMsbfTrgpRvkV9oC8BF5EZc5dJqEKQTbp9UzwKBgQDMODkdPBeM8Xvj2GFOwjt2ZYZ7+BC1cDutUiNdcRG4K8xcc6/nF/VIvlQHP34zuMIPreGyLW4jyiedc0BC6mq3zwDKOTRYfhKrvDZREhthoNyE52KvNsIC1o+pRVDD5q5NCHNastX3QRaJO64bKrXC09HKJkJjrwfeFfKkocqAYQKBgHsyEg4NncBpOBM1ZLy5lx+wO1c9iMzrgtTXd9GkzCb22LpP0eenR3p2Sx3XZ2Ihuh98xT4JNIssxjOvXLkMAJk8WEcLjV/fmsNRj45OvUefO+UCQftb623DcVUrQ97B78e4lYXvzWR65vL6r0zb7JKoxKn4u+Eh3akFOF3ITwb3AoGAGprwheiAQPJs64ATHUmcqY4MeTYWJy0Z+TiMNu42GsJJ+3lu3zJ12CZDmDYjYOWfEp/amXXltW/uvUp4gr87a/rh5XaSZTz3fbwlb2Zmfs+QKxhh+OWvXONLEAhhv0PcTVjefmByE6Y9I1/NDOkBiQJDgIx/dbEl2z8Mv3n8gcECgYEAo+1I69Y3V9zfab2IPBw1BPvLKnRuBgQkt5kyAESHA4hL/xixysqggr1r8o+KqMXxTtuN0ch1Im5RnpIFKjZGMMlIkdHE/FwcRxilXIgnw7nosKkOQdhiS1BS3+uazlshLsQadSJ8eePX9mJTBrjGR9SMdf4CgAOVI2Ks9KKQzeg="; // 替换为实际私钥
    String pid = "2019";

    @Autowired
    private RestTemplate restTemplate;

    @PostMapping(value = "/pay", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public String createPayment(@ModelAttribute PaymentRequest paymentDetails) throws Exception {


        // 获取并编码所有非空请求参数
        Map<String, String> params = new HashMap<>();
        addEncodedParam(params, "out_trade_no", paymentDetails.getOut_trade_no());
        addEncodedParam(params, "name", paymentDetails.getName());
        addEncodedParam(params, "money", paymentDetails.getMoney());
        addEncodedParam(params, "type", paymentDetails.getType());
        params.put("timestamp", String.valueOf(System.currentTimeMillis() / 1000)); // 时间戳无需编码
        addEncodedParam(params, "method", paymentDetails.getMethod());
        addEncodedParam(params, "pid", pid);
        addEncodedParam(params, "notify_url", paymentDetails.getNotify_url());
        addEncodedParam(params, "return_url", paymentDetails.getReturn_url());
        addEncodedParam(params, "clientip", paymentDetails.getClientip());

        // 排序并生成待签名字符串
        List<String> keys = new ArrayList<>(params.keySet());
        Collections.sort(keys);

        StringBuilder signStringBuilder = new StringBuilder();
        for (String key : keys) {
            if (!signStringBuilder.isEmpty()) {
                signStringBuilder.append("&");
            }
            signStringBuilder.append(key).append("=").append(params.get(key));
        }
        String signString = signStringBuilder.toString();

        // 生成RSA签名
        Signature signature = Signature.getInstance("SHA256WithRSA");
        signature.initSign(getPrivateKeyFromString(privateKey));
        signature.update(signString.getBytes(StandardCharsets.UTF_8)); // 明确指定UTF-8
        String sign = Base64.getEncoder().encodeToString(signature.sign());

        // 构建请求体
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        params.forEach(formData::add);
        formData.add("sign", sign);
        formData.add("sign_type", "RSA");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(formData, headers);

        return restTemplate.postForObject(url, request, String.class);
    }

    private void addEncodedParam(Map<String, String> params, String key, String value) throws Exception {
        if (value != null) {
            String encodedValue = URLEncoder.encode(value, StandardCharsets.UTF_8);
            params.put(key, encodedValue);
        }
    }

    private PrivateKey getPrivateKeyFromString(String key) throws Exception {
        byte[] keyBytes = Base64.getDecoder().decode(key);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePrivate(keySpec);
    }
}