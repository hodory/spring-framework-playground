package dev.hodory.restapi.configs;

import dev.hodory.restapi.accounts.Account;
import dev.hodory.restapi.accounts.AccountRole;
import dev.hodory.restapi.accounts.AccountService;
import java.util.Set;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class AppConfig {

  @Bean
  public PasswordEncoder passwordEncoder() {
    return PasswordEncoderFactories.createDelegatingPasswordEncoder();
  }

  @Bean
  public ModelMapper modelMapper() {
    return new ModelMapper();
  }

  @Bean
  public ApplicationRunner applicationRunner() {
    return new ApplicationRunner() {
      @Autowired
      AccountService accountService;

      @Override
      public void run(ApplicationArguments args) throws Exception {
        final Account account = Account.builder()
            .email("me@hodory.dev")
            .password("password@test")
            .roles(Set.of(AccountRole.ADMIN))
            .build();
        accountService.saveAccount(account);
      }
    };
  }
}
