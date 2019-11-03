package dev.hodory.restapi.common;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import java.io.IOException;
import org.springframework.boot.jackson.JsonComponent;
import org.springframework.validation.Errors;

@JsonComponent
public class ErrorsSerializer extends JsonSerializer<Errors> {

  @Override
  public void serialize(Errors errors, JsonGenerator gen, SerializerProvider serializers)
      throws IOException {
    gen.writeStartArray();
    errors.getFieldErrors().forEach(err -> {
      try {
        gen.writeStartObject();
        gen.writeObjectField("field", err.getField());
        gen.writeObjectField("objectName", err.getObjectName());
        gen.writeObjectField("code", err.getCode());
        gen.writeObjectField("defaultMessage", err.getDefaultMessage());

        Object rejectedValue = err.getRejectedValue();
        if (null != rejectedValue) {
          gen.writeObjectField("rejectedValue", rejectedValue.toString());
        }
        gen.writeEndObject();
      } catch (IOException e) {
        e.printStackTrace();
      }
    });

    errors.getGlobalErrors().forEach(err -> {
      try {
        gen.writeStartObject();
        gen.writeObjectField("objectName", err.getObjectName());
        gen.writeObjectField("code", err.getCode());
        gen.writeObjectField("defaultMessage", err.getDefaultMessage());
        gen.writeEndObject();
      } catch (IOException e) {
        e.printStackTrace();
      }
    });
    gen.writeEndArray();
  }
}
