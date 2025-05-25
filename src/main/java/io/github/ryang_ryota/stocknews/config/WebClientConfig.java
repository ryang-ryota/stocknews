package io.github.ryang_ryota.stocknews.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * WebClientの設定を行うConfigurationクラス
 */
@Configuration
public class WebClientConfig {

    /**
     * Alpha Vantage APIのベースURL
     */
    @Value("${alpha-vantage.base-url}")
    private String baseUrl;

    /**
     * Alpha Vantage API用のWebClientを生成する
     *
     * @return 設定済みのWebClientインスタンス
     */
    @Bean
    public WebClient alphaVantageWebClient() {
        return WebClient.builder()
                .baseUrl(baseUrl)
                .build();
    }
}