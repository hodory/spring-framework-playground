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