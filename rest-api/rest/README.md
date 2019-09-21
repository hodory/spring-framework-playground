# [스프링 기반 REST API 개발](https://www.inflearn.com/course/spring_rest-api/dashboard)

## 목적 
스프링부트로 일반적인 API 개발은 가능하나,<br/>
TDD 위주의 강의라는 리뷰를 보고 TDD를 학습하기 위해 해당 강좌를 듣기로 하여 진행한 repo

## 메모사항

- **Must Not**
    - `@EqualsAndHashCode` 의 of 에 사용할 필드를 주의 할 것(상호참조로 인한 Stack overflow 발생할 수 있다.)
    - Entity에 `@Data` 어노테이션을 사용하지 말것! (위와 동일한 사유)