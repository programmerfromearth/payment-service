package com.iprody.payment.service.app.config;

import com.iprody.payment.service.app.common.api.TimeProvider;
import com.iprody.payment.service.app.common.impl.SystemTimeProviderImpl;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TimeProviderConfig {

    @Bean
    @ConditionalOnMissingBean(TimeProvider.class)
    public TimeProvider systemTimeProvider() {
        return new SystemTimeProviderImpl();
    }
}
