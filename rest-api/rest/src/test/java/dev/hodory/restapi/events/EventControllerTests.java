package dev.hodory.restapi.events;

import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.headers.HeaderDocumentation.responseHeaders;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import dev.hodory.restapi.accounts.Account;
import dev.hodory.restapi.accounts.AccountRepository;
import dev.hodory.restapi.accounts.AccountRole;
import dev.hodory.restapi.accounts.AccountService;
import dev.hodory.restapi.common.AppProperties;
import dev.hodory.restapi.common.BaseControllerTest;
import dev.hodory.restapi.common.TestDescription;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.IntStream;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.common.util.Jackson2JsonParser;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

public class EventControllerTests extends BaseControllerTest {

  @Autowired
  EventRepository eventRepository;

  @Autowired
  AccountService accountService;

  @Autowired
  AccountRepository accountRepository;
  @Autowired
  AppProperties appProperties;

  @Before
  public void setUp() {
    this.eventRepository.deleteAll();
    this.accountRepository.deleteAll();
  }

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
        .header(HttpHeaders.AUTHORIZATION, getBearerToken())
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
        .andDo(document(
            "create-event",
            links(
                linkWithRel("self").description("link to self"),
                linkWithRel("query-events").description("link to query events"),
                linkWithRel("update-event").description("link to update an existing event"),
                linkWithRel("profile").description("link to profile")
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
                fieldWithPath("manager").description("manager of new event"),
                fieldWithPath("manager.id").description("manager of new event"),
                fieldWithPath("_links.self.href").description("link to self"),
                fieldWithPath("_links.query-events.href").description("link to query event list"),
                fieldWithPath("_links.update-event.href")
                    .description("link to update existing event"),
                fieldWithPath("_links.profile.href")
                    .description("link to profile")
            )
        ));
  }

  private String getBearerToken() throws Exception {
    return "Bearer " + getAccessToken(true);
  }

  private String getBearerToken(boolean needToMakeAccount) throws Exception {
    return "Bearer " + getAccessToken(needToMakeAccount);
  }

  private String getAccessToken(boolean needToMakeAccount) throws Exception {

    // Given
    if (needToMakeAccount) {
      createAccount();
    }

    ResultActions perform = this.mockMvc.perform(MockMvcRequestBuilders.post("/oauth/token")
        .with(httpBasic(appProperties.getClientId(), appProperties.getClientSecret()))
        .param("username", appProperties.getUserUsername())
        .param("password", appProperties.getUserPassword())
        .param("grant_type", "password"));

    var responseBody = perform.andReturn().getResponse().getContentAsString();
    Jackson2JsonParser parser = new Jackson2JsonParser();
    return parser.parseMap(responseBody).get("access_token").toString();
  }

  private Account createAccount() {
    final String username = "user@hodory.dev";
    final String password = "user@password";
    final Account account = Account.builder().email(username)
        .password(password)
        .roles(Set.of(AccountRole.ADMIN, AccountRole.USER)).build();
    return accountService.saveAccount(account);
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
        .header(HttpHeaders.AUTHORIZATION, getBearerToken())
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
        .header(HttpHeaders.AUTHORIZATION, getBearerToken())
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
        .header(HttpHeaders.AUTHORIZATION, getBearerToken())
        .contentType(MediaType.APPLICATION_JSON_UTF8)
        .content(this.objectMapper.writeValueAsString(eventDto)))
        .andDo(print())
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("content[0].objectName").exists())
        .andExpect(jsonPath("content[0].defaultMessage").exists())
        .andExpect(jsonPath("content[0].code").exists())
        .andExpect(jsonPath("_links.index").exists());
  }


  @Test
  @TestDescription("30개의 이벤트를 10개씩 두번째 페이지 조회하기")
  public void queryEvents() throws Exception {
    // Given
    IntStream.range(0, 30).forEach(this::generateEvent);

    // When
    this.mockMvc.perform(get("/api/events")
        .param("page", "1")
        .param("size", "10")
        .param("sort", "name,DESC"))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("page").exists())
        .andExpect(jsonPath("_embedded.eventList[0]._links.self").exists())
        .andExpect(jsonPath("_links.self").exists())
        .andExpect(jsonPath("_links.profile").exists())
        .andDo(
            document("query-events",
                links(
                    linkWithRel("self").description("link to self"),
                    linkWithRel("profile").description("link to profile"),
                    linkWithRel("first").description("link to first event list"),
                    linkWithRel("prev")
                        .description("link to prev event list"),
                    linkWithRel("self")
                        .description("link to self"),
                    linkWithRel("next")
                        .description("link to next event list"),
                    linkWithRel("last")
                        .description("link to last event list"),
                    linkWithRel("profile")
                        .description("link to profile")
                ),
                responseFields(
                    fieldWithPath("_embedded.eventList[0].id")
                        .description("identifier of new event"),
                    fieldWithPath("_embedded.eventList[0].name").description("Name of new event"),
                    fieldWithPath("_embedded.eventList[0].description")
                        .description("Description of new event"),
                    fieldWithPath("_embedded.eventList[0].beginEnrollmentDateTime")
                        .description("date time of begin of new event"),
                    fieldWithPath("_embedded.eventList[0].closeEnrollmentDateTime")
                        .description("date time of close of new event"),
                    fieldWithPath("_embedded.eventList[0].beginEventDateTime")
                        .description("date time of begin of new event"),
                    fieldWithPath("_embedded.eventList[0].endEventDateTime")
                        .description("date time of end of new event"),
                    fieldWithPath("_embedded.eventList[0].location")
                        .description("location of new event"),
                    fieldWithPath("_embedded.eventList[0].basePrice")
                        .description("base price of new event"),
                    fieldWithPath("_embedded.eventList[0].maxPrice")
                        .description("max price of new event"),
                    fieldWithPath("_embedded.eventList[0].limitOfEnrollment")
                        .description("limit of enrollment of new event"),
                    fieldWithPath("_embedded.eventList[0].free")
                        .description("it tells if this event is free or not"),
                    fieldWithPath("_embedded.eventList[0].offline")
                        .description("it tells if this event is offline event or not"),
                    fieldWithPath("_embedded.eventList[0].eventStatus")
                        .description("event Status"),
                    fieldWithPath("_embedded.eventList[0].manager")
                        .description("manager of event"),
                    fieldWithPath("_embedded.eventList[0]._links.self.href")
                        .description("link to self"),
                    fieldWithPath("_links.first.href").description("link to first event list"),
                    fieldWithPath("_links.prev.href")
                        .description("link to prev event list"),
                    fieldWithPath("_links.self.href")
                        .description("link to self"),
                    fieldWithPath("_links.next.href")
                        .description("link to next event list"),
                    fieldWithPath("_links.last.href")
                        .description("link to last event list"),
                    fieldWithPath("_links.profile.href")
                        .description("link to profile"),
                    fieldWithPath("page.size")
                        .description("List perPage Count"),
                    fieldWithPath("page.totalElements")
                        .description("Event Total Count"),
                    fieldWithPath("page.totalPages")
                        .description("Event List Total Page"),
                    fieldWithPath("page.number")
                        .description("Current Page Number")
                )
            )
        );
  }

  @Test
  @TestDescription("30개의 이벤트를 10개씩 두번째 페이지 조회하기")
  public void queryEventsWithToken() throws Exception {
    // Given
    IntStream.range(0, 30).forEach(this::generateEvent);

    // When
    this.mockMvc.perform(get("/api/events")
        .header(HttpHeaders.AUTHORIZATION, getBearerToken())
        .param("page", "1")
        .param("size", "10")
        .param("sort", "name,DESC"))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("page").exists())
        .andExpect(jsonPath("_embedded.eventList[0]._links.self").exists())
        .andExpect(jsonPath("_links.self").exists())
        .andExpect(jsonPath("_links.profile").exists())
        .andExpect(jsonPath("_links.create-event").exists());
  }

  @Test
  @TestDescription("기존의 이벤트 하나 조회하기")
  public void getEvent() throws Exception {
    // Given
    final Account account = this.createAccount();
    final Event event = this.generateEvent(100, account);

    // When & Then
    this.mockMvc.perform(get("/api/events/{id}", event.getId()))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("id").exists())
        .andExpect(jsonPath("name").exists())
        .andExpect(jsonPath("_links.self").exists())
        .andExpect(jsonPath("_links.profile").exists())
        .andDo(document("get-an-event"));

  }

