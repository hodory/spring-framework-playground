package dev.hodory.restapi.events;

import java.net.URI;
import org.springframework.hateoas.MediaTypes;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/events", produces = MediaTypes.HAL_JSON_UTF8_VALUE)
public class EventController {

    @PostMapping
    public ResponseEntity createEvent(@RequestBody final Event event) {
        final URI createdUri = linkTo(EventController.class).slash("{id}").toUri();
        event.setId(20);
        return ResponseEntity.created(createdUri).body(event);
    }
}
