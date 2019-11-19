package dev.hodory.restapi.events;

import static org.assertj.core.api.Assertions.assertThat;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(JUnitParamsRunner.class)
public class EventTest {

  @Test
  public void builder() {
    Event event = Event.builder()
        .name("Spring REST API")
        .description("REST API Development with Spring").build();
    assertThat(event).isNotNull();
  }

  @Test
  public void javaBean() {
    // given
    String name = "Spring REST API";
    String description = "Spring";

    // when
    Event event = new Event();
    event.setName(name);
    event.setDescription(description);

    // then
    assertThat(event.getName()).isEqualTo(name);
    assertThat(event.getDescription()).isEqualTo(description);
  }

  @Test
  @Parameters
  public void testFree(int basePrice, int maxPrice, boolean isFree) {
    // given
    Event event = Event.builder()
        .basePrice(basePrice)
        .maxPrice(maxPrice)
        .build();

    // when
    event.update();

    // then
    assertThat(event.isFree()).isEqualTo(isFree);
  }

  private Object[] parametersForTestFree() {
    return new Object[]{
        new Object[]{0, 0, true},
        new Object[]{100, 0, false},
        new Object[]{0, 100, false},
        new Object[]{100, 200, false}
    };
  }

  @Test
  @Parameters
  public void testOffline(String location, boolean isOffline) {
    // given
    Event event = Event.builder()
        .location(location)
        .build();

    // when
    event.update();

    // then
    assertThat(event.isOffline()).isEqualTo(isOffline);
  }

  private Object[] parametersForTestOffline() {
    return new Object[]{
        new Object[]{"강남역 네이버 D2 스타트업 팩토리", true},
        new Object[]{null, false},
        new Object[]{"            ", false}
    };
  }
}