//  @Test
//  @TestDescription("기존의 이벤트 하나 조회하기")
//  public void getEventWithToken() throws Exception {
//    // Given
//    final Event event = this.generateEvent(100);
//
//    // When & Then
//    this.mockMvc.perform(
//        get("/api/events/{id}", event.getId())
//            .header(HttpHeaders.AUTHORIZATION, getBearerToken()))
//        .andDo(print())
//        .andExpect(status().isOk())
//        .andExpect(jsonPath("id").exists())
//        .andExpect(jsonPath("name").exists())
//        .andExpect(jsonPath("_links.self").exists())
//        .andExpect(jsonPath("_links.profile").exists())
//        .andDo(document("get-an-event"));
//
//  }

  @Test
  @TestDescription("없는 이벤트 조회시 404 응답 받기")
  public void getEvent404() throws Exception {
    // When & Then
    this.mockMvc.perform(get("/api/events/11832"))
        .andExpect(status().isNotFound());
  }

  @Test
  @TestDescription("정상적으로 이벤트를 수정하는 테스트")
  public void updateEvent() throws Exception {
    // Given
    final Account account = this.createAccount();
    final Event event = this.generateEvent(10, account);

    final String eventName = "Spring Test Framework";
    final EventDto newEvent = modelMapper.map(event, EventDto.class);
    newEvent.setName(eventName);

    // When & Then
    this.mockMvc.perform(
        put("/api/events/{id}", event.getId())
            .header(HttpHeaders.AUTHORIZATION, getBearerToken(false))
            .contentType(MediaType.APPLICATION_JSON_UTF8)
            .content(objectMapper.writeValueAsString(newEvent))
            .accept(MediaTypes.HAL_JSON)
    ).andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("id").exists())
        .andExpect(jsonPath("name").value(eventName))
        .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaTypes.HAL_JSON_UTF8_VALUE))
        .andExpect(jsonPath("free").value(false))
        .andExpect(jsonPath("offline").value(true))
        .andExpect(jsonPath("eventStatus").value(EventStatus.DRAFT.name()))
        .andDo(document("update-an-event"));
  }

  @Test
  @TestDescription("입력이 비어있는 경우 이벤트 수정 실패")
  public void updateEvent400_Empty() throws Exception {
    // Given
    final Event event = this.generateEvent(10);
    final EventDto eventDto = new EventDto();

    // When & Then
    this.mockMvc.perform(
        put("/api/events/{id}", event.getId())
            .header(HttpHeaders.AUTHORIZATION, getBearerToken())
            .contentType(MediaType.APPLICATION_JSON_UTF8)
            .content(objectMapper.writeValueAsString(eventDto))
            .accept(MediaTypes.HAL_JSON)
    ).andDo(print())
        .andExpect(status().isBadRequest());
  }

  @Test
  @TestDescription("입력이 잘못된 경우 이벤트 수정 실패")
  public void updateEvent400_WrongRequest() throws Exception {
    // Given
    final Event event = this.generateEvent(10);
    final EventDto eventDto = this.modelMapper.map(event, EventDto.class);
    eventDto.setBasePrice(20000);
    eventDto.setMaxPrice(10000);

    // When & Then
    this.mockMvc.perform(
        put("/api/events/{id}", event.getId())
            .header(HttpHeaders.AUTHORIZATION, getBearerToken())
            .contentType(MediaType.APPLICATION_JSON_UTF8)
            .content(objectMapper.writeValueAsString(eventDto))
            .accept(MediaTypes.HAL_JSON)
    ).andDo(print())
        .andExpect(status().isBadRequest());
  }

  @Test
  @TestDescription("존재하지 않는 이벤트 수정 실패")
  public void updateEvent404() throws Exception {
    // Given
    final Event event = this.generateEvent(440);
    final EventDto eventDto = this.modelMapper.map(event, EventDto.class);

    // When & Then
    this.mockMvc.perform(
        put("/api/events/1231231")
            .header(HttpHeaders.AUTHORIZATION, getBearerToken())
            .contentType(MediaType.APPLICATION_JSON_UTF8)
            .content(objectMapper.writeValueAsString(eventDto))
            .accept(MediaTypes.HAL_JSON)
    ).andDo(print())
        .andExpect(status().isNotFound());
  }

  private Event generateEvent(final int index, final Account account) {
    final Event event = buildEvent(index);
    event.setManager(account);
    return this.eventRepository.save(event);
  }

  private Event generateEvent(int index) {
    final Event event = buildEvent(index);
    return this.eventRepository.save(event);
  }

  private Event buildEvent(int index) {
    return Event.builder()
        .name("event " + index)
        .description("test event")
        .beginEnrollmentDateTime(LocalDateTime.of(2019, 9, 22, 0, 0))
        .closeEnrollmentDateTime(LocalDateTime.of(2019, 9, 23, 23, 59))
        .beginEventDateTime(LocalDateTime.of(2019, 9, 25, 22, 59))
        .endEventDateTime(LocalDateTime.of(2019, 9, 25, 23, 59))
        .basePrice(100)
        .maxPrice(200)
        .limitOfEnrollment(100)
        .location("강남")
        .free(false)
        .offline(true)
        .eventStatus(EventStatus.DRAFT)
        .build();
  }
}
