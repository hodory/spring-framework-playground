# [스프링 기반 REST API 개발](https://www.inflearn.com/course/spring_rest-api/dashboard)

## 목적 
스프링부트로 일반적인 API 개발은 가능하나,<br/>
TDD 위주의 강의라는 리뷰를 보고 TDD를 학습하기 위해 해당 강좌를 듣기로 하여 진행한 repo

## 메모사항

- **Must Not**
    - `@EqualsAndHashCode` 의 of 에 사용할 필드를 주의 할 것(상호참조로 인한 Stack overflow 발생할 수 있다.)
    - Entity에 `@Data` 어노테이션을 사용하지 말것! (위와 동일한 사유)
- 생성자가 하나만 있고, 생성자로 받아올 파라미터가 이미 Bean으로 등록되어 있으면 @Autowired 어노테이션을 생략해도된다(Spring Ver. >= 4.3)
    - ex) 
    ```java
    public class EventController{
  
      private final EventRepository eventRepository;
  
      public EventController(EventRepository eventRepository){
          this.eventRepository = eventRepository;
      }
    }
    ```
- Mockito를 이용해서 Repository를 `@MockBean` 으로 Mocking 하였는데도 NullPointException이 나는 이유
-> 해당 API에서 받을 때, DTO로 받아 ID 값이 빠져 있고 그것을 Model Mapper를 통해 `Event` Class로 변환하고 Repository에 Save 하였음.
이 과정에서 ID값은 빠져 있는 Event Class Object 이기 때문에 NullPointException이 발생한다.
- Mocking 해야할게 많을때는 `@SpringBootTest` 어노테이션을 사용해서 테스트에 필요한 모든 Bean들이 등록되도록 하는게
더 간편하게 테스트 할 수 있음. -> Application의 `@SpringBootApplcation` 어노테이션을 찾아서 모든 Bean이 등록 되도록 해주는 기능.

Deserialization -> Json 문자열을 Object로 변환하는 과정


### Errors 객체를 Body에 담을 수 없는 이유

---
도메인 객체는 자바 빈(Bean) 스펙을 준수 하기 때문에 BeanSerializer를 이용해 Serialization을 할 수 있어 JSON으로 변환 할 수 있지만,<br/>
Errors 객체는 자바 빈 스펙을 준수하고 있는 객체가 아니라서 BeanSerializer를 이용해 JSON으로 변환 할 수 없다.

ObjectMapper에 등록을 하기 위해 스프링 부트가 제공하는`@JsonComponent` 어노테이션을 사용하면 등록이 완료된다.

### Spring HATEOAS를 바로 사용 가능한 이유
`@EnableHypermediaSupport`와 같은 어노테이션을 붙여야지만 Spring HATEOAS 기능을 사용할 수 있는데<br/>
Spring Boot에서 자동 설정을 해주고 있기 때문이다.

### REST DOCS 설정 방법

---
문서상에서는 mockMvc를 사용할 때
```java
private MockMvc mockMvc;

@Autowired
private WebApplicationContext context;

@Before
public void setUp() {
	this.mockMvc = MockMvcBuilders.webAppContextSetup(this.context)
			.apply(documentationConfiguration(this.restDocumentation)) 
			.build();
}
```
이와 같이 설정 해야 하지만, 스프링 부트를 사용할 때에는 자동으로 설정이 되어 있다.


### Spring RestDocs SnippetException

---
```
org.springframework.restdocs.snippet.SnippetException: The following parts of the payload were not documented:
{
  "_links" : {
    "self" : {
      "href" : "http://localhost:8080/api/events/1"
    },
    "query-events" : {
      "href" : "http://localhost:8080/api/events"
    },
    "update-event" : {
      "href" : "http://localhost:8080/api/events/1"
    }
  }
}
```
이와 같은 에러가 나는 이유는 본문에 `_links` 정보가 들어 있기 때문에,
`responseFields()`를 호출할때 응답 필드들을 검증을 하는데, `_links`가 누락 되어서  이와 같은 오류가 발생한다.

이때에는 해결방안중 하나로 `relaxedResponseFields()`를 사용하여 응답의 일부분만 검증 할 수 있다.

하지만 이렇게 처리 할 경우 일부분만 테스트하기때문에 정확한 문서를 만들지 못할 수 있는 단점이 있으므로,<br/>
`_links` 를 `responseFields()`에 넣어 처리하도록한다.


### TEST 환경과 DB 분리

---

`application.yml` 또는 `application.properties`를 `test\resources` 디렉토리에 동일하게 두면 파일명이 같기 때문에<br/>
빌드시에 `main\resources`로 덮어쓰기가 된다.
메인 컴파일 -> 테스트쪽으로 resources를 복사 -> 테스트 컴파일 -> 테스트 resources를 복사
중복 설정이 많아지기때문에 덮어쓸 값들만 파일명을 변경하여 `test\resources`디렉토리에 설정하면된다.
이때 `@ActiveProfiles` 어노테이션을 사용해서 테스트 코드에 어떠한 값을 사용할지 명시해주어야한다. `ex) @ActiveProfiles("test")` 

### JSON Array는 Unwrap 되지 않는다.

--- 

`@JsonUnwrap` 어노테이션의 주석을 확인하면
> Value is serialized as JSON Object (can not unwrap JSON arrays using this mechanism)

이와 같이 되어있다. 

### ENUM Type

---

roles와 같은 필드는 ElementCollection을 이용해서 여러가지를 가질 수 있도록 해야한다<br/>
Set이나 List는 fetch 타입이 `Lazy fetch` 이다.

### WebSecurity & Method Security

Method Security는 웹과 상관없이 메소드가 호출되었을때 인증 또는 권한을 확인해주는 시큐리티이다.
두개 모두 공통된 SecuriyInterceptor를 사용한다.

Web Security는 Servlet과 연관이 되어있다.(Filter 기반 Security)

요청이 들어오면 `Servlet Filter`가 가로채서 스프링 Filter Security Interceptor 쪽으로 요청을 보내고,<br/>
인증이 필요한지 확인 후 필터가 필요하다면 `Security Interceptor`에 들어오게된다.

#### 인증(Authentication)
그 후, `Security Context Holder(Thread Local)`에 인증 정보가 없을 경우, `AuthenticationManager`를 이용하여 로그인을 한다.<br/>
로그인이 성공하면, `Security Context Holder`에 인증 정보를 저장한다.

#### 인가(Authorization)
인증 정보를 가지고 `AccessDecisionManager`를 이용해 적절한 권한을 가지고 있는지 확인한다.(주로 유저의 `role`을 확인한다.)

### AuthenticationManager

---

#### 주요 사용하는 메소드
- UserDetailsService
- PasswordEncoder : 유저가 입력한 패스워드가 맞는지 검사
  
#### Basic Authentication
`Authentication: Basic {userId + Password가 인코딩된 문자열}` 을 헤더로 입력 받음.