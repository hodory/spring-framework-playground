package dev.hodory.restapi.accounts;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.util.Set;
import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class AccountServiceTest {

  @Rule
  public ExpectedException expectedException = ExpectedException.none();
  @Autowired
  AccountService accountService;

  @Autowired
  AccountRepository accountRepository;

  @Test
  public void findByUsername() {
    // Given
    final String username = "mo02@outlook.kr";
    final String password = "hykim";
    final Account account = Account.builder()
        .email(username)
        .password(password)
        .roles(Set.of(AccountRole.ADMIN, AccountRole.USER))
        .build();

    this.accountRepository.save(account);

    // When
    final UserDetailsService userDetailsService = (UserDetailsService) accountService;
    final UserDetails userDetails = userDetailsService.loadUserByUsername(username);

    // Then
    assertThat(userDetails.getPassword()).isEqualTo(password);
  }

  @Test
  public void findByUsernameFail() {
    // Expected
    final String username = "master@hodory.dev";
    expectedException.expect(UsernameNotFoundException.class);
    expectedException.expectMessage(Matchers.containsString(username));

    accountService.loadUserByUsername(username);
  }
}