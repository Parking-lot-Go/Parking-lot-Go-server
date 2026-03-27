package car.demo.global.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

  @Bean
  @Qualifier("seoulApiClient")
  public WebClient webClient() {
    return WebClient.builder()
        .baseUrl("http://openapi.seoul.go.kr:8088")
        .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        .codecs(configurer -> configurer
            .defaultCodecs()
            .maxInMemorySize(2 * 1024 * 1024)) // 2MB
        .build();
  }

  @Bean
  @Qualifier("GyeonggiDo")
  public WebClient webClientGyy(){
    return WebClient.builder()
        .baseUrl("https://openapi.gg.go.kr")
        .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        .codecs(configurer -> configurer
            .defaultCodecs()
            .maxInMemorySize(2 * 1024 * 1024)) // 2MB
        .build();
  }

}
