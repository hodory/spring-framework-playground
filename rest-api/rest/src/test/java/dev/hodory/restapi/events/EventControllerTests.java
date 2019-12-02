package dev.hodory.restapi.events;

import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.headers.HeaderDocumentation.responseHeaders;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.hodory.restapi.common.RestDocsConfiguration;
import dev.hodory.restapi.common.TestDescription;
import java.time.LocalDateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs
@Import(RestDocsConfiguration.class)
public class EventControllerTests {

  @Autowired
  MockMvc mockMvc;

  @Autowired
  ObjectMapper objectMapper;

  @Test
  @TestDescription("정상적인 이벤트를 등록하는 테스트")
  public void createEvent() throws Exception {
    final EventDto event = EventDto.builder().name("Spring Framework")
        .description("REST API Development With Spring Framework")
        .beginEnrollmentDateTime(LocalDateTime.of(2019, 9, 22, 0, 0))
        .closeEnrollmentDateTime(LocalDateTime.of(2019, 9, 23, 23, 59))
        .beginEventDateTime(LocalDateTime.of(2019, 9, 25, 22, 59))
        .endEventDateTime(LocalDateTime.of(2019, 9, 25, 23, 59))
        .basePrice(100)
        .maxPrice(200)
        .limitOfEnrollment(100)
        .location("강남")
        .build();

    mockMvc.perform(post("/api/events/")
        .contentType(MediaType.APPLICATION_JSON_UTF8)
        .content(objectMapper.writeValueAsString(event))
        .accept(MediaTypes.HAL_JSON))
        .andDo(print())
        .andExpect(status().isCreated())
        .andExpect(jsonPath("id").exists())
        .andExpect(header().exists(HttpHeaders.LOCATION))
        .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaTypes.HAL_JSON_UTF8_VALUE))
        .andExpect(jsonPath("free").value(false))
        .andExpect(jsonPath("offline").value(true))
        .andExpect(jsonPath("eventStatus").value(EventStatus.DRAFT.name()))
        .andExpect(jsonPath("_links.self").exists())
        .andExpect(jsonPath("_links.query-events").exists())
        .andExpect(jsonPath("_links.update-event").exists())
        .andDo(document(
            "create-event",
            links(
                linkWithRel("self").description("link to self"),
                linkWithRel("query-events").description("link to query events"),
                linkWithRel("update-event").description("link to update an existing event")
            ),
            requestHeaders(
                headerWithName(HttpHeaders.ACCEPT).description("accept header"),
                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type")
            ),
            requestFields(
                fieldWithPath("name").description("Name of new event"),
                fieldWithPath("description").description("Description of new event"),
                fieldWithPath("beginEnrollmentDateTime")
                    .description("date time of begin of new event"),
                fieldWithPath("closeEnrollmentDateTime")
                    .description("date time of close of new event"),
                fieldWithPath("beginEventDateTime").description("date time of begin of new event"),
                fieldWithPath("endEventDateTime").description("date time of end of new event"),
                fieldWithPath("location").description("location of new event"),
                fieldWithPath("basePrice").description("base price of new event"),
                fieldWithPath("maxPrice").description("max price of new event"),
                fieldWithPath("limitOfEnrollment").description("limit of enrollment of new event")
            ),
            responseHeaders(
                headerWithName(HttpHeaders.LOCATION).description("Location Header"),
                headerWithName(HttpHeaders.CONTENT_TYPE).description("Content type")
            ),
            responseFields(
                fieldWithPath("id").description("identifier of new event"),
                fieldWithPath("name").description("Name of new event"),
                fieldWithPath("description").description("Description of new event"),
                fieldWithPath("beginEnrollmentDateTime")
                    .description("date time of begin of new event"),
                fieldWithPath("closeEnrollmentDateTime")
                    .description("date time of close of new event"),
                fieldWithPath("beginEventDateTime").description("date time of begin of new event"),
                fieldWithPath("endEventDateTime").description("date time of end of new event"),
                fieldWithPath("location").description("location of new event"),
                fieldWithPath("basePrice").description("base price of new event"),
                fieldWithPath("maxPrice").description("max price of new event"),
                fieldWithPath("limitOfEnrollment").description("limit of enrollment of new event"),
                fieldWithPath("free").description("it tells if this event is free or not"),
                fieldWithPath("offline")
                    .description("it tells if this event is offline event or not"),
                fieldWithPath("eventStatus")
                    .description("event Status"),
                fieldWithPath("_links.self.href").description("link to self"),
                fieldWithPath("_links.query-events.href").description("link to query event list"),
                fieldWithPath("_links.update-event.href")
                    .description("link to update existing event")
            )
        ));
  }

  @Test
  @TestDescription("입력 받을 수 없는 값을 사용하여 에러가 발생하는 테스트")
  public void createEventBadRequest() throws Exception {
    final Event event = Event.builder()
        .id(100)
        .name("Spring")
        .description("REST API Development with Spring")
        .beginEnrollmentDateTime(LocalDateTime.of(2018, 11, 23, 14, 21))
        .closeEnrollmentDateTime(LocalDateTime.of(2018, 11, 24, 14, 21))
        .beginEventDateTime(LocalDateTime.of(2018, 11, 25, 14, 21))
        .endEventDateTime(LocalDateTime.of(2018, 11, 26, 14, 21))
        .basePrice(100)
        .maxPrice(200)
        .limitOfEnrollment(100)
        .location("강남역 D2 스타텁 팩토리")
        .free(true)
        .offline(false)
        .eventStatus(EventStatus.PUBLISHED)
        .build();

    mockMvc.perform(post("/api/events/")
        .contentType(MediaType.APPLICATION_JSON_UTF8)
        .content(objectMapper.writeValueAsString(event))
        .accept(MediaTypes.HAL_JSON))
        .andDo(print())
        .andExpect(status().isBadRequest());
  }

  @Test
  @TestDescription("입력 값이 비어 있는 경우 에러가 발생하는 테스트")
  public void createEventBadRequestEmptyInput() throws Exception {
    final EventDto eventDto = EventDto.builder().build();

    mockMvc.perform(post("/api/events/")
        .contentType(MediaType.APPLICATION_JSON_UTF8)
        .content(this.objectMapper.writeValueAsString(eventDto)))
        .andExpect(status().isBadRequest());
  }

  @Test
  @TestDescription("입력값이 잘못된 경우에 에러가 발생하는 테스트")
  public void createEventBadRequestWrongInput() throws Exception {
    final EventDto eventDto = EventDto.builder()
        .name("Spring")
        .description("REST API Development with Spring")
        .beginEnrollmentDateTime(LocalDateTime.of(2018, 11, 26, 14, 21))
        .closeEnrollmentDateTime(LocalDateTime.of(2018, 11, 25, 14, 21))
        .beginEventDateTime(LocalDateTime.of(2018, 11, 24, 14, 21))
        .endEventDateTime(LocalDateTime.of(2018, 11, 23, 14, 21))
        .basePrice(10000)
        .maxPrice(200)
        .limitOfEnrollment(100)
        .location("강남역 D2 스타텁 팩토리")
        .build();

    mockMvc.perform(post("/api/events/")
        .contentType(MediaType.APPLICATION_JSON_UTF8)
        .content(this.objectMapper.writeValueAsString(eventDto)))
        .andDo(print())
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$[0].objectName").exists())
        .andExpect(jsonPath("$[0].defaultMessage").exists())
        .andExpect(jsonPath("$[0].code").exists());
  }
}
