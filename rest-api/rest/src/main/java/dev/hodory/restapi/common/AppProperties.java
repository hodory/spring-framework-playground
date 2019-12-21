package dev.hodory.restapi.common;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "my-app")
public class AppProperties {

  private String adminUsername;
  private String adminPassword;

  private String userUsername;
  private String userPassword;

  private String clientId;
  private String clientSecret;
}
