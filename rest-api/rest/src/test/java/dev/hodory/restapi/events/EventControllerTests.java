package dev.hodory.restapi.events;

import java.time.LocalDateTime;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.MediaType;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest
public class EventControllerTests {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;


    @Test
    public void createEvent() throws Exception {
        final Event event = Event.builder().name("Spring Framework")
                .description("REST API Development With Spring Framework")
                .beginEnrollmentDateTime(LocalDateTime.of(2019, 9, 22, 0, 0))
                .endEnrollmentDateTime(LocalDateTime.of(2019, 9, 23, 23, 59))
                .beginEventDateTime(LocalDateTime.of(2019, 9, 25, 22, 59))
                .endEventDateTime(LocalDateTime.of(2019, 9, 25, 23, 59)).basicPrice(100)
                .maxPrice(200).limitOfEnrollment(100).location("강남").build();

        mockMvc.perform(post("/api/events/").contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(objectMapper.writeValueAsString(event)).accept(MediaTypes.HAL_JSON))
                .andDo(print()).andExpect(status().isCreated()).andExpect(jsonPath("id").exists());
    }
}
