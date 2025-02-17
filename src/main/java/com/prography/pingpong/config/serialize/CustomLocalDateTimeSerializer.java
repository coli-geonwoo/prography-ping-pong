package com.prography.pingpong.config.serialize;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.prography.pingpong.exception.custom.PingPongServerException;
import com.prography.pingpong.exception.errorcode.ServerErrorCode;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.springframework.boot.jackson.JsonComponent;

@JsonComponent
public class CustomLocalDateTimeSerializer extends JsonSerializer<LocalDateTime> {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public void serialize(
            LocalDateTime localDateTime,
            JsonGenerator jsonGenerator,
            SerializerProvider serializerProvider
    ) {
        try {
            jsonGenerator.writeString(DATE_TIME_FORMATTER.format(localDateTime));
        } catch (IOException exception) {
            throw new PingPongServerException(ServerErrorCode.INTERNAL_SERVER_ERROR);
        }
    }
}
