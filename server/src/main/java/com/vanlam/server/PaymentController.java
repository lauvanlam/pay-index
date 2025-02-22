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

    @Autowired
    private RestTemplate restTemplate;

    @PostMapping(value = "/pay", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public String createPayment(@ModelAttribute PaymentRequest paymentDetails) throws Exception {
        String url = "https://www.skpayapp.com/api/pay/create";

        // 获取并编码所有非空请求参数
        Map<String, String> params = new HashMap<>();
        addEncodedParam(params, "out_trade_no", paymentDetails.getOut_trade_no());
        addEncodedParam(params, "name", paymentDetails.getName());
        addEncodedParam(params, "money", paymentDetails.getMoney());
        addEncodedParam(params, "type", paymentDetails.getType());
        params.put("timestamp", String.valueOf(System.currentTimeMillis() / 1000)); // 时间戳无需编码
        addEncodedParam(params, "method", paymentDetails.getMethod());
        addEncodedParam(params, "pid", paymentDetails.getPid());
        addEncodedParam(params, "notify_url", paymentDetails.getNotify_url());
        addEncodedParam(params, "return_url", paymentDetails.getReturn_url());
        addEncodedParam(params, "clientip", paymentDetails.getClientip());

        // 排序并生成待签名字符串
        List<String> keys = new ArrayList<>(params.keySet());
        Collections.sort(keys);

        StringBuilder signStringBuilder = new StringBuilder();
        for (String key : keys) {
            if (signStringBuilder.length() > 0) {
                signStringBuilder.append("&");
            }
            signStringBuilder.append(key).append("=").append(params.get(key));
        }
        String signString = signStringBuilder.toString();

        // 生成RSA签名
        String privateKey = "MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQDXFHnRqbFr3Visi62SIUIYDNzQs01LP1LzkhoYTuqZCLOrerkTJLU5tiTRrEhL1K6c9f45sXxXk3KHUDo8BsBjk1oJzMnBFmYZ3h/uYcEq9khu3elD4YvHOy5AeLtx74h+4KWeo7Kzt3NfHVE6NTnAO4GsCKqYopmzjqgNuWq7qz5NVekx+JIvxWT2xUQHOzEpktjy0cZjz9jcITedDlZHNuaACPJWBbxajQgD1JkGylmelJA81vHQK2zfouXVo5fq0++wdiLkJwboWMwyib6yV46CbuxPm+3Iuq7IwaXBjZsA/cNay/cIYlbsAW4JDM5WAH8WUvDW3ivPx5hymRXnAgMBAAECggEACeUIQawD3Il/VdjLMGjVSJP26KcHDEoqf8gd6TZgNpAe6+mmcAoFSFVn1jmNzedH7TKn8nGG3eSLpUv8kdxhp3MU9lR0h6yHH/OHFolqwJY5+Ne+rn/G1XCUjSTAwtoZNnURSX4Qb5Il/Z80qG/ga0aRjZ+McZ8xYQBUHOWF0oJVn42b6UHQpreRCZDc2wfLc0Tr6L2Iecb1XX767YF+bE2aPExhvw/15bYZ24VfqnghfJpDvlSnQ2Yj/SBX/RDSxxxuf28gJCzvSZddendzg9ERhiW2WOfU3G8wIr9FB1JReimjLx7lU1IAEDQ/RdbdZwX0x/xvWjk5bbkbrE/s6QKBgQD+O5TZWOeqtcZDvevQJayiRYOIxwPAvy9oa3VO20LdlfOgtSpHAy1sStusxHxHdWXJhVcOXXXXHPownV9HfZBqcTmyetB0I83gPgn3g/TCm2H7T03n54ZETmPckh91JL9U8H1jK23QMjmR5whlw/diet7CtUgRYsK+IM8njPGFrQKBgQDYkzhnfDL13ak5Efg9H71jEo7qeqXBxkxzzEI/JyPeFzxvJsYKuS9ZASxtBoV5Z0uaeugPR7LSLvR+IBpCGSxoqXkoaxY5Cs6KY3BHaHQKq9b6ma3BBX2GeQ+0PwFOiP4YLEHEvNGsuCim8cHNFMJGa7N0uJCpDlZieuA1VyB0YwKBgQDs9GWAyr9qOfAoFW0j3OlxeW7mXe+eh9NM9NMqg2xoESo4wII+G7ULeR4UgjH/fGk5kcEZT3zU2VpLU3KJtHuU6iFHu/ZsOS7a8ZfijafkdmS1ki3flshrA9FJtRwC2BRIu7lyY/j/EsDbv0TxbNw3eqQDQGmCYtV11iieCVzXJQKBgH2AwtzlUjq8WwYhbbMuI3e4F2216Txjh4ZLRdCHA/f9ix32YClyqwdu8Km1b7+splk9BCFmsS+v2isEu3K2V8/G9dkARX6Ezq/PdA9NboGIlyndyJzka5tzqDARmtZB+M1VSD+UFAV9KsGjs2T+tJ5OZ04qblopXtHu1uLSPJJ1AoGBAITyAhxwGX6MmjMszmAIu8b0OCcoczvP9V2vMG1C3VOURez2xkQGYRY8wMSbLjTa7O7DLCcU3NDU4ZmVoVuMFugFzjfZNX9iqAiYjNI+YXzkRoKBah9WhcBiYOwLCRw0THOktN/J5CNB1recUlr+0QRGPB6Lxp6vlpZpkqffcv7c"; // 替换为实际私钥
        Signature signature = Signature.getInstance("SHA256WithRSA");
        signature.initSign(getPrivateKeyFromString(privateKey));
        signature.update(signString.getBytes(StandardCharsets.UTF_8)); // 明确指定UTF-8
        String sign = Base64.getEncoder().encodeToString(signature.sign());

        // 构建请求体
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        params.forEach((k, v) -> formData.add(k, v));
        formData.add("sign", sign);
        formData.add("sign_type", "RSA");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(formData, headers);

        return restTemplate.postForObject(url, request, String.class);
    }

    private void addEncodedParam(Map<String, String> params, String key, String value) throws Exception {
        if (value != null) {
            String encodedValue = URLEncoder.encode(value, StandardCharsets.UTF_8.toString());
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