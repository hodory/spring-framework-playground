package dev.hodory.restapi.configs;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import dev.hodory.restapi.accounts.Account;
import dev.hodory.restapi.accounts.AccountRole;
import dev.hodory.restapi.accounts.AccountService;
import dev.hodory.restapi.common.BaseControllerTest;
import dev.hodory.restapi.common.TestDescription;
import java.util.Set;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;


public class AuthServerConfigTest extends BaseControllerTest {

  @Autowired
  AccountService accountService;


  @Test
  @TestDescription("인증 토큰을 받는 테스트")
  public void getAuthToken() throws Exception {
    // Given
    final String username = "mo02@outlook.kr";
    final String password = "my@password";
    final Account account = Account.builder().email(username)
        .password(password)
        .roles(Set.of(AccountRole.ADMIN, AccountRole.USER)).build();
    accountService.saveAccount(account);

    final String clientId = "myApp";
    final String clientSecret = "pass";

    this.mockMvc.perform(post("/oauth/token")
        .with(httpBasic(clientId, clientSecret))
        .param("username", username)
        .param("password", password)
        .param("grant_type", "password"))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("access_token").exists());
  }

}