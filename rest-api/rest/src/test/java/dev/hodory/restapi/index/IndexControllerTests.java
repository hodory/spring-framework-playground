package dev.hodory.restapi.index;

import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import dev.hodory.restapi.common.BaseControllerTest;
import org.junit.Test;

public class IndexControllerTests extends BaseControllerTest {

  @Test
  public void index() throws Exception {
    this.mockMvc.perform(get("/api/"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("_links.events").exists());
  }
}
