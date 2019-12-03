package dev.hodory.restapi.common;

import static org.springframework.hateoas.core.DummyInvocationUtils.methodOn;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

import dev.hodory.restapi.index.IndexController;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;
import org.springframework.validation.Errors;

public class ErrorsResource extends Resource<Errors> {

  public ErrorsResource(Errors content, Link... links) {
    super(content, links);
    add(linkTo(methodOn(IndexController.class).index()).withRel("index"));
  }
}
