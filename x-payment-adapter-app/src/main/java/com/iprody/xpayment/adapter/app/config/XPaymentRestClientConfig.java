package com.iprody.xpayment.adapter.app.config;

import com.iprody.xpayment.api.ApiClient;
import com.iprody.xpayment.api.client.DefaultApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
class XPaymentRestClientConfig {

    @Bean
    public RestTemplate xpaymentRestTemplate(
            @Value("${app.x-payment-provider-api.client.username}") String username,
            @Value("${app.x-payment-provider-api.client.password}") String password,
            @Value("${app.x-payment-provider-api.client.x-pay-account-header}") String xPayAccountHeader,
            @Value("${app.x-payment-provider-api.client.account}") String xPayAccount) {
        final RestTemplate restTemplate = new RestTemplate();

        restTemplate.getInterceptors().add((req, body, ex) -> {
            req.getHeaders().setBasicAuth(username, password);
            req.getHeaders().add(xPayAccountHeader, xPayAccount);
            return ex.execute(req, body);
        });

        return restTemplate;
    }

    @Bean
    public ApiClient xpaymentApiClient(@Value("${app.x-payment-provider-api.client.url}") String xPaymentUrl,
                                       RestTemplate xpaymentRestTemplate) {
        final ApiClient apiClient = new ApiClient(xpaymentRestTemplate);
        apiClient.setBasePath(xPaymentUrl);

        return apiClient;
    }

    @Bean
    public DefaultApi defaultApi(ApiClient apiClient) {
        return new DefaultApi(apiClient);
    }
}
